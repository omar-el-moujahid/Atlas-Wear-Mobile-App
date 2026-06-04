package com.example.atlaswear.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
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
import com.example.atlaswear.model.User
import com.example.atlaswear.ui.theme.*
import com.example.atlaswear.viewmodel.FavorisViewModel
import com.example.atlaswear.viewmodel.PanierViewModel

@Composable
fun FavorisScreen(
    user: User,
    navController: NavController,
    favorisViewModel: FavorisViewModel = viewModel(),
    panierViewModel: PanierViewModel = viewModel()
) {
    val favoris by favorisViewModel.favoris.collectAsState()
    val isLoading by favorisViewModel.isLoading.collectAsState()

    LaunchedEffect(user.uid) {
        favorisViewModel.loadFavoris(user.uid)
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
                        text = "Mes Favoris",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    // Badge count
                    if (favoris.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .background(Dore, RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${favoris.size}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Noir
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }
            }
        }
    ) { paddingValues ->
        when {
            isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Vert)
            }

            favoris.isEmpty() -> Box(
                Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Vert.copy(alpha = 0.3f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Aucun favori pour l'instant", color = Noir.copy(alpha = 0.5f), fontSize = 16.sp)
                }
            }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(favoris) { favori ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = favori.image,
                                contentDescription = favori.nomProduit,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Beige),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    favori.nomProduit,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Noir
                                )
                                Text(
                                    "Quantité : 1",
                                    fontSize = 13.sp,
                                    color = Noir.copy(alpha = 0.5f)
                                )
                                Text(
                                    "${favori.prix.toInt()} MAD",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Vert
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Bouton retirer favori
                                IconButton(
                                    onClick = { favorisViewModel.retirerFavori(user.uid, favori.favoriId) }
                                ) {
                                    Icon(Icons.Default.Favorite, contentDescription = "Retirer", tint = Color.Red.copy(alpha = 0.7f))
                                }
                            }
                        }
                        // Bouton Ajouter au panier
                        Button(
                            onClick = {
                                panierViewModel.ajouterAuPanier(
                                    clientId = user.uid,
                                    item = com.example.atlaswear.model.PanierItem(
                                        produitId = favori.produitId,
                                        nomProduit = favori.nomProduit,
                                        image = favori.image,
                                        prix = favori.prix,
                                        artisanId = "",
                                        nomArtisan = "",
                                        quantite = 1
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Vert)
                        ) {
                            Text("Ajouter au panier", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}