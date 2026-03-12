package com.teethcure.demo

import android.content.Context
import android.graphics.Bitmap
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import kotlin.math.abs
import kotlin.math.atan2

data class FaceOverlayState(
    val centerX: Float = 0f,   // 0.0 ~ 1.0
    val centerY: Float = 0f,   // 0.0 ~ 1.0
    val faceWidth: Float = 0f, // normalized width
    val rotation: Float = 0f,  // degrees
    val isDetected: Boolean = false
)

interface FaceTracker {
    fun start(
        onFaceDetected: (FaceOverlayState) -> Unit,
        onStatusChanged: (String) -> Unit = {}
    )
    fun processBitmap(bitmap: Bitmap, timestampMs: Long, isFrontCamera: Boolean)
    fun stop()
}

class MediaPipeFaceTracker(
    private val context: Context,
) : FaceTracker {
    private var onFaceDetected: (FaceOverlayState) -> Unit = {}
    private var onStatusChanged: (String) -> Unit = {}
    private var started = false
    private var faceLandmarker: FaceLandmarker? = null

    override fun start(
        onFaceDetected: (FaceOverlayState) -> Unit,
        onStatusChanged: (String) -> Unit
    ) {
        this.onFaceDetected = onFaceDetected
        this.onStatusChanged = onStatusChanged
        started = true
        ensureLandmarker()
    }

    override fun processBitmap(bitmap: Bitmap, timestampMs: Long, isFrontCamera: Boolean) {
        if (!started || faceLandmarker == null) return

        try {
            val mpImage = BitmapImageBuilder(bitmap).build()
            val faceResult = faceLandmarker?.detectForVideo(mpImage, timestampMs)
            val faceLandmarks = faceResult?.faceLandmarks()?.firstOrNull()

            if (faceLandmarks.isNullOrEmpty()) {
                onFaceDetected(FaceOverlayState(isDetected = false))
                return
            }

            fun fx(x: Float): Float = if (isFrontCamera) 1f - x else x

            val nose = faceLandmarks[1]
            val leftFace = faceLandmarks[234]
            val rightFace = faceLandmarks[454]

            val centerX = fx(nose.x())
            val centerY = nose.y()
            val faceWidth = abs(rightFace.x() - leftFace.x())

            val angleRad = atan2(
                (rightFace.y() - leftFace.y()).toDouble(),
                (rightFace.x() - leftFace.x()).toDouble()
            )
            val rotationDegrees = Math.toDegrees(angleRad).toFloat()

            onFaceDetected(
                FaceOverlayState(
                    centerX = centerX,
                    centerY = centerY,
                    faceWidth = faceWidth,
                    rotation = rotationDegrees,
                    isDetected = true
                )
            )
        } catch (t: Throwable) {
            onStatusChanged("FaceTracker error: ${t.message}")
        }
    }

    override fun stop() {
        started = false
        faceLandmarker?.close()
        faceLandmarker = null
    }

    private fun ensureLandmarker() {
        if (faceLandmarker != null) return

        runCatching {
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath("models/face_landmarker.task")
                .build()

            val options = FaceLandmarker.FaceLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.VIDEO)
                .setNumFaces(1)
                .build()

            faceLandmarker = FaceLandmarker.createFromOptions(context, options)
        }.onFailure {
            onStatusChanged("FaceLandmarker init failed")
        }
    }
}