package com.example.atlaswear.model

import com.google.firebase.Timestamp


data class Favori (
    val favoriId: String = "",
    val clientId: String = "",
    val produitId: String = "",
    val nomProduit: String = "",
    val image: String = "",
    val prix: Double = 0.0,
    val createdAt: Timestamp = Timestamp.now()
    )