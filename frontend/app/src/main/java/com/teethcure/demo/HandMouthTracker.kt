package com.teethcure.demo

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import kotlin.math.abs

data class SkeletonPoint(
    val x: Float,
    val y: Float,
)

data class SkeletonFrame(
    val facePoints: List<SkeletonPoint> = emptyList(),
    val handPoints: List<SkeletonPoint> = emptyList(),
) {
    companion object {
        val Empty = SkeletonFrame()
    }
}

interface HandMouthTracker {
    fun start(
        onStrokeDetected: (MouthZone) -> Unit,
        onSkeletonDetected: (SkeletonFrame) -> Unit = {},
        onStatusChanged: (String) -> Unit = {},
    )

    fun processFrame(imageProxy: ImageProxy, isFrontCamera: Boolean)

    fun stop()
}

class MediaPipeHandMouthTracker(
    private val context: Context,
) : HandMouthTracker {
    private val strokeCounter = StrokeCounter()
    private var onStrokeDetected: (MouthZone) -> Unit = {}
    private var onSkeletonDetected: (SkeletonFrame) -> Unit = {}
    private var onStatusChanged: (String) -> Unit = {}
    private var started = false
    private var handLandmarker: HandLandmarker? = null
    private var faceLandmarker: FaceLandmarker? = null
    private var lastTimestampMs = 0L
    private var lastSkeletonFrame: SkeletonFrame = SkeletonFrame.Empty
    private var lastSkeletonAtMs: Long = 0L

    override fun start(
        onStrokeDetected: (MouthZone) -> Unit,
        onSkeletonDetected: (SkeletonFrame) -> Unit,
        onStatusChanged: (String) -> Unit,
    ) {
        this.onStrokeDetected = onStrokeDetected
        this.onSkeletonDetected = onSkeletonDetected
        this.onStatusChanged = onStatusChanged
        started = true
        ensureLandmarkers()
        if (handLandmarker != null && faceLandmarker != null) {
            onStatusChanged("MediaPipe tracking")
        }
    }

    @OptIn(ExperimentalGetImage::class)
    override fun processFrame(imageProxy: ImageProxy, isFrontCamera: Boolean) {
        if (!started || handLandmarker == null || faceLandmarker == null) {
            imageProxy.close()
            return
        }

        try {
            val rawTimestampMs = imageProxy.imageInfo.timestamp / 1_000_000L
            val baseTimestampMs = if (rawTimestampMs > 0L) rawTimestampMs else SystemClock.elapsedRealtime()
            val timestampMs = if (baseTimestampMs <= lastTimestampMs) lastTimestampMs + 1L else baseTimestampMs
            lastTimestampMs = timestampMs
            val bitmap = imageProxyToBitmap(imageProxy) ?: return
            val mpImage = BitmapImageBuilder(bitmap).build()

            val handResult = handLandmarker?.detectForVideo(mpImage, timestampMs)
            val faceResult = faceLandmarker?.detectForVideo(mpImage, timestampMs)

            val handLandmarks = handResult?.landmarks()?.firstOrNull()
            val faceLandmarks = faceResult?.faceLandmarks()?.firstOrNull()
            if (handLandmarks.isNullOrEmpty() || faceLandmarks.isNullOrEmpty()) {
                val keepRecent = (timestampMs - lastSkeletonAtMs) <= 200L
                onSkeletonDetected(if (keepRecent) lastSkeletonFrame else SkeletonFrame.Empty)
                return
            }

            fun fx(x: Float): Float = if (isFrontCamera) 1f - x else x
            fun point(x: Float, y: Float) = SkeletonPoint(fx(x), y)

            val handForUi = listOf(0, 5, 9, 13, 17, 8)
                .filter { it < handLandmarks.size }
                .map { point(handLandmarks[it].x(), handLandmarks[it].y()) }

            val faceForUi = listOf(33, 263, 1, 61, 13, 291, 14)
                .filter { it < faceLandmarks.size }
                .map { point(faceLandmarks[it].x(), faceLandmarks[it].y()) }

            val frame = SkeletonFrame(facePoints = faceForUi, handPoints = handForUi)
            lastSkeletonFrame = frame
            lastSkeletonAtMs = timestampMs
            onSkeletonDetected(frame)

            if (faceLandmarks.size <= 291 || handLandmarks.size <= 8) return

            val mouthLeft = faceLandmarks[61]
            val mouthRight = faceLandmarks[291]
            val upperLip = faceLandmarks[13]
            val lowerLip = faceLandmarks[14]
            val wrist = handLandmarks[0]

            val mouthCenterX = fx((mouthLeft.x() + mouthRight.x()) / 2f)
            val mouthCenterY = (upperLip.y() + lowerLip.y()) / 2f
            val mouthWidth = abs(mouthRight.x() - mouthLeft.x()).coerceAtLeast(0.05f)
            val fist = isFist(handLandmarks)

            strokeCounter.observe(
                MouthObservation(
                    handX = fx(wrist.x()),
                    handY = wrist.y(),
                    mouthCenterX = mouthCenterX,
                    mouthCenterY = mouthCenterY,
                    mouthWidth = mouthWidth,
                    isFist = fist,
                    timestampMs = timestampMs,
                ),
            )?.let { zone ->
                onStrokeDetected(zone)
            }
        } catch (t: Throwable) {
            val reason = t.message?.take(100) ?: t::class.java.simpleName
            onStatusChanged("MediaPipe frame error: $reason")
        } finally {
            imageProxy.close()
        }
    }

    override fun stop() {
        started = false
        lastTimestampMs = 0L
        lastSkeletonFrame = SkeletonFrame.Empty
        lastSkeletonAtMs = 0L
        onSkeletonDetected(SkeletonFrame.Empty)
    }

    private fun ensureLandmarkers() {
        if (handLandmarker != null && faceLandmarker != null) return
        if (!assetExists("models/hand_landmarker.task") || !assetExists("models/face_landmarker.task")) {
            handLandmarker = null
            faceLandmarker = null
            onStatusChanged("Missing model files in assets/models")
            return
        }

        runCatching {
            val baseHand = BaseOptions.builder()
                .setModelAssetPath("models/hand_landmarker.task")
                .build()
            val handOptions = HandLandmarker.HandLandmarkerOptions.builder()
                .setBaseOptions(baseHand)
                .setRunningMode(RunningMode.VIDEO)
                .setNumHands(1)
                .build()
            handLandmarker = HandLandmarker.createFromOptions(context, handOptions)

            val baseFace = BaseOptions.builder()
                .setModelAssetPath("models/face_landmarker.task")
                .build()
            val faceOptions = FaceLandmarker.FaceLandmarkerOptions.builder()
                .setBaseOptions(baseFace)
                .setRunningMode(RunningMode.VIDEO)
                .setNumFaces(1)
                .build()
            faceLandmarker = FaceLandmarker.createFromOptions(context, faceOptions)
        }.onFailure {
            handLandmarker = null
            faceLandmarker = null
            onStatusChanged("MediaPipe init failed")
        }
    }

    private fun assetExists(path: String): Boolean {
        return runCatching {
            context.assets.open(path).close()
            true
        }.getOrDefault(false)
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        val width = imageProxy.width
        val height = imageProxy.height
        if (imageProxy.planes.isEmpty() || width <= 0 || height <= 0) return null
        val plane = imageProxy.planes[0]
        val buffer = plane.buffer
        val rowStride = plane.rowStride
        val pixelStride = plane.pixelStride
        if (pixelStride <= 0 || rowStride <= 0) return null

        val pixels = IntArray(width * height)
        buffer.rewind()

        for (y in 0 until height) {
            val rowStart = y * rowStride
            for (x in 0 until width) {
                val i = rowStart + x * pixelStride
                if (i + 3 >= buffer.limit()) continue
                val r = buffer.get(i).toInt() and 0xFF
                val g = buffer.get(i + 1).toInt() and 0xFF
                val b = buffer.get(i + 2).toInt() and 0xFF
                val a = buffer.get(i + 3).toInt() and 0xFF
                pixels[y * width + x] = (a shl 24) or (r shl 16) or (g shl 8) or b
            }
        }

        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }

    private fun isFist(hand: List<com.google.mediapipe.tasks.components.containers.NormalizedLandmark>): Boolean {
        // Tip-MCP distance for each finger; smaller distances indicate closed fist.
        if (hand.size <= 20) return false
        fun d(a: Int, b: Int): Float {
            val dx = hand[a].x() - hand[b].x()
            val dy = hand[a].y() - hand[b].y()
            return kotlin.math.sqrt(dx * dx + dy * dy)
        }
        val index = d(8, 5)
        val middle = d(12, 9)
        val ring = d(16, 13)
        val pinky = d(20, 17)
        val avg = (index + middle + ring + pinky) / 4f
        return avg < 0.08f
    }
}
