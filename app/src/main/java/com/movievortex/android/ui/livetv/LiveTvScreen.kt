package com.movievortex.android.ui.livetv

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.movievortex.android.model.LiveChannel
import com.movievortex.android.model.LiveChannelsRepository
import com.movievortex.android.ui.components.VortexIcons
import com.movievortex.android.ui.theme.ManropeFontFamily
import com.movievortex.android.ui.theme.VortexColors

@Composable
fun LiveTvScreen(
    onChannelClick: (LiveChannel) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All Channels") }
    var searchQuery by remember { mutableStateOf("") }

    val featuredChannels = remember {
        LiveChannelsRepository.AllChannels.filter { it.isFeatured }
    }
    val entertainmentChannels = remember {
        LiveChannelsRepository.getChannelsByCategory("📺 Hindi Entertainment")
    }
    val moviesChannels = remember {
        LiveChannelsRepository.getChannelsByCategory("🎬 Hindi Movies")
    }
    val newsChannels = remember {
        LiveChannelsRepository.getChannelsByCategory("📰 News")
    }
    val sportsChannels = remember {
        LiveChannelsRepository.getChannelsByCategory("🏏 Sports")
    }
    val musicChannels = remember {
        LiveChannelsRepository.getChannelsByCategory("🎵 Music")
    }
    val kidsChannels = remember {
        LiveChannelsRepository.getChannelsByCategory("👶 Kids")
    }
    val documentaryChannels = remember {
        LiveChannelsRepository.getChannelsByCategory("🌍 Documentary")
    }
    val devotionalChannels = remember {
        LiveChannelsRepository.getChannelsByCategory("🕉️ Devotional")
    }
    val regionalChannels = remember {
        LiveChannelsRepository.AllChannels.filter {
            it.category in listOf("Marathi", "Tamil", "Telugu", "Kannada", "Bengali", "Malayalam", "Punjabi")
        }
    }
    val internationalChannels = remember {
        LiveChannelsRepository.getChannelsByCategory("🌐 International")
    }

    val filteredChannels = remember(selectedCategory, searchQuery) {
        val base = if (selectedCategory == "All Channels") {
            LiveChannelsRepository.AllChannels
        } else {
            LiveChannelsRepository.getChannelsByCategory(selectedCategory)
        }
        if (searchQuery.isBlank()) {
            base
        } else {
            base.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.currentProgram.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val isCategorizedView = selectedCategory == "All Channels" && searchQuery.isBlank()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VortexColors.BackgroundOled)
    ) {
        if (isCategorizedView) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 110.dp)
            ) {
                // Live TV Header Hero
                item {
                    val flagship = featuredChannels.firstOrNull() ?: LiveChannelsRepository.AllChannels.first()
                    LiveTvHeroBanner(
                        channel = flagship,
                        onPlayClick = { onChannelClick(flagship) }
                    )
                }

                // Channel Search Bar
                item {
                    LiveTvSearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                // Category Chips Row
                item {
                    LiveCategoryTabsRow(
                        categories = LiveChannelsRepository.Categories,
                        selectedCategory = selectedCategory,
                        onSelectCategory = { selectedCategory = it }
                    )
                }

                // Section: Featured Live Channels
                item {
                    LiveChannelSectionRow(
                        title = "🔥 Featured Live Now",
                        channels = featuredChannels,
                        onChannelClick = onChannelClick
                    )
                }

                // Section: Hindi Entertainment
                if (entertainmentChannels.isNotEmpty()) {
                    item {
                        LiveChannelSectionRow(
                            title = "📺 Hindi Entertainment",
                            channels = entertainmentChannels,
                            onChannelClick = onChannelClick
                        )
                    }
                }

                // Section: Hindi Movies
                if (moviesChannels.isNotEmpty()) {
                    item {
                        LiveChannelSectionRow(
                            title = "🎬 24/7 Movie Channels",
                            channels = moviesChannels,
                            onChannelClick = onChannelClick
                        )
                    }
                }

                // Section: Breaking News
                if (newsChannels.isNotEmpty()) {
                    item {
                        LiveChannelSectionRow(
                            title = "📰 24/7 Breaking News",
                            channels = newsChannels,
                            onChannelClick = onChannelClick
                        )
                    }
                }

                // Section: Live Sports
                if (sportsChannels.isNotEmpty()) {
                    item {
                        LiveChannelSectionRow(
                            title = "🏏 Live Sports & Tournaments",
                            channels = sportsChannels,
                            onChannelClick = onChannelClick
                        )
                    }
                }

                // Section: Music
                if (musicChannels.isNotEmpty()) {
                    item {
                        LiveChannelSectionRow(
                            title = "🎵 Non-Stop Music & Hits",
                            channels = musicChannels,
                            onChannelClick = onChannelClick
                        )
                    }
                }

                // Section: Kids
                if (kidsChannels.isNotEmpty()) {
                    item {
                        LiveChannelSectionRow(
                            title = "👶 Anime & Kids",
                            channels = kidsChannels,
                            onChannelClick = onChannelClick
                        )
                    }
                }

                // Section: Documentary
                if (documentaryChannels.isNotEmpty()) {
                    item {
                        LiveChannelSectionRow(
                            title = "🌍 Documentaries & Nature",
                            channels = documentaryChannels,
                            onChannelClick = onChannelClick
                        )
                    }
                }

                // Section: Devotional
                if (devotionalChannels.isNotEmpty()) {
                    item {
                        LiveChannelSectionRow(
                            title = "🕉️ Devotional & Spiritual",
                            channels = devotionalChannels,
                            onChannelClick = onChannelClick
                        )
                    }
                }

                // Section: Regional Channels
                if (regionalChannels.isNotEmpty()) {
                    item {
                        LiveChannelSectionRow(
                            title = "🚩 Regional Favorites (Marathi, Punjabi, South)",
                            channels = regionalChannels,
                            onChannelClick = onChannelClick
                        )
                    }
                }

                // Section: International
                if (internationalChannels.isNotEmpty()) {
                    item {
                        LiveChannelSectionRow(
                            title = "🌐 World News & International",
                            channels = internationalChannels,
                            onChannelClick = onChannelClick
                        )
                    }
                }
            }
        } else {
            // Grid View for Selected Category or Search Results
            Column(modifier = Modifier.fillMaxSize()) {
                LiveTvSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )

                LiveCategoryTabsRow(
                    categories = LiveChannelsRepository.Categories,
                    selectedCategory = selectedCategory,
                    onSelectCategory = { selectedCategory = it }
                )

                Text(
                    text = if (searchQuery.isNotBlank()) "Search results (${filteredChannels.size})" else "$selectedCategory (${filteredChannels.size})",
                    color = Color(0xFFE0E0E0),
                    fontSize = 15.sp,
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 110.dp, top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredChannels, key = { it.id }) { channel ->
                        LiveChannelCard(
                            channel = channel,
                            onClick = { onChannelClick(channel) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiveTvHeroBanner(
    channel: LiveChannel,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF141414),
                        Color(0xFF0A0A0A),
                        VortexColors.BackgroundOled
                    )
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Live Status Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Pulsing Red Live Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xE6E50914))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .scale(pulseAlpha)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Text(
                            text = "LIVE NOW",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Resolution Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x33FFFFFF))
                        .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = channel.resolution,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Category Tag
                Text(
                    text = "•  ${channel.category}",
                    color = Color(0xBBFFFFFF),
                    fontSize = 12.sp,
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Medium
                )
            }

            // Channel Title & Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFF1A1A1A))
                        .border(1.dp, Color(0x33FFFFFF), CircleShape)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = channel.logoUrl,
                        contentDescription = channel.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = channel.name,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = channel.currentProgram,
                        color = Color(0xAAFFFFFF),
                        fontSize = 12.sp,
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Quick Watch Live Pill Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .clickable(onClick = onPlayClick)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = VortexIcons.Play,
                    contentDescription = "Watch Live",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Watch Live Stream",
                    color = Color.Black,
                    fontSize = 13.sp,
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun LiveTvSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF161616))
            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(24.dp))
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = VortexIcons.Search,
                contentDescription = "Search",
                tint = Color(0x88FFFFFF),
                modifier = Modifier.size(18.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "Search channels by name, sports, news...",
                        color = Color(0x55FFFFFF),
                        fontSize = 13.sp,
                        fontFamily = ManropeFontFamily
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 13.sp,
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(Color.White),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (query.isNotEmpty()) {
                Icon(
                    imageVector = VortexIcons.Close,
                    contentDescription = "Clear",
                    tint = Color(0x88FFFFFF),
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onQueryChange("") }
                )
            }
        }
    }
}

