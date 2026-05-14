package com.example.shaalevikaas.viewmodel

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shaalevikaas.model.Announcement
import com.example.shaalevikaas.model.Need
import com.example.shaalevikaas.model.Pledge
import com.example.shaalevikaas.model.Alumni
import com.example.shaalevikaas.repository.NeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NeedViewModel @Inject constructor(
    private val repository: NeedRepository
) : ViewModel() {

    private val _needs = MutableStateFlow<List<Need>>(emptyList())
    val needs: StateFlow<List<Need>> = _needs

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements

    private val _hallOfFame = MutableStateFlow<List<Alumni>>(emptyList())
    val hallOfFame: StateFlow<List<Alumni>> = _hallOfFame

    private val _allAlumni = MutableStateFlow<List<Alumni>>(emptyList())
    val allAlumni: StateFlow<List<Alumni>> = _allAlumni

    private val _allPledges = MutableStateFlow<List<Pledge>>(emptyList())
    val allPledges: StateFlow<List<Pledge>> = _allPledges

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    init {
        fetchNeeds()
        fetchAnnouncements()
        fetchHallOfFame()
        fetchAllAlumni()
        fetchAllPledges()
    }

    private fun fetchNeeds() {
        viewModelScope.launch {
            repository.getNeeds()
                .catch { _error.value = "Failed to load needs: ${it.message}" }
                .collect { _needs.value = it }
        }
    }

    private fun fetchAnnouncements() {
        viewModelScope.launch {
            repository.getAnnouncements()
                .catch { _error.value = "Failed to load announcements: ${it.message}" }
                .collect { _announcements.value = it }
        }
    }

    private fun fetchHallOfFame() {
        viewModelScope.launch {
            repository.getHallOfFame()
                .catch { _error.value = "Failed to load Hall of Fame: ${it.message}" }
                .collect { _hallOfFame.value = it }
        }
    }

    private fun fetchAllAlumni() {
        viewModelScope.launch {
            repository.getAllAlumni()
                .catch { _error.value = "Failed to load alumni: ${it.message}" }
                .collect { _allAlumni.value = it }
        }
    }

    private fun fetchAllPledges() {
        viewModelScope.launch {
            repository.getAllPledges()
                .catch { _error.value = "Failed to load pledges: ${it.message}" }
                .collect { _allPledges.value = it }
        }
    }

    fun addNeed(need: Need, mainImageUri: Uri?, beforeImageUri: Uri?, onComplete: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.addNeed(need, mainImageUri, beforeImageUri)
                onComplete()
            } catch (e: Exception) {
                _error.value = "Failed to add need: ${e.message}"
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateNeed(need: Need, mainImageUri: Uri?, afterImageUri: Uri?, onComplete: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.updateNeed(need, mainImageUri, afterImageUri)
                onComplete()
            } catch (e: Exception) {
                _error.value = "Failed to update need: ${e.message}"
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteNeed(needId: String) {
        viewModelScope.launch {
            try {
                repository.deleteNeed(needId)
            } catch (e: Exception) {
                _error.value = "Failed to delete: ${e.message}"
            }
        }
    }

    fun submitPledge(pledge: Pledge, onComplete: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.submitPledge(pledge)
                onComplete()
            } catch (e: Exception) {
                _error.value = "Pledge failed: ${e.message}"
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    fun addAnnouncement(announcement: Announcement, imageUri: Uri?, onComplete: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.addAnnouncement(announcement, imageUri)
                onComplete()
            } catch (e: Exception) {
                _error.value = "Failed to send announcement: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}
