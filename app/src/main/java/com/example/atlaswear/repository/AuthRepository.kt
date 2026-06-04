package com.example.atlaswear.repository

import com.example.atlaswear.model.User
import com.example.atlaswear.model.UserRole
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun register(nom: String, prenom: String, email: String, password: String, ville: String, photoUrl: String = "", role: String = UserRole.CLIENT): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: return Result.failure(Exception("UID introuvable"))

            val newUser = User(
                uid = uid,
                nom = nom,
                prenom = prenom,
                email = email,
                ville = ville,
                photoUrl = photoUrl,
                role = role,
                creerle = Timestamp.now()
            )

            firestore.collection("users").document(uid).set(newUser).await()
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(Exception(getErrorMessage(e)))
        }
    }

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun logout() {
        auth.signOut()
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: return Result.failure(Exception("UID introuvable"))

            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java)?.copy(uid = uid)  // ✅ forcer l'uid
                ?: return Result.failure(Exception("Utilisateur introuvable"))

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception(getErrorMessage(e)))
        }
    }

    suspend fun getCurrentUser(): Result<User> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Aucun utilisateur connecté"))

            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java)?.copy(uid = uid)  // ✅ forcer l'uid
                ?: return Result.failure(Exception("Utilisateur introuvable"))

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getErrorMessage(e: Exception): String {
        return when {
            e.message?.contains("Given String is empty or null") == true ->
                "Veuillez remplir tous les champs"

            e.message?.contains("badly formatted") == true ||
                    e.message?.contains("badly-formatted") == true ->
                "Format d'email invalide"

            e.message?.contains("supplied auth credential is incorrect") == true ||
                    e.message?.contains("INVALID_LOGIN_CREDENTIALS") == true ->
                "Email ou mot de passe incorrect"

            e.message?.contains("network error") == true ||
                    e.message?.contains("Network error") == true ->
                "Erreur de connexion, vérifiez votre internet"

            e.message?.contains("email-already-in-use") == true ->
                "Un compte existe déjà avec cet email"

            e.message?.contains("weak-password") == true ->
                "Le mot de passe doit contenir au moins 6 caractères"

            e.message?.contains("user-disabled") == true ->
                "Ce compte a été désactivé"

            e.message?.contains("too-many-requests") == true ->
                "Trop de tentatives, réessayez plus tard"

            else -> "Une erreur est survenue : ${e.message}"
        }
    }

}