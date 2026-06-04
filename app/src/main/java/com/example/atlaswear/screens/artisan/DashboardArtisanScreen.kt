package com.example.atlaswear.screens.artisan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.atlaswear.components.BottomNavBarArtisan
import com.example.atlaswear.model.User
import com.example.atlaswear.navigation.Routes
import com.example.atlaswear.ui.theme.*
import com.example.atlaswear.viewmodel.ArtisanViewModel

@Composable
fun DashboardArtisanScreen(
    user: User,
    navController: NavController,
    viewModel: ArtisanViewModel = viewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val commandesRecentes by viewModel.commandesRecentes.collectAsState()

    LaunchedEffect(user.uid) {
        viewModel.loadStats(user.uid)
        viewModel.loadCommandesRecentes(user.uid)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Routes.DASHBOARD

    Scaffold(
        containerColor = Beige,
        bottomBar = {
            BottomNavBarArtisan(
                currentRoute = currentRoute,
                onNavigate = { navController.navigate(it) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Noir)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Bonjour,", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                        Text(
                            "${user.prenom} ${user.nom}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text("ATLAS", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                        color = Dore, letterSpacing = 3.sp)
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Notifications, null, tint = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Stats 2x2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Inventory,
                    value = "${stats.nbProduits}",
                    label = "Produits publiés",
                    trend = "+12%"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.ShoppingBag,
                    value = "${stats.nbCommandes}",
                    label = "Commandes totales",
                    trend = "+12%"
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AttachMoney,
                    value = "${stats.revenus.toInt()} MAD",
                    label = "MAD ce mois",
                    trend = "+12%"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.HourglassEmpty,
                    value = "${stats.nbEnAttente}",
                    label = "En attente",
                    trend = "-2",
                    trendPositive = false
                )
            }

            Spacer(Modifier.height(20.dp))

            // Commandes récentes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Commandes récentes", fontSize = 15.sp,
                    fontWeight = FontWeight.Bold, color = Noir)
                TextButton(onClick = { navController.navigate(Routes.ARTISAN_COMMANDES) }) {
                    Text("Voir tout →", color = Dore, fontSize = 13.sp)
                }
            }

            commandesRecentes.forEach { commande ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Image premier produit
                        val image = commande.lignes.firstOrNull()?.produits?.firstOrNull()?.image ?: ""
                        AsyncImage(
                            model = image,
                            contentDescription = null,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Beige),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            val nomProduit = commande.lignes.firstOrNull()?.produits?.firstOrNull()?.nomProduit ?: ""
                            val nomClient = commande.lignes.firstOrNull()?.nomArtisan ?: ""
                            Text(nomProduit, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Noir)
                            Text("👤 $nomClient", fontSize = 12.sp, color = Noir.copy(alpha = 0.5f))
                            Text("${commande.total.toInt()} MAD", fontSize = 13.sp,
                                fontWeight = FontWeight.Bold, color = Dore)
                        }
                        StatutBadgeSmall(statut = commande.statut)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    trend: String,
    trendPositive: Boolean = true
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, null, tint = Dore.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                Text(
                    trend,
                    fontSize = 11.sp,
                    color = if (trendPositive) Color(0xFF22C55E) else Color.Red,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Noir)
            Text(label, fontSize = 11.sp, color = Noir.copy(alpha = 0.5f))
        }
    }
}

@Composable
private fun StatutBadgeSmall(statut: String) {
    val (bg, text, label) = when (statut) {
        "en_attente" -> Triple(Dore.copy(alpha = 0.15f), Dore, "Nouveau")
        "en_cours"   -> Triple(Color(0xFF3B82F6).copy(alpha = 0.15f), Color(0xFF3B82F6), "En cours")
        "terminee"   -> Triple(Vert.copy(alpha = 0.15f), Vert, "Livré")
        else         -> Triple(Beige, Noir.copy(alpha = 0.4f), statut)
    }
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(label, fontSize = 11.sp, color = text, fontWeight = FontWeight.SemiBold)
    }
}