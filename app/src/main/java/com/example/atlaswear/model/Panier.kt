package com.example.atlaswear.model

import com.google.firebase.Timestamp

data class Panier(
    val clientId: String = "",
    val items: List<PanierItem> = emptyList(),
    val updatedAt: Timestamp = Timestamp.now()
) {
    val total: Double
        get() = items.sumOf { it.sousTotal }

    val nombreArticles: Int
        get() = items.sumOf { it.quantite }

    // Items groupés par artisan — utile pour l'affichage
    val itemsParArtisan: Map<String, List<PanierItem>>
        get() = items.groupBy { it.artisanId }
}