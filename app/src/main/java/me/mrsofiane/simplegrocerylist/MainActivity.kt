package me.mrsofiane.simplegrocerylist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import me.mrsofiane.simplegrocerylist.ui.GroceryScreen
import me.mrsofiane.simplegrocerylist.viewmodel.GroceryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("grocery_prefs", MODE_PRIVATE)
        val viewModel = GroceryViewModel(prefs)

        setContent {
            GroceryScreen(viewModel)
        }
    }
}