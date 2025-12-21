package me.mrsofiane.simplegrocerylist.model

import java.util.UUID

data class GroceryItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String,
    val quantity: String,
    val isChecked: Boolean = false
)