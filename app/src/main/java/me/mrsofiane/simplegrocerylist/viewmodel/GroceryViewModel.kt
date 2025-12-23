package me.mrsofiane.simplegrocerylist.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.mrsofiane.simplegrocerylist.model.GroceryItem

class GroceryViewModel(
    private val prefs: SharedPreferences
) : ViewModel() {

    private val gson = Gson()

    private val _items = MutableStateFlow(loadItems())
    val items: StateFlow<List<GroceryItem>> = _items

    private fun save() {
        try {
            prefs.edit()
                .putString("items", gson.toJson(_items.value))
                .apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save items", e)
        }
    }

    fun addItem(item: GroceryItem) {
        // Validate input
        val trimmedName = item.name.trim()
        if (trimmedName.isBlank()) return

        val validItem = item.copy(
            name = trimmedName.take(MAX_NAME_LENGTH),
            quantity = item.quantity.trim().take(MAX_QUANTITY_LENGTH)
        )
        _items.value = _items.value + validItem
        save()
    }

    fun toggleItem(item: GroceryItem) {
        _items.value = _items.value.map {
            if (it.id == item.id) it.copy(isChecked = !it.isChecked) else it
        }
        save()
    }

    fun removeItem(item: GroceryItem) {
        _items.value = _items.value.filter { it.id != item.id }
        save()
    }

    fun updateItem(item: GroceryItem) {
        val trimmedName = item.name.trim()
        if (trimmedName.isBlank()) return

        val validItem = item.copy(
            name = trimmedName.take(MAX_NAME_LENGTH),
            quantity = item.quantity.trim().take(MAX_QUANTITY_LENGTH)
        )
        _items.value = _items.value.map {
            if (it.id == validItem.id) validItem else it
        }
        save()
    }

    fun exportAsJson(): String {
        return gson.toJson(_items.value)
    }

    fun importFromJson(json: String): Boolean {
        return try {
            val type = object : TypeToken<List<GroceryItem>>() {}.type
            val importedItems: List<GroceryItem>? = gson.fromJson(json, type)
            if (importedItems != null && importedItems.isNotEmpty()) {
                _items.value = importedItems.filter { it.name.isNotBlank() }
                save()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to import items", e)
            false
        }
    }

    private fun loadItems(): List<GroceryItem> {
        val json = prefs.getString("items", null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<GroceryItem>>() {}.type
            val items: List<GroceryItem>? = gson.fromJson(json, type)
            items?.filter { it.name.isNotBlank() } ?: emptyList()
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "Failed to parse saved items, clearing corrupted data", e)
            prefs.edit().remove("items").apply()
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error loading items", e)
            emptyList()
        }
    }

    companion object {
        private const val TAG = "GroceryViewModel"
        private const val MAX_NAME_LENGTH = 100
        private const val MAX_QUANTITY_LENGTH = 20
    }
}
