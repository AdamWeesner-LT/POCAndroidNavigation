package com.example.pocnavigation.composeNavigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.example.pocnavigation.FirstScreen
import com.example.pocnavigation.SecondScreen
import com.example.pocnavigation.ThirdScreen

@Composable
fun ComposeNavigation(
    navController: NavHostController,
    firstScreenOnClick: () -> Unit,
    secondScreenOnClick: () -> Unit,
    showDialogClick: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            navController = navController,
            startDestination = "first"
        ) {
            composable("first") {
                FirstScreen(
                    label = "Compose",
                    onClick = firstScreenOnClick
                )
            }
            composable("second") {
                SecondScreen(
                    label = "Compose",
                    onClick = secondScreenOnClick,
                    showDialogClick = showDialogClick
                )
            }
            dialog("third") {
                ThirdScreen(
                    label = "Compose Dialog",
                    onClick = {
                        navController.popBackStack()
                    },
                )
            }
        }
    }
}