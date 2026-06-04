package com.example.atlaswear.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ProfilStats(
    val nbCommandes: Int = 0,
    val nbFavoris: Int = 0,
    val nbEnCours: Int = 0 ,

    // pour l artisan on pourrait ajouter :
    val nbProduitsPublies: Int = 0,
    val gainMensuel: Int = 0
)

class ProfilViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _stats = MutableStateFlow(ProfilStats())
    val stats: StateFlow<ProfilStats> = _stats

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    fun loadStats(clientId: String) {
        viewModelScope.launch {
            try {
                // Commandes totales
                val commandes = db.collection("commandes")
                    .whereEqualTo("clientId", clientId)
                    .get().await()

                val total = commandes.size()
                val enCours = commandes.documents.count {
                    val statut = it.getString("statut") ?: ""
                    statut == "en_attente" || statut == "en_cours"
                }

                // Favoris
                val favoris = db.collection("favoris")
                    .whereEqualTo("clientId", clientId)
                    .get().await()

                _stats.value = ProfilStats(
                    nbCommandes = total,
                    nbFavoris   = favoris.size(),
                    nbEnCours   = enCours
                )
            } catch (e: Exception) {
                // garder les valeurs par défaut
            }
        }
    }

    //  CHARGEMENT STATS ARTISAN
    fun loadArtisanStats(artisanId: String) {
        viewModelScope.launch {
            try {
                // 1. Récupérer le nombre de produits publiés par l'artisan
                val produits = db.collection("produits")
                    .whereEqualTo("artisanId", artisanId)
                    .get().await()
                val totalProduits = produits.size()

                // 2. Récupérer TOUTES les commandes reçues par l'artisan
                val commandes = db.collection("commandes")
                    .whereEqualTo("artisanId", artisanId)
                    .get().await()
                val totalCommandes = commandes.size()

                // 3. Calculer l'argent total (Toutes les commandes sauf "annule")
                var totalGains = 0
                for (doc in commandes.documents) {
                    val statut = doc.getString("statut") ?: ""

                    // Filtre : On prend tout SAUF les commandes annulées
                    if (statut != "annule") {
                        val prix = doc.getLong("total")?.toInt()
                            ?: doc.getDouble("total")?.toInt()
                            ?: 0
                        totalGains += prix
                    }
                }

                // Mettre à jour l'état de l'interface graphique
                _stats.value = ProfilStats(
                    nbProduitsPublies = totalProduits,
                    nbCommandes = totalCommandes, // Contient bien TOUTES les commandes reçues
                    gainMensuel = totalGains      // Somme de l'argent (hors annulées)
                )
            } catch (e: Exception) {
                // En cas d'erreur ou base vide, on laisse les valeurs par défaut (0)
            }
        }
    }

    fun sauvegarderInfos(
        uid: String,
        nom: String,
        prenom: String,
        ville: String
    ) {
        viewModelScope.launch {
            try {
                db.collection("users").document(uid)
                    .update(mapOf(
                        "nom"    to nom,
                        "prenom" to prenom,
                        "ville"  to ville
                    )).await()
                _saveSuccess.value = true
            } catch (e: Exception) {
                _saveSuccess.value = false
            }
        }
    }

    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }
}