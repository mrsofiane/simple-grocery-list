package me.mrsofiane.simplegrocerylist.ui.dialogs


import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun ImportDialog(
    onDismiss: () -> Unit,
    onImport: (String) -> Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var importJsonText by remember { mutableStateOf("") }

    val jsonFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val json = inputStream?.bufferedReader()?.use { reader -> reader.readText() } ?: ""
                val count = onImport(json)
                if (count > 0) {
                    Toast.makeText(context, "Imported $count items", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Invalid file format", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Could not read file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text("Import List") },
        text = {
            Column {
                Text(
                    "Paste JSON data or import from file:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = importJsonText,
                    onValueChange = { importJsonText = it },
                    label = { Text("JSON data") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    maxLines = 8
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { jsonFilePicker.launch("*/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Import from File")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (importJsonText.isNotBlank()) {
                        val count = onImport(importJsonText)
                        if (count > 0) {
                            Toast.makeText(context, "Imported $count items", Toast.LENGTH_SHORT)
                                .show()
                            onDismiss()
                        } else {
                            Toast.makeText(context, "Invalid format", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            ) {
                Text("Import")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
