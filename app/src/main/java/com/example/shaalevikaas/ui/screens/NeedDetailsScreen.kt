package com.example.shaalevikaas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.shaalevikaas.model.Pledge
import com.example.shaalevikaas.viewmodel.AuthViewModel
import com.example.shaalevikaas.viewmodel.NeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeedDetailsScreen(
    needId: String,
    viewModel: NeedViewModel,
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val needs by viewModel.needs.collectAsState()
    val need = needs.find { it.id == needId }
    val user by authViewModel.userState.collectAsState()
    
    var showPledgeDialog by remember { mutableStateOf(false) }
    var pledgeAmount by remember { mutableStateOf("") }
    val loading by viewModel.loading

    if (need == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Project Details") },
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
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = need.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = need.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Badge(
                        containerColor = when(need.status) {
                            "Open" -> Color(0xFF4CAF50)
                            "In Progress" -> Color(0xFFFF9800)
                            else -> Color(0xFF2196F3)
                        }
                    ) {
                        Text(need.status, color = Color.White, modifier = Modifier.padding(4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = need.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Progress Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Funding Progress", fontWeight = FontWeight.Bold)
                            Text("${(need.progress * 100).toInt()}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { need.progress },
                            modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Raised: ₹${need.pledgedAmount.toInt()}", style = MaterialTheme.typography.bodyMedium)
                            Text("Goal: ₹${need.cost.toInt()}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (need.beforeImage.isNotEmpty() || need.afterImage.isNotEmpty()) {
                    Text(
                        text = "Project Photos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (need.beforeImage.isNotEmpty()) {
                            PhotoCard("Before", need.beforeImage, Modifier.weight(1f))
                        }
                        if (need.afterImage.isNotEmpty()) {
                            PhotoCard("After", need.afterImage, Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                if (need.status != "Completed" && user != null) {
                    Button(
                        onClick = { showPledgeDialog = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Pledge Support", fontSize = 18.sp)
                    }
                }
            }
        }
    }

    if (showPledgeDialog) {
        AlertDialog(
            onDismissRequest = { showPledgeDialog = false },
            title = { Text("Make a Pledge") },
            text = {
                Column {
                    Text("Thank you for supporting your school! Please enter the amount you wish to contribute.")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = pledgeAmount,
                        onValueChange = { pledgeAmount = it },
                        label = { Text("Amount (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = pledgeAmount.toDoubleOrNull() ?: 0.0
                        if (amount > 0 && user != null) {
                            val pledge = Pledge(
                                alumniId = user!!.uid,
                                alumniName = user!!.name,
                                needId = need.id,
                                amount = amount
                            )
                            viewModel.submitPledge(pledge) {
                                showPledgeDialog = false
                                pledgeAmount = ""
                            }
                        }
                    },
                    enabled = !loading
                ) {
                    if (loading) CircularProgressIndicator(modifier = Modifier.size(20.dp)) else Text("Confirm Pledge")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPledgeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PhotoCard(label: String, imageUrl: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp)
        )
    }
}
