package com.example.shaalevikaas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.shaalevikaas.model.Need
import com.example.shaalevikaas.ui.components.NeedCard
import com.example.shaalevikaas.viewmodel.AuthViewModel
import com.example.shaalevikaas.viewmodel.NeedViewModel

@Composable
fun AlumniDashboard(
    viewModel: NeedViewModel,
    authViewModel: AuthViewModel,
    onNeedClick: (String) -> Unit
) {
    val user by authViewModel.userState.collectAsState()
    val needs by viewModel.needs.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val hallOfFame by viewModel.hallOfFame.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Welcome Banner
        item {
            WelcomeBanner(userName = user?.name ?: "Alumni")
        }

        // Announcements Section
        if (announcements.isNotEmpty()) {
            item {
                SectionHeader(title = "Latest Updates")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(announcements.take(5)) { announcement ->
                        AnnouncementCard(announcement = announcement)
                    }
                }
            }
        }

        // School Needs Section
        item {
            SectionHeader(title = "School Infrastructure Needs", onSeeAll = {})
        }
        items(needs.take(3)) { need ->
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                NeedCard(need = need, onClick = { onNeedClick(need.id) })
            }
        }

        // Hall of Fame Highlight
        if (hallOfFame.isNotEmpty()) {
            item {
                SectionHeader(title = "Hall of Fame", onSeeAll = {})
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(hallOfFame.take(5)) { alumni ->
                        DonorHighlightCard(alumni = alumni)
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeBanner(userName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1976D2), Color(0xFF0D47A1))
                )
            )
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                text = "Welcome back,",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = userName,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Let's build a better future for our school together.",
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAll: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        onSeeAll?.let {
            TextButton(onClick = it) {
                Text("See All")
            }
        }
    }
}

@Composable
fun AnnouncementCard(announcement: com.example.shaalevikaas.model.Announcement) {
    Card(
        modifier = Modifier.width(280.dp).height(160.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box {
            AsyncImage(
                model = if (announcement.imageUrl.isNotEmpty()) announcement.imageUrl else "https://images.unsplash.com/photo-1544652478-6653e09f18a2?q=80&w=2070&auto=format&fit=crop",
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))))
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = announcement.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DonorHighlightCard(alumni: com.example.shaalevikaas.model.Alumni) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        AsyncImage(
            model = if (alumni.profileImage.isNotEmpty()) alumni.profileImage else "https://ui-avatars.com/api/?name=${alumni.name}&background=random",
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(35.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = alumni.name.split(" ").first(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
        Text(
            text = "₹${alumni.donatedAmount.toInt()}",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFD4AF37), // Gold
            fontWeight = FontWeight.Bold
        )
    }
}
