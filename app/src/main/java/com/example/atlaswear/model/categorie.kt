package com.example.atlaswear.model

import com.google.firebase.Timestamp

data class Categorie(
    val categorieId: String = "",
    val nom: String = "",
    val iconeUrl: String = "",
    val validee: Boolean = false,
    val createdAt: Timestamp = Timestamp.now()
)