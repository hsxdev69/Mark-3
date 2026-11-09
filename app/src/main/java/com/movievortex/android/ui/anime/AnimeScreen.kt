package com.movievortex.android.ui.anime

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movievortex.android.controller.AnimeController
import com.movievortex.android.model.MovieVortexMedia
import com.movievortex.android.ui.components.PosterCard
import com.movievortex.android.ui.home.ContentRowSection
import com.movievortex.android.ui.home.FeaturedHero
import com.movievortex.android.ui.theme.VortexColors

@Composable
fun AnimeScreen(
    animeController: AnimeController,
    onMediaClick: (MovieVortexMedia) -> Unit,
    onPlayClick: (MovieVortexMedia) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by animeController.uiState.collectAsState()
    val scrollState = rememberLazyListState()

    if (state.isLoading) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(VortexColors.PureBlack),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    color = VortexColors.SubtlePurple,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading Anime Universe...",
                    color = VortexColors.MutedText,
                    fontSize = 13.sp
                )
            }
        }
        return
    }

    LazyColumn(
        state = scrollState,
        modifier = modifier
            .fillMaxSize()
            .background(VortexColors.PureBlack),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Featured Anime Hero
        if (state.heroItems.isNotEmpty()) {
            item {
                FeaturedHero(
                    items = state.heroItems,
                    onMediaClick = onMediaClick,
                    onPlayClick = onPlayClick
                )
            }
        }

        // 2. Anime Curated Rows (Top Airing, Popular Shonen, Isekai, etc.)
        items(state.rows) { row ->
            Spacer(modifier = Modifier.height(24.dp))
            ContentRowSection(
                row = row,
                onMediaClick = onMediaClick
            )
        }

        // 3. Anime Explore More Grid
        if (state.exploreGrid.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(28.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Explore Anime Catalog",
                        color = VortexColors.PrimaryText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Broadcast simulcasts, movies, OVAs, and classic masterpieces",
                        color = VortexColors.MutedText,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            val chunked = state.exploreGrid.chunked(3)
            items(chunked) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { media ->
                        PosterCard(
                            media = media,
                            onClick = { onMediaClick(media) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Auto-load trigger
            item {
                LaunchedEffect(Unit) {
                    animeController.loadMoreExplore()
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isExploreLoading) {
                        CircularProgressIndicator(
                            color = VortexColors.SubtlePurple,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
