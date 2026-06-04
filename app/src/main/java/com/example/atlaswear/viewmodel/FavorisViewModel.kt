package com.example.atlaswear.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atlaswear.model.Favori
import com.example.atlaswear.repository.FavorisRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class FavorisViewModel : ViewModel() {
    private val repository = FavorisRepository()

    private val _favoris = MutableStateFlow<List<Favori>>(emptyList())
    val favoris: StateFlow<List<Favori>> = _favoris

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadFavoris(clientId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getFavoris(clientId).fold(
                onSuccess = { _favoris.value = it },
                onFailure = { _favoris.value = emptyList() }
            )
            _isLoading.value = false
        }
    }

    fun estEnFavori(produitId: String): Boolean {
        return _favoris.value.any { it.produitId == produitId }
    }

    fun toggleFavori(clientId: String, produitId: String, nomProduit: String, image: String, prix: Double) {
        viewModelScope.launch {
            val existant = _favoris.value.find { it.produitId == produitId }
            if (existant != null) {
                // Retirer
                repository.retirerFavori(existant.favoriId).fold(
                    onSuccess = {
                        _favoris.value = _favoris.value.filter { it.produitId != produitId }
                    },
                    onFailure = {}
                )
            } else {
                // Ajouter
                val favori = Favori(
                    favoriId   = UUID.randomUUID().toString(),
                    clientId   = clientId,
                    produitId  = produitId,
                    nomProduit = nomProduit,
                    image      = image,
                    prix       = prix
                )
                repository.ajouterFavori(clientId, favori).fold(
                    onSuccess = {
                        _favoris.value = _favoris.value + favori
                    },
                    onFailure = {}
                )
            }
        }
    }

    fun retirerFavori(clientId: String, favoriId: String) {
        viewModelScope.launch {
            repository.retirerFavori(favoriId).fold(
                onSuccess = {
                    _favoris.value = _favoris.value.filter { it.favoriId != favoriId }
                },
                onFailure = {}
            )
        }
    }
}