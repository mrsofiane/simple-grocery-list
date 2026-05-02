package me.mrsofiane.simplegrocerylist.model

import java.util.UUID

data class GroceryList(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val items: List<GroceryItem> = emptyList()
)
