package com.example.atlaswear.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atlaswear.navigation.Routes
import com.example.atlaswear.ui.theme.Dore

sealed class BottomNavArtisanItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Accueil   : BottomNavArtisanItem(Routes.DASHBOARD,         "Accueil",    Icons.Default.Home)
    object Ajouter   : BottomNavArtisanItem(Routes.ARTISAN_PRODUITS,  "Ajouter",    Icons.Default.Add)
    object Commandes : BottomNavArtisanItem(Routes.ARTISAN_COMMANDES, "Commandes",  Icons.Default.ShoppingCart)
    object Profil    : BottomNavArtisanItem(Routes.ARTISAN_PROFILE,   "Profil",     Icons.Default.Person)
}

@Composable
fun BottomNavBarArtisan(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavArtisanItem.Accueil,
        BottomNavArtisanItem.Ajouter,
        BottomNavArtisanItem.Commandes,
        BottomNavArtisanItem.Profil
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Dore,
                    selectedTextColor = Dore,
                    indicatorColor = Dore.copy(alpha = 0.1f),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}