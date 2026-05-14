package com.example.shaalevikaas.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.shaalevikaas.model.Need
import com.example.shaalevikaas.viewmodel.NeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNeedScreen(
    needId: String?,
    viewModel: NeedViewModel,
    onBack: () -> Unit
) {
    val needs by viewModel.needs.collectAsState()
    val existingNeed = needs.find { it.id == needId }

    var title by remember { mutableStateOf(existingNeed?.title ?: "") }
    var description by remember { mutableStateOf(existingNeed?.description ?: "") }
    var cost by remember { mutableStateOf(existingNeed?.cost?.toString() ?: "") }
    var status by remember { mutableStateOf(existingNeed?.status ?: "Open") }
    
    var mainImageUri by remember { mutableStateOf<Uri?>(null) }
    var beforeImageUri by remember { mutableStateOf<Uri?>(null) }
    var afterImageUri by remember { mutableStateOf<Uri?>(null) }
    
    val loading by viewModel.loading
    val error by viewModel.error

    val mainImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { mainImageUri = it }
    val beforeImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { beforeImageUri = it }
    val afterImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { afterImageUri = it }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (needId == null) "Add School Need" else "Edit Need") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (error != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = error!!, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.weight(1f))
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss", color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = cost,
                onValueChange = { cost = it },
                label = { Text("Estimated Cost (₹)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            
            if (needId != null) {
                Text("Status", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = status == "Open",
                        onClick = { status = "Open" },
                        label = { Text("Open") }
                    )
                    FilterChip(
                        selected = status == "In Progress",
                        onClick = { status = "In Progress" },
                        label = { Text("In Progress") }
                    )
                    FilterChip(
                        selected = status == "Completed",
                        onClick = { status = "Completed" },
                        label = { Text("Completed") }
                    )
                }
            }

            HorizontalDivider()
            
            Text("Images", style = MaterialTheme.typography.titleMedium)
            
            ImagePickerSection("Main Banner Image", mainImageUri, existingNeed?.imageUrl) { mainImageLauncher.launch("image/*") }
            ImagePickerSection("Before Image (Condition)", beforeImageUri, existingNeed?.beforeImage) { beforeImageLauncher.launch("image/*") }
            if (status == "Completed") {
                ImagePickerSection("After Image (Completion)", afterImageUri, existingNeed?.afterImage) { afterImageLauncher.launch("image/*") }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = {
                        // Sanitize cost input (remove non-numeric chars except decimal)
                        val sanitizedCost = cost.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
                        val need = (existingNeed ?: Need()).copy(
                            title = title,
                            description = description,
                            cost = sanitizedCost,
                            status = status
                        )
                        if (needId == null) {
                            viewModel.addNeed(need, mainImageUri, beforeImageUri) { onBack() }
                        } else {
                            viewModel.updateNeed(need, mainImageUri, afterImageUri) { onBack() }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = title.isNotEmpty() && cost.isNotEmpty()
                ) {
                    Text(if (needId == null) "Post Need" else "Update Project")
                }
            }
        }
    }
}

@Composable
fun ImagePickerSection(label: String, selectedUri: Uri?, existingUrl: String?, onClick: () -> Unit) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            if (selectedUri != null || !existingUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = selectedUri ?: existingUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Button(
                onClick = onClick,
                modifier = Modifier.align(androidx.compose.ui.Alignment.Center),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f))
            ) {
                Text(if (selectedUri != null || !existingUrl.isNullOrEmpty()) "Change Image" else "Select Image")
            }
        }
    }
}
