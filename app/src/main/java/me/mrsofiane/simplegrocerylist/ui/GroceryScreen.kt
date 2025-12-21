package me.mrsofiane.simplegrocerylist.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import me.mrsofiane.simplegrocerylist.model.GroceryItem
import me.mrsofiane.simplegrocerylist.ui.theme.CategoryDairy
import me.mrsofiane.simplegrocerylist.ui.theme.CategoryFruits
import me.mrsofiane.simplegrocerylist.ui.theme.CategoryGeneral
import me.mrsofiane.simplegrocerylist.ui.theme.CategoryHousehold
import me.mrsofiane.simplegrocerylist.ui.theme.CategoryMeat
import me.mrsofiane.simplegrocerylist.ui.theme.CategoryVegetables
import me.mrsofiane.simplegrocerylist.ui.theme.DeleteRed
import me.mrsofiane.simplegrocerylist.viewmodel.GroceryViewModel

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryScreen(viewModel: GroceryViewModel) {

    val items by viewModel.items.collectAsState()

    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("General") }
    var hideChecked by remember { mutableStateOf(false) }
    var filterCategory by remember { mutableStateOf("All") }
    var isInputExpanded by remember { mutableStateOf(false) }

    val categories = listOf("General", "Fruits", "Vegetables", "Dairy", "Meat", "Household")

    val visibleItems = items.filter {
        (if (hideChecked) !it.isChecked else true) &&
                (filterCategory == "All" || it.category == filterCategory)
    }

    // Progress stats
    val totalItems = items.size
    val checkedItems = items.count { it.isChecked }
    val progress = if (totalItems > 0) checkedItems.toFloat() / totalItems else 0f

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Grocery List") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
                        // Toggle input expansion button
                        IconButton(onClick = { isInputExpanded = !isInputExpanded }) {
                            Icon(
                                imageVector = if (isInputExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isInputExpanded) "Collapse" else "Expand"
                            )
                        }
                    }
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
                        // Open input if trying to add with empty name
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

            // Collapsible Add item card
            AnimatedVisibility(
                visible = isInputExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Item name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = { Text("Quantity") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(Modifier.height(8.dp))

                        CategoryDropdown(
                            label = "Category",
                            options = categories,
                            selected = selectedCategory,
                            onSelect = { selectedCategory = it }
                        )

                        Spacer(Modifier.height(12.dp))

                        Button(
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
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Add Item")
                        }
                    }
                }
            }

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
                        options = listOf("All") + categories,
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
            if (totalItems > 0) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$checkedItems of $totalItems completed",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

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
                            onRemove = { viewModel.removeItem(item) }
                        )
                    }
                }
            }
        }
    } // Scaffold
    } // Surface
}

@Composable
fun EmptyState(
    hasItems: Boolean,
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (hasItems) "All items are hidden" else "Your list is empty",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (hasItems) "Uncheck \"Hide done\" to see completed items" else "Tap + to add your first item",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableGroceryRow(
    item: GroceryItem,
    onToggle: () -> Unit,
    onRemove: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onRemove()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            // Delete background
            val color by animateColorAsState(
                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
                    DeleteRed else Color.Transparent,
                label = "delete_bg_color"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true
    ) {
        GroceryRow(
            item = item,
            onToggle = onToggle,
            onRemove = onRemove
        )
    }
}

@Composable
fun GroceryRow(
    item: GroceryItem,
    onToggle: () -> Unit,
    onRemove: () -> Unit
) {
    val categoryColor = getCategoryColor(item.category)
    val alpha by animateFloatAsState(
        targetValue = if (item.isChecked) 0.6f else 1f,
        label = "item_alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Category color indicator bar
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(72.dp)
                    .background(categoryColor)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        color = if (item.isChecked)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(item.category),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = categoryColor
                        )
                        Text(
                            "${item.quantity.ifBlank { "1" }} • ${item.category}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = item.isChecked,
                        onCheckedChange = { onToggle() }
                    )
                    IconButton(onClick = onRemove) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Remove",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            singleLine = true
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (option != "All") {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(getCategoryColor(option))
                                )
                            }
                            Text(option)
                        }
                    },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
