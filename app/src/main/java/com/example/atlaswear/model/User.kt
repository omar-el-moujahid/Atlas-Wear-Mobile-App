package com.example.atlaswear.model

import com.google.firebase.Timestamp
data class User (
    val uid: String = "",
    val nom : String = "",
    val prenom : String = "",
    val email : String = "",
    val ville : String = "",
    val photoUrl : String = "",
    val creerle : Timestamp = com.google.firebase.Timestamp.now(),
//    val role : UserRole = UserRole.Client
    // Pour simplifier la sérialisation, on stocke le rôle en tant que chaîne de caractères
    // par defaut client
    val role: String = "client",
    ){
    constructor() : this (
        uid = "",
        nom = "",
        prenom = "",
        email = "",
        ville = "",
        photoUrl = "",
        creerle = Timestamp.now(),
        role = "client"
            )
}

