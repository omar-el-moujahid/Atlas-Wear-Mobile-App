package com.example.atlaswear.model

data class LigneCommande(
    val artisanId: String = "",
    val nomArtisan: String = "",
    val produits: List<ProduitCommande> = emptyList(),
    val sousTotal: Double = 0.0
)