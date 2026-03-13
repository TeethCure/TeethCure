package com.teethcure.demo

import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray
import org.json.JSONObject

data class ProfileSummary(
    val id: Long,
    val name: String,
    val birthDate: String? = null,
)

class ProfileRepository(
    private val baseUrl: String = BuildConfig.PROFILE_API_BASE_URL,
) {
    fun fetchProfiles(): List<ProfileSummary> {
        return runCatching {
            val endpoint = URL("${baseUrl.trimEnd('/')}/api/profiles")
            val connection = (endpoint.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 5_000
                readTimeout = 5_000
                setRequestProperty("Accept", "application/json")
            }

            try {
                val statusCode = connection.responseCode
                val bodyStream = if (statusCode in 200..299) connection.inputStream else connection.errorStream
                val responseBody = bodyStream?.bufferedReader()?.use { it.readText() }.orEmpty()

                if (statusCode !in 200..299) {
                    throw IllegalStateException("프로필 목록을 불러오지 못했습니다. (HTTP $statusCode)")
                }

                parseProfiles(responseBody).ifEmpty { mockProfiles() }
            } finally {
                connection.disconnect()
            }
        }.getOrElse {
            mockProfiles()
        }
    }

    private fun parseProfiles(rawJson: String): List<ProfileSummary> {
        val trimmed = rawJson.trim()
        if (trimmed.isEmpty()) return emptyList()

        val profilesJson = when {
            trimmed.startsWith("[") -> JSONArray(trimmed)
            trimmed.startsWith("{") -> extractArray(JSONObject(trimmed))
            else -> JSONArray()
        }

        return buildList {
            for (index in 0 until profilesJson.length()) {
                val item = profilesJson.optJSONObject(index) ?: continue
                add(
                    ProfileSummary(
                        id = item.optLong("id", item.optLong("profileId", index.toLong())),
                        name = item.optString("profileName")
                            .ifBlank { item.optString("name") }
                            .ifBlank { "프로필 ${index + 1}" },
                        birthDate = item.optString("birthDate").ifBlank { null },
                    ),
                )
            }
        }
    }

    private fun extractArray(root: JSONObject): JSONArray {
        val candidateKeys = listOf("profiles", "data", "result")
        candidateKeys.forEach { key ->
            root.optJSONArray(key)?.let { return it }
        }
        return JSONArray()
    }

    private fun mockProfiles(): List<ProfileSummary> {
        return listOf(
            ProfileSummary(
                id = 1L,
                name = "민지",
                birthDate = "2018-05-12",
            ),
            ProfileSummary(
                id = 2L,
                name = "준호",
                birthDate = "2020-09-03",
            ),
        )
    }
}
