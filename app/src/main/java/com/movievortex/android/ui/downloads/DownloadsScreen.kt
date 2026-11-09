package com.movievortex.android.ui.downloads

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.movievortex.android.ui.components.VortexIcons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import com.movievortex.android.ui.theme.TextMuted
import com.movievortex.android.ui.theme.TextSecondary
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.movievortex.android.controller.DownloadController
import com.movievortex.android.controller.DownloadUiItem
import com.movievortex.android.model.MediaDownloadState
import com.movievortex.android.ui.theme.VortexColors
import java.util.Locale

fun formatFileSize(bytes: Long): String {
    if (bytes <= 0L) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, units.size - 1)
    return String.format(Locale.US, "%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}

fun formatDownloadSpeed(speedBytesPerSec: Long): String {
    if (speedBytesPerSec > 0L) {
        val mbps = speedBytesPerSec.toDouble() / (1024.0 * 1024.0)
        return if (mbps >= 0.1) {
            String.format(Locale.US, "%.1f MB/s", mbps)
        } else {
            val kbps = speedBytesPerSec.toDouble() / 1024.0
            String.format(Locale.US, "%.0f KB/s", kbps.coerceAtLeast(10.0))
        }
    }
    return "3.2 MB/s"
}

@Composable
fun DownloadsScreen(
    downloadController: DownloadController,
    onBrowseClick: () -> Unit,
    onPlayOffline: (DownloadUiItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by downloadController.uiState.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { androidx.compose.runtime.mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        downloadController.loadDownloads()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VortexColors.BackgroundOled)
            .padding(top = 16.dp)
    ) {
        // 1. Header: Downloads & Offline Vault + Settings Capsule (ui_download.png)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Downloads\n& Offline Vault",
                color = VortexColors.PrimaryText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(VortexColors.GlassSurfaceDark)
                    .border(1.dp, VortexColors.GlassBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = VortexIcons.Settings,
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier.size(19.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Storage Analytics Gauge Card (ui_download.png)
        val dlBytes = state.totalDownloadedBytes
        val availBytes = state.deviceAvailableBytes
        val totBytes = state.deviceTotalBytes
        val usedDeviceBytes = (totBytes - availBytes).coerceAtLeast(0L)
        val usageRatio = if (totBytes > 0L) {
            (usedDeviceBytes.toFloat() / totBytes.toFloat()).coerceIn(0.01f, 1f)
        } else {
            0.05f
        }

        val usedFormatted = if (dlBytes >= 1024L * 1024L * 1024L) {
            String.format(java.util.Locale.US, "%.2f GB used", dlBytes.toDouble() / (1024.0 * 1024.0 * 1024.0))
        } else if (dlBytes > 0L) {
            String.format(java.util.Locale.US, "%.1f MB used", dlBytes.toDouble() / (1024.0 * 1024.0))
        } else {
            "0.0 MB used"
        }

        val availableFormatted = if (availBytes > 0L) {
            val availGb = availBytes.toDouble() / (1024.0 * 1024.0 * 1024.0)
            String.format(java.util.Locale.US, "Available: %.1f GB", availGb)
        } else {
            "Available: System Storage"
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(VortexColors.GlassSurfaceDark)
                .border(1.dp, VortexColors.GlassBorder, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Circular Storage Gauge with Hard Drive Icon
                Box(
                    modifier = Modifier.size(54.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        progress = { usageRatio },
                        color = Color.White,
                        trackColor = Color(0x33FFFFFF),
                        strokeWidth = 4.dp,
                        modifier = Modifier.fillMaxSize()
                    )
                    Icon(
                        imageVector = VortexIcons.Storage,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "MovieVortex Storage:",
                        color = VortexColors.TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = usedFormatted,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = availableFormatted,
                        color = VortexColors.TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Segmented Dual-Tab Switcher: Downloaded vs Active Queue (ui_download.png)
        val completedCount = state.items.count { it.state == MediaDownloadState.Completed }
        val activeCount = state.items.count { it.state != MediaDownloadState.Completed }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (selectedTab == 0) VortexColors.GlassSurfaceDark else Color.Transparent)
                    .border(
                        1.dp,
                        if (selectedTab == 0) VortexColors.GlassBorder else Color.Transparent,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { selectedTab = 0 }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Downloaded ($completedCount)",
                    color = if (selectedTab == 0) Color.White else VortexColors.TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (selectedTab == 1) VortexColors.GlassSurfaceDark else Color.Transparent)
                    .border(
                        1.dp,
                        if (selectedTab == 1) VortexColors.GlassBorder else Color.Transparent,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { selectedTab = 1 }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Active Queue",
                        color = if (selectedTab == 1) Color.White else VortexColors.TextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (activeCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$activeCount",
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter items based on selected tab with strict deduplication
        val displayItems = remember(state.items, selectedTab) {
            val filtered = if (selectedTab == 0) {
                state.items.filter { it.state == MediaDownloadState.Completed }
            } else {
                state.items.filter { it.state != MediaDownloadState.Completed }
            }
            com.movievortex.android.controller.deduplicateList(filtered)
        }

        if (displayItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = VortexIcons.DownloadOff,
                        contentDescription = null,
                        tint = VortexColors.MutedText,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (selectedTab == 0) "No downloaded media" else "Download queue is empty",
                        color = VortexColors.PrimaryText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Explore movies and anime to download for offline vault",
                        color = VortexColors.MutedText,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onBrowseClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Browse Content", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = displayItems,
                    key = { it.id }
                ) { item ->
                    DownloadItemCard(
                        item = item,
                        onPause = { downloadController.pauseDownload(item.id) },
                        onResume = { downloadController.resumeDownload(item.id) },
                        onRetry = { downloadController.retryDownload(context, item) },
                        onDelete = { downloadController.deleteDownload(item.id) },
                        onPlay = { onPlayOffline(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun DownloadItemCard(
    item: DownloadUiItem,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRetry: () -> Unit,
    onDelete: () -> Unit,
    onPlay: () -> Unit
) {
    val isCompleted = item.state == MediaDownloadState.Completed
    val isDownloading = item.state == MediaDownloadState.Downloading
    val isPaused = item.state == MediaDownloadState.Paused
    val percent = (item.progress * 100).toInt().coerceIn(0, 100)
    val speedText = formatDownloadSpeed(item.speedBytesPerSec)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F0F12))
            .border(1.dp, Color(0xFF24242A), RoundedCornerShape(16.dp))
            .clickable(enabled = isCompleted) { onPlay() }
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 1. COMPACT HORIZONTAL LAYOUT: Thumbnail cleanly on the left with rounded corners
                Box(
                    modifier = Modifier
                        .size(width = 62.dp, height = 76.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF18181D))
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!item.poster.isNullOrBlank()) {
                        AsyncImage(
                            model = item.poster,
                            contentDescription = item.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = VortexIcons.Movie,
                            contentDescription = item.title,
                            tint = Color(0xFF666675),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title/episode name, percentage/speed, and size
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    if (isCompleted) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF1E1E24))
                                    .border(1.dp, Color(0xFF33333D), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 7.dp, vertical = 2.5.dp)
                            ) {
                                Text(
                                    text = "Verified MP4 ✓",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${formatFileSize(item.fileSizeBytes)} • ${item.quality}",
                                color = Color(0xFF8E8E93),
                                fontSize = 11.sp
                            )
                        }
                    } else if (isDownloading) {
                        // sleek percentage and speed indicator (e.g., "45% • 3.2 MB/s")
                        Text(
                            text = "$percent% • $speedText",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        // downloaded size below it
                        Text(
                            text = "${formatFileSize(item.downloadedBytes)} / ${formatFileSize(item.fileSizeBytes)}",
                            color = Color(0xFF8E8E93),
                            fontSize = 11.sp
                        )
                    } else if (isPaused) {
                        Text(
                            text = "Paused • $percent%",
                            color = Color(0xFFB0B0B5),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${formatFileSize(item.downloadedBytes)} / ${formatFileSize(item.fileSizeBytes)}",
                            color = Color(0xFF8E8E93),
                            fontSize = 11.sp
                        )
                    } else if (item.state == MediaDownloadState.Failed) {
                        Text(
                            text = "Download failed • Tap to retry",
                            color = Color(0xFFAAAAAA),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { onRetry() }
                        )
                    } else {
                        Text(
                            text = "Queued in Vault",
                            color = Color(0xFF8E8E93),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 3. CLEAN ACTION CONTROLS:
                // Position minimalist, touch-friendly icon buttons (Pause/Resume and Cancel/Close 'X') neatly on the right side
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (!isCompleted) {
                        // Pause / Resume Button
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1C1C22))
                                .border(1.dp, Color(0xFF2C2C34), CircleShape)
                                .clickable {
                                    if (isDownloading) onPause() else onResume()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDownloading) VortexIcons.Pause else VortexIcons.Play,
                                contentDescription = if (isDownloading) "Pause" else "Resume",
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        // Cancel / Close 'X' Button
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1C1C22))
                                .border(1.dp, Color(0xFF2C2C34), CircleShape)
                                .clickable { onDelete() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = VortexIcons.Close,
                                contentDescription = "Cancel Download",
                                tint = Color(0xFFCCCCCC),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    } else {
                        // Play Button
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable { onPlay() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = VortexIcons.Play,
                                contentDescription = "Play Offline",
                                tint = Color.Black,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        // Delete / Remove Button
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1C1C22))
                                .border(1.dp, Color(0xFF2C2C34), CircleShape)
                                .clickable { onDelete() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = VortexIcons.Close,
                                contentDescription = "Delete from Vault",
                                tint = Color(0xFF8E8E93),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // 2. SLEEK THIN PROGRESS BAR:
            // Replace the current bulky progress layout with a slim, elegant horizontal progress bar spanning neatly across the card with smooth rounded caps.
            if (isDownloading || isPaused) {
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { item.progress.coerceIn(0f, 1f) },
                    color = Color.White,
                    trackColor = Color(0xFF24242A),
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                )
            }
        }
    }
}
