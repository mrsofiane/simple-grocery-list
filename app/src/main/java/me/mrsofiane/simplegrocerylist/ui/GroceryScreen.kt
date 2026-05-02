package me.mrsofiane.simplegrocerylist.ui

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import me.mrsofiane.simplegrocerylist.model.Categories
import me.mrsofiane.simplegrocerylist.model.GroceryItem
import me.mrsofiane.simplegrocerylist.ui.components.AddItemCard
import me.mrsofiane.simplegrocerylist.ui.components.CategoryDropdown
import me.mrsofiane.simplegrocerylist.ui.components.EmptyState
import me.mrsofiane.simplegrocerylist.ui.components.GroceryTopBar
import me.mrsofiane.simplegrocerylist.ui.components.ListSwitcher
import me.mrsofiane.simplegrocerylist.ui.components.ProgressStats
import me.mrsofiane.simplegrocerylist.ui.components.SwipeableGroceryRow
import me.mrsofiane.simplegrocerylist.ui.dialogs.DeleteListDialog
import me.mrsofiane.simplegrocerylist.ui.dialogs.EditItemDialog
import me.mrsofiane.simplegrocerylist.ui.dialogs.ImportDialog
import me.mrsofiane.simplegrocerylist.ui.dialogs.NewListDialog
import me.mrsofiane.simplegrocerylist.ui.dialogs.RenameListDialog
import me.mrsofiane.simplegrocerylist.viewmodel.GroceryViewModel

@Composable
fun GroceryScreen(viewModel: GroceryViewModel) {
    val context = LocalContext.current
    val activeList by viewModel.activeList.collectAsState()
    val items = activeList?.items ?: emptyList()

    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("General") }
    var hideChecked by remember { mutableStateOf(false) }
    var filterCategory by remember { mutableStateOf("All") }
    var isInputExpanded by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<GroceryItem?>(null) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showSwitcher by remember { mutableStateOf(false) }
    val lists by viewModel.lists.collectAsState()
    val activeListId by viewModel.activeListId.collectAsState()
    var showNewListDialog by remember { mutableStateOf(false) }
    var showRenameListDialog by remember { mutableStateOf(false) }
    var showDeleteListDialog by remember { mutableStateOf(false) }

    val visibleItems = items.filter {
        (if (hideChecked) !it.isChecked else true) &&
                (filterCategory == "All" || it.category == filterCategory)
    }

    // Progress stats
    val totalItems = items.size
    val checkedItems = items.count { it.isChecked }

    // Edit Dialog
    editingItem?.let { item ->
        EditItemDialog(
            item = item,
            categories = Categories,
            onDismiss = { editingItem = null },
            onSave = { updatedItem ->
                viewModel.updateItem(updatedItem)
                editingItem = null
            }
        )
    }

    if (showSwitcher) {
        ListSwitcher(
            lists = lists,
            activeListId = activeListId,
            onListSelected = { id ->
                viewModel.setActiveList(id)
                showSwitcher = false
            },
            onCreateNew = {
                showSwitcher = false
                showNewListDialog = true
            },
            onDismiss = { showSwitcher = false },
        )
    }

    if (showNewListDialog) {
        NewListDialog(
            onDismiss = { showNewListDialog = false },
            onCreate = { name ->
                viewModel.createList(name)
                showNewListDialog = false
            },
        )
    }

    if (showRenameListDialog) {
        activeList?.let { list ->
            RenameListDialog(
                currentName = list.name,
                onDismiss = { showRenameListDialog = false },
                onRename = { newName ->
                    viewModel.renameList(list.id, newName)
                    showRenameListDialog = false
                },
            )
        }
    }

    if (showDeleteListDialog) {
        activeList?.let { list ->
            DeleteListDialog(
                listName = list.name,
                onDismiss = { showDeleteListDialog = false },
                onConfirm = { viewModel.deleteList(list.id) },
            )
        }
    }

    // Import Dialog
    if (showImportDialog) {
        ImportDialog(
            onDismiss = { showImportDialog = false },
            onImport = { json -> viewModel.importFromJson(json) }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                GroceryTopBar(
                    title = activeList?.name ?: "Grocery List",
                    isInputExpanded = isInputExpanded,
                    onToggleInput = { isInputExpanded = !isInputExpanded },
                    onShareClick = {
                        val json = viewModel.exportAsJson()
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, json)
                            type = "text/plain"
                        }
                        context.startActivity(
                            Intent.createChooser(
                                sendIntent,
                                "Share grocery list"
                            )
                        )
                    },
                    onImportClick = { showImportDialog = true },
                    onTitleClick = { showSwitcher = true },
                    onRenameClick = { showRenameListDialog = true },
                    onDeleteClick = { showDeleteListDialog = true },
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addItem(
                                GroceryItem(
                                    name = name,
                                    quantity = quantity,
                                    category = selectedCategory
                                )
                            )
                            name = ""
                            quantity = ""
                            selectedCategory = "General"
                            isInputExpanded = false
                        } else {
                            isInputExpanded = true
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {

                // Add item card
                AddItemCard(
                    expanded = isInputExpanded,
                    name = name,
                    onNameChange = { name = it },
                    quantity = quantity,
                    onQuantityChange = { quantity = it },
                    selectedCategory = selectedCategory,
                    onCategoryChange = { selectedCategory = it },
                    onAddClick = {
                        if (name.isNotBlank()) {
                            viewModel.addItem(
                                GroceryItem(
                                    name = name,
                                    quantity = quantity,
                                    category = selectedCategory
                                )
                            )
                            name = ""
                            quantity = ""
                            selectedCategory = "General"
                            isInputExpanded = false
                        }
                    }

                )
                if (isInputExpanded) {
                    Spacer(Modifier.height(16.dp))
                }

                // Filters Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category filter dropdown (takes available space)
                    Box(modifier = Modifier.weight(1f)) {
                        CategoryDropdown(
                            label = "Filter",
                            options = listOf("All") + Categories,
                            selected = filterCategory,
                            onSelect = { filterCategory = it }
                        )
                    }

                    // Hide completed toggle
                    FilterChip(
                        selected = hideChecked,
                        onClick = { hideChecked = !hideChecked },
                        label = { Text("Hide done") }
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Progress Stats
                ProgressStats(
                    totalItems,
                    checkedItems
                )
                Spacer(Modifier.height(16.dp))

                // List or Empty State
                if (visibleItems.isEmpty()) {
                    EmptyState(
                        hasItems = items.isNotEmpty(),
                        onAddClick = { isInputExpanded = true }
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = visibleItems,
                            key = { it.id }
                        ) { item ->
                            SwipeableGroceryRow(
                                item = item,
                                onToggle = { viewModel.toggleItem(item) },
                                onRemove = { viewModel.removeItem(item) },
                                onEdit = { editingItem = item }
                            )
                        }
                    }
                }
            }
        } // Scaffold
    } // Surface
}
