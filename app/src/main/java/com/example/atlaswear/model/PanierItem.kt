package com.example.atlaswear.model

data class PanierItem(
    val produitId: String = "",
    val nomProduit: String = "",
    val image: String = "",
    val prix: Double = 0.0,
    val artisanId: String = "",
    val nomArtisan: String = "",
    val quantite: Int = 1
) {
    val sousTotal: Double
        get() = prix * quantite
}