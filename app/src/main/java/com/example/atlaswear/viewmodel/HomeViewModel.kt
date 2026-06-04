package com.example.atlaswear.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atlaswear.model.Categorie
import com.example.atlaswear.model.Produit
import com.example.atlaswear.repository.CategorieRepository
import com.example.atlaswear.repository.ProduitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val produitRepository = ProduitRepository()
    private val categorieRepository = CategorieRepository()

    private val _produits = MutableStateFlow<List<Produit>>(emptyList())
    val produits: StateFlow<List<Produit>> = _produits

    private val _categories = MutableStateFlow<List<Categorie>>(emptyList())
    val categories: StateFlow<List<Categorie>> = _categories

    private val _selectedCategorie = MutableStateFlow<String?>(null)
    val selectedCategorie: StateFlow<String?> = _selectedCategorie

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadCategories()
        loadProduits()
    }

    fun loadCategories() {
        viewModelScope.launch {
            categorieRepository.getCategories().fold(
                onSuccess = { _categories.value = it },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun loadProduits(categorieId: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = if (categorieId == null) {
                produitRepository.getProduits()
            } else {
                produitRepository.getProduitsByCategorie(categorieId)
            }
            result.fold(
                onSuccess = { _produits.value = it },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }

    fun selectCategorie(categorieId: String?) {
        _selectedCategorie.value = categorieId
        loadProduits(categorieId)
    }
}