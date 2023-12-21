package com.example.pocnavigation

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

class FirstFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            FirstScreen(
                label = "Fragment",
                onClick = {
                    ComposeNav.navigation.navigate("second")
                    DecomposeNav.navigation.navigate(Route.Second())
                    findNavController().navigate(FirstFragmentDirections.actionFirstFragmentToSecondFragment())
                }
            )
        }
    }
}

@Composable
fun FirstScreen(
    label: String,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("First Screen: $label")

        Button(
            onClick = onClick
        ) {
            Text("Click Me")
        }
    }
}