package com.kaushalya.karnataka.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaushalya.karnataka.data.repository.WorkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AiBioUiState {
    object Idle    : AiBioUiState()
    object Loading : AiBioUiState()
    data class Success(val bio: String) : AiBioUiState()
    data class Error(val message: String) : AiBioUiState()
}

@HiltViewModel
class AiBioViewModel @Inject constructor(
    private val repo: WorkerRepository,
) : ViewModel() {

    // 4-step questionnaire fields
    val name       = MutableStateFlow("")
    val skill      = MutableStateFlow("")
    val experience = MutableStateFlow("")
    val speciality = MutableStateFlow("")
    val location   = MutableStateFlow("")
    val editableBio = MutableStateFlow("")

    val uiState = MutableStateFlow<AiBioUiState>(AiBioUiState.Idle)
    val savedSuccessfully = MutableStateFlow(false)

    fun generateBio() {
        viewModelScope.launch {
            uiState.value = AiBioUiState.Loading
            val result = repo.generateBio(
                name       = name.value,
                skill      = skill.value,
                experience = experience.value,
                speciality = speciality.value,
                location   = location.value,
            )
            uiState.value = result.fold(
                onSuccess = { bio ->
                    editableBio.value = bio
                    AiBioUiState.Success(bio)
                },
                onFailure = { AiBioUiState.Error(it.message ?: "Unknown error") }
            )
        }
    }

    fun saveToProfile() {
        viewModelScope.launch {
            val uid = repo.currentUid
            if (uid.isNotBlank() && editableBio.value.isNotBlank()) {
                repo.updateBio(uid, editableBio.value)
                savedSuccessfully.value = true
            }
        }
    }
}
