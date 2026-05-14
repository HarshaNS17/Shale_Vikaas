package com.example.shaalevikaas.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shaalevikaas.ui.components.NeedCard
import com.example.shaalevikaas.viewmodel.AuthViewModel
import com.example.shaalevikaas.viewmodel.NeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    needViewModel: NeedViewModel,
    authViewModel: AuthViewModel,
    onNeedClick: (String) -> Unit,
    onAdminClick: () -> Unit
) {
    val needs by needViewModel.needs.collectAsState()
    val isLoggedIn = authViewModel.isUserLoggedIn()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shaale-Vikas Needs") },
                actions = {
                    IconButton(onClick = onAdminClick) {
                        Icon(Icons.Default.Person, contentDescription = "Admin")
                    }
                }
            )
        },
        floatingActionButton = {
            if (isLoggedIn) {
                FloatingActionButton(onClick = onAdminClick) {
                    Icon(Icons.Default.Add, contentDescription = "Add Need")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(needs) { need ->
                NeedCard(need = need, onClick = { onNeedClick(need.id) })
            }
        }
    }
}
