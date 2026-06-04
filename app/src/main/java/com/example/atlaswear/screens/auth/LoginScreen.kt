package com.example.atlaswear.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atlaswear.R
import com.example.atlaswear.model.User
import com.example.atlaswear.ui.theme.AtlasWearTheme
import com.example.atlaswear.ui.theme.Beige
import com.example.atlaswear.ui.theme.Dore
import com.example.atlaswear.ui.theme.Noir
import com.example.atlaswear.ui.theme.Vert
import com.example.atlaswear.viewmodel.AuthUiState
import com.example.atlaswear.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegisterClient: () -> Unit,
    onNavigateToLoginArtisan: () -> Unit,
    onLoginSuccess: (User) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess((uiState as AuthUiState.Success).user)
        }
    }

    LoginScreenContent(
        email = email,
        password = password,
        passwordVisible = passwordVisible,
        isLoading = uiState is AuthUiState.Loading,
        errorMessage = if (uiState is AuthUiState.Error) (uiState as AuthUiState.Error).message else null,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onPasswordVisibleToggle = { passwordVisible = !passwordVisible },
        onLoginClick = { viewModel.login(email, password) },
        onNavigateToRegisterClient = onNavigateToRegisterClient,
        onNavigateToLoginArtisan = onNavigateToLoginArtisan
    )
}

@Composable
fun LoginScreenContent(
    email: String,
    password: String,
    passwordVisible: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibleToggle: () -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToRegisterClient: () -> Unit,
    onNavigateToLoginArtisan: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Beige
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Icon(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Atlas Wear Logo",
                modifier = Modifier.size(200.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Connecter Vous",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Noir
            )

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Nom utilisateur", fontSize = 13.sp, color = Noir, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = onEmailChange,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Vert,
                                unfocusedBorderColor = Color(0xFFD0C8B0),
                                cursorColor = Vert
                            ),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Mots de Passe", fontSize = 13.sp, color = Noir, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = onPasswordChange,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            visualTransformation = if (passwordVisible)
                                VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = onPasswordVisibleToggle) {
                                    Icon(
                                        imageVector = if (passwordVisible)
                                            Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = Vert
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Vert,
                                unfocusedBorderColor = Color(0xFFD0C8B0),
                                cursorColor = Vert
                            ),
                            singleLine = true
                        )
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        TextButton(
                            onClick = {},
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Text(
                                "Oublier le mots de passe ?",
                                fontSize = 12.sp,
                                color = Noir.copy(alpha = 0.5f)
                            )
                        }
                    }

                    errorMessage?.let {
                        Text(
                            it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Vert),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Connexion", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFD0C8B0))
                        Text("  OU  ", fontSize = 12.sp, color = Noir.copy(alpha = 0.5f))
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFD0C8B0))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onNavigateToRegisterClient,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Vert)
                    ) {
                        Text("Créer un compte", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Espace artisan — juste connexion
                    OutlinedButton(
                        onClick = onNavigateToLoginArtisan,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFFFFF8E8),
                            contentColor = Color(0xFF8B6914)
                        ),
                        border = BorderStroke(1.5.dp, Dore)
                    ) {
                        Text("Espace Artisan →", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5EFE0)
@Composable
fun LoginScreenPreview() {
    AtlasWearTheme {
        LoginScreenContent(
            email = "",
            password = "",
            passwordVisible = false,
            isLoading = false,
            errorMessage = null,
            onEmailChange = {},
            onPasswordChange = {},
            onPasswordVisibleToggle = {},
            onLoginClick = {},
            onNavigateToRegisterClient = {},
            onNavigateToLoginArtisan = {}
        )
    }
}