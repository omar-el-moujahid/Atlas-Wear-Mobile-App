package com.example.atlaswear.screens.artisan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
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
import com.example.atlaswear.model.User
import com.example.atlaswear.ui.theme.*
import com.example.atlaswear.viewmodel.ArtisanViewModel
import androidx.compose.material.icons.automirrored.filled.ArrowBack
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjouterProduitScreen(
    user: User,
    navController: NavController,
    viewModel: ArtisanViewModel = viewModel()
) {
    var nom by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var prix by remember { mutableStateOf("") }
    var prixReduit by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var categorieSelectionnee by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadCategories() }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            viewModel.resetSaveSuccess()
            navController.popBackStack()
        }
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
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                    Text(
                        "Ajouter un produit",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Zone photo
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .border(
                            width = 1.5.dp,
                            color = Dore.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.AddPhotoAlternate,
                            null,
                            tint = Dore,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Ajouter des photos", fontWeight = FontWeight.SemiBold, color = Noir)
                        Text("JPG, PNG — max 5 photos", fontSize = 12.sp, color = Noir.copy(alpha = 0.4f))
                    }
                }
            }

            // Nom produit
            ProduitField(
                value = nom,
                onValueChange = { nom = it },
                label = "Nom du produit *"
            )

            // Catégorie dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = categorieSelectionnee,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Catégorie *", fontSize = 13.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Dore,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                        focusedLabelColor = Dore
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nom) },
                            onClick = {
                                categorieSelectionnee = cat.nom
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Prix
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ProduitField(
                    value = prix,
                    onValueChange = { prix = it },
                    label = "Prix (MAD) *",
                    modifier = Modifier.weight(1f)
                )
                ProduitField(
                    value = prixReduit,
                    onValueChange = { prixReduit = it },
                    label = "Prix réduit",
                    modifier = Modifier.weight(1f)
                )
            }

            // Stock
            ProduitField(
                value = stock,
                onValueChange = { stock = it },
                label = "Stock disponible *"
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description", fontSize = 13.sp) },
                placeholder = { Text("Décrivez votre produit", fontSize = 13.sp,
                    color = Noir.copy(alpha = 0.3f)) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Dore,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                    focusedLabelColor = Dore
                )
            )

            // Bouton publier
            Button(
                onClick = {
                    viewModel.ajouterProduit(
                        artisanId  = user.uid,
                        nomArtisan = "${user.prenom} ${user.nom}",
                        nom        = nom,
                        description = description,
                        prix       = prix.toDoubleOrNull() ?: 0.0,
                        prixReduit = prixReduit.toDoubleOrNull() ?: 0.0,
                        stock      = stock.toIntOrNull() ?: 0,
                        categorie  = categorieSelectionnee
                    )
                },
                enabled = nom.isNotBlank() && prix.isNotBlank() &&
                        stock.isNotBlank() && !isLoading,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Vert)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Text("Publier le produit", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ProduitField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Dore,
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
            focusedLabelColor = Dore
        )
    )
}