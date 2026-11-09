package com.movievortex.android.ui.library

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.lagradost.cloudstream3.utils.DataStoreHelper
import com.movievortex.android.controller.DownloadController
import com.movievortex.android.controller.LibraryController
import com.movievortex.android.model.MovieVortexMedia
import com.movievortex.android.ui.components.PosterCard
import com.movievortex.android.ui.components.VortexIcons
import com.movievortex.android.ui.downloads.formatFileSize
import com.movievortex.android.ui.theme.ManropeFontFamily

enum class LibraryTab(val title: String) {
    Watchlist("Watchlist"),
    History("Recently Viewed"),
    Downloads("Downloads")
}

@Composable
fun LibraryScreen(
    libraryController: LibraryController,
    downloadController: DownloadController,
    onMediaClick: (MovieVortexMedia) -> Unit,
    onPlayOffline: (filePath: String, title: String, id: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(LibraryTab.Watchlist) }
    val watchlistState by libraryController.uiState.collectAsState()
    val downloadState by downloadController.uiState.collectAsState()

    LaunchedEffect(Unit) {
        libraryController.loadMyList()
    }

    val historyItems = remember {
        val resumeMap = DataStoreHelper.getAllResumeStateIds() ?: emptyList()
        val list = mutableListOf<MovieVortexMedia>()
        resumeMap.forEach { resumeItem ->
            val pos = DataStoreHelper.getViewPos(resumeItem)
            val bookmarked = DataStoreHelper.getBookmarkedData(resumeItem)
            val title = bookmarked?.name ?: "Watched Item"
            val poster = bookmarked?.posterUrl
            val progress = if (pos != null && pos.duration > 0) {
                (pos.position.toFloat() / pos.duration.toFloat()).coerceIn(0f, 1f)
            } else 0f

            list.add(
                MovieVortexMedia(
                    id = resumeItem.toString(),
                    title = title,
                    poster = poster,
                    backdrop = poster,
                    watchProgress = progress,
                    rawUrl = bookmarked?.url ?: resumeItem.toString(),
                    providerApiName = bookmarked?.apiName ?: ""
                )
            )
        }
        list
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F141C))
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // 1. Library Header & Modern Glassmorphic Category Pills
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Text(
                text = "Library",
                color = Color.White,
                fontSize = 26.sp,
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.6).sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Glassmorphic Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(23.dp))
                    .background(Color(0x382A3644))
                    .border(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(
                                Color(0x3DFFFFFF),
                                Color(0x14FFFFFF)
                            )
                        ),
                        RoundedCornerShape(23.dp)
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LibraryTab.values().forEach { tab ->
                    val isSelected = tab == selectedTab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { selectedTab = tab }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.title,
                            color = if (isSelected) Color(0xFF0F141C) else Color(0xFF8E95A3),
                            fontSize = 12.sp,
                            fontFamily = ManropeFontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 2. Tab Content View
        Crossfade(targetState = selectedTab, label = "library_tab_crossfade") { tab ->
            when (tab) {
                LibraryTab.Watchlist -> {
                    if (watchlistState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(28.dp))
                        }
                    } else if (watchlistState.items.isEmpty()) {
                        EmptyLibraryState(
                            icon = VortexIcons.VideoHeart,
                            title = "Your Watchlist is Empty",
                            subtitle = "Save movies and series you want to watch later."
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 135.dp),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 110.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(watchlistState.items) { item ->
                                PosterCard(
                                    media = item,
                                    onClick = { onMediaClick(item) }
                                )
                            }
                        }
                    }
                }

                LibraryTab.History -> {
                    if (historyItems.isEmpty()) {
                        EmptyLibraryState(
                            icon = VortexIcons.History,
                            title = "No Watch History",
                            subtitle = "Movies and episodes you watch will appear here."
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 135.dp),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 110.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(historyItems) { item ->
                                PosterCard(
                                    media = item,
                                    onClick = { onMediaClick(item) }
                                )
                            }
                        }
                    }
                }

                LibraryTab.Downloads -> {
                    if (downloadState.items.isEmpty()) {
                        EmptyLibraryState(
                            icon = VortexIcons.Download,
                            title = "No Downloaded Movies",
                            subtitle = "Downloaded media will be available for offline playback here."
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 110.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(downloadState.items) { dl ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0x33253240))
                                        .border(0.8.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
                                        .clickable {
                                            if (!dl.localFilePath.isNullOrBlank()) {
                                                onPlayOffline(dl.localFilePath, dl.title, dl.id)
                                            }
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp, 80.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF141923))
                                    ) {
                                        AsyncImage(
                                            model = dl.poster,
                                            contentDescription = dl.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = dl.title,
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontFamily = ManropeFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${formatFileSize(dl.downloadedBytes)} / ${formatFileSize(dl.fileSizeBytes)}",
                                            color = Color(0xFF8E95A3),
                                            fontSize = 12.sp,
                                            fontFamily = ManropeFontFamily
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x33FFFFFF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = VortexIcons.Play,
                                            contentDescription = "Play Offline",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyLibraryState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 90.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color(0x22354354))
                    .border(1.dp, Color(0x22FFFFFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF8E95A3),
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = Color(0xFF8E95A3),
                fontSize = 13.sp,
                fontFamily = ManropeFontFamily,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}
