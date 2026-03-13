package com.teethcure.demo

import android.content.Context
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.framework.image.MediaImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import kotlin.math.*

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
    fun processFrame(imageProxy: ImageProxy, isFrontCamera: Boolean)
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

    override fun processFrame(imageProxy: ImageProxy, isFrontCamera: Boolean) {
        if (!started || faceLandmarker == null) return

        try {
            val mediaImage = imageProxy.image ?: return // ImageProxy에서 원본 이미지 꺼냄
            val mpImage: MPImage = MediaImageBuilder(mediaImage).build() // MediaPipe가 쓸 수 있도록 MPImage로 변환
            val sensorRotation = imageProxy.imageInfo.rotationDegrees    // 카메라 센서가 감지한 회전 각도
            try {
                // 카메라 회전 보정 적용
                val imageProcessingOptions = ImageProcessingOptions.builder()
                    .setRotationDegrees(sensorRotation)
                    .build()
                val timestampMs = imageProxy.imageInfo.timestamp / 1_000_000L
                // MediaPipe로 얼굴 랜드마크 추출
                val faceResult = faceLandmarker?.detectForVideo(mpImage, imageProcessingOptions, timestampMs)
                val faceLandmarks = faceResult?.faceLandmarks()?.firstOrNull()

                if (faceLandmarks.isNullOrEmpty()) {
                    onFaceDetected(FaceOverlayState(isDetected = false))
                    return
                }

                val leftFace = faceLandmarks[234]   // 얼굴 왼쪽 끝
                val rightFace = faceLandmarks[454]  // 얼굴 오른쪽 끝
                val topFace = faceLandmarks[10]     // 이마 끝
                val bottomFace = faceLandmarks[152] // 턱 끝

                // 중심점 계산
                val rawCenterX = (leftFace.x() + rightFace.x()) / 2f
                val centerX = if (isFrontCamera) 1f - rawCenterX else rawCenterX
                val centerY = topFace.y()

                // faceWidth, faceHeight 계산
                val dx = rightFace.x() - leftFace.x()
                val dy = rightFace.y() - leftFace.y()
                val faceWidth = kotlin.math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
                val faceHeight = kotlin.math.abs(bottomFace.y() - topFace.y())

                // 회전 각도 계산 및 전면 카메라 보정
                val angleRad = kotlin.math.atan2(dy.toDouble(), dx.toDouble())
                var rotationDegrees = Math.toDegrees(angleRad).toFloat() - 90f // 90도 보정 추가

                // 전면 카메라일 경우 각도 방향을 반대로 뒤집어줘야 오버레이가 따라옴
                if (isFrontCamera) rotationDegrees = -rotationDegrees

                onFaceDetected(
                    FaceOverlayState(
                        centerX = centerX,
                        centerY = centerY,
                        faceWidth = faceWidth,
                        rotation = rotationDegrees,
                        isDetected = true
                    )
                )
            } finally {
                mpImage.close()
            }
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
