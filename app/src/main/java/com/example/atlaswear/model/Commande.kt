package com.example.atlaswear.model

import com.google.firebase.Timestamp

data class Commande(
    val commandeId: String = "",
    val clientId: String = "",
    val statut: String = StatutCommande.EN_ATTENTE,
    val adresse: String = "",
    val lignes: List<LigneCommande> = emptyList(),
    val sousTotal: Double = 0.0,
    val reduction: Double = 0.0,
    val total: Double = 0.0,
    val createdAt: Timestamp = Timestamp.now()
)

