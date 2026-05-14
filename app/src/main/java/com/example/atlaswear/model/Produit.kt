package com.example.atlaswear.model

import com.google.firebase.Timestamp

data class Produit(
    val produitId: String = "",
    val artisanId: String = "",
    val nomArtisan: String = "",
    val categorieId: String = "",
    val nomCategorie: String = "",
    val nom: String = "",
    val description: String = "",
    val prix: Double = 0.0,
    val prixReduit: Double = 0.0,
    val stock: Int = 0,
    val images: List<String> = emptyList(),
    val disponible: Boolean = true,
    val createdAt: Timestamp = Timestamp.now()
) {
    // Prix effectif : réduit si défini, sinon normal
    val prixEffectif: Double
        get() = if (prixReduit > 0.0) prixReduit else prix

    val aUneReduction: Boolean
        get() = prixReduit > 0.0 && prixReduit < prix
}