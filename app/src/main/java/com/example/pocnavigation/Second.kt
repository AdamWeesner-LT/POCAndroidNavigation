package com.example.pocnavigation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pocnavigation.decomposeNavigation.Route

class SecondFragment : Fragment() {
    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            SecondScreen(
                label = "Fragment",
                onClick = {
                    ComposeNav.navigation.navigate("first")
                    DecomposeNav.navigation.navigate(Route.First)
                    findNavController().navigate(SecondFragmentDirections.actionSecondFragmentToFirstFragment())
                },
                showDialogClick = {
                    findNavController().navigate(SecondFragmentDirections.actionSecondFragmentToThirdFragment())
                }
            )
        }
    }
}

@Composable
fun SecondScreen(
    label: String,
    onClick: () -> Unit,
    showDialogClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Second Screen: $label")

        Button(
            onClick = onClick
        ) {
            Text("Click Me")
        }

        Button(
            onClick = showDialogClick
        ) {
            Text("Show Dialog")
        }
    }
}