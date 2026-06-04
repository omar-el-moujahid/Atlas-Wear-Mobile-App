package com.example.atlaswear.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.atlaswear.R
import com.example.atlaswear.components.BottomNavBar
import com.example.atlaswear.components.ProduitCard
import com.example.atlaswear.model.User
import com.example.atlaswear.ui.theme.AtlasWearTheme
import com.example.atlaswear.ui.theme.Beige
import com.example.atlaswear.ui.theme.Noir
import com.example.atlaswear.model.Categorie
import com.example.atlaswear.model.Produit
import com.example.atlaswear.ui.theme.Vert
import com.example.atlaswear.viewmodel.HomeViewModel
import androidx.compose.foundation.clickable
import androidx.compose.material3.Surface
@Composable
fun HomeScreen(
    user: User,
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val produits by viewModel.produits.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategorie by viewModel.selectedCategorie.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    HomeScreenContent(
        user = user,
        produits = produits,
        categories = categories,
        selectedCategorie = selectedCategorie,
        isLoading = isLoading,
        currentRoute = currentRoute,
        onCategorieSelected = { viewModel.selectCategorie(it) },
        onNavigate = { route -> navController.navigate(route) }
    )
}

@Composable
fun HomeScreenContent(
    user: User,
    produits: List<Produit>,
    categories: List<Categorie>,
    selectedCategorie: String?,
    isLoading: Boolean,
    currentRoute: String,
    onCategorieSelected: (String?) -> Unit,
    onNavigate: (String) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        },
        containerColor = Beige,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                        Text("Bonjour", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                        Text(user.prenom, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Text(
                        text = "ATLAS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC8941A),
                        letterSpacing = 3.sp
                    )

                    IconButton(onClick = { onNavigate("panier") }) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Panier",
                            tint = Color.White
                        )
                    }
                }
            }

            // Barre de recherche
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Noir)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate("recherche") },
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Rechercher un produit artisanal...",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Catégories
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.width(8.dp))

                // Bouton "Tout"
                FilterChip(
                    selected = selectedCategorie == null,
                    onClick = { onCategorieSelected(null) },
                    label = { Text("Tout") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Vert,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = Noir
                    )
                )

                categories.forEach { categorie ->
                    FilterChip(
                        selected = selectedCategorie == categorie.categorieId,
                        onClick = { onCategorieSelected(categorie.categorieId) },
                        label = { Text(categorie.nom) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Vert,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Noir
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
            }

            // Grille produits
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Vert)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(produits) { produit ->
                        ProduitCard(
                            produit = produit,
                            onClick = {
                                onNavigate("detail_produit/${produit.produitId}")
                            }
                        )
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true, backgroundColor = 0xFFF5EFE0)
@Composable
fun HomeScreenPreview() {
    AtlasWearTheme {
        HomeScreenContent(
            user = User(
                uid = "preview",
                nom = "Tazi",
                prenom = "Omar",
                email = "omar@test.ma",
                ville = "Casablanca",
                role = "client"
            ),
            produits = listOf(
                Produit(produitId = "1", nom = "Zellige Fès", prix = 150.0),
                Produit(produitId = "2", nom = "Tapis Atlas", prix = 1200.0)
            ),
            categories = listOf(
                Categorie(categorieId = "1", nom = "Céramique"),
                Categorie(categorieId = "2", nom = "Tapis")
            ),
            selectedCategorie = null,
            isLoading = false,
            currentRoute = "home",
            onCategorieSelected = {},
            onNavigate = {}
        )
    }
}
