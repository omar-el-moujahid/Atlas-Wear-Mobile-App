package com.example.atlaswear.repository

import com.example.atlaswear.model.Produit
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProduitRepository {

    private val firestore = FirebaseFirestore.getInstance()

    // Tous les produits
    suspend fun getProduits(): Result<List<Produit>> {
        return try {
            val snapshot = firestore.collection("produits").get().await()
            val produits = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Produit::class.java)?.copy(produitId = doc.id)
            }
            Result.success(produits)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Produits par catégorie
    suspend fun getProduitsByCategorie(categorieId: String): Result<List<Produit>> {
        return try {
            val snapshot = firestore.collection("produits")
                .whereEqualTo("categorieId", categorieId)
                .get().await()
            val produits = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Produit::class.java)?.copy(produitId = doc.id)
            }
            Result.success(produits)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Produits par artisan
    suspend fun getProduitsByArtisan(artisanId: String): Result<List<Produit>> {
        return try {
            val snapshot = firestore.collection("produits")
                .whereEqualTo("artisanId", artisanId)
                .get().await()
            val produits = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Produit::class.java)?.copy(produitId = doc.id)
            }
            Result.success(produits)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Un seul produit
    suspend fun getProduitById(produitId: String): Result<Produit> {
        return try {
            val doc = firestore.collection("produits").document(produitId).get().await()
            val produit = doc.toObject(Produit::class.java)?.copy(produitId = doc.id)
                ?: return Result.failure(Exception("Produit introuvable"))
            Result.success(produit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Ajouter un produit
    suspend fun addProduit(produit: Produit): Result<Unit> {
        return try {
            val ref = firestore.collection("produits").document()
            ref.set(produit.copy(produitId = ref.id)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Modifier un produit
    suspend fun updateProduit(produit: Produit): Result<Unit> {
        return try {
            firestore.collection("produits").document(produit.produitId).set(produit).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Supprimer un produit
    suspend fun deleteProduit(produitId: String): Result<Unit> {
        return try {
            firestore.collection("produits").document(produitId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    // chercher produits par nom
    suspend fun searchProduitsByName(query: String): Result<List<Produit>> {
        return try {
            val snapshot = firestore.collection("produits").whereGreaterThanOrEqualTo("nom", query)
                .orderBy("nom")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get().await()
            val produits = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Produit::class.java)?.copy(produitId = doc.id)
            }
            Result.success(produits)
        } catch (e: Exception) {
            Result.failure(e)
        }
        }
}