package me.mrsofiane.simplegrocerylist.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.mrsofiane.simplegrocerylist.model.GroceryItem

class GroceryViewModel(
    private val prefs: SharedPreferences
) : ViewModel() {

    private val gson = Gson()

    private val _items =
        MutableStateFlow(loadItems())

    val items: StateFlow<List<GroceryItem>> = _items

    private fun save() {
        prefs.edit()
            .putString("items", gson.toJson(_items.value))
            .apply()
    }

    fun addItem(item: GroceryItem) {
        _items.value = _items.value + item
        save()
    }

    fun toggleItem(item: GroceryItem) {
        _items.value = _items.value.map {
            if (it == item) it.copy(isChecked = !it.isChecked) else it
        }
        save()
    }

    fun removeItem(item: GroceryItem) {
        _items.value = _items.value - item
        save()
    }

    private fun loadItems(): List<GroceryItem> {
        val json = prefs.getString("items", null) ?: return emptyList()
        val type = object : TypeToken<List<GroceryItem>>() {}.type
        return gson.fromJson(json, type)
    }
}
