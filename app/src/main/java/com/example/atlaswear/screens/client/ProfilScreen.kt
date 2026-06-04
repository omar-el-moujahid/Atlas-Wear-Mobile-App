package com.example.atlaswear.screens.client

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
import com.example.atlaswear.viewmodel.AuthViewModel
import com.example.atlaswear.viewmodel.ProfilViewModel

@Composable
fun ProfilScreen(
    user: User,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val profilViewModel: ProfilViewModel = viewModel()
    val stats by profilViewModel.stats.collectAsState()

    LaunchedEffect(user.uid) {
        profilViewModel.loadStats(user.uid)
    }

    Scaffold(containerColor = Beige) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header noir ──────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Noir)
                    .statusBarsPadding()
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ligne 1 : retour + panier
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }
                    Text(
                        "Mon Profil",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = { navController.navigate(Routes.PANIER) }) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Panier",
                            tint = Color.White
                        )
                    }
                }

                // Badge Client
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Vert.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✦", color = Vert, fontSize = 11.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Client Atlas Wear",
                            fontSize = 12.sp,
                            color = Vert,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Avatar
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Dore, CircleShape)
                        .border(2.dp, Dore.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initiale = "${user.prenom.firstOrNull() ?: ""}${user.nom.firstOrNull() ?: ""}"
                    Text(
                        text = initiale.uppercase(),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Noir
                    )
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    "${user.prenom} ${user.nom}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Dore,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text("${user.ville}, Maroc", fontSize = 12.sp, color = Dore)
                }

                Spacer(Modifier.height(3.dp))

                Text(
                    user.email,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )

                Spacer(Modifier.height(16.dp))

                // Stats dynamiques
                Row(horizontalArrangement = Arrangement.spacedBy(36.dp)) {
                    StatItem("${stats.nbCommandes}", "Commandes")
                    StatItem("${stats.nbFavoris}", "Favoris")
                    StatItem("${stats.nbEnCours}", "En cours")
                }
            }

            Spacer(Modifier.height(20.dp))

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
                    MenuRow(
                        icon = Icons.Default.Person,
                        label = "Mes infos personnelles",
                        iconBg = Color(0xFF3B82F6).copy(alpha = 0.1f),
                        iconTint = Color(0xFF3B82F6),
                        onClick = { navController.navigate(Routes.INFOS_PERSO) }
                    )
                    HorizontalDivider(color = Beige)
                    MenuRow(
                        icon = Icons.Default.Favorite,
                        label = "Mes Favoris",
                        badge = if (stats.nbFavoris > 0) "${stats.nbFavoris}" else null,
                        iconBg = Color.Red.copy(alpha = 0.08f),
                        iconTint = Color.Red.copy(alpha = 0.7f),
                        onClick = { navController.navigate(Routes.FAVORIS) }
                    )
                    HorizontalDivider(color = Beige)
                    MenuRow(
                        icon = Icons.Default.ShoppingCart,
                        label = "Mes Commandes",
                        badge = if (stats.nbCommandes > 0) "${stats.nbCommandes}" else null,
                        iconBg = Dore.copy(alpha = 0.1f),
                        iconTint = Dore,
                        onClick = { navController.navigate(Routes.COMMANDES) }
                    )
                    HorizontalDivider(color = Beige)
                    MenuRow(
                        icon = Icons.Default.LocalShipping,
                        label = "Suivi livraison",
                        badge = if (stats.nbEnCours > 0) "${stats.nbEnCours}" else null,
                        iconBg = Vert.copy(alpha = 0.1f),
                        iconTint = Vert,
                        onClick = { navController.navigate(Routes.COMMANDES) }
                    )
                    HorizontalDivider(color = Beige)
                    MenuRow(
                        icon = Icons.Default.Settings,
                        label = "Paramètres",
                        iconBg = Color.Gray.copy(alpha = 0.1f),
                        iconTint = Color.Gray,
                        onClick = { }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Déconnexion ───────────────────────────────────────
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
                            launchSingleTop = true
                        }
                    }
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Composants ────────────────────────────────────────────────────

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
            Icon(
                icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(19.dp)
            )
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