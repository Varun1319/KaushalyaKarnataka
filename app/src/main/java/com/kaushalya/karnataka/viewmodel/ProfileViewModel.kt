package com.kaushalya.karnataka.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaushalya.karnataka.data.model.*
import com.kaushalya.karnataka.data.repository.WorkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: WorkerRepository,
) : ViewModel() {

    private val _workerId = MutableStateFlow("")

    val worker: StateFlow<Worker?> = _workerId
        .flatMapLatest { id -> if (id.isBlank()) flowOf(null) else repo.getWorker(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val serviceCards: StateFlow<List<ServiceCard>> = _workerId
        .flatMapLatest { id -> if (id.isBlank()) flowOf(emptyList()) else repo.getServiceCards(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val reviews: StateFlow<List<Review>> = _workerId
        .flatMapLatest { id -> if (id.isBlank()) flowOf(emptyList()) else repo.getReviews(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val workPhotos: StateFlow<List<WorkPhoto>> = _workerId
        .flatMapLatest { id -> if (id.isBlank()) flowOf(emptyList()) else repo.getWorkPhotos(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val hireRequestSent = MutableStateFlow(false)
    val reviewSubmitted = MutableStateFlow(false)

    fun loadWorker(workerId: String) { _workerId.value = workerId }

    fun sendHireRequest(workerId: String, customerName: String, customerPhone: String, service: String) {
        viewModelScope.launch {
            repo.sendHireRequest(
                HireRequest(
                    workerId      = workerId,
                    customerName  = customerName,
                    customerPhone = customerPhone,
                    serviceNeeded = service,
                )
            )
            hireRequestSent.value = true
        }
    }

    fun addReview(workerId: String, customerName: String, rating: Int, text: String) {
        viewModelScope.launch {
            repo.addReview(Review(workerId = workerId, customerName = customerName, rating = rating, reviewText = text))
            reviewSubmitted.value = true
        }
    }

    fun deleteServiceCard(cardId: String) = viewModelScope.launch { repo.deleteServiceCard(cardId) }

    fun saveServiceCard(card: ServiceCard) = viewModelScope.launch { repo.saveServiceCard(card) }
}
