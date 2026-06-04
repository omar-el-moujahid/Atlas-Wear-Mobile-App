package com.example.atlaswear.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atlaswear.model.User
import com.example.atlaswear.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Ajouter cet enum
enum class AuthState { LOADING, LOGGED_IN, LOGGED_OUT }

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // ✅ Remplace isInitialized
    private val _authState = MutableStateFlow(AuthState.LOADING)
    val authState: StateFlow<AuthState> = _authState

    init {
        viewModelScope.launch {
            android.util.Log.d("AUTH_INIT", "Début init, isLoggedIn=${repository.isLoggedIn()}")
            if (repository.isLoggedIn()) {
                repository.getCurrentUser().fold(
                    onSuccess = {
                        _currentUser.value = it
                        _authState.value = AuthState.LOGGED_IN
                        android.util.Log.d("AUTH_INIT", "User chargé: ${it.email}")
                    },
                    onFailure = {
                        _authState.value = AuthState.LOGGED_OUT
                        android.util.Log.e("AUTH_INIT", "Erreur: ${it.message}")
                    }
                )
            } else {
                _authState.value = AuthState.LOGGED_OUT
            }
            android.util.Log.d("AUTH_INIT", "authState=${_authState.value}")
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.login(email, password).fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.LOGGED_IN
                    _uiState.value = AuthUiState.Success(user)
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Erreur inconnue")
                }
            )
        }
    }

    fun register(
        nom: String, prenom: String, email: String,
        password: String, ville: String,
        photoUrl: String = "", role: String = "client"
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.register(nom, prenom, email, password, ville, photoUrl, role).fold(
                onSuccess = { user ->
                    _uiState.value = AuthUiState.Success(user)
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Erreur inconnue")
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    fun logout() {
        repository.logout()
        _currentUser.value = null
        _authState.value = AuthState.LOGGED_OUT
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}