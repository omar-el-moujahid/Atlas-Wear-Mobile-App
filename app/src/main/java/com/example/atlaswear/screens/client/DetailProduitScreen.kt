package com.example.atlaswear.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.atlaswear.model.PanierItem
import com.example.atlaswear.model.User
import com.example.atlaswear.ui.theme.*
import com.example.atlaswear.viewmodel.FavorisViewModel
import com.example.atlaswear.viewmodel.PanierViewModel
import com.example.atlaswear.viewmodel.ProduitViewModel

@Composable
fun DetailProduitScreen(
    produitId: String,
    user: User,
    navController: NavController,
    produitViewModel: ProduitViewModel = viewModel(),
    panierViewModel: PanierViewModel = viewModel(),
    favorisViewModel: FavorisViewModel = viewModel()
) {
    val produit by produitViewModel.produitSelectionne.collectAsState()
    val message by panierViewModel.message.collectAsState()
    val favoris by favorisViewModel.favoris.collectAsState()
    var quantite by remember { mutableStateOf(1) }

    LaunchedEffect(produitId) {
        produitViewModel.loadProduitById(produitId)
        favorisViewModel.loadFavoris(user.uid)
    }

    LaunchedEffect(message) {
        if (message != null) {
            kotlinx.coroutines.delay(2000)
            panierViewModel.clearMessage()
        }
    }

    produit?.let { p ->

        val estFavori = favoris.any { it.produitId == produitId }

        Box(modifier = Modifier.fillMaxSize().background(Beige)) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── Header noir ──────────────────────────────────
                Surface(color = Noir, shadowElevation = 2.dp) {
                    Row(
                        modifier = Modifier
                            .statusBarsPadding()
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Retour
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Retour",
                                tint = Color.White
                            )
                        }

                        // Favori ✅ rouge si déjà en favori
                        IconButton(
                            onClick = {
                                favorisViewModel.toggleFavori(
                                    clientId   = user.uid,
                                    produitId  = p.produitId,
                                    nomProduit = p.nom,
                                    image      = p.images.firstOrNull() ?: "",
                                    prix       = p.prixEffectif
                                )
                            }
                        ) {
                            Icon(
                                imageVector = if (estFavori) Icons.Default.Favorite
                                else Icons.Default.FavoriteBorder,
                                contentDescription = "Favori",
                                tint = if (estFavori) Color.Red else Color.White
                            )
                        }
                    }
                }

                // ── Image produit ─────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    if (p.images.isNotEmpty()) {
                        AsyncImage(
                            model = p.images.first(),
                            contentDescription = p.nom,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .border(
                                    width = 1.5.dp,
                                    color = Dore.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .border(
                                    width = 1.5.dp,
                                    color = Dore.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .background(Color(0xFFE8E0D0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(p.nom.first().toString(), fontSize = 60.sp, color = Vert)
                        }
                    }
                }

                // ── Description scrollable ────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Text(
                        p.nom,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Noir
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        "par ${p.nomArtisan} · Artisan ${p.nomCategorie}",
                        fontSize = 13.sp,
                        color = Noir.copy(alpha = 0.5f)
                    )

                    Spacer(Modifier.height(12.dp))

                    // Prix
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${p.prixEffectif.toInt()} MAD",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Vert
                        )
                        if (p.aUneReduction) {
                            Spacer(Modifier.width(12.dp))
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Dore.copy(alpha = 0.15f)
                            ) {
                                val reduction = ((1 - p.prixEffectif / p.prix) * 100).toInt()
                                Text(
                                    "-$reduction% réduction",
                                    color = Dore,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    if (p.aUneReduction) {
                        Text(
                            "${p.prix.toInt()} MAD",
                            fontSize = 15.sp,
                            color = Noir.copy(alpha = 0.4f),
                            textDecoration = TextDecoration.LineThrough
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        p.description,
                        fontSize = 14.sp,
                        color = Noir.copy(alpha = 0.7f),
                        lineHeight = 22.sp
                    )

                    Spacer(Modifier.height(24.dp))

                    // Quantité
                    Text(
                        "Quantité",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Noir
                    )
                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (quantite > 1) quantite-- },
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(50))
                                .size(40.dp)
                        ) {
                            Text("-", fontSize = 20.sp, color = Noir, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            quantite.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        IconButton(
                            onClick = { if (quantite < p.stock) quantite++ },
                            modifier = Modifier
                                .background(Vert, RoundedCornerShape(50))
                                .size(40.dp)
                        ) {
                            Text("+", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = {
                            panierViewModel.ajouterAuPanier(
                                clientId = user.uid,
                                item = PanierItem(
                                    produitId  = p.produitId,
                                    nomProduit = p.nom,
                                    image      = p.images.firstOrNull() ?: "",
                                    prix       = p.prixEffectif,
                                    artisanId  = p.artisanId,
                                    nomArtisan = p.nomArtisan,
                                    quantite   = quantite
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Vert)
                    ) {
                        Text(
                            "Ajouter au panier",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }

            // Toast message
            message?.let {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Noir.copy(alpha = 0.85f)
                ) {
                    Text(
                        text = it,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
            }
        }

    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Vert)
    }
}