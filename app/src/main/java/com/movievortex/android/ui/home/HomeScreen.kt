package com.movievortex.android.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.movievortex.android.controller.ContentRow
import com.movievortex.android.controller.HomeController
import com.movievortex.android.model.MediaType
import com.movievortex.android.model.MovieVortexMedia
import com.movievortex.android.model.StreamingPlatform
import com.movievortex.android.ui.components.HomeScreenSkeleton
import com.movievortex.android.ui.components.PosterCard
import com.movievortex.android.ui.components.VortexIcons
import com.movievortex.android.ui.theme.ManropeFontFamily
import kotlin.math.abs

@Composable
fun HomeScreen(
    homeController: HomeController,
    onMediaClick: (MovieVortexMedia) -> Unit,
    onPlayClick: (MovieVortexMedia) -> Unit,
    onPlatformClick: (StreamingPlatform) -> Unit,
    onOpenCategory: (String) -> Unit = {},
    onRequireAuth: (String) -> Unit = {},
    onNavigateToLiveTv: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val state by homeController.uiState.collectAsState()
    val scrollState = rememberLazyListState()
    var selectedCategory by remember { mutableStateOf("Trending") }

    val categories = listOf("Trending", "New", "Movies", "Series", "Animation", "TV shows")

    val filteredRows = remember(selectedCategory, state.rows) {
        state.rows
    }

    // Trending Hero Carousel: At least 10 real movies without dummy data
    val heroList = remember(state.heroItems, state.rows) {
        val pool = (state.heroItems + state.rows.flatMap { it.items })
            .distinctBy { it.rawUrl.ifBlank { it.title.trim().lowercase() } }
        if (pool.isNotEmpty()) pool.take(10) else emptyList()
    }

    // If everything is completely empty while initial cold loading, show shimmering skeleton
    if (heroList.isEmpty() && state.rows.isEmpty() && state.isLoading) {
        HomeScreenSkeleton(modifier = modifier)
        return
    }

    LazyColumn(
        state = scrollState,
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF08090B)),
        contentPadding = PaddingValues(bottom = 110.dp)
    ) {
        // 1. Top Category Tabs (Obsidian + Periwinkle)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(top = 10.dp, bottom = 4.dp)
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(categories) { cat ->
                        val isSelected = cat == selectedCategory
                        Text(
                            text = cat,
                            color = if (isSelected) Color(0xFFFFFFFF) else Color(0xFF8E95A3),
                            fontSize = if (isSelected) 22.sp else 15.sp,
                            fontFamily = ManropeFontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            letterSpacing = (-0.4).sp,
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        if (cat == "Trending") {
                                            selectedCategory = "Trending"
                                        } else {
                                            onOpenCategory(cat)
                                        }
                                    }
                                )
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 2. Liquid 3D Perspective Fling Carousel (Expanded Length & Breadth)
        if (heroList.isNotEmpty()) {
            item {
                VideoStyle3DHeroCarousel(
                    items = heroList,
                    onTap = onMediaClick
                )
            }
        }

        // 3. Promo Announcement Banner (From Reference UI)
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF141820))
                    .border(1.dp, Color(0x334A5462), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp)
                    ) {
                        Text(
                            text = "Great news! You can now upgrade to our Pro mode at a discount of 60% off. Don't miss out on this limited-time offer!",
                            color = Color(0xFFFFFFFF),
                            fontSize = 11.sp,
                            fontFamily = ManropeFontFamily,
                            lineHeight = 16.sp
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        val promoThumbnails = heroList.take(3).mapNotNull { it.poster ?: it.backdrop }
                        promoThumbnails.forEach { thumb ->
                            AsyncImage(
                                model = thumb,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(34.dp, 48.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                        }
                    }
                }
            }
        }

        // 4. "For you" Row
        val forYouItems = filteredRows.firstOrNull()?.items ?: emptyList()
        if (forYouItems.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "For you",
                        color = Color(0xFFFFFFFF),
                        fontSize = 19.sp,
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.4).sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onOpenCategory("For you") }
                    ) {
                        Text(
                            text = "See all",
                            color = Color(0xFF8E95A3),
                            fontSize = 12.sp,
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = VortexIcons.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF8E95A3),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(forYouItems) { item ->
                        PosterCard(
                            media = item,
                            onClick = { onMediaClick(item) },
                            width = 145.dp,
                            showDetails = false
                        )
                    }
                }
            }
        }

        // 5. Content Rows (Recently Added Movies, Popular Now, etc.)
        val remainingRows = filteredRows.drop(1)
        items(remainingRows) { row ->
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = row.title,
                    color = Color(0xFFFFFFFF),
                    fontSize = 19.sp,
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.4).sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onOpenCategory(row.title) }
                ) {
                    Text(
                        text = "See all",
                        color = Color(0xFF8E95A3),
                        fontSize = 12.sp,
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = VortexIcons.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFF8E95A3),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(row.items) { item ->
                    PosterCard(
                        media = item,
                        onClick = { onMediaClick(item) },
                        width = 145.dp
                    )
                }
            }
        }

        // 6. Explore More / Grid Loader
        item {
            LaunchedEffect(Unit) {
                homeController.loadMoreExplore()
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state.isExploreLoading) {
                    CircularProgressIndicator(
                        color = Color(0xFFFFFFFF),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun VideoStyle3DHeroCarousel(
    items: List<MovieVortexMedia>,
    onTap: (MovieVortexMedia) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    // Infinite Virtual Page Count (allows continuous looping in both directions)
    val virtualCount = 10000
    val initialPage = (virtualCount / (2 * items.size)) * items.size
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { virtualCount }
    )

    // 1. Automatic horizontal sliding every 5 seconds (smooth infinite Apple-like carousel)
    LaunchedEffect(pagerState, items.size) {
        while (true) {
            kotlinx.coroutines.delay(5000)
            if (!pagerState.isScrollInProgress) {
                pagerState.animateScrollToPage(
                    page = pagerState.currentPage + 1,
                    animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    val activeIndex = (pagerState.currentPage % items.size).let { if (it < 0) it + items.size else it }
    val currentItem = items.getOrNull(activeIndex) ?: items.first()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val cardWidth = (screenWidth * 0.82f).coerceIn(295.dp, 350.dp)
    val cardHeight = 455.dp // Elegant vertical ratio matching MovieVortex_NavBar_Dune_Preview spec
    val horizontalPadding = ((screenWidth - cardWidth) / 2).coerceAtLeast(0.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(475.dp),
            contentAlignment = Alignment.Center
        ) {
            // Dynamic Ambient Colorful Aura Glow with smooth Crossfade behind active card
            Crossfade(
                targetState = currentItem.poster ?: currentItem.backdrop,
                animationSpec = tween(550),
                label = "apple_hero_aura_crossfade"
            ) { auraUrl ->
                AsyncImage(
                    model = auraUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(cardWidth + 20.dp, cardHeight)
                        .blur(55.dp)
                        .graphicsLayer {
                            alpha = 0.45f
                            scaleX = 1.15f
                            scaleY = 1.15f
                        }
                )
            }

            // Apple Vision Pro Style 3D Cylindrical Ring Carousel Pager
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fixed(cardWidth),
                contentPadding = PaddingValues(horizontal = horizontalPadding),
                pageSpacing = (-22).dp, // Overlap spacing tucks side cards along 3D cylinder arc
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val realIdx = (page % items.size).let { if (it < 0) it + items.size else it }
                val item = items[realIdx]
                val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                val absOffset = abs(pageOffset).coerceIn(0f, 2.5f)

                val interactionSource = remember { MutableInteractionSource() }
                var isPressed by remember { mutableStateOf(false) }
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect { interaction ->
                        when (interaction) {
                            is PressInteraction.Press -> isPressed = true
                            is PressInteraction.Release,
                            is PressInteraction.Cancel -> isPressed = false
                        }
                    }
                }

                val pressScale by animateFloatAsState(
                    targetValue = if (isPressed) 0.94f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "apple_hero_card_press"
                )

                Box(
                    modifier = Modifier
                        .zIndex(1f - absOffset) // Active center card is strictly on top
                        .graphicsLayer {
                            val scale = (1f - absOffset * 0.14f).coerceIn(0.72f, 1f) * pressScale
                            scaleX = scale
                            scaleY = scale
                            rotationY = pageOffset.coerceIn(-1.5f, 1.5f) * -32f
                            cameraDistance = 16f * density
                            translationX = pageOffset * -10f * density
                            translationY = (absOffset * absOffset * 4.0f) * density
                            alpha = (1f - absOffset * 0.30f).coerceIn(0.48f, 1f)
                        }
                        .size(cardWidth, cardHeight)
                        .shadow(
                            elevation = if (absOffset < 0.5f) 26.dp else 8.dp,
                            shape = RoundedCornerShape(22.dp),
                            spotColor = Color(0xDD000000),
                            ambientColor = Color(0x88000000)
                        )
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFF141820))
                        // Apple Dual-Bevel Specular Glass Border
                        .border(
                            width = 1.2.dp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color(0x55FFFFFF), // Top specular reflection highlight
                                    Color(0x224A5462), // Translucent mid edge
                                    Color(0x1008090B)  // Faint bottom shadow edge
                                )
                            ),
                            shape = RoundedCornerShape(22.dp)
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onTap(item) }
                        )
                ) {
                    // Movie Poster Artwork
                    AsyncImage(
                        model = item.poster ?: item.backdrop,
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Depth-of-Field (DOF) Frosted Scrim for inactive side cards
                    if (absOffset > 0.05f) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF08090B).copy(alpha = (absOffset * 0.44f).coerceIn(0f, 0.52f)))
                        )
                    }

                    // Top-Left Heart / Bookmark Action Button (from Reference UI)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0x6608090B))
                            .border(1.dp, Color(0x44FFFFFF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = VortexIcons.Heart,
                            contentDescription = "Save",
                            tint = Color(0xFFFFFFFF),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Bottom Dark Glass Gradient Scrim Overlay for Typography
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.55f)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color(0x6608090B),
                                        Color(0xDD08090B),
                                        Color(0xFF08090B)
                                    )
                                )
                            )
                    )

                    // Integrated Information Inside Card (Matching Reference UI exactly)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 14.dp, vertical = 14.dp)
                    ) {
                        // Title + Type Pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = item.title,
                                color = Color(0xFFFFFFFF),
                                fontSize = 19.sp,
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.3).sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x33334050))
                                    .border(0.8.dp, Color(0x444A5462), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (item.type == MediaType.TvSeries) "Series" else "Movie",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 10.sp,
                                    fontFamily = ManropeFontFamily,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // 2-line real synopsis
                        val synopsis = item.synopsis?.takeIf { it.isNotBlank() }
                            ?: "Watch the high-rated cinematic experience streaming exclusively on MovieVortex."
                        Text(
                            text = synopsis,
                            color = Color(0xFFA0A5AD),
                            fontSize = 11.sp,
                            fontFamily = ManropeFontFamily,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Metadata Badges Row: [Genre] [Genre2] [Year] [+15] [★ 8.8 IMDb]
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val genres = item.genres.take(2).ifEmpty { listOf("Cinema") }
                            genres.forEach { g ->
                                HeroMetadataBadge(text = g)
                            }

                            val yearText = item.year?.toString() ?: "2024"
                            HeroMetadataBadge(text = yearText)

                            HeroMetadataBadge(text = "+15")

                            val rating = item.rating?.takeIf { it > 0f } ?: 8.5f
                            HeroMetadataBadge(text = "★ %.1f IMDb".format(rating), isHighlight = true)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons: Pure White "Watch Now" Pill + Frosted Glass "+ Wishlist"
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Pure White "Watch Now" Button (Direct match to MovieVortex_NavBar_Dune_Preview.png)
                            Row(
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(21.dp))
                                    .background(Color.White)
                                    .clickable { onTap(item) },
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = VortexIcons.Play,
                                    contentDescription = "Watch Now",
                                    tint = Color(0xFF0A0A0D),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Watch Now",
                                    color = Color(0xFF0A0A0D),
                                    fontSize = 13.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Glassy Frosted "+ Wishlist" Button
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(21.dp))
                                    .background(Color(0x33FFFFFF))
                                    .border(1.dp, Color(0x4DFFFFFF), RoundedCornerShape(21.dp))
                                    .clickable { /* Toggle Wishlist */ },
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "+ Wishlist",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Apple-Style Glassy Pager Pill Indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val totalDots = items.size.coerceAtMost(10)
            for (idx in 0 until totalDots) {
                val isSelected = idx == (activeIndex % totalDots)
                val dotWidth by animateDpAsState(
                    targetValue = if (isSelected) 22.dp else 5.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "apple_dot_width"
                )
                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .width(dotWidth)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFFFFFFFF) else Color(0x33FFFFFF))
                )
            }
        }
    }
}

