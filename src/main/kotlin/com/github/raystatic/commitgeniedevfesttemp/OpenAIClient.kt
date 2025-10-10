package com.github.raystatic.commitgeniedevfesttemp

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

object OpenAIClient {

    private val client = OkHttpClient()
    private val API_END_POINT = "https://api.openai.com/v1/chat/completions"

    private fun buildBodyPayload(diff: String): JSONObject {
        val messagesArray = JSONArray()

        val systemMessage = JSONObject()
        systemMessage.apply {
            put("role", "system")
            put("content", """
                You are an AI assistant that writes professional Git commit messages based on diffs.
                Follow the Conventional Commits specification: <type>: <description>.
                Types: feat, fix, chore, docs, style, refactor, perf, test.
                Use imperative mood and concise description.
            """.trimIndent())
        }

        val userMessage = JSONObject()
        userMessage.apply {
            put("role", "user")
            put("content", """
                Generate a commit message for the following git diff:
                ```diff
                $diff
                ```
            """.trimIndent())
        }

        messagesArray.put(systemMessage)
        messagesArray.put(userMessage)

        val json = JSONObject()
        json.apply {
            put("model", "gpt-3.5-turbo")
            put("messages", messagesArray)
        }

        return json

    }

    fun generateCommitMessage(apiKey: String, diff: String, result: (Result) -> Unit) {

        val body = buildBodyPayload(diff).toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(API_END_POINT)
            .addHeader("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                result(Result.Failure("Failed to generate commit"))
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    result(Result.Failure("API error: $responseBody", response.code))
                    return
                }

                try {
                    val jsonObject = JSONObject(responseBody)
                    val choices = jsonObject.getJSONArray("choices")
                    if (choices.length() == 0) {
                        result(Result.Failure("No choices found in response", response.code))
                        return
                    }
                    val message = choices.getJSONObject(0).getJSONObject("message").getString("content")
                    result(Result.Success(message))
                } catch (e: Exception) {
                    e.printStackTrace()
                    result(Result.Failure("Failed to parse response", response.code))
                }
            }
        })

    }

}

sealed class Result {
    data class Success(val message: String): Result()
    data class Failure(val error: String, val code: Int? = null): Result()
}