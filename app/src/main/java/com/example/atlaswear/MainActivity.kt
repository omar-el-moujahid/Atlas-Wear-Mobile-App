package com.example.atlaswear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.atlaswear.navigation.NavGraph
import com.example.atlaswear.ui.theme.AtlasWearTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AtlasWearTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}