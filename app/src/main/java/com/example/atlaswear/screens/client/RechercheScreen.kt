package com.example.atlaswear.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.atlaswear.components.ProduitCard
import com.example.atlaswear.ui.theme.Beige
import com.example.atlaswear.ui.theme.Noir
import com.example.atlaswear.ui.theme.Vert
import com.example.atlaswear.viewmodel.HomeViewModel

@Composable
fun RechercheScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    var query by remember { mutableStateOf("") }
    val produits by viewModel.produits.collectAsState()

    // Filtre en temps réel
    val produitsFiltres = remember(query, produits) {
        if (query.isBlank()) produits
        else produits.filter {
            it.nom.contains(query, ignoreCase = true) ||
                    it.nomCategorie.contains(query, ignoreCase = true) ||
                    it.nomArtisan.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }
    }

    Scaffold(
        containerColor = Beige,
        topBar = {
            Surface(
                shadowElevation = 4.dp,
                color = Noir,
            ) {
                Column(modifier = Modifier.statusBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
                        }
                        Text(
                            text = "Recherche",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Caftan...", color = Color.White.copy(alpha = 0.5f)) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.7f))
                        },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { query = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = Color.White)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedBorderColor = Color(0xFFC8941A),
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White,
                            cursorColor = Color(0xFFC8941A)
                        ),
                        singleLine = true
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
        ) {
            // Nombre de résultats
            if (query.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${produitsFiltres.size} résultats",
                        fontSize = 14.sp,
                        color = Noir.copy(alpha = 0.6f)
                    )
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Vert
                    ) {
                        Text(
                            text = "↑ Trier",
                            color = Color.White,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (produitsFiltres.isEmpty() && query.isNotBlank()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Aucun produit trouvé pour \"$query\"",
                        color = Noir.copy(alpha = 0.5f),
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(produitsFiltres) { produit ->
                        ProduitCard(
                            produit = produit,
                            onClick = { navController.navigate("detail_produit/${produit.produitId}") }
                        )
                    }
                }
            }
        }
    }
}