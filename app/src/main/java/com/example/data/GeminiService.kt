package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.json.JSONArray
import com.example.BuildConfig

data class GroundingSource(
    val title: String,
    val url: String
)

data class GeminiSearchResponse(
    val answer: String,
    val sources: List<GroundingSource>
)

object GeminiService {
    private const val TAG = "GeminiService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    suspend fun searchGroundingQuery(query: String): GeminiSearchResponse = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext GeminiSearchResponse(
                answer = "Error: Please set your Gemini API key in the AI Studio Secrets panel.\n\nخطأ: يرجى تهيئة مفتاح واجهة برمجة تطبيقات Gemini في لوحة الأسرار.",
                sources = emptyList()
            )
        }

        val requestUrl = "$BASE_URL?key=$apiKey"

        // Construct raw JSON body with googleSearch tool for real-time grounding search
        val jsonRequest = """
            {
              "contents": [
                {
                  "parts": [
                    {
                      "text": ${JSONObject.quote(query)}
                    }
                  ]
                }
              ],
              "tools": [
                {
                  "googleSearch": {}
                }
              ],
              "generationConfig": {
                "temperature": 0.3
              }
            }
        """.trimIndent()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonRequest.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(requestUrl)
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val code = response.code
                    val errMsg = response.body?.string() ?: ""
                    Log.e(TAG, "Gemini API failed with response code $code and body: $errMsg")
                    return@withContext GeminiSearchResponse(
                        answer = "Network Error ($code). Unable to retrieve search results.\n\nخطأ في الشبكة ($code). لم نتمكن من جلب نتائج البحث.",
                        sources = emptyList()
                    )
                }

                val responseBodyStr = response.body?.string() ?: ""
                Log.d(TAG, "Gemini response: $responseBodyStr")

                val jsonResponse = JSONObject(responseBodyStr)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    return@withContext GeminiSearchResponse(
                        answer = "No candidate responses returned by AI.\n\nلم يتم إرجاع أي رد من الذكاء الاصطناعي.",
                        sources = emptyList()
                    )
                }

                val firstCandidate = candidates.getJSONObject(0)
                val responseContent = firstCandidate.optJSONObject("content")
                val parts = responseContent?.optJSONArray("parts")
                val textResponse = if (parts != null && parts.length() > 0) {
                    parts.getJSONObject(0).optString("text") ?: "No textual feedback."
                } else {
                    "No response text available."
                }

                // Parse search grounding sources
                val sourcesList = mutableListOf<GroundingSource>()
                val groundingMetadata = firstCandidate.optJSONObject("groundingMetadata")
                if (groundingMetadata != null) {
                    val groundingChunks = groundingMetadata.optJSONArray("groundingChunks")
                    if (groundingChunks != null) {
                        for (i in 0 until groundingChunks.length()) {
                            val chunk = groundingChunks.getJSONObject(i)
                            val web = chunk.optJSONObject("web")
                            if (web != null) {
                                val title = web.optString("title") ?: "Web Source"
                                val uri = web.optString("uri") ?: ""
                                if (uri.isNotEmpty()) {
                                    sourcesList.add(GroundingSource(title, uri))
                                }
                            }
                        }
                    }
                }

                return@withContext GeminiSearchResponse(
                    answer = textResponse,
                    sources = sourcesList.distinctBy { it.url } // Remove duplicate URLs
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during searchGroundingQuery", e)
            return@withContext GeminiSearchResponse(
                answer = "Error query connection: ${e.localizedMessage}\n\nخطأ في استعلام الاتصال: ${e.localizedMessage}",
                sources = emptyList()
            )
        }
    }
}
