package com.movievortex.android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.movievortex.android.ui.theme.ManropeFontFamily
import com.movievortex.android.ui.theme.VortexColors

enum class MainDestination {
    Home,
    Categories,
    Search,
    Bookmarks,
    Me,
    LiveTv,
    Downloads;

    companion object {
        val MoviesTv get() = Home
        val Anime get() = Home
        val Settings get() = Me
        val Library get() = Bookmarks
        val Profile get() = Me
    }
}

@Composable
fun MovieVortexTopBar(
    onSearchClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    isTransparent: Boolean = true
) {
    val bgModifier = if (isTransparent) {
        Modifier.background(
            Brush.verticalGradient(
                listOf(
                    Color(0xF008090B),
                    Color(0xCC10121A),
                    Color(0x6608090B),
                    Color.Transparent
                )
            )
        )
    } else {
        Modifier.background(Color(0xFF08090B))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(bgModifier)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Circular Frosted Glass Button with Staggered Menu Icon
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0x262A3442))
                .border(1.dp, Color(0x444A5462), CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { /* Menu */ }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = VortexIcons.StaggeredMenu,
                contentDescription = "Menu",
                tint = Color(0xFFFFFFFF),
                modifier = Modifier.size(20.dp)
            )
        }

        // Center: High-End Monochrome OTT Title "MovieVortex" (Pure White + Crisp Silver)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Movie",
                color = Color(0xFFFFFFFF),
                fontSize = 20.sp,
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.4).sp
            )
            Text(
                text = "Vortex",
                color = Color(0xFFA8B4C8),
                fontSize = 20.sp,
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.4).sp
            )
        }

        // Right: Circular Frosted Glass Button with Search Icon
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0x262A3442))
                .border(1.dp, Color(0x444A5462), CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onSearchClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = VortexIcons.Search,
                contentDescription = "Search",
                tint = Color(0xFFFFFFFF),
                modifier = Modifier.size(19.dp)
            )
        }
    }
}

@Composable
fun MovieVortexBottomBar(
    currentDestination: MainDestination,
    onDestinationSelected: (MainDestination) -> Unit,
    modifier: Modifier = Modifier,
    activeDownloadCount: Int = 0,
    wishlistBadgeCount: Int = 7
) {
    // Specs from MovieVortex_NavBar_Dune_Preview.png:
    // Background: Translucent dark color (#0a0a0d) with backdrop blur and subtle top border (border-white/[0.07])
    // Layout: Centered, max-width constraint, spaced items (justify-between)
    // Items: 1. Home, 2. Categories, 3. Search, 4. Wishlist (Heart with red badge '7'), 5. Profile
    // Active State: scaled up (scale-110), text pure white, illuminated horizontal gradient indicator bar (amber-400 to rose-500) directly above icon
    // Inactive State: muted gray (text-zinc-500: #71717A)
    // Typography: Small, clean, modern sans-serif (9px, tracking-wide)
    // Bottom Bar: iOS-style home indicator bar (h-1 w-28 bg-white/25) below nav bar
    val darkBg = Color(0xF20A0A0D)
    val topBorderColor = Color(0x12FFFFFF)
    val zinc500 = Color(0xFF71717A)
    val pureWhite = Color(0xFFFFFFFF)
    val rose500 = Color(0xFFF43F5E)
    val amber400 = Color(0xFFFBBF24)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(darkBg)
            .drawBehind {
                drawLine(
                    color = topBorderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .windowInsetsPadding(WindowInsets.navigationBars),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Home
            val isHome = currentDestination == MainDestination.Home
            MovieVortexNavItem(
                name = "Home",
                icon = if (isHome) VortexIcons.Home else VortexIcons.HomeOutline,
                isSelected = isHome,
                activeColor = pureWhite,
                inactiveColor = zinc500,
                amberColor = amber400,
                roseColor = rose500,
                onClick = { onDestinationSelected(MainDestination.Home) }
            )

            // 2. Categories
            val isCategories = currentDestination == MainDestination.Categories
            MovieVortexNavItem(
                name = "Categories",
                icon = VortexIcons.GridView,
                isSelected = isCategories,
                activeColor = pureWhite,
                inactiveColor = zinc500,
                amberColor = amber400,
                roseColor = rose500,
                onClick = { onDestinationSelected(MainDestination.Categories) }
            )

            // 3. Search
            val isSearch = currentDestination == MainDestination.Search
            MovieVortexNavItem(
                name = "Search",
                icon = VortexIcons.Search,
                isSelected = isSearch,
                activeColor = pureWhite,
                inactiveColor = zinc500,
                amberColor = amber400,
                roseColor = rose500,
                onClick = { onDestinationSelected(MainDestination.Search) }
            )

            // 4. Wishlist
            val isWishlist = currentDestination == MainDestination.Bookmarks
            MovieVortexNavItem(
                name = "Wishlist",
                icon = if (isWishlist) VortexIcons.Heart else VortexIcons.HeartBorder,
                isSelected = isWishlist,
                badgeText = wishlistBadgeCount.toString(),
                badgeBgColor = rose500,
                activeColor = pureWhite,
                inactiveColor = zinc500,
                amberColor = amber400,
                roseColor = rose500,
                onClick = { onDestinationSelected(MainDestination.Bookmarks) }
            )

            // 5. Profile
            val isProfile = currentDestination == MainDestination.Me
            MovieVortexNavItem(
                name = "Profile",
                icon = if (isProfile) VortexIcons.Person else VortexIcons.PersonOutline,
                isSelected = isProfile,
                activeColor = pureWhite,
                inactiveColor = zinc500,
                amberColor = amber400,
                roseColor = rose500,
                onClick = { onDestinationSelected(MainDestination.Me) }
            )
        }

        // iOS-style home indicator bar (h-1 w-28 bg-white/25)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(112.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0x40FFFFFF))
        )
        Spacer(modifier = Modifier.height(6.dp))
    }
}

@Composable
private fun MovieVortexNavItem(
    name: String,
    icon: ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    amberColor: Color,
    roseColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeText: String? = null,
    badgeBgColor: Color = Color(0xFFF43F5E)
) {
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
        targetValue = if (isPressed) 0.90f else if (isSelected) 1.10f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "nav_item_scale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else inactiveColor,
        animationSpec = tween(durationMillis = 200),
        label = "nav_icon_color"
    )

    Column(
        modifier = modifier
            .width(64.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Illuminated horizontal gradient indicator bar directly above the icon
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(42.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(amberColor, roseColor)
                        )
                    )
            )
        } else {
            Spacer(modifier = Modifier.height(3.dp))
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Icon with optional notification badge
        Box(
            modifier = Modifier
                .size(30.dp)
                .graphicsLayer {
                    scaleX = pressScale
                    scaleY = pressScale
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )

            // Red notification badge (bg-rose-500) displaying number '7'
            if (badgeText != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = (-2).dp, end = (-4).dp)
                        .size(15.dp)
                        .clip(CircleShape)
                        .background(badgeBgColor)
                        .border(1.dp, Color(0xFF0A0A0D), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeText,
                        color = Color.White,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Typography: Small, clean, medium-weight font (9px, tracking-wide)
        Text(
            text = name,
            color = iconColor,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 0.5.sp,
            maxLines = 1
        )
    }
}
