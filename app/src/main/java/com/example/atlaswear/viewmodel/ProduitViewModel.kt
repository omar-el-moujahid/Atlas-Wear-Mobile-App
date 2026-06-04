package com.example.atlaswear.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atlaswear.model.Produit
import com.example.atlaswear.repository.ProduitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProduitViewModel : ViewModel() {

    private val produitRepository = ProduitRepository()

    private val _produitSelectionne = MutableStateFlow<Produit?>(null)
    val produitSelectionne: StateFlow<Produit?> = _produitSelectionne

    private val _produits = MutableStateFlow<List<Produit>>(emptyList())
    val produits: StateFlow<List<Produit>> = _produits

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadProduitById(produitId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            produitRepository.getProduitById(produitId).fold(
                onSuccess = { _produitSelectionne.value = it },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }

    fun loadProduits() {
        viewModelScope.launch {
            _isLoading.value = true
            produitRepository.getProduits().fold(
                onSuccess = { _produits.value = it },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }
}