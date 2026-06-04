package com.example.atlaswear.repository

import com.example.atlaswear.model.Categorie
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CategorieRepository {
    private  val firestore = FirebaseFirestore.getInstance()

    // Récupérer toutes les catégories
    suspend fun getCategories(): Result<List<Categorie>> {
        return try {
            val snapshot = firestore.collection("categories").get().await()
            val categories = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Categorie::class.java)?.copy(categorieId = doc.id)
            }
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Récupérer une catégorie par son ID
    suspend fun getCategorieById(categorieId: String): Result<String> {
        return try {
            val doc = firestore.collection("categories").document(categorieId).get().await()
            val categorie = doc.getString("nom") ?: return Result.failure(Exception("Catégorie introuvable"))
            Result.success(categorie)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}