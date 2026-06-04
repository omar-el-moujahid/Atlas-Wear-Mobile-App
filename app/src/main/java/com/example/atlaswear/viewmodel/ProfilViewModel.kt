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
    val nbEnCours: Int = 0
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