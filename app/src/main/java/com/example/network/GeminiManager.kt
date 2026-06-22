package com.example.network

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

annotation class Keep

@Keep
data class GeminiPart(val text: String?)

@Keep
data class GeminiContent(val parts: List<GeminiPart>?)

@Keep
data class GeminiRequest(val contents: List<GeminiContent>?)

@Keep
data class GeminiCandidate(val content: GeminiContent?)

@Keep
data class GeminiResponse(val candidates: List<GeminiCandidate>?)

object GeminiManager {
    private const val TAG = "GeminiManager"
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Ask the Gemini AI. Replaces template placeholders dynamically.
     */
    suspend fun generateResponse(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "API Key is empty or placeholder! Simulating offline response.")
            return@withContext getMockResponse(prompt)
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val combinedPrompt = if (systemInstruction != null) {
            "System Instruction: $systemInstruction\n\nUser Query: $prompt"
        } else {
            prompt
        }

        val requestPayload = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(combinedPrompt))
                )
            )
        )

        val requestAdapter = moshi.adapter(GeminiRequest::class.java)
        val jsonString = requestAdapter.toJson(requestPayload)

        val request = Request.Builder()
            .url(url)
            .post(jsonString.toRequestBody(JSON_MEDIA_TYPE))
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errMsg = response.peekBody(1024).string()
                    Log.e(TAG, "Unsuccessful response from Gemini API: code=${response.code}, message=$errMsg")
                    return@withContext "Error callback: Code ${response.code} from API. (Ensure key is active and prompt is clean. If key is missing, add it in AI Studio Secrets)"
                }

                val bodyString = response.body?.string() ?: return@withContext "Error: Received empty response from model."
                val responseAdapter = moshi.adapter(GeminiResponse::class.java)
                val geminiResponse = responseAdapter.fromJson(bodyString)
                val responseText = geminiResponse?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (!responseText.isNullOrBlank()) {
                    responseText
                } else {
                    "No output response from Gemini. Check status and configurations."
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network exception calling Gemini API", e)
            "Network exception: ${e.localizedMessage ?: "Connection Timeout"}. Setup your key properly."
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected exception calling Gemini API", e)
            "Unexpected execution error: ${e.localizedMessage}. Check the logs."
        }
    }

    private fun getMockResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("price") || lower.contains("checker") || lower.contains("value") -> {
                "🤖 [Offline Advisor Mode]\n\n" +
                "Estimated Valuation Analysis:\n" +
                "• Approximate Value: 110,000,000 to 145,000,000 Grand RP Currency\n" +
                "• Market Demand Rating: ⭐⭐⭐⭐☆ (Highly Liquid)\n" +
                "• Seller Recommendation: If you are selling, set asking price to 135,000,000 for space to negotiate. If buying, try offering 115,000,000.\n\n" +
                "Note: Configure your GEMINI_API_KEY in the AI Studio Secrets panel for accurate real-time market data extraction!"
            }
            lower.contains("invest") || lower.contains("profit") || lower.contains("business") -> {
                "🤖 [Offline Advisor Mode]\n\n" +
                "Business Strategy Review:\n" +
                "• Payout Period: 50 - 62 days depending on the state of notary fees and active promotions.\n" +
                "• Profitability Margin: Excellent (~15% annual yield equivalents).\n" +
                "• Growth Prospect: Keep high staffing (minimum 5 active employees) and buy targeted advertisement spots during weekends.\n\n" +
                "Note: Add a valid Gemini API key to query real active player averages!"
            }
            lower.contains("advertisement") || lower.contains("ad ") || lower.contains("promote") -> {
                "🤖 [Offline Advisor Mode]\n\n" +
                "Generated Promotional Campaign:\n" +
                "📢 [HOT DEAL] 🌟 Premium Opportunity!\n" +
                "⚡ Don't miss this exclusive asset. Top placement, verified clean history, and highly negotiable state price!\n" +
                "📞 PM now to place your bids. Let's make an elite trade today!\n\n" +
                "Copy this pitch or activate your AI Key to customize styling!"
            }
            else -> {
                "🤖 [Offline Advisor Mode]\n\n" +
                "Hello, Grand Mobile RP Trader!\n" +
                "I am your AI Advisor. Ask me anything about vehicles, businesses, pricing, investments, or how to design the best listing description!\n\n" +
                "💡 Tip: Configure your personal GEMINI_API_KEY in the Secrets panel of AI Studio to activate live, unconstrained Gemini 3.5 reasoning!"
            }
        }
    }
}
