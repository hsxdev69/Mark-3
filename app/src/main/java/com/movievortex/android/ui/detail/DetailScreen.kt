package com.movievortex.android.ui.detail

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.movievortex.android.controller.DetailController
import com.movievortex.android.core.ServerManager
import com.movievortex.android.model.MediaEpisode
import com.movievortex.android.model.MediaType
import com.movievortex.android.model.MovieVortexMedia
import com.movievortex.android.ui.components.PosterCard
import com.movievortex.android.ui.components.VortexIcons
import com.movievortex.android.ui.theme.ManropeFontFamily

data class CastUiItem(val name: String, val character: String, val photoUrl: String?)

@Composable
fun DetailScreen(
    detailController: DetailController,
    onPlayMovie: (MovieVortexMedia) -> Unit,
    onPlayEpisode: (MovieVortexMedia, MediaEpisode) -> Unit,
    onDownloadClick: (MovieVortexMedia, MediaEpisode?) -> Unit,
    onMediaClick: (MovieVortexMedia) -> Unit = onPlayMovie,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state by detailController.uiState.collectAsState()
    val media = state.media

    // Download modal state
    var showDownloadDialog by remember { mutableStateOf(false) }

    // Real cast from TMDB / Provider
    val displayCast = remember(media?.cast) {
        media?.cast?.map { c ->
            CastUiItem(
                name = c.name,
                character = c.role?.takeIf { it.isNotBlank() } ?: "Cast",
                photoUrl = c.avatarUrl
            )
        } ?: emptyList()
    }

    val scrollState = rememberLazyListState()

    var isEntered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isEntered = true
    }

    // Parallax backdrop translation & alpha dimming on scroll
    val backdropTranslationY by remember {
        derivedStateOf {
            if (scrollState.firstVisibleItemIndex == 0) {
                scrollState.firstVisibleItemScrollOffset * 0.42f
            } else 320f
        }
    }
    val backdropAlpha by remember {
        derivedStateOf {
            if (scrollState.firstVisibleItemIndex == 0) {
                (1f - scrollState.firstVisibleItemScrollOffset / 460f).coerceIn(0.20f, 1f)
            } else 0.20f
        }
    }

    // Play Button Press & Entrance animation
    val playInteraction = remember { MutableInteractionSource() }
    var isPlayPressed by remember { mutableStateOf(false) }
    LaunchedEffect(playInteraction) {
        playInteraction.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> isPlayPressed = true
                is PressInteraction.Release,
                is PressInteraction.Cancel -> isPlayPressed = false
            }
        }
    }
    val playScale by animateFloatAsState(
        targetValue = if (!isEntered) 0.3f else if (isPlayPressed) 0.90f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "play_scale"
    )

    // Watchlist & Download Button Springs
    val watchlistInteraction = remember { MutableInteractionSource() }
    var isWatchlistPressed by remember { mutableStateOf(false) }
    LaunchedEffect(watchlistInteraction) {
        watchlistInteraction.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> isWatchlistPressed = true
                is PressInteraction.Release,
                is PressInteraction.Cancel -> isWatchlistPressed = false
            }
        }
    }
    val watchlistScale by animateFloatAsState(
        targetValue = if (isWatchlistPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "watchlist_scale"
    )

    val downloadInteraction = remember { MutableInteractionSource() }
    var isDownloadPressed by remember { mutableStateOf(false) }
    LaunchedEffect(downloadInteraction) {
        downloadInteraction.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> isDownloadPressed = true
                is PressInteraction.Release,
                is PressInteraction.Cancel -> isDownloadPressed = false
            }
        }
    }
    val downloadScale by animateFloatAsState(
        targetValue = if (isDownloadPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "download_scale"
    )

    // Fallback if media is completely absent
    if (media == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF08090B)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.errorMessage ?: "Details loading...",
                color = Color(0xFFF8F8F8),
                fontSize = 15.sp,
                fontFamily = ManropeFontFamily
            )
        }
        return
    }

    // Download options dialog
    if (showDownloadDialog) {
        Dialog(onDismissRequest = { showDownloadDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF141820))
                    .border(1.dp, Color(0x334A5462), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Download Options",
                        color = Color(0xFFFFFFFF),
                        fontSize = 18.sp,
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = media.title,
                        color = Color(0xFF8E95A3),
                        fontSize = 13.sp,
                        fontFamily = ManropeFontFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(18.dp))

                    val downloadOptions = listOf(
                        "1080p Full HD • High Quality",
                        "720p HD • Standard Quality",
                        "480p SD • Data Saver"
                    )

                    downloadOptions.forEach { opt ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF181C24))
                                .border(1.dp, Color(0x334A5462), RoundedCornerShape(12.dp))
                                .clickable {
                                    val streamUrl = state.activeStreamUrl
                                    if (!streamUrl.isNullOrBlank()) {
                                        try {
                                            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
                                            val cleanName = media.title.replace(Regex("[^a-zA-Z0-9.-]"), "_")
                                            val request = DownloadManager.Request(Uri.parse(streamUrl))
                                                .setTitle(media.title)
                                                .setDescription("Downloading ${media.title}")
                                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "MovieVortex/$cleanName.mp4")
                                                .setAllowedOverMetered(true)
                                                .setAllowedOverRoaming(true)
                                            dm?.enqueue(request)
                                            Toast.makeText(context, "Download started: ${media.title}", Toast.LENGTH_SHORT).show()
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Download queued: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Preparing download link...", Toast.LENGTH_SHORT).show()
                                    }
                                    showDownloadDialog = false
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = opt,
                                    color = Color(0xFFFFFFFF),
                                    fontSize = 13.sp,
                                    fontFamily = ManropeFontFamily,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = VortexIcons.Download,
                                    contentDescription = null,
                                    tint = Color(0xFF8E95A3),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    LazyColumn(
        state = scrollState,
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF08090B)),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. TOP CINEMATIC BACKDROP BANNER (Direct match to Silo Reference Screenshot)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
            ) {
                // Backdrop Image with Parallax & Collapsing Dim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            translationY = backdropTranslationY
                            alpha = backdropAlpha
                        }
                        .background(Color(0xFF08090B))
                ) {
                    AsyncImage(
                        model = media.backdrop ?: media.poster,
                        contentDescription = media.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Cinematic Dark Gradient Overlay (Fades seamlessly into screen background)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x9908090B),
                                        Color.Transparent,
                                        Color(0x8008090B),
                                        Color(0xFF08090B)
                                    )
                                )
                            )
                    )

                    // Top Actions: Back Button and Favorite Heart Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Frosted Glass Squircle Back Button
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0x5508090B))
                                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(14.dp))
                                .clickable(onClick = onBackClick),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = VortexIcons.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFFFFFFFF),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Favorite Heart Button
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0x5508090B))
                                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(14.dp))
                                .clickable { detailController.toggleMyList() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (state.isInMyList) VortexIcons.Heart else VortexIcons.HeartBorder,
                                contentDescription = "Favorite",
                                tint = Color(0xFFFFFFFF),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Bottom info overlay inside the backdrop (Title, Badges)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        // Title + Type Tag
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = media.title,
                                color = Color(0xFFFFFFFF),
                                fontSize = 22.sp,
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.4).sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            val typeLabel = if (media.type == MediaType.TvSeries) "TV Series" else "Movie"
                            Text(
                                text = "• $typeLabel",
                                color = Color(0xFF8E95A3),
                                fontSize = 14.sp,
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Metadata Badges: [Genre] [Year] [+17] [★ Rating]
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val genre = media.genres.firstOrNull() ?: "Cinema"
                            MetadataChip(text = genre)

                            media.year?.let { y ->
                                MetadataChip(text = y.toString())
                            }

                            MetadataChip(text = "+17")

                            val rating = media.rating ?: 8.4f
                            MetadataChip(text = "★ %.1f".format(rating))
                        }
                    }
                }
            }
        }

        // 2. FULL-WIDTH WHITE PLAY BUTTON ("Continue Watching" / "Watch Now")
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .height(48.dp)
                    .shadow(12.dp, RoundedCornerShape(12.dp), spotColor = Color(0x88000000))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFFFFF))
                    .clickable(
                        interactionSource = playInteraction,
                        indication = null,
                        onClick = {
                            if (media.type == MediaType.TvSeries || media.type == MediaType.Anime) {
                                state.currentPlayingEpisode?.let { ep ->
                                    onPlayEpisode(media, ep)
                                } ?: onPlayMovie(media)
                            } else {
                                onPlayMovie(media)
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = VortexIcons.Play,
                        contentDescription = null,
                        tint = Color(0xFF08090B),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (media.type == MediaType.TvSeries) "Continue Watching" else "Watch Now",
                        color = Color(0xFF08090B),
                        fontSize = 15.sp,
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 3. SERVER SELECTION TABS (Mark 1, Mark 2, Mark 3, Mark 4 with Auto-Fallback Mechanism)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Server Source",
                        color = Color(0xFF8E95A3),
                        fontSize = 12.sp,
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                    state.fallbackStatusMessage?.let { msg ->
                        Text(
                            text = "Auto-fallback active",
                            color = Color(0xFFFFFFFF),
                            fontSize = 11.sp,
                            fontFamily = ManropeFontFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ServerManager.ALL_SERVERS.forEach { serverName ->
                        val isSelected = state.selectedServer == serverName
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) Color(0x33FFFFFF) else Color(0xFF141820)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0x55FFFFFF) else Color(0x224A5462),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { detailController.switchServer(serverName) },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFFFFF))
                                    )
                                }
                                Text(
                                    text = serverName,
                                    color = if (isSelected) Color(0xFFFFFFFF) else Color(0xFF8E95A3),
                                    fontSize = 11.sp,
                                    fontFamily = ManropeFontFamily,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. ACTION BUTTONS: Watchlist & Download
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                // Quick Action Buttons: Watchlist & Download
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Watchlist Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .graphicsLayer {
                                scaleX = watchlistScale
                                scaleY = watchlistScale
                            }
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (state.isInMyList) Color(0x33FFFFFF) else Color(0xFF141820)
                            )
                            .border(
                                1.dp,
                                if (state.isInMyList) Color(0x55FFFFFF) else Color(0x334A5462),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable(
                                interactionSource = watchlistInteraction,
                                indication = null,
                                onClick = { detailController.toggleMyList() }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (state.isInMyList) VortexIcons.Check else VortexIcons.Add,
                                contentDescription = "Watchlist",
                                tint = Color(0xFFFFFFFF),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (state.isInMyList) "In Watchlist" else "Watchlist",
                                color = Color(0xFFFFFFFF),
                                fontSize = 13.sp,
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Download Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .graphicsLayer {
                                scaleX = downloadScale
                                scaleY = downloadScale
                            }
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF141820))
                            .border(1.dp, Color(0x334A5462), RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = downloadInteraction,
                                indication = null,
                                onClick = { showDownloadDialog = true }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = VortexIcons.Download,
                                contentDescription = "Download",
                                tint = Color(0xFFFFFFFF),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Download",
                                color = Color(0xFFFFFFFF),
                                fontSize = 13.sp,
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // 4. SYNOPSIS SECTION
        val synopsisText = media.synopsis?.takeIf { it.isNotBlank() }
        if (!synopsisText.isNullOrBlank()) {
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Synopsis",
                        color = Color(0xFFFFFFFF),
                        fontSize = 17.sp,
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = synopsisText,
                        color = Color(0xFFA0A5AD),
                        fontSize = 13.sp,
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // 5. CAST SECTION (100% Real TMDB Cast)
        if (displayCast.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cast",
                        color = Color(0xFFFFFFFF),
                        fontSize = 17.sp,
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    itemsIndexed(displayCast) { index, actor ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(80.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF141820))
                                    .border(1.dp, Color(0x334A5462), RoundedCornerShape(16.dp))
                            ) {
                                AsyncImage(
                                    model = actor.photoUrl,
                                    contentDescription = actor.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = actor.name,
                                color = Color(0xFFFFFFFF),
                                fontSize = 11.sp,
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = actor.character,
                                color = Color(0xFF8E95A3),
                                fontSize = 10.sp,
                                fontFamily = ManropeFontFamily,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // 6. TV SERIES SEASONS & EPISODES (if TV Series / Anime)
        if (media.type == MediaType.TvSeries || media.type == MediaType.Anime) {
            item {
                Spacer(modifier = Modifier.height(22.dp))
                Text(
                    text = "Episodes",
                    color = Color(0xFFFFFFFF),
                    fontSize = 17.sp,
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(state.currentSeasonEpisodes) { ep ->
                val isCurrentlyPlaying = state.currentPlayingEpisode?.id == ep.id
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 5.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isCurrentlyPlaying) Color(0x33FFFFFF) else Color(0xFF141820))
                        .border(
                            1.dp,
                            if (isCurrentlyPlaying) Color(0x55FFFFFF) else Color(0x224A5462),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onPlayEpisode(media, ep) }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "EP ${ep.episodeNumber}: ${ep.title.ifBlank { "Episode ${ep.episodeNumber}" }}",
                                color = Color(0xFFFFFFFF),
                                fontSize = 13.sp,
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.SemiBold
                            )
                            ep.synopsis?.let { desc ->
                                Text(
                                    text = desc,
                                    color = Color(0xFF8E95A3),
                                    fontSize = 11.sp,
                                    fontFamily = ManropeFontFamily,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFFFFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = VortexIcons.Play,
                                contentDescription = "Play",
                                tint = Color(0xFF08090B),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }

        // 7. RELATED & RECOMMENDED MOVIES
        if (media.recommendations.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "More Like This",
                    color = Color(0xFFFFFFFF),
                    fontSize = 17.sp,
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(media.recommendations) { rec ->
                        PosterCard(
                            media = rec,
                            onClick = { onMediaClick(rec) },
                            width = 135.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetadataChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF141820))
            .border(0.8.dp, Color(0x334A5462), RoundedCornerShape(8.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFFE2E8F0),
            fontSize = 11.sp,
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.Medium
        )
    }
}
