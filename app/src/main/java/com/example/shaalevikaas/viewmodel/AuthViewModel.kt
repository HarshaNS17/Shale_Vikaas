package com.example.shaalevikaas.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shaalevikaas.model.Alumni
import com.example.shaalevikaas.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<Alumni?>(null)
    val userState: StateFlow<Alumni?> = _userState

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private var userJob: Job? = null

    init {
        checkUserStatus()
    }

    private fun checkUserStatus() {
        userJob?.cancel()
        val currentUser = repository.getCurrentUser()
        if (currentUser != null) {
            userJob = viewModelScope.launch {
                repository.getAlumniFlow(currentUser.uid).collectLatest { alumni ->
                    _userState.value = alumni
                }
            }
        }
    }

    fun isUserLoggedIn() = repository.isUserLoggedIn()

    fun isAdmin(): Boolean {
        val user = repository.getCurrentUser()
        return isUserLoggedIn() && _userState.value == null && user?.email?.contains("admin") == true
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.login(email, pass)
                checkUserStatus()
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun registerAlumni(alumni: Alumni, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.registerAlumni(alumni, pass)
                checkUserStatus()
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        userJob?.cancel()
        repository.logout()
        _userState.value = null
        onSuccess()
    }
}
