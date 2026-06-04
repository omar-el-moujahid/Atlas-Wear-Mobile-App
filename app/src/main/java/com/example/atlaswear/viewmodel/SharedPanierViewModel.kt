package com.example.atlaswear.viewmodel

import androidx.lifecycle.ViewModel
import com.example.atlaswear.model.Panier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedPanierViewModel : ViewModel() {
    private val _panierSnapshot = MutableStateFlow(Panier())
    val panierSnapshot: StateFlow<Panier> = _panierSnapshot

    fun sauvegarderPanier(panier: Panier) {
        _panierSnapshot.value = panier
    }
}