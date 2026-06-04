package com.example.atlaswear.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.atlaswear.screens.artisan.AjouterProduitScreen
import com.example.atlaswear.screens.artisan.CommandesArtisanScreen
import com.example.atlaswear.screens.artisan.DashboardArtisanScreen
import com.example.atlaswear.screens.artisan.InfosPersonnellesArtisanScreen
import com.example.atlaswear.screens.artisan.ProfilArtisanScreen
import com.example.atlaswear.screens.auth.LoginArtisanScreen
import com.example.atlaswear.screens.auth.LoginScreen
import com.example.atlaswear.screens.auth.RegisterScreen
import com.example.atlaswear.screens.client.*
import com.example.atlaswear.ui.theme.Dore
import com.example.atlaswear.ui.theme.Noir
import com.example.atlaswear.viewmodel.AuthState
import com.example.atlaswear.viewmodel.AuthViewModel
import com.example.atlaswear.viewmodel.SharedPanierViewModel

@Composable
fun NavGraph(navController: NavHostController) {

    val authViewModel: AuthViewModel = viewModel()
    val sharedPanierViewModel: SharedPanierViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // Splash
    if (authState == AuthState.LOADING) {
        Box(
            modifier = Modifier.fillMaxSize().background(Noir),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Atlas Wear", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Dore)
                Spacer(Modifier.height(32.dp))
                CircularProgressIndicator(color = Dore, modifier = Modifier.size(32.dp), strokeWidth = 2.dp)
            }
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegisterClient = { navController.navigate(Routes.REGISTER_CLIENT) },
                onNavigateToLoginArtisan = { navController.navigate(Routes.LOGIN_ARTISAN) },
                onLoginSuccess = { user ->
                    if (user.role == "artisan") {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.REGISTER_CLIENT) {
            RegisterScreen(
                viewModel = authViewModel,
                role = "client",
                onRegisterSuccess = {
                    authViewModel.resetState()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    authViewModel.resetState()
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.LOGIN_ARTISAN) {
            LoginArtisanScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HOME) {
            val user by authViewModel.currentUser.collectAsState()
            // ✅ Si pas connecté → aller LOGIN
            if (authState == AuthState.LOGGED_OUT) {
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
                return@composable
            }
            user?.let {
                HomeScreen(user = it, navController = navController)
            }
        }

        composable(Routes.DASHBOARD) {
            if (authState == AuthState.LOGGED_OUT) {
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                return@composable
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Dashboard Artisan — En construction")
            }
        }

        composable(Routes.RECHERCHE) {
            RechercheScreen(navController = navController)
        }

        composable(Routes.DETAIL_PRODUIT) { backStackEntry ->
            val produitId = backStackEntry.arguments?.getString("produitId") ?: ""
            val user by authViewModel.currentUser.collectAsState()
            user?.let {
                DetailProduitScreen(produitId = produitId, user = it, navController = navController)
            }
        }

        composable(Routes.PANIER) {
            val user by authViewModel.currentUser.collectAsState()
            user?.let {
                PanierScreen(
                    user = it,
                    navController = navController,
                    onNavigateToPaiement = { panier ->
                        sharedPanierViewModel.sauvegarderPanier(panier)
                        navController.navigate(Routes.PAIEMENT)
                    }
                )
            }
        }

        composable(Routes.PAIEMENT) {
            val user by authViewModel.currentUser.collectAsState()
            val panier by sharedPanierViewModel.panierSnapshot.collectAsState()
            user?.let {
                PaiementScreen(user = it, panier = panier, navController = navController)
            }
        }

        composable("confirmation/{total}/{nombreArticles}") { backStackEntry ->
            val total = backStackEntry.arguments?.getString("total")?.toIntOrNull() ?: 0
            val nombreArticles = backStackEntry.arguments?.getString("nombreArticles")?.toIntOrNull() ?: 0
            ConfirmationScreen(navController = navController, total = total, nombreArticles = nombreArticles)
        }

        composable(Routes.PROFILE) {
            val user by authViewModel.currentUser.collectAsState()
            user?.let {
                ProfilScreen(user = it, navController = navController, authViewModel = authViewModel)
            }
        }

        composable(Routes.FAVORIS) {
            val user by authViewModel.currentUser.collectAsState()
            user?.let { FavorisScreen(user = it, navController = navController) }
        }

        composable(Routes.COMMANDES) {
            val user by authViewModel.currentUser.collectAsState()
            user?.let { CommandesClientScreen(user = it, navController = navController) }
        }

        composable(Routes.INFOS_PERSO) {
            val user by authViewModel.currentUser.collectAsState()
            user?.let { InfosPersonnellesScreen(user = it, navController = navController) }
        }
        composable(Routes.DASHBOARD) {
            val user by authViewModel.currentUser.collectAsState()
            user?.let {
                DashboardArtisanScreen(user = it, navController = navController)
            }
        }

        composable(Routes.ARTISAN_COMMANDES) {
            val user by authViewModel.currentUser.collectAsState()
            user?.let {
                CommandesArtisanScreen(user = it, navController = navController)
            }
        }

        composable(Routes.ARTISAN_PRODUITS) {
            val user by authViewModel.currentUser.collectAsState()
            user?.let {
                AjouterProduitScreen(user = it, navController = navController)
            }
        }

        composable(Routes.ARTISAN_PROFILE) {
            val user by authViewModel.currentUser.collectAsState()
            user?.let {
                ProfilArtisanScreen(
                    user = it,
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
        }
        composable(Routes.ARTISAN_EDIT_INFOS) {
            val user by authViewModel.currentUser.collectAsState()
            user?.let {
                InfosPersonnellesArtisanScreen(user = it, navController = navController)
            }
        }
    }
}