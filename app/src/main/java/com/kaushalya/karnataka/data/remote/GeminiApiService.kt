package com.kaushalya.karnataka.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// ── OpenAI DTOs ──────────────────────────────────────────────────────
data class OpenAiRequest(
    val model      : String = "gpt-3.5-turbo",
    val messages   : List<OpenAiMessage>,
    val max_tokens : Int   = 200,
    val temperature: Float = 0.7f,
)

data class OpenAiMessage(
    val role   : String,
    val content: String,
)

data class OpenAiResponse(val choices: List<OpenAiChoice>?)
data class OpenAiChoice(val message: OpenAiMessage?)

// ── Retrofit service ─────────────────────────────────────────────────
interface OpenAiApiService {
    @POST("v1/chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") bearer: String,
        @Body request: OpenAiRequest,
    ): OpenAiResponse
}

// ── Prompt builder (PDR §13.2 template, OpenAI format) ───────────────
object AiPrompt {
    fun bioPrompt(name: String, skill: String, experience: String, speciality: String, location: String): OpenAiRequest =
        OpenAiRequest(
            messages = listOf(
                OpenAiMessage(
                    role    = "system",
                    content = "You are a helpful assistant that writes professional profiles for skilled " +
                              "blue-collar workers in India. Write in simple, clear English. " +
                              "Keep the bio under 80 words. Make the worker sound trustworthy and experienced.",
                ),
                OpenAiMessage(
                    role    = "user",
                    content = "Write a professional bio for this worker:\n" +
                              "Name: $name\nSkill: $skill\nYears of Experience: $experience\n" +
                              "Best at: $speciality\nLocation: $location",
                ),
            )
        )

    fun OpenAiResponse.extractText(): String =
        choices?.firstOrNull()?.message?.content?.trim()
            ?: "Unable to generate bio. Please try again."
}
