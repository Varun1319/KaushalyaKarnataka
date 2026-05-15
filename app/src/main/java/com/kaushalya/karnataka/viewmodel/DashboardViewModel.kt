package com.kaushalya.karnataka.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaushalya.karnataka.data.model.HireRequest
import com.kaushalya.karnataka.data.model.Worker
import com.kaushalya.karnataka.data.repository.WorkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repo: WorkerRepository,
) : ViewModel() {

    // Use first worker as the "logged in" demo worker
    // In production this would be the authenticated user's profile
    private val _allWorkers = MutableStateFlow<List<com.kaushalya.karnataka.data.model.Worker>>(emptyList())

    val worker: StateFlow<Worker?> = _allWorkers
        .map { it.firstOrNull() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val hireRequests: StateFlow<List<HireRequest>> = _allWorkers
        .flatMapLatest { workers ->
            val id = workers.firstOrNull()?.workerId ?: return@flatMapLatest flowOf(emptyList())
            repo.getHireRequests(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val profileViews = MutableStateFlow(128)

    init {
        viewModelScope.launch {
            repo.getAllWorkers().collect { _allWorkers.value = it }
        }
    }

    fun updateRequestStatus(requestId: String, status: String) {
        viewModelScope.launch { repo.updateHireRequestStatus(requestId, status) }
    }

    fun toggleAvailability(available: Boolean) {
        viewModelScope.launch {
            val id = _allWorkers.value.firstOrNull()?.workerId ?: return@launch
            repo.toggleAvailability(id, available)
        }
    }
}