@Composable
private fun HeroMetadataBadge(text: String, isHighlight: Boolean = false) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isHighlight) Color(0x33FFC107) else Color(0x3325282D))
            .border(0.8.dp, if (isHighlight) Color(0x66FFC107) else Color(0x444A5462), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = if (isHighlight) Color(0xFFFFC107) else Color(0xFFFFFFFF),
            fontSize = 10.sp,
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onSeeAllClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp
            )
        }
        Text(
            text = "See all",
            color = Color(0xFF8E95A3),
            fontSize = 12.sp,
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onSeeAllClick?.invoke() }
        )
    }
}

@Composable
fun ContentRowSection(
    row: ContentRow,
    onMediaClick: (MovieVortexMedia) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(title = row.title, icon = null)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(row.items) { item ->
                PosterCard(
                    media = item,
                    onClick = { onMediaClick(item) },
                    width = 135.dp
                )
            }
        }
    }
}

@Composable
fun FeaturedHero(
    items: List<MovieVortexMedia>,
    onMediaClick: (MovieVortexMedia) -> Unit,
    onPlayClick: (MovieVortexMedia) -> Unit,
    onMyListClick: (MovieVortexMedia) -> Unit = {},
    modifier: Modifier = Modifier
) {
    VideoStyle3DHeroCarousel(
        items = items,
        onTap = onMediaClick,
        modifier = modifier
    )
}
