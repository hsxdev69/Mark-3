package com.movievortex.android.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movievortex.android.controller.LibraryController
import com.movievortex.android.model.MovieVortexMedia
import com.movievortex.android.ui.components.PosterCard
import com.movievortex.android.ui.components.VortexIcons
import com.movievortex.android.ui.theme.VortexColors

@Composable
fun WatchlistScreen(
    libraryController: LibraryController,
    onMediaClick: (MovieVortexMedia) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by libraryController.uiState.collectAsState()

    LaunchedEffect(Unit) {
        libraryController.loadMyList()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VortexColors.BackgroundOled)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = VortexIcons.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "My Watchlist",
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${state.items.size} titles",
                color = VortexColors.TextMuted,
                fontSize = 13.sp,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(end = 12.dp)
            )
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = VortexColors.NeonCrimson,
                    strokeWidth = 2.5.dp,
                    modifier = Modifier.size(36.dp)
                )
            }
        } else if (state.items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = VortexIcons.Bookmark,
                        contentDescription = null,
                        tint = VortexColors.TextMuted,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your Watchlist is Empty",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Explore movies and series on Home or Search, and tap '+ My List' to bookmark titles here.",
                        color = VortexColors.TextMuted,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.items, key = { it.id }) { media ->
                    PosterCard(
                        media = media,
                        onClick = { onMediaClick(media) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
