package com.example.atlaswear.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atlaswear.model.Panier
import com.example.atlaswear.model.PanierItem
import com.example.atlaswear.repository.PanierRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PanierViewModel : ViewModel() {

    private val repository = PanierRepository()

    private val _panier = MutableStateFlow(Panier())
    val panier: StateFlow<Panier> = _panier

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun loadPanier(clientId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getPanier(clientId).fold(
                onSuccess = { _panier.value = it },
                onFailure = { _message.value = it.message }
            )
            _isLoading.value = false
        }
    }

    fun ajouterAuPanier(clientId: String, item: PanierItem) {
        viewModelScope.launch {
            repository.ajouterOuMettreAJour(clientId, item).fold(
                onSuccess = {
                    _message.value = "Ajouté au panier ✓"
                    loadPanier(clientId)
                },
                onFailure = { _message.value = it.message }
            )
        }
    }

    fun modifierQuantite(clientId: String, produitId: String, quantite: Int) {
        viewModelScope.launch {
            repository.modifierQuantite(clientId, produitId, quantite).fold(
                onSuccess = { loadPanier(clientId) },
                onFailure = { _message.value = it.message }
            )
        }
    }

    fun supprimerItem(clientId: String, produitId: String) {
        viewModelScope.launch {
            repository.supprimerItem(clientId, produitId).fold(
                onSuccess = { loadPanier(clientId) },
                onFailure = { _message.value = it.message }
            )
        }
    }

    fun viderPanier(clientId: String) {
        viewModelScope.launch {
            repository.viderPanier(clientId).fold(
                onSuccess = { _panier.value = Panier(clientId = clientId) },
                onFailure = { _message.value = it.message }
            )
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    fun getPanierActuel(): Panier = _panier.value

}