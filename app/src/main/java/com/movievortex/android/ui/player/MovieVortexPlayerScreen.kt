package com.movievortex.android.ui.player

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Environment
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import com.movievortex.android.controller.PlayerController
import com.movievortex.android.controller.PlayerStatus
import com.movievortex.android.model.MediaEpisode
import com.movievortex.android.core.ServerManager
import com.movievortex.android.model.MovieVortexMedia
import com.movievortex.android.model.StreamSource
import com.movievortex.android.ui.components.VortexIcons
import com.movievortex.android.ui.theme.VortexColors
import kotlinx.coroutines.delay

private fun mapServerDisplayName(rawName: String): String {
    return ServerManager.toDisplayName(rawName)
}

@Composable
fun MovieVortexPlayerScreen(
    playerController: PlayerController,
    media: MovieVortexMedia? = null,
    currentEpisode: MediaEpisode? = null,
    episodes: List<MediaEpisode> = emptyList(),
    onEpisodeClick: ((MediaEpisode) -> Unit)? = null,
    recommendations: List<MovieVortexMedia> = emptyList(),
    onMediaClick: ((MovieVortexMedia) -> Unit)? = null,
    onCloseClick: () -> Unit,
    onPlayOnTvClick: (() -> Unit)? = null,
    onDownloadClick: ((MovieVortexMedia, MediaEpisode?, String?) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val state by playerController.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isPlaying by remember { mutableStateOf(true) }
    var isFullscreen by remember { mutableStateOf(false) }
    var isSpeedingUp by remember { mutableStateOf(false) }
    var showQualityPicker by remember { mutableStateOf(false) }
    var showSubtitlePicker by remember { mutableStateOf(false) }
    var selectedServerName by remember { mutableStateOf(ServerManager.DEFAULT_SERVER) }

    val allSeasons = remember(episodes) {
        if (episodes.isNotEmpty()) episodes.map { it.seasonNumber }.distinct().sorted() else listOf(1)
    }
    var selectedSeasonNum by remember(currentEpisode, allSeasons) {
        mutableIntStateOf(currentEpisode?.seasonNumber ?: allSeasons.firstOrNull() ?: 1)
    }

    val activity = context as? Activity

    BackHandler {
        if (isFullscreen) {
            isFullscreen = false
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            onCloseClick()
        }
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            try {
                exoPlayer.release()
            } catch (_: Exception) {}
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    // Attach stream source to ExoPlayer
    LaunchedEffect(state.currentSource) {
        state.currentSource?.let { source ->
            try {
                selectedServerName = mapServerDisplayName(source.extractorName.ifBlank { ServerManager.DEFAULT_SERVER })
                val mediaItem = MediaItem.fromUri(source.url)
                val dataSourceFactory = DefaultHttpDataSource.Factory()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                if (source.headers.isNotEmpty()) {
                    dataSourceFactory.setDefaultRequestProperties(source.headers)
                }
                val mediaSource = DefaultMediaSourceFactory(dataSourceFactory)
                    .createMediaSource(mediaItem)

                exoPlayer.setMediaSource(mediaSource)
                if (state.currentPositionMs > 0) {
                    exoPlayer.seekTo(state.currentPositionMs)
                }
                exoPlayer.prepare()
                exoPlayer.play()
                isPlaying = true
            } catch (_: Exception) {
                try {
                    val mediaItem = MediaItem.fromUri(source.url)
                    exoPlayer.setMediaItem(mediaItem)
                    if (state.currentPositionMs > 0) {
                        exoPlayer.seekTo(state.currentPositionMs)
                    }
                    exoPlayer.prepare()
                    exoPlayer.play()
                    isPlaying = true
                } catch (_: Exception) {}
            }
        }
    }

    // Periodic progress syncing with controller
    LaunchedEffect(exoPlayer) {
        while (true) {
            delay(500)
            if (exoPlayer.isPlaying || exoPlayer.currentPosition > 0) {
                val currentPos = exoPlayer.currentPosition
                val dur = exoPlayer.duration.coerceAtLeast(0L)
                playerController.updateProgress(currentPos, dur)
            }
        }
    }

    val onNextEpisodeAction: () -> Unit = {
        if (episodes.isNotEmpty()) {
            val currentIdx = if (currentEpisode != null) {
                episodes.indexOfFirst { it.id == currentEpisode.id }
            } else {
                0
            }
            if (currentIdx in 0 until episodes.size - 1) {
                val nextEp = episodes[currentIdx + 1]
                onEpisodeClick?.invoke(nextEp)
            } else {
                Toast.makeText(context, "No next episode", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Movie: play next recommendation if available, else seek 10s
            if (recommendations.isNotEmpty()) {
                onMediaClick?.invoke(recommendations.first())
            } else {
                val dur = exoPlayer.duration.coerceAtLeast(0L)
                val newPos = (exoPlayer.currentPosition + 10000L).coerceAtMost(dur)
                exoPlayer.seekTo(newPos)
                playerController.updateProgress(newPos, dur)
            }
        }
    }

    val onPrevEpisodeAction: () -> Unit = {
        if (exoPlayer.currentPosition > 5000L) {
            exoPlayer.seekTo(0L)
            playerController.updateProgress(0L, exoPlayer.duration.coerceAtLeast(0L))
        } else if (episodes.isNotEmpty()) {
            val currentIdx = if (currentEpisode != null) {
                episodes.indexOfFirst { it.id == currentEpisode.id }
            } else {
                0
            }
            if (currentIdx > 0) {
                onEpisodeClick?.invoke(episodes[currentIdx - 1])
            } else {
                exoPlayer.seekTo(0L)
            }
        } else {
            val newPos = (exoPlayer.currentPosition - 10000L).coerceAtLeast(0L)
            exoPlayer.seekTo(newPos)
            playerController.updateProgress(newPos, exoPlayer.duration.coerceAtLeast(0L))
        }
    }

    val handleDownloadAction: () -> Unit = {
        val streamUrl = state.currentSource?.url
        val displayTitle = state.mediaTitle.ifBlank { media?.title ?: "MovieVortex Media" }
        val cleanName = displayTitle.replace(Regex("[^a-zA-Z0-9.-]"), "_")
        if (!streamUrl.isNullOrBlank()) {
            try {
                val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as? android.app.DownloadManager
                if (dm != null) {
                    val request = android.app.DownloadManager.Request(Uri.parse(streamUrl))
                        .setTitle(displayTitle)
                        .setDescription("Downloading $displayTitle from MovieVortex")
                        .setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "MovieVortex/$cleanName.mp4")
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)
                    dm.enqueue(request)
                    Toast.makeText(context, "Download started: $displayTitle", Toast.LENGTH_SHORT).show()
                } else {
                    onDownloadClick?.invoke(media ?: MovieVortexMedia(id = cleanName, title = displayTitle), currentEpisode, streamUrl)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Download queued: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Connecting to stream before downloading...", Toast.LENGTH_SHORT).show()
        }
    }

    var selectedQualityLabel by remember { mutableStateOf("Auto") }

    val onQualitySelected: (String, Int?, Int?) -> Unit = { label, w, h ->
        selectedQualityLabel = label
        if (h == null) {
            exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon()
                .clearOverridesOfType(androidx.media3.common.C.TRACK_TYPE_VIDEO)
                .setMaxVideoSize(Int.MAX_VALUE, Int.MAX_VALUE)
                .setMinVideoSize(0, 0)
                .build()
            Toast.makeText(context, "Quality: Auto", Toast.LENGTH_SHORT).show()
        } else {
            val videoGroups = exoPlayer.currentTracks.groups.filter { it.type == androidx.media3.common.C.TRACK_TYPE_VIDEO }
            var targetOverride: androidx.media3.common.TrackSelectionOverride? = null
            var bestDiff = Int.MAX_VALUE
            for (group in videoGroups) {
                for (i in 0 until group.mediaTrackGroup.length) {
                    val format = group.getTrackFormat(i)
                    if (format.height > 0) {
                        val diff = kotlin.math.abs(format.height - h)
                        if (diff < bestDiff) {
                            bestDiff = diff
                            targetOverride = androidx.media3.common.TrackSelectionOverride(group.mediaTrackGroup, i)
                        }
                    }
                }
            }
            val builder = exoPlayer.trackSelectionParameters.buildUpon()
            builder.clearOverridesOfType(androidx.media3.common.C.TRACK_TYPE_VIDEO)
            if (targetOverride != null) {
                builder.setOverrideForType(targetOverride)
            }
            builder.setMaxVideoSize(w ?: 1920, h)
            builder.setMinVideoSize(0, 0)
            exoPlayer.trackSelectionParameters = builder.build()
            Toast.makeText(context, "Quality: ${h}p", Toast.LENGTH_SHORT).show()
        }
        showQualityPicker = false
    }

    if (showQualityPicker) {
        QualityPickerDialog(
            selectedQuality = selectedQualityLabel,
            onDismiss = { showQualityPicker = false },
            onSelectQuality = onQualitySelected
        )
    }

    // Fullscreen Mode
    if (isFullscreen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { playerController.toggleControlsVisibility() },
                            onDoubleTap = { offset ->
                                val w = size.width
                                val dur = exoPlayer.duration.coerceAtLeast(0L)
                                if (offset.x < w / 2) {
                                    val newPos = (exoPlayer.currentPosition - 10000L).coerceAtLeast(0L)
                                    exoPlayer.seekTo(newPos)
                                    playerController.updateProgress(newPos, dur)
                                } else {
                                    val newPos = (exoPlayer.currentPosition + 10000L).coerceAtMost(dur)
                                    exoPlayer.seekTo(newPos)
                                    playerController.updateProgress(newPos, dur)
                                }
                            },
                            onPress = {
                                var speedJob: Job? = null
                                try {
                                    speedJob = coroutineScope.launch {
                                        delay(350)
                                        isSpeedingUp = true
                                        exoPlayer.setPlaybackSpeed(2.0f)
                                    }
                                    tryAwaitRelease()
                                } finally {
                                    speedJob?.cancel()
                                    if (isSpeedingUp) {
                                        exoPlayer.setPlaybackSpeed(1.0f)
                                        isSpeedingUp = false
                                    }
                                }
                            }
                        )
                    }
            )

            // 2X Speed Indicator Badge
            if (isSpeedingUp) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 18.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xE6000000))
                        .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "2X",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "▶▶",
                            color = Color(0xFFFFC107),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Fullscreen Overlay Controls
            AnimatedVisibility(
                visible = state.isControlsVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                PlayerControlsOverlay(
                    title = state.mediaTitle.ifBlank { media?.title ?: "MovieVortex" },
                    subtitle = state.mediaSubtitle,
                    isPlaying = isPlaying,
                    currentPos = state.currentPositionMs,
                    duration = state.durationMs,
                    onPlayPause = {
                        if (isPlaying) {
                            exoPlayer.pause()
                            isPlaying = false
                        } else {
                            exoPlayer.play()
                            isPlaying = true
                        }
                    },
                    onSeek = { frac ->
                        val dur = exoPlayer.duration.coerceAtLeast(0L)
                        val newPos = (frac * dur).toLong()
                        exoPlayer.seekTo(newPos)
                        playerController.updateProgress(newPos, dur)
                    },
                    onRewind = onPrevEpisodeAction,
                    onForward = onNextEpisodeAction,
                    onToggleFullscreen = {
                        isFullscreen = false
                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    },
                    isFullscreen = true,
                    onBackClick = {
                        isFullscreen = false
                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    },
                    onSubtitleClick = { showSubtitlePicker = !showSubtitlePicker },
                    onQualityClick = { showQualityPicker = !showQualityPicker }
                )
            }
        }
        return
    }

    // Normal Integrated View (Screenshot_20261015_084607.jpg)
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VortexColors.PureBlack)
    ) {
        // 1. Embedded 16:9 Video Player View
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { playerController.toggleControlsVisibility() },
                            onDoubleTap = { offset ->
                                val w = size.width
                                val dur = exoPlayer.duration.coerceAtLeast(0L)
                                if (offset.x < w / 2) {
                                    val newPos = (exoPlayer.currentPosition - 10000L).coerceAtLeast(0L)
                                    exoPlayer.seekTo(newPos)
                                    playerController.updateProgress(newPos, dur)
                                } else {
                                    val newPos = (exoPlayer.currentPosition + 10000L).coerceAtMost(dur)
                                    exoPlayer.seekTo(newPos)
                                    playerController.updateProgress(newPos, dur)
                                }
                            },
                            onPress = {
                                var speedJob: Job? = null
                                try {
                                    speedJob = coroutineScope.launch {
                                        delay(350)
                                        isSpeedingUp = true
                                        exoPlayer.setPlaybackSpeed(2.0f)
                                    }
                                    tryAwaitRelease()
                                } finally {
                                    speedJob?.cancel()
                                    if (isSpeedingUp) {
                                        exoPlayer.setPlaybackSpeed(1.0f)
                                        isSpeedingUp = false
                                    }
                                }
                            }
                        )
                    }
            )

            // 2X Speed Indicator Badge
            if (isSpeedingUp) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 14.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xE6000000))
                        .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "2X",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "▶▶",
                            color = Color(0xFFFFC107),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Loading / Resolving state indicator
            if (state.status == PlayerStatus.ResolvingStreams) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Embedded Overlay Controls
            androidx.compose.animation.AnimatedVisibility(
                visible = state.isControlsVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                PlayerControlsOverlay(
                    title = state.mediaTitle.ifBlank { media?.title ?: "MovieVortex" },
                    subtitle = state.mediaSubtitle,
                    isPlaying = isPlaying,
                    currentPos = state.currentPositionMs,
                    duration = state.durationMs,
                    onPlayPause = {
                        if (isPlaying) {
                            exoPlayer.pause()
                            isPlaying = false
                        } else {
                            exoPlayer.play()
                            isPlaying = true
                        }
                    },
                    onSeek = { frac ->
                        val dur = exoPlayer.duration.coerceAtLeast(0L)
                        val newPos = (frac * dur).toLong()
                        exoPlayer.seekTo(newPos)
                        playerController.updateProgress(newPos, dur)
                    },
                    onRewind = onPrevEpisodeAction,
                    onForward = onNextEpisodeAction,
                    onToggleFullscreen = {
                        isFullscreen = true
                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    },
                    isFullscreen = false,
                    onBackClick = {
                        try { exoPlayer.stop() } catch (_: Exception) {}
                        onCloseClick()
                    },
                    onSubtitleClick = { showSubtitlePicker = !showSubtitlePicker },
                    onQualityClick = { showQualityPicker = !showQualityPicker }
                )
            }
        }

        // 2. Scrollable Detail, Server & Episode Section Below Player
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 36.dp)
        ) {
            // Title, Rating, Year
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    val displayTitle = state.mediaTitle.ifBlank { media?.title ?: "MovieVortex Media" }
                    Text(
                        text = displayTitle,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val ratingValue = media?.rating ?: 8.6f
                        Text(
                            text = "★ %.1f".format(ratingValue),
                            color = Color(0xFFFFC107),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = media?.year?.let { "$it" } ?: "2024",
                            color = Color(0xFFAAAAAA),
                            fontSize = 13.sp
                        )
                        if (currentEpisode != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "S${currentEpisode.seasonNumber} E${currentEpisode.episodeNumber}",
                                color = Color(0xFFAAAAAA),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons Row (Add to List, Play on TV, Share, Report, Download)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            ActionPillButton(
                                icon = VortexIcons.BookmarkBorder,
                                label = "Add to List",
                                onClick = {
                                    Toast.makeText(context, "Added to My List", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        item {
                            ActionPillButton(
                                icon = VortexIcons.Cast,
                                label = "Play on TV",
                                onClick = {
                                    onPlayOnTvClick?.invoke() ?: run {
                                        Toast.makeText(context, "Scanning for TV displays...", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                        item {
                            ActionPillButton(
                                icon = VortexIcons.Share,
                                label = "Share",
                                onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, displayTitle)
                                        putExtra(Intent.EXTRA_TEXT, "Watch $displayTitle on MovieVortex!")
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Media"))
                                }
                            )
                        }
                        item {
                            ActionPillButton(
                                icon = VortexIcons.Report,
                                label = "Report",
                                onClick = {
                                    Toast.makeText(context, "Feedback reported. Thank you!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        item {
                            ActionPillButton(
                                icon = VortexIcons.Download,
                                label = "Download",
                                onClick = handleDownloadAction
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Server Selection Section (Screenshot_20261015_084607.jpg)
                    Text(
                        text = "If current server is not working, try a different one:",
                        color = Color(0xFFDDDDDD),
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Servers Row: Mark-1, Mark-2, Mark-3, Mark-4
                    val serverOptions = ServerManager.ALL_SERVERS

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Servers :",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(serverOptions) { serverName ->
                                val isSelected = selectedServerName.equals(serverName, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) Color.White else Color(0xFF222222))
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) Color.White else Color(0xFF333333),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable {
                                            selectedServerName = serverName
                                            val matchedSource = state.streamSources.firstOrNull { source ->
                                                val mapped = mapServerDisplayName(source.extractorName.ifBlank { source.quality })
                                                mapped.equals(serverName, ignoreCase = true) ||
                                                (serverName == ServerManager.MARK_1 && (source.extractorName.contains("Turbo", true) || source.extractorName.contains("AniVortex", true) || source.extractorName.contains("AnVortex", true))) ||
                                                (serverName == ServerManager.MARK_2 && (source.extractorName.contains("Core", true) || source.extractorName.contains("StreamFlare", true) || source.extractorName.contains("IStreamFlare", true))) ||
                                                (serverName == ServerManager.MARK_3 && (source.extractorName.contains("Rive", true) || source.extractorName.contains("MovieBox", true))) ||
                                                (serverName == ServerManager.MARK_4 && (source.extractorName.contains("Spiral", true) || source.extractorName.contains("SuperStream", true)))
                                            }
                                            if (matchedSource != null) {
                                                playerController.selectSource(matchedSource)
                                            } else {
                                                Toast.makeText(context, "Switched to $serverName", Toast.LENGTH_SHORT).show()
                                                playerController.retryPlayback()
                                            }
                                        }
                                        .padding(horizontal = 16.dp, vertical = 7.dp)
                                ) {
                                    Text(
                                        text = serverName,
                                        color = if (isSelected) Color.Black else Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Comments Section Card
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Comments",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "1",
                            color = Color(0xFFAAAAAA),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF141414))
                            .border(1.dp, Color(0xFF222222), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE50914)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("A", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Adhithya Sarjun",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "~1mo",
                                        color = Color(0xFF888888),
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "servers are not working",
                                    color = Color(0xFFCCCCCC),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // If TV Series / Anime with episodes (Screenshot_20261015_084607.jpg)
                    if (episodes.isNotEmpty()) {
                        // Season Pills
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(allSeasons) { seasonNum ->
                                val isSeasonSelected = selectedSeasonNum == seasonNum
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSeasonSelected) Color.White else Color(0xFF1E1E1E))
                                        .border(
                                            width = 1.dp,
                                            color = if (isSeasonSelected) Color.White else Color(0xFF333333),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable { selectedSeasonNum = seasonNum }
                                        .padding(horizontal = 16.dp, vertical = 7.dp)
                                ) {
                                    Text(
                                        text = "Season $seasonNum",
                                        color = if (isSeasonSelected) Color.Black else Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Episodes Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Episodes",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = VortexIcons.GridView,
                                contentDescription = "Grid",
                                tint = Color(0xFFAAAAAA),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        // Movie: You May Also Like Header
                        Text(
                            text = "You May Also Like",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Episodes Grid (2 columns) or Movie Recommendations Grid (2 columns)
            if (episodes.isNotEmpty()) {
                val currentSeasonEps = episodes.filter { it.seasonNumber == selectedSeasonNum }
                val chunkedEps = currentSeasonEps.chunked(2)

                items(chunkedEps) { epRow ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        epRow.forEach { ep ->
                            val isPlayingThis = (currentEpisode?.id == ep.id) || (currentEpisode == null && ep.episodeNumber == 1 && selectedSeasonNum == 1)
                            EpisodeGridCard(
                                episode = ep,
                                isPlaying = isPlayingThis,
                                fallbackBackdrop = media?.backdrop ?: media?.poster,
                                onClick = { onEpisodeClick?.invoke(ep) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (epRow.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            } else {
                val fallbackRecommendations = listOf(
                    MovieVortexMedia(
                        id = "dune-1",
                        title = "Dune: Part One",
                        poster = "https://image.tmdb.org/t/p/w500/d5NXSklXo0qyIYkgV94XAgMIckC.jpg",
                        year = 2021,
                        rating = 8.0f
                    ),
                    MovieVortexMedia(
                        id = "eeaao",
                        title = "Everything Everywhere All at Once",
                        poster = "https://image.tmdb.org/t/p/w500/w3LxiVYPqEIIAqocRGSYQJzag4T.jpg",
                        year = 2022,
                        rating = 7.7f
                    ),
                    MovieVortexMedia(
                        id = "dawn-apes",
                        title = "Dawn of the Planet of the Apes",
                        poster = "https://image.tmdb.org/t/p/w500/kScdOzMeqm79jA280vjDkL6aWf3.jpg",
                        year = 2014,
                        rating = 7.6f
                    ),
                    MovieVortexMedia(
                        id = "wandering-earth",
                        title = "The Wandering Earth II",
                        poster = "https://image.tmdb.org/t/p/w500/4n743bJ3nC6c5XbXvT4d0eK3g1f.jpg",
                        year = 2023,
                        rating = 7.2f
                    )
                )

                val displayList = if (recommendations.isNotEmpty()) recommendations else fallbackRecommendations
                val chunked = displayList.chunked(2)

                items(chunked) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { item ->
                            RecommendationCard(
                                media = item,
                                badge = if (item.id.contains("dune")) "MULTI" else if (item.id.contains("eeaao")) "ORI" else "DUAL",
                                onClick = { onMediaClick?.invoke(item) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EpisodeGridCard(
    episode: MediaEpisode,
    isPlaying: Boolean,
    fallbackBackdrop: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(16f / 9.5f)
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (isPlaying) 1.5.dp else 1.dp,
                color = if (isPlaying) Color.White else Color(0xFF222222),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = episode.poster ?: fallbackBackdrop,
            contentDescription = episode.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark gradient vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x80000000),
                            Color(0xE6000000)
                        )
                    )
                )
        )

        // Episode info overlay at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Episode : ${episode.episodeNumber}",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            val desc = episode.synopsis?.ifBlank { episode.title } ?: episode.title
            if (desc.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    color = Color(0xFFCCCCCC),
                    fontSize = 10.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 13.sp
                )
            }
        }
    }
}

@Composable
private fun ActionPillButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1E1E1E))
            .border(1.dp, Color(0xFF2E2E2E), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun RecommendationCard(
    media: MovieVortexMedia,
    badge: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF222222))
        ) {
            AsyncImage(
                model = media.poster,
                contentDescription = media.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .padding(6.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badge,
                    color = Color.Black,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = media.title,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Movie • ${media.year ?: 2024}",
                color = Color(0xFF888888),
                fontSize = 11.sp
            )
            media.rating?.let { r ->
                Text(
                    text = "★ %.1f".format(r),
                    color = Color(0xFFFFC107),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PlayerControlsOverlay(
    title: String,
    subtitle: String,
    isPlaying: Boolean,
    currentPos: Long,
    duration: Long,
    onPlayPause: () -> Unit,
    onSeek: (Float) -> Unit,
    onRewind: () -> Unit,
    onForward: () -> Unit,
    onToggleFullscreen: () -> Unit,
    isFullscreen: Boolean,
    onBackClick: () -> Unit,
    onSubtitleClick: () -> Unit,
    onQualityClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000))
    ) {
        // Top Controls Bar (Back, Title, CC, Tune)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = VortexIcons.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        color = Color(0xFFAAAAAA),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(onClick = onSubtitleClick) {
                Icon(
                    imageVector = VortexIcons.Subtitles,
                    contentDescription = "Subtitles",
                    tint = Color.White,
                    modifier = Modifier.size(19.dp)
                )
            }

            IconButton(onClick = onQualityClick) {
                Icon(
                    imageVector = VortexIcons.Tune,
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier.size(19.dp)
                )
            }
        }

        // Center Controls (Prev/Rewind, Play/Pause, Next Episode)
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onRewind) {
                Icon(
                    imageVector = VortexIcons.SkipPrevious,
                    contentDescription = "Previous Episode",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(onClick = onPlayPause),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) VortexIcons.Pause else VortexIcons.Play,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(onClick = onForward) {
                Icon(
                    imageVector = VortexIcons.SkipNext,
                    contentDescription = "Next Episode",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Bottom Timeline Bar (0:00, seekbar, duration, PIP, Fullscreen)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatTime(currentPos),
                color = Color.White,
                fontSize = 11.sp
            )

            val progressFraction = if (duration > 0) {
                (currentPos.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
            } else 0f

            Slider(
                value = progressFraction,
                onValueChange = onSeek,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color(0x66FFFFFF)
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )

            Text(
                text = formatTime(duration),
                color = Color(0xFFAAAAAA),
                fontSize = 11.sp
            )

            IconButton(onClick = { /* PIP */ }, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = VortexIcons.PictureInPicture,
                    contentDescription = "PIP",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            IconButton(onClick = onToggleFullscreen, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = if (isFullscreen) VortexIcons.FullscreenExit else VortexIcons.Fullscreen,
                    contentDescription = "Fullscreen",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = (millis / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@Composable
private fun QualityPickerDialog(
    selectedQuality: String,
    onDismiss: () -> Unit,
    onSelectQuality: (String, Int?, Int?) -> Unit
) {
    val options = listOf(
        Triple("Auto", null, null),
        Triple("1080p", 1920, 1080),
        Triple("720p", 1280, 720),
        Triple("480p", 854, 480),
        Triple("360p", 640, 360)
    )

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF16191F))
                .border(1.dp, Color(0xFF292D35), RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Text(
                text = "Select Quality",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            options.forEach { (label, w, h) ->
                val isSelected = label.equals(selectedQuality, ignoreCase = true)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSelectQuality(label, w, h) }
                        .padding(vertical = 12.dp, horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color(0xFF6EA8FE) else Color.White,
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = VortexIcons.Check,
                            contentDescription = "Selected",
                            tint = Color(0xFF6EA8FE),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
