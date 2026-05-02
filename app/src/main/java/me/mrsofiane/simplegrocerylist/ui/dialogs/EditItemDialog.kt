package me.mrsofiane.simplegrocerylist.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.mrsofiane.simplegrocerylist.model.GroceryItem
import me.mrsofiane.simplegrocerylist.ui.components.CategoryDropdown

@Composable
fun EditItemDialog(
    item: GroceryItem,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (GroceryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var editName by remember { mutableStateOf(item.name) }
    var editQuantity by remember { mutableStateOf(item.quantity) }
    var editCategory by remember { mutableStateOf(item.category) }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text("Edit Item") },
        text = {
            Column {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Item name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = editQuantity,
                    onValueChange = { editQuantity = it },
                    label = { Text("Quantity") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                CategoryDropdown(
                    label = "Category",
                    options = categories,
                    selected = editCategory,
                    onSelect = { editCategory = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (editName.isNotBlank()) {
                        onSave(
                            item.copy(
                                name = editName,
                                quantity = editQuantity,
                                category = editCategory
                            )
                        )
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}