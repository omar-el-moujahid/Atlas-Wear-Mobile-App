package com.example.atlaswear.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atlaswear.navigation.Routes
import com.example.atlaswear.ui.theme.Vert

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Accueil   : BottomNavItem(Routes.HOME,     "Accueil",   Icons.Default.Home)
    object Recherche : BottomNavItem(Routes.RECHERCHE, "Recherche", Icons.Default.Search)
    object Favoris   : BottomNavItem(Routes.FAVORIS,   "Favoris",   Icons.Default.FavoriteBorder)
    object Panier    : BottomNavItem(Routes.PANIER,    "Panier",    Icons.Default.ShoppingCart)
    object Profil    : BottomNavItem(Routes.PROFILE,   "Profil",    Icons.Default.Person)
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Accueil,
        BottomNavItem.Recherche,
        BottomNavItem.Favoris,
        BottomNavItem.Panier,
        BottomNavItem.Profil
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Vert,
                    selectedTextColor = Vert,
                    indicatorColor = Vert.copy(alpha = 0.1f),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}