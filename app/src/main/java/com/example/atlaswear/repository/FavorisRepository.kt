package com.example.atlaswear.repository

import com.example.atlaswear.model.Favori
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FavorisRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getFavoris(clientId: String): Result<List<Favori>> {
        return try {
            val snapshot = db.collection("favoris")
                .whereEqualTo("clientId", clientId)
                .get().await()
            val favoris = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Favori::class.java)?.copy(favoriId = doc.id)
            }
            Result.success(favoris)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun ajouterFavori(clientId: String, favori: Favori): Result<Unit> {
        return try {
            val favoriId = UUID.randomUUID().toString()
            val data = hashMapOf(
                "favoriId"   to favoriId,
                "clientId"   to clientId,
                "produitId"  to favori.produitId,
                "nomProduit" to favori.nomProduit,
                "image"      to favori.image,
                "prix"       to favori.prix,
                "createdAt"  to Timestamp.now()
            )
            db.collection("favoris").document(favoriId).set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun retirerFavori(favoriId: String): Result<Unit> {
        return try {
            db.collection("favoris").document(favoriId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}