@Composable
fun LiveCategoryTabsRow(
    categories: List<String>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            val chipBg = if (isSelected) Color.White else Color(0xFF161616)
            val chipText = if (isSelected) Color.Black else Color(0xAAFFFFFF)
            val chipBorder = if (isSelected) Color.White else Color(0x25FFFFFF)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(chipBg)
                    .border(1.dp, chipBorder, RoundedCornerShape(20.dp))
                    .clickable { onSelectCategory(category) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category,
                    color = chipText,
                    fontSize = 12.sp,
                    fontFamily = ManropeFontFamily,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun LiveChannelSectionRow(
    title: String,
    channels: List<LiveChannel>,
    onChannelClick: (LiveChannel) -> Unit,
    modifier: Modifier = Modifier
) {
    if (channels.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 17.sp,
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(channels, key = { it.id }) { channel ->
                LiveChannelCard(
                    channel = channel,
                    onClick = { onChannelClick(channel) },
                    modifier = Modifier.width(180.dp)
                )
            }
        }
    }
}

@Composable
fun LiveChannelCard(
    channel: LiveChannel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF121212))
            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(10.dp)
    ) {
        // Thumbnail Card with Logo & Badges
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.6f)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF222222),
                            Color(0xFF0F0F0F)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Channel Logo
            AsyncImage(
                model = channel.logoUrl,
                contentDescription = channel.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(62.dp)
                    .padding(4.dp)
            )

            // Top-left Live badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xE6E50914))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(Color.White))
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Top-right Resolution badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0x99000000))
                    .border(0.5.dp, Color(0x44FFFFFF), RoundedCornerShape(4.dp))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = channel.resolution.substringBefore(" "),
                    color = Color(0xFFDDDDDD),
                    fontSize = 8.sp,
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title and Current Program
        Text(
            text = channel.name,
            color = Color.White,
            fontSize = 13.sp,
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = channel.currentProgram,
            color = Color(0x77FFFFFF),
            fontSize = 11.sp,
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
