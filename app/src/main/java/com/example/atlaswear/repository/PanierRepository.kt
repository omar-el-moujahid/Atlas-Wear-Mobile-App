package com.example.atlaswear.repository

import android.util.Log
import com.example.atlaswear.model.Panier
import com.example.atlaswear.model.PanierItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PanierRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getPanier(clientId: String): Result<Panier> {
        return try {
            val doc = firestore.collection("panier").document(clientId).get().await()
            val panier = if (doc.exists()) {
                doc.toObject(Panier::class.java) ?: Panier(clientId = clientId)
            } else {
                Panier(clientId = clientId)
            }
            Result.success(panier)
        } catch (e: Exception) {
            Result.failure(Exception("Erreur de chargement du panier"))
        }
    }

    suspend fun ajouterOuMettreAJour(clientId: String, item: PanierItem): Result<Unit> {
        return try {
            Log.d("PANIER", "clientId = '$clientId'")
            val ref = firestore.collection("panier").document(clientId)
            val doc = ref.get().await()

            val itemsExistants = if (doc.exists()) {
                val panier = doc.toObject(Panier::class.java)
                panier?.items?.toMutableList() ?: mutableListOf()
            } else {
                mutableListOf()
            }

            val index = itemsExistants.indexOfFirst { it.produitId == item.produitId }
            if (index >= 0) {
                itemsExistants[index] = itemsExistants[index].copy(
                    quantite = itemsExistants[index].quantite + item.quantite
                )
            } else {
                itemsExistants.add(item)
            }

            // ✅ Sauvegarder avec hashmap explicite
            ref.set(
                mapOf(
                    "clientId" to clientId,
                    "items" to itemsExistants.map { panierItem ->
                        mapOf(
                            "produitId" to panierItem.produitId,
                            "nomProduit" to panierItem.nomProduit,
                            "image" to panierItem.image,
                            "prix" to panierItem.prix,
                            "artisanId" to panierItem.artisanId,
                            "nomArtisan" to panierItem.nomArtisan,
                            "quantite" to panierItem.quantite
                        )
                    },
                    "updatedAt" to com.google.firebase.Timestamp.now()
                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Erreur lors de l'ajout au panier: ${e.message}"))
        }
    }


    suspend fun supprimerItem(clientId: String, produitId: String): Result<Unit> {
        return modifierQuantite(clientId, produitId, 0)
    }

    suspend fun modifierQuantite(clientId: String, produitId: String, quantite: Int): Result<Unit> {
        return try {
            // Valider clientId
            if (clientId.isBlank()) {
                return Result.failure(Exception("ClientId vide — impossible de modifier le panier"))
            }

            val doc = firestore.collection("panier").document(clientId).get().await()
            val panier = doc.toObject(Panier::class.java) ?: return Result.failure(Exception("Panier introuvable"))

            val items = panier.items.toMutableList()
            val index = items.indexOfFirst { it.produitId == produitId }

            if (index >= 0) {
                if (quantite <= 0) {
                    items.removeAt(index)
                } else {
                    items[index] = items[index].copy(quantite = quantite)
                }
            }

            // ✅ Utiliser HashMap comme dans ajouterOuMettreAJour
            firestore.collection("panier").document(clientId).set(
                mapOf(
                    "clientId" to clientId,
                    "items" to items.map { panierItem ->
                        mapOf(
                            "produitId" to panierItem.produitId,
                            "nomProduit" to panierItem.nomProduit,
                            "image" to panierItem.image,
                            "prix" to panierItem.prix,
                            "artisanId" to panierItem.artisanId,
                            "nomArtisan" to panierItem.nomArtisan,
                            "quantite" to panierItem.quantite
                        )
                    },
                    "updatedAt" to com.google.firebase.Timestamp.now()
                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Erreur lors de la modification: ${e.message}"))
        }
    }

    suspend fun viderPanier(clientId: String): Result<Unit> {
        return try {
            // Valider clientId
            if (clientId.isBlank()) {
                return Result.failure(Exception("ClientId vide — impossible de vider le panier"))
            }

            // ✅ Utiliser HashMap
            firestore.collection("panier").document(clientId).set(
                mapOf(
                    "clientId" to clientId,
                    "items" to emptyList<Map<String, Any>>(),
                    "updatedAt" to com.google.firebase.Timestamp.now()
                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Erreur lors du vidage du panier: ${e.message}"))
        }
    }
}