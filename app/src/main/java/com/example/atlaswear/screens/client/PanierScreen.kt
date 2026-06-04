package com.example.atlaswear.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import com.example.atlaswear.model.Panier
import com.example.atlaswear.model.User
import com.example.atlaswear.navigation.Routes
import com.example.atlaswear.ui.theme.Beige
import com.example.atlaswear.ui.theme.Noir
import com.example.atlaswear.ui.theme.Vert
import com.example.atlaswear.viewmodel.PanierViewModel

@Composable
fun PanierScreen(
    user: User,
    navController: NavController,
    viewModel: PanierViewModel = viewModel(),
    onNavigateToPaiement: (Panier) -> Unit
) {
    val panier by viewModel.panier.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(user.uid) {
        android.util.Log.d("PANIER", "LOAD UID = '${user.uid}'")
        viewModel.loadPanier(user.uid)
    }

    Scaffold(
        containerColor = Beige,
        topBar = {
            Surface(color = Color.White,
                shadowElevation = 2.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                    Text(
                        text = "Mon Panier",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Noir,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Vert)
            }
        } else if (panier.items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛒", fontSize = 60.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Votre panier est vide", fontSize = 18.sp, color = Noir.copy(alpha = 0.5f))
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Liste items
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(panier.items) { item ->
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
                                // Image
                                AsyncImage(
                                    model = item.image,
                                    contentDescription = item.nomProduit,
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Beige),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                // Infos
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        item.nomProduit,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Noir
                                    )
                                    Text(
                                        "Quantité: ${item.quantite}",
                                        fontSize = 13.sp,
                                        color = Noir.copy(alpha = 0.5f)
                                    )
                                    Text(
                                        "${item.sousTotal.toInt()} MAD",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Vert
                                    )
                                }

                                // Supprimer
                                IconButton(
                                    onClick = { viewModel.supprimerItem(user.uid, item.produitId) }
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Supprimer",
                                        tint = Color.Red.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Récapitulatif
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Sous-total", color = Noir.copy(alpha = 0.7f))
                            Text("${panier.total.toInt()} MAD", fontWeight = FontWeight.Medium)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Livraison", color = Noir.copy(alpha = 0.7f))
                            Text("30 MAD", fontWeight = FontWeight.Medium)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                "${(panier.total + 30).toInt()} MAD",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Vert
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onNavigateToPaiement(panier) },                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Vert)
                        ) {
                            Text("Payer", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}