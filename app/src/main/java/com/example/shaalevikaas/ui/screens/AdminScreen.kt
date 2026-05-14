package com.example.shaalevikaas.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.shaalevikaas.model.Need
import com.example.shaalevikaas.viewmodel.NeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    needId: String?,
    needViewModel: NeedViewModel,
    onBack: () -> Unit
) {
    val needs by needViewModel.needs.collectAsState()
    val existingNeed = needs.find { it.id == needId }

    var title by remember { mutableStateOf(existingNeed?.title ?: "") }
    var description by remember { mutableStateOf(existingNeed?.description ?: "") }
    var cost by remember { mutableStateOf(existingNeed?.cost?.toString() ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    
    val loading by needViewModel.loading

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (needId == null) "Add Need" else "Edit Need") },
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
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = cost,
                onValueChange = { cost = it },
                label = { Text("Cost (Goal)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(onClick = { launcher.launch("image/*") }) {
                Text("Select Image")
            }
            
            val displayImage = imageUri ?: existingNeed?.imageUrl?.let { if (it.isNotEmpty()) Uri.parse(it) else null }
            
            displayImage?.let {
                AsyncImage(
                    model = it,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = {
                        val need = (existingNeed ?: Need()).copy(
                            title = title,
                            description = description,
                            cost = cost.toDoubleOrNull() ?: 0.0
                        )
                        if (needId == null) {
                            needViewModel.addNeed(need, imageUri, null) { onBack() }
                        } else {
                            needViewModel.updateNeed(need, imageUri, null) { onBack() }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (needId == null) "Create Need" else "Save Changes")
                }
            }
        }
    }
}
