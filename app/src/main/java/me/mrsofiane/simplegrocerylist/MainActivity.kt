package me.mrsofiane.simplegrocerylist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import me.mrsofiane.simplegrocerylist.ui.GroceryScreen
import me.mrsofiane.simplegrocerylist.ui.theme.SimpleGroceryListTheme
import me.mrsofiane.simplegrocerylist.viewmodel.GroceryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("grocery_prefs", MODE_PRIVATE)
        val viewModel = GroceryViewModel(prefs)

        setContent {
            val darkTheme = isSystemInDarkTheme()

            // Update system bar styles based on theme
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = if (darkTheme) {
                        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT
                        )
                    },
                    navigationBarStyle = if (darkTheme) {
                        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT
                        )
                    }
                )
                onDispose {}
            }

            SimpleGroceryListTheme(darkTheme = darkTheme) {
                GroceryScreen(viewModel)
            }
        }
    }
}