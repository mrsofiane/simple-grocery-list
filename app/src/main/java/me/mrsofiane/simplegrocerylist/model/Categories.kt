package me.mrsofiane.simplegrocerylist.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import me.mrsofiane.simplegrocerylist.ui.theme.CategoryDairy
import me.mrsofiane.simplegrocerylist.ui.theme.CategoryFruits
import me.mrsofiane.simplegrocerylist.ui.theme.CategoryGeneral
import me.mrsofiane.simplegrocerylist.ui.theme.CategoryHousehold
import me.mrsofiane.simplegrocerylist.ui.theme.CategoryMeat
import me.mrsofiane.simplegrocerylist.ui.theme.CategoryVegetables


val Categories = listOf("General", "Fruits", "Vegetables", "Dairy", "Meat", "Household")

// Helper function to get category color
fun getCategoryColor(category: String): Color {
    return when (category) {
        "Fruits" -> CategoryFruits
        "Vegetables" -> CategoryVegetables
        "Dairy" -> CategoryDairy
        "Meat" -> CategoryMeat
        "Household" -> CategoryHousehold
        else -> CategoryGeneral
    }
}

// Helper function to get category icon
fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        "Fruits" -> Icons.Default.ShoppingCart
        "Vegetables" -> Icons.Default.ShoppingCart
        "Dairy" -> Icons.Default.ShoppingCart
        "Meat" -> Icons.Default.ShoppingCart
        "Household" -> Icons.Default.Home
        else -> Icons.Default.ShoppingCart
    }
}