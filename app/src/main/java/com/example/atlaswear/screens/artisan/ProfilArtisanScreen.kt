package com.example.atlaswear.screens.artisan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.atlaswear.model.User
import com.example.atlaswear.navigation.Routes
import com.example.atlaswear.ui.theme.*
import com.example.atlaswear.viewmodel.ArtisanViewModel
import com.example.atlaswear.viewmodel.AuthViewModel

@Composable
fun ProfilArtisanScreen(
    user: User,
    navController: NavController,
    authViewModel: AuthViewModel,
    artisanViewModel: ArtisanViewModel = viewModel() // Réutilisation directe du même ViewModel que le Dashboard
) {
    // Recharger les données de l'artisan à l'ouverture de l'écran
    LaunchedEffect(user.uid) {
        artisanViewModel.loadStats(user.uid)
    }

    // Récupération de l'état des statistiques calculées
    val stats by artisanViewModel.stats.collectAsState()

    Scaffold(containerColor = Beige) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header noir ajusté ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Noir)
                    .statusBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 36.dp, bottom = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // Badge Artisan Certifié
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Dore.copy(alpha = 0.15f),
                        border = ButtonDefaults.outlinedButtonBorder
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("✦", color = Dore, fontSize = 11.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Artisan Certifié",
                                fontSize = 12.sp,
                                color = Dore,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Avatar (Rond doré avec l'initiale)
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .background(Dore, CircleShape)
                            .border(2.dp, Dore.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val initiale = "${user.prenom.firstOrNull() ?: ""}${user.nom.firstOrNull() ?: ""}"
                        Text(
                            text = initiale.uppercase(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Noir
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    // Prénom + Nom (Dynamique)
                    Text(
                        "${user.prenom} ${user.nom}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(Modifier.height(6.dp))

                    // Ville (Dynamique)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Dore,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${user.ville}, Maroc",
                            fontSize = 13.sp,
                            color = Dore
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    // Email de l'artisan (Dynamique)
                    Text(user.email, fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f))

                    Spacer(Modifier.height(22.dp))

                    // Zone des Statistiques DYNAMIQUES (Synchronisées avec le Dashboard)
                    Row(horizontalArrangement = Arrangement.spacedBy(36.dp)) {
                        StatItem("${stats.nbProduits}", "Produits publiés")
                        StatItem("${stats.nbCommandes}", "Commandes")
                        StatItem("${stats.revenus.toInt()} MAD", "MAD ce mois")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Section MON COMPTE ────────────────────────────────
            Text(
                "MON COMPTE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Noir.copy(alpha = 0.4f),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column {
                    // 1. Ajouter un produit
                    MenuRow(
                        icon = Icons.Default.Add,
                        label = "Ajouter un produit",
                        iconBg = Vert.copy(alpha = 0.1f),
                        iconTint = Vert,
                        onClick = { navController.navigate(Routes.ARTISAN_PRODUITS) }
                    )
                    HorizontalDivider(color = Beige)

                    // 2. Mes Produits (Badge Dynamique)
                    MenuRow(
                        icon = Icons.Default.Inventory,
                        label = "Mes Produits",
                        badge = if (stats.nbProduits > 0) "${stats.nbProduits}" else null,
                        iconBg = Color(0xFF8B5CF6).copy(alpha = 0.1f),
                        iconTint = Color(0xFF8B5CF6),
                        onClick = { /* Navigation vers la liste des produits si existante */ }
                    )
                    HorizontalDivider(color = Beige)

                    // 3. Commandes reçues (Badge Dynamique)
                    MenuRow(
                        icon = Icons.Default.ShoppingCart,
                        label = "Commandes reçues",
                        badge = if (stats.nbCommandes > 0) "${stats.nbCommandes}" else null,
                        iconBg = Dore.copy(alpha = 0.1f),
                        iconTint = Dore,
                        onClick = { navController.navigate(Routes.ARTISAN_COMMANDES) }
                    )
                    HorizontalDivider(color = Beige)

                    // 4. Modifier mes informations (Séparé sur un autre écran)
                    MenuRow(
                        icon = Icons.Default.Person,
                        label = "Mes infos personnelles",
                        iconBg = Color(0xFF3B82F6).copy(alpha = 0.1f),
                        iconTint = Color(0xFF3B82F6),
                        onClick = { navController.navigate(Routes.ARTISAN_EDIT_INFOS) }
                    )
                    HorizontalDivider(color = Beige)

                    // 5. Paramètres boutique
                    MenuRow(
                        icon = Icons.Default.Settings,
                        label = "Paramètres boutique",
                        iconBg = Color.Gray.copy(alpha = 0.1f),
                        iconTint = Color.Gray,
                        onClick = { /* Navigation paramètres */ }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Section Déconnexion ───────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                MenuRow(
                    icon = Icons.Default.ExitToApp,
                    label = "Se déconnecter",
                    iconBg = Color.Red.copy(alpha = 0.08f),
                    iconTint = Color.Red.copy(alpha = 0.8f),
                    textColor = Color.Red.copy(alpha = 0.8f),
                    onClick = {
                        authViewModel.logout()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Dore)
        Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
    }
}

@Composable
private fun MenuRow(
    icon: ImageVector,
    label: String,
    badge: String? = null,
    iconBg: Color = Vert.copy(alpha = 0.1f),
    iconTint: Color = Vert,
    textColor: Color = Color(0xFF1A1A1A),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconBg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(19.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(label, fontSize = 15.sp, color = textColor, modifier = Modifier.weight(1f))
        if (badge != null) {
            Box(
                modifier = Modifier
                    .background(Vert, RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(badge, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(8.dp))
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.Gray.copy(alpha = 0.35f),
            modifier = Modifier.size(18.dp)
        )
    }
}