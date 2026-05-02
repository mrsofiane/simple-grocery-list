package me.mrsofiane.simplegrocerylist.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import me.mrsofiane.simplegrocerylist.model.GroceryItem
import me.mrsofiane.simplegrocerylist.model.GroceryList
import java.util.UUID

class GroceryViewModel(
    private val prefs: SharedPreferences
) : ViewModel() {

    private val gson = Gson()

    private val _lists = MutableStateFlow<List<GroceryList>>(emptyList())
    val lists: StateFlow<List<GroceryList>> = _lists

    private val _activeListId = MutableStateFlow<String>("")
    val activeListId: StateFlow<String> = _activeListId

    val activeList: StateFlow<GroceryList?> =
        combine(_lists, _activeListId) { lists, id ->
            lists.firstOrNull { it.id == id }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    init {
        load()
    }

    private fun save() {
        try {
            prefs.edit()
                .putString("lists", gson.toJson(_lists.value))
                .putString("active_list_id", _activeListId.value)
                .apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save items", e)
        }
    }

    fun addItem(item: GroceryItem) {
        val activeId = _activeListId.value
        val trimmedName = item.name.trim()
        if (trimmedName.isBlank()) return

        val validItem = item.copy(
            name = trimmedName.take(MAX_NAME_LENGTH),
            quantity = item.quantity.trim().take(MAX_QUANTITY_LENGTH)
        )
        _lists.value = _lists.value.map { list ->
            if (list.id == activeId) list.copy(items = list.items + validItem)
            else list
        }
        save()
    }

    fun toggleItem(item: GroceryItem) {
        val activeId = _activeListId.value
        _lists.value = _lists.value.map { list ->
            if (list.id == activeId) list.copy(items = list.items.map {
                if (it.id == item.id) it.copy(
                    isChecked = !it.isChecked
                ) else it
            })
            else list
        }
        save()
    }

    fun removeItem(item: GroceryItem) {
        val activeId = _activeListId.value
        _lists.value = _lists.value.map { list ->
            if (list.id == activeId) list.copy(items = list.items.filter { it.id != item.id })
            else list
        }
        save()
    }

    fun updateItem(item: GroceryItem) {
        val activeId = _activeListId.value
        val trimmedName = item.name.trim()
        if (trimmedName.isBlank()) return

        val validItem = item.copy(
            name = trimmedName.take(MAX_NAME_LENGTH),
            quantity = item.quantity.trim().take(MAX_QUANTITY_LENGTH)
        )
        _lists.value = _lists.value.map { list ->
            if (list.id == activeId) list.copy(items = list.items.map { if (it.id == validItem.id) validItem else it })
            else list
        }
        save()
    }

    fun createList(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return
        val newList = GroceryList(name = trimmed.take(MAX_NAME_LENGTH))
        _lists.value = _lists.value + newList
        _activeListId.value = newList.id
        save()
    }

    fun renameList(id: String, newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isBlank()) return
        _lists.value = _lists.value.map {
            if (it.id == id) it.copy(name = trimmed.take(MAX_NAME_LENGTH)) else it
        }
        save()
    }

    fun deleteList(id: String) {
        val remaining = _lists.value.filter { it.id != id }
        if (remaining.isEmpty()) {
            val fresh = GroceryList(name = DEFAULT_LIST_NAME)
            _lists.value = listOf(fresh)
            _activeListId.value = fresh.id
        } else {
            _lists.value = remaining
            if (_activeListId.value == id) {
                _activeListId.value = remaining.first().id
            }
        }
        save()
    }

    fun setActiveList(id: String) {
        if (_lists.value.any { it.id == id }) {
            _activeListId.value = id
            save()
        }
    }

    fun exportAsJson(): String {
        val active = _lists.value.firstOrNull { it.id == _activeListId.value } ?: return "{}"
        return gson.toJson(active)
    }

    fun importFromJson(json: String): Int {
        if (json.isBlank()) return 0
        return try {
            val imported = gson.fromJson(json, GroceryList::class.java) ?: return 0
            if (imported.items.isEmpty()) return 0
            val newList = imported.copy(id = UUID.randomUUID().toString())
            _lists.value += newList
            _activeListId.value = newList.id
            save()
            newList.items.size

        } catch (e: Exception) {
            Log.e(TAG, "Failed to import items", e)
            0
        }
    }

    private fun load() {
        val listsJson = prefs.getString("lists", null)
        if (listsJson != null) {
            try {
                val type = object : TypeToken<List<GroceryList>>() {}.type
                val loaded: List<GroceryList>? = gson.fromJson(listsJson, type)
                if (loaded != null && loaded.isNotEmpty()) {
                    _lists.value = loaded
                    _activeListId.value = prefs.getString("active_list_id", null)
                        ?.takeIf { id -> loaded.any { it.id == id } }
                        ?: loaded.first().id
                    return
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse saved lists", e)
            }
        }

        val legacyJson = prefs.getString("items", null)
        if (legacyJson != null) {
            try {
                val type = object : TypeToken<List<GroceryItem>>() {}.type
                val legacyItems: List<GroceryItem>? = gson.fromJson(legacyJson, type)
                val validItems = legacyItems?.filter { it.name.isNotBlank() } ?: emptyList()
                val migratedList = GroceryList(name = DEFAULT_LIST_NAME, items = validItems)
                _lists.value = listOf(migratedList)
                _activeListId.value = migratedList.id
                save()
                prefs.edit().remove("items").apply()
                return
            } catch (e: Exception) {
                Log.e(TAG, "Failed to migrate legacy items", e)
            }
        }

        val freshList = GroceryList(name = DEFAULT_LIST_NAME)
        _lists.value = listOf(freshList)
        _activeListId.value = freshList.id
        save()
    }

    companion object {
        private const val TAG = "GroceryViewModel"
        private const val MAX_NAME_LENGTH = 100
        private const val MAX_QUANTITY_LENGTH = 20
        private const val DEFAULT_LIST_NAME = "My List"
    }
}
