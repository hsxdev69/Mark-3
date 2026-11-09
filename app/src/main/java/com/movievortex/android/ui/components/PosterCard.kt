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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.movievortex.android.model.MediaType
import com.movievortex.android.model.MovieVortexMedia
import com.movievortex.android.ui.theme.ManropeFontFamily
import com.movievortex.android.ui.theme.VortexColors

@Composable
fun PosterCard(
    media: MovieVortexMedia,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 135.dp,
    showDetails: Boolean = true,
    showBadges: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Track press state for scale animation
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

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "posterCardScale"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 12.dp,
        animationSpec = tween(durationMillis = 150),
        label = "posterElevation"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isPressed) Color(0x66FFFFFF) else Color(0x224A5462),
        animationSpec = tween(durationMillis = 150),
        label = "posterBorder"
    )

    // Animate watch progress for smooth fill
    val animatedProgress by animateFloatAsState(
        targetValue = media.watchProgress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "watchProgress"
    )

    Column(
        modifier = modifier
            .width(width)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        // Poster Box with cinematic shadow glow, 16dp rounding & glass border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .shadow(shadowElevation, RoundedCornerShape(16.dp), spotColor = Color(0xAA000000))
                .clip(RoundedCornerShape(16.dp))
                .background(VortexColors.CardSurface)
                .border(1.dp, borderColor, RoundedCornerShape(16.dp))
        ) {
            // Poster image
            SubcomposeAsyncImage(
                model = media.poster ?: media.backdrop,
                contentDescription = media.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    PosterSkeleton(cornerRadius = 16.dp)
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(VortexColors.CardSurface)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = VortexIcons.Movie,
                                contentDescription = null,
                                tint = VortexColors.MutedText,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = media.title,
                                color = VortexColors.MutedText,
                                fontSize = 10.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            )

            // Bottom cinematic gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xCC000000))
                        )
                    )
            )

            // Top Badges (4K UHD & Star Rating) — only when enabled
            if (showBadges) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 4K UHD Badge with gradient background
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xE0000000), Color(0xC0000000))
                                )
                            )
                            .border(0.8.dp, Color(0x66FFFFFF), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        val label = when (media.type) {
                            MediaType.Anime -> "ANIME"
                            MediaType.TvSeries -> "SERIES"
                            else -> "4K UHD"
                        }
                        Text(
                            text = label,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.4.sp
                        )
                    }

                    // Star Rating Badge with gold star
                    val rating = media.rating ?: 0f
                    if (rating > 0f) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xE0000000), Color(0xC0000000))
                                    )
                                )
                                .border(0.8.dp, Color(0x66FFFFFF), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(SpanStyle(color = Color(0xFFFFD700))) {
                                        append("★ ")
                                    }
                                    withStyle(SpanStyle(color = Color.White)) {
                                        append("%.1f".format(rating))
                                    }
                                },
                                fontSize = 9.sp,
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Watch Progress Bar (if watched)
            if (media.watchProgress > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(Color(0x33FFFFFF))
                        .align(Alignment.BottomStart)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress.coerceIn(0.05f, 1f))
                            .height(3.dp)
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(Color.White)
                    )
                }
            }
        }

        if (showDetails) {
            Spacer(modifier = Modifier.height(8.dp))

            // Title — 13sp Bold Manrope with tighter letter spacing
            Text(
                text = media.title,
                color = VortexColors.PrimaryText,
                fontSize = 13.sp,
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Subtitle: Genre • Year • 4K
            val subtitleText = buildString {
                media.genres.firstOrNull()?.let { genre ->
                    append(genre)
                    append(" • ")
                }
                media.year?.let { year ->
                    append(year)
                    append(" • ")
                }
                append("4K")
            }
            Text(
                text = subtitleText,
                color = VortexColors.MutedText,
                fontSize = 11.sp,
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
