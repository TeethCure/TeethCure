package com.teethcure.demo

import kotlin.math.abs

data class MouthObservation(
    val handX: Float,
    val handY: Float,
    val mouthCenterX: Float,
    val mouthCenterY: Float,
    val mouthWidth: Float,
    val isFist: Boolean,
    val timestampMs: Long,
)

class StrokeCounter(
    private val minDxToCount: Float = 0.015f,
    private val zoneCooldownMs: Long = 110L,
) {
    private var lastX: Float? = null
    private var lastCountedAt: MutableMap<MouthZone, Long> = mutableMapOf()

    fun observe(input: MouthObservation): MouthZone? {
        if (!input.isFist) {
            lastX = input.handX
            return null
        }
        if (!isNearMouth(input)) {
            lastX = input.handX
            return null
        }

        val previousX = lastX
        lastX = input.handX
        if (previousX == null) return null

        val dx = abs(input.handX - previousX)
        if (dx < minDxToCount) return null

        val zone = classifyZone(input)
        val lastAt = lastCountedAt[zone] ?: 0L
        if (input.timestampMs - lastAt < zoneCooldownMs) return null

        lastCountedAt[zone] = input.timestampMs
        return zone
    }

    private fun isNearMouth(input: MouthObservation): Boolean {
        val verticalDistance = abs(input.handY - input.mouthCenterY)
        val tolerance = input.mouthWidth * 0.7f
        return verticalDistance <= tolerance
    }

    private fun classifyZone(input: MouthObservation): MouthZone {
        val relativeX = (input.handX - input.mouthCenterX) / input.mouthWidth
        return when {
            relativeX < -0.2f -> MouthZone.LEFT
            relativeX > 0.2f -> MouthZone.RIGHT
            else -> MouthZone.CENTER
        }
    }
}
