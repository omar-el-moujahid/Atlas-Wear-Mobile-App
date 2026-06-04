package com.example.atlaswear.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.atlaswear.model.Commande
import com.example.atlaswear.model.StatutCommande
import com.example.atlaswear.model.User
import com.example.atlaswear.ui.theme.*
import com.example.atlaswear.viewmodel.CommandeViewModel
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun CommandesClientScreen(
    user: User,
    navController: NavController,
    viewModel: CommandeViewModel = viewModel()
) {
    val commandes by viewModel.commandes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var filtreActif by remember { mutableStateOf("tout") }

    val commandesFiltrees = if (filtreActif == "tout") {
        commandes
    } else {
        commandes.filter { it.statut == filtreActif }
    }
    LaunchedEffect(commandes) {
        commandes.forEach {
            android.util.Log.d("COMMANDE", "id=${it.commandeId} statut='${it.statut}'")
        }
    }
    LaunchedEffect(user.uid) {
        viewModel.loadCommandesClient(user.uid)
    }



    Scaffold(
        containerColor = Beige,
        topBar = {
            Surface(color = Noir, shadowElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                    Text(
                        text = "Mes commandes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    // Badge total
                    Box(
                        modifier = Modifier
                            .background(Dore, CircleShape)
                            .size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${commandes.size}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Noir
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs filtre
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FiltreTab(
                    label = "Tout",
                    actif = filtreActif == "tout",
                    onClick = { filtreActif = "tout" }
                )
                FiltreTab(
                    label = "En cours",
                    actif = filtreActif == StatutCommande.EN_COURS,
                    onClick = { filtreActif = StatutCommande.EN_COURS }
                )
                FiltreTab(
                    label = "Livrées",
                    actif = filtreActif == StatutCommande.TERMINEE,
                    onClick = { filtreActif = StatutCommande.TERMINEE }
                )
                FiltreTab(
                    label = "Annulées",
                    actif = filtreActif == StatutCommande.ANNULEE,
                    onClick = { filtreActif = StatutCommande.ANNULEE }
                )
            }

            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Vert)
                }

                commandesFiltrees.isEmpty() -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Aucune commande dans cette catégorie",
                        color = Noir.copy(alpha = 0.4f),
                        fontSize = 14.sp
                    )
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(commandesFiltrees) { commande ->
                        CommandeCard(
                            commande = commande,
                            onRacheter = { /* TODO */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FiltreTab(label: String, actif: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (actif) Vert else Color.White,
        border = if (!actif) ButtonDefaults.outlinedButtonBorder else null
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (actif) FontWeight.Bold else FontWeight.Normal,
            color = if (actif) Color.White else Noir.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun CommandeCard(commande: Commande, onRacheter: () -> Unit) {
    val estLivree = commande.statut == StatutCommande.TERMINEE

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header : ID + statut + date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "#ATL-${commande.commandeId.take(4).uppercase()}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Noir
                    )
                    Text(
                        text = "Aujourd'hui 14:32",
                        fontSize = 12.sp,
                        color = Noir.copy(alpha = 0.4f)
                    )
                }
                StatutBadge(statut = commande.statut)
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Beige)
            Spacer(Modifier.height(12.dp))

            // Produits
            commande.lignes.forEach { ligne ->
                ligne.produits.forEach { produit ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        // Image produit
                        AsyncImage(
                            model = produit.image,
                            contentDescription = produit.nomProduit,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Beige),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                produit.nomProduit,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Noir
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Noir.copy(alpha = 0.4f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    ligne.nomArtisan,
                                    fontSize = 12.sp,
                                    color = Noir.copy(alpha = 0.5f)
                                )
                            }
                            Text(
                                "Quantité: ${produit.quantite}",
                                fontSize = 12.sp,
                                color = Noir.copy(alpha = 0.5f)
                            )
                            Text(
                                "${produit.prix.toInt()} MAD",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Vert
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            HorizontalDivider(color = Beige)
            Spacer(Modifier.height(12.dp))

            // Suivi livraison
            Text(
                "Suivi livraison",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Noir
            )
            Spacer(Modifier.height(10.dp))
            SuiviLivraison(statut = commande.statut)

            // Bouton Racheter (seulement pour livrées)
            if (estLivree) {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onRacheter,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(44.dp)
                        .widthIn(min = 140.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Vert)
                ) {
                    Text("Racheter", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SuiviLivraison(statut: String) {
    val etapes = listOf("Commandé", "Confirmé", "Livré")
    val etapeActive = when (statut) {
        StatutCommande.EN_ATTENTE -> 0
        StatutCommande.EN_COURS   -> 1
        StatutCommande.TERMINEE   -> 2
        else -> 0
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        etapes.forEachIndexed { index, label ->
            val estAtteint = index <= etapeActive

            // Cercle étape
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        if (estAtteint) Vert else Color.Gray.copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    fontSize = 12.sp,
                    color = if (estAtteint) Color.White else Color.Gray.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold
                )
            }

            // Ligne entre étapes
            if (index < etapes.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(
                            if (index < etapeActive) Vert else Color.Gray.copy(alpha = 0.2f)
                        )
                )
            }
        }
    }

    // Labels sous les cercles
    Row(modifier = Modifier.fillMaxWidth()) {
        etapes.forEachIndexed { index, label ->
            Text(
                text = label,
                fontSize = 10.sp,
                color = Noir.copy(alpha = 0.5f),
                modifier = Modifier
                    .then(
                        if (index == 0) Modifier.wrapContentWidth(Alignment.Start)
                        else if (index == etapes.size - 1) Modifier.weight(1f).wrapContentWidth(Alignment.End)
                        else Modifier.weight(1f).wrapContentWidth(Alignment.CenterHorizontally)
                    )
            )
            if (index < etapes.size - 1 && index == 0) {
                Modifier.weight(1f)
            }
        }
    }
}

@Composable
private fun StatutBadge(statut: String) {
    val (bgColor, textColor, label) = when (statut) {
        StatutCommande.EN_ATTENTE -> Triple(Dore.copy(alpha = 0.15f), Dore, "En attente")
        StatutCommande.EN_COURS   -> Triple(Dore.copy(alpha = 0.15f), Dore, "En cours")
        StatutCommande.TERMINEE   -> Triple(Vert.copy(alpha = 0.15f), Vert, "Livrées")
        StatutCommande.ANNULEE    -> Triple(Color.Red.copy(alpha = 0.15f), Color.Red, "Annulée")
        else -> Triple(Beige, Noir.copy(alpha = 0.5f), statut)
    }
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}