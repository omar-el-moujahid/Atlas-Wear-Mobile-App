package com.example.atlaswear.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atlaswear.model.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

data class ArtisanStats(
    val nbProduits: Int = 0,
    val nbCommandes: Int = 0,
    val revenus: Double = 0.0,
    val nbEnAttente: Int = 0
)

class ArtisanViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _stats = MutableStateFlow(ArtisanStats())
    val stats: StateFlow<ArtisanStats> = _stats

    private val _commandes = MutableStateFlow<List<Commande>>(emptyList())
    val commandes: StateFlow<List<Commande>> = _commandes

    private val _commandesRecentes = MutableStateFlow<List<Commande>>(emptyList())
    val commandesRecentes: StateFlow<List<Commande>> = _commandesRecentes

    private val _categories = MutableStateFlow<List<Categorie>>(emptyList())
    val categories: StateFlow<List<Categorie>> = _categories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    fun loadStats(artisanId: String) {
        viewModelScope.launch {
            try {
                val produits = db.collection("produits")
                    .whereEqualTo("artisanId", artisanId).get().await()

                val commandes = db.collection("commandes").get().await()
                val mesCommandes = commandes.documents.filter { doc ->
                    val lignes = doc.get("lignes") as? List<*> ?: emptyList<Any>()
                    lignes.any { ligne ->
                        (ligne as? Map<*, *>)?.get("artisanId") == artisanId
                    }
                }

                val revenus = mesCommandes
                    .filter { it.getString("statut") == StatutCommande.TERMINEE }
                    .sumOf { (it.getDouble("total") ?: 0.0) }

                val enAttente = mesCommandes.count {
                    it.getString("statut") == StatutCommande.EN_ATTENTE
                }

                _stats.value = ArtisanStats(
                    nbProduits  = produits.size(),
                    nbCommandes = mesCommandes.size,
                    revenus     = revenus,
                    nbEnAttente = enAttente
                )
            } catch (e: Exception) { }
        }
    }

    fun loadCommandesRecentes(artisanId: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("commandes").get().await()
                val liste = snapshot.documents
                    .filter { doc ->
                        val lignes = doc.get("lignes") as? List<*> ?: emptyList<Any>()
                        lignes.any { (it as? Map<*, *>)?.get("artisanId") == artisanId }
                    }
                    .take(3)
                    .mapNotNull { parseCommande(it.id, it.data ?: return@mapNotNull null) }
                _commandesRecentes.value = liste
            } catch (e: Exception) { }
        }
    }

    fun loadCommandesArtisan(artisanId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = db.collection("commandes").get().await()
                _commandes.value = snapshot.documents
                    .filter { doc ->
                        val lignes = doc.get("lignes") as? List<*> ?: emptyList<Any>()
                        lignes.any { (it as? Map<*, *>)?.get("artisanId") == artisanId }
                    }
                    .mapNotNull { parseCommande(it.id, it.data ?: return@mapNotNull null) }
            } catch (e: Exception) {
                _commandes.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun changerStatut(commandeId: String, nouveauStatut: String) {
        viewModelScope.launch {
            try {
                db.collection("commandes").document(commandeId)
                    .update("statut", nouveauStatut).await()
                _commandes.value = _commandes.value.map {
                    if (it.commandeId == commandeId) it.copy(statut = nouveauStatut) else it
                }
            } catch (e: Exception) { }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("categories").get().await()
                _categories.value = snapshot.documents.mapNotNull {
                    it.toObject(Categorie::class.java)
                }
            } catch (e: Exception) { }
        }
    }

    fun ajouterProduit(
        artisanId: String,
        nomArtisan: String,
        nom: String,
        description: String,
        prix: Double,
        prixReduit: Double,
        stock: Int,
        categorie: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val produitId = UUID.randomUUID().toString()
                val data = hashMapOf(
                    "produitId"    to produitId,
                    "artisanId"    to artisanId,
                    "nomArtisan"   to nomArtisan,
                    "nom"          to nom,
                    "description"  to description,
                    "prix"         to prix,
                    "prixReduit"   to prixReduit,
                    "stock"        to stock,
                    "nomCategorie" to categorie,
                    "categorieId"  to "",
                    "images"       to emptyList<String>(),
                    "disponible"   to true,
                    "createdAt"    to Timestamp.now()
                )
                db.collection("produits").document(produitId).set(data).await()
                _saveSuccess.value = true
            } catch (e: Exception) {
                _isLoading.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetSaveSuccess() { _saveSuccess.value = false }

    @Suppress("UNCHECKED_CAST")
    private fun parseCommande(id: String, data: Map<String, Any>): Commande {
        val lignesRaw = data["lignes"] as? List<Map<String, Any>> ?: emptyList()
        val lignes = lignesRaw.map { ligneMap ->
            val produitsRaw = ligneMap["produits"] as? List<Map<String, Any>> ?: emptyList()
            LigneCommande(
                artisanId  = ligneMap["artisanId"]  as? String ?: "",
                nomArtisan = ligneMap["nomArtisan"] as? String ?: "",
                produits   = produitsRaw.map { p ->
                    ProduitCommande(
                        produitId  = p["produitId"]  as? String ?: "",
                        nomProduit = p["nomProduit"] as? String ?: "",
                        image      = p["image"]      as? String ?: "",
                        prix       = (p["prix"]      as? Number)?.toDouble() ?: 0.0,
                        quantite   = (p["quantite"]  as? Number)?.toInt()    ?: 1,
                        sousTotal  = (p["sousTotal"] as? Number)?.toDouble() ?: 0.0
                    )
                },
                sousTotal = (ligneMap["sousTotal"] as? Number)?.toDouble() ?: 0.0
            )
        }
        return Commande(
            commandeId = id,
            clientId   = data["clientId"]  as? String ?: "",
            statut     = data["statut"]    as? String ?: StatutCommande.EN_ATTENTE,
            lignes     = lignes,
            total      = (data["total"]    as? Number)?.toDouble() ?: 0.0,
            createdAt  = data["createdAt"] as? Timestamp ?: Timestamp.now()
        )
    }
}