package com.example.atlaswear.screens.artisan

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.atlaswear.model.User
import com.example.atlaswear.ui.theme.*
import com.example.atlaswear.viewmodel.ProfilViewModel

@Composable
fun InfosPersonnellesArtisanScreen(
    user: User?,
    navController: NavController,
    viewModel: ProfilViewModel = viewModel()
) {
    // Si l'utilisateur est nul, on affiche un écran de chargement temporaire
    if (user == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(Beige),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Dore)
        }
        return
    }

    var nom by remember { mutableStateOf(user.nom) }
    var prenom by remember { mutableStateOf(user.prenom) }
    var ville by remember { mutableStateOf(user.ville) }
    var isEditing by remember { mutableStateOf(false) }

    val saveSuccess by viewModel.saveSuccess.collectAsState()

    // Retour automatique après sauvegarde réussie
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            kotlinx.coroutines.delay(1200)
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
                        "Mes informations",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { isEditing = !isEditing }) {
                        Text(
                            if (isEditing) "Annuler" else "Modifier",
                            color = Dore,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
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
            // Notification de succès
            AnimatedVisibility(
                visible = saveSuccess,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut()
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Vert)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Informations sauvegardées !", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Affichage de l'avatar circulaire
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Dore, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initiale = "${user.prenom.firstOrNull() ?: ""}${user.nom.firstOrNull() ?: ""}"
                    Text(initiale.uppercase(), fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Noir)
                }
            }

            // Formulaire des informations
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Informations du compte artisan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Noir.copy(alpha = 0.5f))
                    Spacer(Modifier.height(16.dp))

                    // Email (Toujours verrouillé)
                    ArtisanInfoField(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = user.email,
                        enabled = false
                    )
                    Spacer(Modifier.height(12.dp))

                    // Prénom & Nom
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            ArtisanInfoField(
                                icon = Icons.Default.Person,
                                label = "Prénom",
                                value = prenom,
                                enabled = isEditing,
                                onValueChange = { prenom = it }
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            ArtisanInfoField(
                                icon = Icons.Default.Person,
                                label = "Nom",
                                value = nom,
                                enabled = isEditing,
                                onValueChange = { nom = it }
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))

                    // Ville
                    ArtisanInfoField(
                        icon = Icons.Default.LocationOn,
                        label = "Ville",
                        value = ville,
                        enabled = isEditing,
                        onValueChange = { ville = it }
                    )
                }
            }

            // Badge Type de compte : Écrit "Artisan" au lieu de "Client"
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Compte", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Noir.copy(alpha = 0.5f))
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Type de compte", fontSize = 14.sp, color = Noir.copy(alpha = 0.6f))
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Dore.copy(alpha = 0.15f)
                        ) {
                            Text("Artisan", fontSize = 13.sp, color = Dore, fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                        }
                    }
                }
            }

            // Bouton de validation (Visible uniquement si "Modifier" est cliqué)
            AnimatedVisibility(visible = isEditing) {
                Button(
                    onClick = {
                        viewModel.sauvegarderInfos(
                            uid = user.uid,
                            nom = nom,
                            prenom = prenom,
                            ville = ville
                        )
                        isEditing = false
                    },
                    enabled = nom.isNotBlank() && prenom.isNotBlank() && ville.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Vert)
                ) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Sauvegarder", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ArtisanInfoField(
    icon: ImageVector,
    label: String,
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        leadingIcon = {
            Icon(icon, null, tint = if (enabled) Vert else Color.Gray.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
        },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Vert,
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.25f),
            focusedLabelColor = Vert,
            disabledBorderColor = Color.Gray.copy(alpha = 0.15f),
            disabledTextColor = Noir.copy(alpha = 0.5f),
            disabledLabelColor = Noir.copy(alpha = 0.35f)
        )
    )
}