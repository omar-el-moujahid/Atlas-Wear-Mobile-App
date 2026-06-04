package com.example.atlaswear.repository

import com.example.atlaswear.model.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class CommandeRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun creerCommande(
        clientId: String,
        panier: Panier,
        adresse: String
    ): Result<String> {
        return try {
            val commandeId = UUID.randomUUID().toString()

            // Grouper les items par artisan → LigneCommande
            val lignes = panier.items
                .groupBy { it.artisanId }
                .map { (artisanId, items) ->
                    val produits = items.map { item ->
                        ProduitCommande(
                            produitId  = item.produitId,
                            nomProduit = item.nomProduit,
                            image      = item.image,
                            prix       = item.prix,
                            quantite   = item.quantite,
                            sousTotal  = item.sousTotal
                        )
                    }
                    LigneCommande(
                        artisanId  = artisanId,
                        nomArtisan = items.first().nomArtisan,
                        produits   = produits,
                        sousTotal  = items.sumOf { it.sousTotal }
                    )
                }

            val commande = hashMapOf(
                "commandeId" to commandeId,
                "clientId"   to clientId,
                "statut"     to StatutCommande.EN_ATTENTE,
                "adresse"    to adresse,
                "lignes"     to lignes.map { ligne ->
                    hashMapOf(
                        "artisanId"  to ligne.artisanId,
                        "nomArtisan" to ligne.nomArtisan,
                        "sousTotal"  to ligne.sousTotal,
                        "produits"   to ligne.produits.map { p ->
                            hashMapOf(
                                "produitId"  to p.produitId,
                                "nomProduit" to p.nomProduit,
                                "image"      to p.image,
                                "prix"       to p.prix,
                                "quantite"   to p.quantite,
                                "sousTotal"  to p.sousTotal
                            )
                        }
                    )
                },
                "sousTotal"  to panier.total,
                "reduction"  to 0.0,
                "total"      to panier.total + 30.0, // +livraison
                "createdAt"  to Timestamp.now()
            )

            db.collection("commandes")
                .document(commandeId)
                .set(commande)
                .await()

            Result.success(commandeId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}