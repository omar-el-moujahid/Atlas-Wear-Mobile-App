package com.example.atlaswear.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.atlaswear.model.Panier
import com.example.atlaswear.model.User
import com.example.atlaswear.navigation.Routes
import com.example.atlaswear.ui.theme.*
import com.example.atlaswear.viewmodel.CommandeViewModel

@Composable
fun PaiementScreen(
    user: User,
    panier: Panier,
    navController: NavController,
    viewModel: CommandeViewModel = viewModel()
) {
    var adresse by remember { mutableStateOf("") }
    var ville by remember { mutableStateOf(user.ville) }
    var numeroCarte by remember { mutableStateOf("") }
    var nomCarte by remember { mutableStateOf("") }
    var expiration by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val commandePassee by viewModel.commandePassee.collectAsState()
    val erreur by viewModel.erreur.collectAsState()

    // Quand la commande est passée → naviguer vers confirmation
    LaunchedEffect(commandePassee) {
        commandePassee?.let { _ ->
            val total = (panier.total + 30).toInt()
            val nbArticles = panier.nombreArticles
            viewModel.resetCommandePassee()
            navController.navigate("confirmation/$total/$nbArticles") {
                popUpTo(Routes.HOME) { inclusive = false }
            }
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                    Text(
                        "Paiement",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- Section adresse ---
            SectionCard(titre = "Adresse de livraison", icone = {
                Icon(Icons.Default.LocationOn, null, tint = Vert, modifier = Modifier.size(20.dp))
            }) {
                AtlasTextField(
                    value = adresse,
                    onValueChange = { adresse = it },
                    label = "Adresse complète"
                )
                Spacer(Modifier.height(10.dp))
                AtlasTextField(
                    value = ville,
                    onValueChange = { ville = it },
                    label = "Ville"
                )
            }

            // --- Section carte ---
            SectionCard(titre = "Carte bancaire", icone = {
                Icon(Icons.Default.CreditCard, null, tint = Vert, modifier = Modifier.size(20.dp))
            }) {
                AtlasTextField(
                    value = numeroCarte,
                    onValueChange = { if (it.length <= 16) numeroCarte = it },
                    label = "Numéro de carte",
                    keyboardType = KeyboardType.Number,
                    placeholder = "1234 5678 9012 3456"
                )
                Spacer(Modifier.height(10.dp))
                AtlasTextField(
                    value = nomCarte,
                    onValueChange = { nomCarte = it },
                    label = "Nom sur la carte"
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AtlasTextField(
                        value = expiration,
                        onValueChange = { input ->
                            val digits = input.filter { it.isDigit() }
                            expiration = when {
                                digits.length >= 3 -> "${digits.take(2)}/${digits.drop(2).take(2)}"
                                digits.length == 2 -> "${digits}/"
                                else -> digits
                            }
                        },
                        label = "MM/AA",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                    AtlasTextField(
                        value = cvv,
                        onValueChange = { if (it.length <= 3) cvv = it },
                        label = "CVV",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // --- Récapitulatif ---
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Sous-total", color = Noir.copy(alpha = 0.6f))
                        Text("${panier.total.toInt()} MAD")
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Livraison", color = Noir.copy(alpha = 0.6f))
                        Text("30 MAD")
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            "${(panier.total + 30).toInt()} MAD",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Vert
                        )
                    }
                }
            }

            // Erreur
            erreur?.let {
                Text(it, color = Color.Red, fontSize = 13.sp)
            }

            // --- Bouton valider ---
            val formValide = adresse.isNotBlank() && ville.isNotBlank() &&
                    numeroCarte.length == 16 && nomCarte.isNotBlank() &&
                    expiration.length >= 4 && cvv.length == 3

            Button(
                onClick = {
                    viewModel.passerCommande(
                        clientId = user.uid,
                        panier   = panier,
                        adresse  = "$adresse, $ville"
                    )
                },
                enabled = formValide && !isLoading,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Vert)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Text("Valider la commande", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SectionCard(
    titre: String,
    icone: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icone()
                Spacer(Modifier.width(8.dp))
                Text(titre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Noir)
            }
            Spacer(Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun AtlasTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        placeholder = if (placeholder.isNotEmpty()) ({ Text(placeholder, fontSize = 13.sp) }) else null,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Vert,
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
            focusedLabelColor = Vert
        )
    )
}