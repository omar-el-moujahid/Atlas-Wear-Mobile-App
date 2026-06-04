package com.example.atlaswear.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.atlaswear.model.Produit
import com.example.atlaswear.ui.theme.Beige
import com.example.atlaswear.ui.theme.Noir
import com.example.atlaswear.ui.theme.Vert

@Composable
fun ProduitCard(
    produit: Produit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Image produit
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Beige)
            ) {
                if (produit.images.isNotEmpty()) {
                    AsyncImage(
                        model = produit.images.first(),
                        contentDescription = produit.nom,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder si pas d'image
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Beige),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = produit.nom.first().toString(),
                            fontSize = 40.sp,
                            color = Vert,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Infos produit
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = produit.nom,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Noir,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = produit.nomCategorie,
                    fontSize = 12.sp,
                    color = Noir.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${produit.prixEffectif.toInt()} MAD",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Vert
                    )
                    if (produit.aUneReduction) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${produit.prix.toInt()}",
                            fontSize = 12.sp,
                            color = Noir.copy(alpha = 0.4f),
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
            }
        }
    }
}