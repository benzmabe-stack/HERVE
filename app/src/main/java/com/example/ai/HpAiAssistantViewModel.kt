package com.example.ai

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.util.concurrent.CancellationException

sealed interface AiUiState {
    data object Idle : AiUiState
    data object Loading : AiUiState
    data class Success(val result: String) : AiUiState
    data class Error(val message: String) : AiUiState
}

class HpAiAssistantViewModel : ViewModel() {
    private val TAG = "HpAiAssistantViewModel"

    private val model by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    private val _uiState = MutableStateFlow<AiUiState>(AiUiState.Idle)
    val uiState: StateFlow<AiUiState> = _uiState.asStateFlow()

    fun processRequest(action: String, content: String, targetLanguage: String = "French") {
        viewModelScope.launch {
            _uiState.value = AiUiState.Loading
            try {
                val prompt = when (action) {
                    "summarize" -> "Summarize the following web page content concisely:\n\n$content"
                    "explain" -> "Explain the key concepts of the following text in clear, professional terms:\n\n$content"
                    "translate" -> "Translate the following text into $targetLanguage accurately:\n\n$content"
                    "qa" -> content
                    else -> "Analyze the following content:\n\n$content"
                }

                val response = withTimeout(60_000L) {
                    model.generateContent(prompt)
                }
                val text = response.text
                if (!text.isNullOrBlank()) {
                    _uiState.value = AiUiState.Success(text)
                } else {
                    _uiState.value = AiUiState.Error("AI returned an empty response.")
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "AI request timed out", e)
                _uiState.value = AiUiState.Error("AI request timed out after 60 seconds.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI request failed", e)
                _uiState.value = AiUiState.Error(e.localizedMessage ?: "AI processing failed.")
            }
        }
    }

    fun resetState() {
        _uiState.value = AiUiState.Idle
    }
}
