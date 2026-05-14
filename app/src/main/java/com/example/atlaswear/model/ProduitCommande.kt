package com.example.atlaswear.model

data class ProduitCommande(
    val produitId: String = "",
    val nomProduit: String = "",
    val image: String = "",
    val prix: Double = 0.0,
    val quantite: Int = 1,
    val sousTotal: Double = 0.0
)
