package com.example.pocnavigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.NavHostFragment
import com.arkivanov.decompose.defaultComponentContext
import com.example.pocnavigation.composeNavigation.ComposeNavigation
import com.example.pocnavigation.decomposeNavigation.DecomposeNavigation
import com.example.pocnavigation.decomposeNavigation.DecomposeNavigationRoot
import com.example.pocnavigation.decomposeNavigation.DefaultDecomposeNavigation
import com.example.pocnavigation.decomposeNavigation.Route

object DecomposeNav {
    lateinit var navigation: DecomposeNavigation
}

object ComposeNav {
    lateinit var navigation: NavHostController
}

class MainActivity : AppCompatActivity() {
    private val fragmentNavController: NavController by lazy {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_content) as NavHostFragment
        navHostFragment.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_navigation)

        // COMPOSE NAVIGATION
        findViewById<ComposeView>(R.id.compose_content).apply {
            setContent {
                ComposeNav.navigation = rememberNavController()

                ComposeNavigation(
                    navController = ComposeNav.navigation,
                    firstScreenOnClick = {
                        ComposeNav.navigation.navigate("second")
                        DecomposeNav.navigation.navigate(Route.Second())
                        fragmentNavController.navigate(FirstFragmentDirections.actionFirstFragmentToSecondFragment())
                    },
                    secondScreenOnClick = {
                        ComposeNav.navigation.navigate("first")
                        DecomposeNav.navigation.navigate(Route.First)
                        fragmentNavController.navigate(SecondFragmentDirections.actionSecondFragmentToFirstFragment())
                    },
                    showDialogClick = {
                        ComposeNav.navigation.navigate("third")
                    }
                )
            }
        }

        // DECOMPOSE NAVIGATION
        findViewById<ComposeView>(R.id.decompose_content).apply {
            DecomposeNav.navigation = DefaultDecomposeNavigation(defaultComponentContext())

            setContent {
                DecomposeNavigationRoot(
                    navigation = DecomposeNav.navigation,
                    firstScreenOnClick = {
                        ComposeNav.navigation.navigate("second")
                        DecomposeNav.navigation.navigate(Route.Second())
                        fragmentNavController.navigate(FirstFragmentDirections.actionFirstFragmentToSecondFragment())
                    },
                    secondScreenOnClick = {
                        ComposeNav.navigation.navigate("first")
                        DecomposeNav.navigation.navigate(Route.First)
                        fragmentNavController.navigate(SecondFragmentDirections.actionSecondFragmentToFirstFragment())
                    },
                    showDialogClick = {
                        DecomposeNav.navigation.navigate(Route.Third)
                    }
                )
            }
        }
    }
}
