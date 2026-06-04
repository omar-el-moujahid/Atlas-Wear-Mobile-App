package com.example.atlaswear.screens.artisan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.atlaswear.components.BottomNavBarArtisan
import com.example.atlaswear.model.Commande
import com.example.atlaswear.model.StatutCommande
import com.example.atlaswear.model.User
import com.example.atlaswear.navigation.Routes
import com.example.atlaswear.ui.theme.*
import com.example.atlaswear.viewmodel.ArtisanViewModel

@Composable
fun CommandesArtisanScreen(
    user: User,
    navController: NavController,
    viewModel: ArtisanViewModel = viewModel()
) {
    val commandes by viewModel.commandes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var filtreActif by remember { mutableStateOf("en_attente") }

    LaunchedEffect(user.uid) {
        viewModel.loadCommandesArtisan(user.uid)
    }

    val commandesFiltrees = when (filtreActif) {
        "en_attente" -> commandes.filter { it.statut == StatutCommande.EN_ATTENTE }
        "en_cours"   -> commandes.filter { it.statut == StatutCommande.EN_COURS }
        "terminee"   -> commandes.filter { it.statut == StatutCommande.TERMINEE }
        else -> commandes
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Routes.ARTISAN_COMMANDES

    Scaffold(
        containerColor = Beige,
        bottomBar = {
            BottomNavBarArtisan(currentRoute = currentRoute, onNavigate = { navController.navigate(it) })
        },
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
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                    Text(
                        "Commandes reçues",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .background(Dore, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text("${commandes.size}", fontSize = 13.sp,
                            fontWeight = FontWeight.Bold, color = Noir)
                    }
                    Spacer(Modifier.width(8.dp))
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // Tabs filtre
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "en_attente" to "Nouvelles",
                    "en_cours"   to "En cours",
                    "terminee"   to "Livrées"
                ).forEach { (statut, label) ->
                    Surface(
                        onClick = { filtreActif = statut },
                        shape = RoundedCornerShape(20.dp),
                        color = if (filtreActif == statut) Vert else Color.White
                    ) {
                        Text(
                            label,
                            fontSize = 13.sp,
                            fontWeight = if (filtreActif == statut) FontWeight.Bold else FontWeight.Normal,
                            color = if (filtreActif == statut) Color.White else Noir.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Vert)
                }
            } else if (commandesFiltrees.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucune commande", color = Noir.copy(alpha = 0.4f))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(commandesFiltrees) { commande ->
                        CommandeArtisanCard(
                            commande = commande,
                            onConfirmer = { viewModel.changerStatut(commande.commandeId, StatutCommande.EN_COURS) },
                            onLivrer = { viewModel.changerStatut(commande.commandeId, StatutCommande.TERMINEE) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommandeArtisanCard(
    commande: Commande,
    onConfirmer: () -> Unit,
    onLivrer: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "#ATL-${commande.commandeId.take(4).uppercase()}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Noir
                    )
                    Text("Aujourd'hui 14:32", fontSize = 12.sp, color = Noir.copy(alpha = 0.4f))
                }
                // Badge statut
                val (bg, tc, label) = when (commande.statut) {
                    StatutCommande.EN_ATTENTE -> Triple(Dore.copy(alpha = 0.15f), Dore, "Nouvelles")
                    StatutCommande.EN_COURS -> Triple(Color(0xFF3B82F6).copy(0.15f), Color(0xFF3B82F6), "En cours")
                    StatutCommande.TERMINEE -> Triple(Vert.copy(0.15f), Vert, "Livrée")
                    else -> Triple(Beige, Noir.copy(0.4f), commande.statut)
                }
                Box(modifier = Modifier.background(bg, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = tc)
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Beige)
            Spacer(Modifier.height(12.dp))

            // Produits
            commande.lignes.forEach { ligne ->
                ligne.produits.forEach { produit ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = produit.image,
                            contentDescription = null,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Beige),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(produit.nomProduit, fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold, color = Noir)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, null,
                                    tint = Noir.copy(alpha = 0.4f),
                                    modifier = Modifier.size(13.dp))
                                Spacer(Modifier.width(3.dp))
                                Text(ligne.nomArtisan, fontSize = 12.sp, color = Noir.copy(alpha = 0.5f))
                            }
                            Text("Quantité: ${produit.quantite}", fontSize = 12.sp, color = Noir.copy(alpha = 0.5f))
                            Text("${produit.prix.toInt()} MAD", fontSize = 14.sp,
                                fontWeight = FontWeight.Bold, color = Dore)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Bouton action selon statut
            when (commande.statut) {
                StatutCommande.EN_ATTENTE -> {
                    Button(
                        onClick = onConfirmer,
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Vert)
                    ) {
                        Text("✓ Confirmer", fontWeight = FontWeight.Bold)
                    }
                }
                StatutCommande.EN_COURS -> {
                    Button(
                        onClick = onLivrer,
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        Text("Marquer livré", fontWeight = FontWeight.Bold)
                    }
                }
                else -> {}
            }
        }
    }
}