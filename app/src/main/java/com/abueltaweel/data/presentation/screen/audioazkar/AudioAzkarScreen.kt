package com.abueltaweel.presentation.screen.audioazkar

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abueltaweel.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun AudioAzkarScreen(viewModel: AudioAzkarViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()

    val filteredTracks = remember(state.selectedCategory, state.tracks) {
        state.tracks.filter { it.category == state.selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(listOf(Color(0xFF1B3A4B), Color(0xFF0D1B2A)))
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(R.drawable.ic_azkar),
                    contentDescription = null,
                    tint = Color(0xFFC9A84C),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "الأذكار الصوتية",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Category tabs
        LazyRow(
            modifier = Modifier.padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(AzkarCategory.entries) { category ->
                val isSelected = state.selectedCategory == category
                val bgColor by animateColorAsState(
                    if (isSelected) Color(0xFFC9A84C) else Color(0xFF1B3A4B),
                    label = "tab_color"
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(bgColor)
                        .clickable { viewModel.selectCategory(category) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.titleAr,
                        color = if (isSelected) Color.Black else Color.White,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Tracks list
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredTracks, key = { it.id }) { track ->
                val isCurrentlyPlaying = state.currentTrack?.id == track.id && state.isPlaying
                TrackItem(
                    track = track,
                    isPlaying = isCurrentlyPlaying,
                    isLoading = isCurrentlyPlaying && state.isLoading,
                    onClick = {
                        if (isCurrentlyPlaying) viewModel.stopTrack()
                        else viewModel.playTrack(track)
                    }
                )
            }
        }

        // Mini player
        if (state.currentTrack != null) {
            MiniPlayer(
                track = state.currentTrack!!,
                isPlaying = state.isPlaying,
                onStop = { viewModel.stopTrack() }
            )
        }
    }
}

@Composable
private fun TrackItem(
    track: AzkarTrack,
    isPlaying: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        if (isPlaying) Color(0x33C9A84C) else Color(0xFF1B3A4B),
        label = "track_bg"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    if (isPlaying) Color(0xFFC9A84C) else Color(0xFF2A4B5A),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    painter = painterResource(
                        if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                    ),
                    contentDescription = null,
                    tint = if (isPlaying) Color.Black else Color(0xFFC9A84C),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.titleAr,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = track.reciter,
                color = Color(0xFFC9A84C),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun MiniPlayer(track: AzkarTrack, isPlaying: Boolean, onStop: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1B3A4B))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.titleAr,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = track.reciter, color = Color(0xFFC9A84C), fontSize = 12.sp)
        }
        IconButton(onClick = onStop) {
            Icon(
                painter = painterResource(R.drawable.ic_pause),
                contentDescription = "إيقاف",
                tint = Color(0xFFC9A84C),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
