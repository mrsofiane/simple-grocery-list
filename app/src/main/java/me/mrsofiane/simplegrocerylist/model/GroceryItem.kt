package me.mrsofiane.simplegrocerylist.model

data class GroceryItem(
    val name: String,
    val category: String,
    val quantity: String,
    val isChecked: Boolean = false
)