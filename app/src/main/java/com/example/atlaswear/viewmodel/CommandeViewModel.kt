package com.example.atlaswear.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atlaswear.model.*
import com.example.atlaswear.repository.CommandeRepository
import com.example.atlaswear.repository.PanierRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CommandeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val commandeRepository = CommandeRepository()
    private val panierRepository = PanierRepository()

    private val _commandes = MutableStateFlow<List<Commande>>(emptyList())
    val commandes: StateFlow<List<Commande>> = _commandes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Pour la page de confirmation
    private val _commandePassee = MutableStateFlow<String?>(null)
    val commandePassee: StateFlow<String?> = _commandePassee

    private val _erreur = MutableStateFlow<String?>(null)
    val erreur: StateFlow<String?> = _erreur

    fun loadCommandesClient(clientId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = db.collection("commandes")
                    .whereEqualTo("clientId", clientId)
                    .get().await()
                _commandes.value = snapshot.documents.mapNotNull { doc ->
                    parseCommande(doc.id, doc.data ?: return@mapNotNull null)
                }
            } catch (e: Exception) {
                _commandes.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun passerCommande(clientId: String, panier: Panier, adresse: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _erreur.value = null
            try {
                // 1. Créer la commande en base
                val result = commandeRepository.creerCommande(clientId, panier, adresse)
                result.fold(
                    onSuccess = { commandeId ->
                        // 2. Vider le panier
                        panierRepository.viderPanier(clientId)
                        // 3. Signaler le succès
                        _commandePassee.value = commandeId
                    },
                    onFailure = { e ->
                        _erreur.value = "Erreur lors de la commande : ${e.message}"
                    }
                )
            } catch (e: Exception) {
                _erreur.value = "Erreur inattendue : ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetCommandePassee() {
        _commandePassee.value = null
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseCommande(id: String, data: Map<String, Any>): Commande {
        val lignesRaw = data["lignes"] as? List<Map<String, Any>> ?: emptyList()
        val lignes = lignesRaw.map { ligneMap ->
            val produitsRaw = ligneMap["produits"] as? List<Map<String, Any>> ?: emptyList()
            val produits = produitsRaw.map { prodMap ->
                ProduitCommande(
                    produitId  = prodMap["produitId"]  as? String ?: "",
                    nomProduit = prodMap["nomProduit"] as? String ?: "",
                    image      = prodMap["image"]      as? String ?: "",
                    prix       = (prodMap["prix"]      as? Number)?.toDouble() ?: 0.0,
                    quantite   = (prodMap["quantite"]  as? Number)?.toInt()    ?: 1,
                    sousTotal  = (prodMap["sousTotal"] as? Number)?.toDouble() ?: 0.0
                )
            }
            LigneCommande(
                artisanId  = ligneMap["artisanId"]  as? String ?: "",
                nomArtisan = ligneMap["nomArtisan"] as? String ?: "",
                produits   = produits,
                sousTotal  = (ligneMap["sousTotal"] as? Number)?.toDouble() ?: 0.0
            )
        }
        return Commande(
            commandeId = id,
            clientId   = data["clientId"]   as? String ?: "",
            statut     = data["statut"]      as? String ?: StatutCommande.EN_ATTENTE,
            adresse    = data["adresse"]     as? String ?: "",
            lignes     = lignes,
            sousTotal  = (data["sousTotal"]  as? Number)?.toDouble() ?: 0.0,
            reduction  = (data["reduction"]  as? Number)?.toDouble() ?: 0.0,
            total      = (data["total"]      as? Number)?.toDouble() ?: 0.0,
            createdAt  = data["createdAt"]   as? Timestamp ?: Timestamp.now()
        )
    }
}