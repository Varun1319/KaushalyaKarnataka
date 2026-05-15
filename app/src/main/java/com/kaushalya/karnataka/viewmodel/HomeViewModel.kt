package com.kaushalya.karnataka.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaushalya.karnataka.data.model.Worker
import com.kaushalya.karnataka.data.repository.WorkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: WorkerRepository,
) : ViewModel() {

    private val _allWorkers = MutableStateFlow<List<Worker>>(emptyList())
    val searchQuery    = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All")
    val isLoading      = MutableStateFlow(true)

    /** Filtered list reacts to both search query and category chip */
    val workers: StateFlow<List<Worker>> = combine(
        _allWorkers, searchQuery, selectedCategory
    ) { workers, query, category ->
        workers.filter { w ->
            val matchesCategory = category == "All" || w.skillCategory.equals(category, ignoreCase = true)
            val matchesQuery    = query.isBlank() ||
                    w.fullName.contains(query, ignoreCase = true) ||
                    w.skillCategory.contains(query, ignoreCase = true) ||
                    w.locationText.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            repo.getAllWorkers().collect {
                _allWorkers.value = it
                isLoading.value = false
            }
        }
    }
}
