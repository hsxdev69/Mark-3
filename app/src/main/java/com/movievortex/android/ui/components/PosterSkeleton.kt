package com.movievortex.android.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.movievortex.android.ui.theme.VortexColors

@Composable
fun PosterSkeleton(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    aspectRatio: Float = 2f / 3f
) {
    val transition = rememberInfiniteTransition(label = "shimmer_transition")

    val translateAnim by transition.animateFloat(
        initialValue = -300f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translation"
    )

    val breathScale by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_scale"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            VortexColors.SkeletonDark,
            VortexColors.SkeletonShimmer,
            Color(0xFF383848),
            VortexColors.SkeletonShimmer,
            VortexColors.SkeletonDark
        ),
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim + 200f, translateAnim + 200f)
    )

    Box(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(cornerRadius))
            .background(shimmerBrush),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(0.35f * breathScale),
            contentAlignment = Alignment.Center
        ) {
            MovieVortexMark(
                size = 38.dp,
                showGlow = false
            )
        }
    }
}

/**
 * Shimmering skeleton screen displayed on Home Screen while MovieVortex content loads.
 * Mirrors the real Home layout: Hero banner, Category chips, and multiple Content Rows.
 */
@Composable
fun HomeScreenSkeleton(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "home_skeleton_shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -400f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_anim"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF141414),
            Color(0xFF262626),
            Color(0xFF141414)
        ),
        start = Offset(translateAnim - 300f, translateAnim - 300f),
        end = Offset(translateAnim + 300f, translateAnim + 300f)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VortexColors.PureBlack),
        contentPadding = PaddingValues(bottom = 96.dp),
        userScrollEnabled = false
    ) {
        // 1. Full-Bleed Hero Carousel Skeleton
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.72f)
                    .background(shimmerBrush)
            ) {
                // Bottom gradient fade into pure black
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.55f)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0x99000000),
                                    VortexColors.PureBlack
                                )
                            )
                        )
                )

                // Hero Content Placeholders (Badges, Title, Button)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(width = 68.dp, height = 20.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF222222))
                        )
                        Box(
                            modifier = Modifier
                                .size(width = 48.dp, height = 20.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF222222))
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Title placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.68f)
                            .height(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF222222))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Subtitle / genres
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.42f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF1E1E1E))
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Pill Button
                    Box(
                        modifier = Modifier
                            .width(135.dp)
                            .height(46.dp)
                            .clip(RoundedCornerShape(23.dp))
                            .background(Color(0xFF2A2A2A))
                    )
                }
            }
        }

        // 2. Category Chips Skeleton Row
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .width(72.dp)
                            .height(34.dp)
                            .clip(RoundedCornerShape(17.dp))
                            .background(shimmerBrush)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 3. Content Section 1 Skeleton (Trending Now)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(shimmerBrush)
                    )
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(shimmerBrush)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(3) {
                        Column(modifier = Modifier.width(135.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(2f / 3f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(shimmerBrush)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(shimmerBrush)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(shimmerBrush)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // 4. Content Section 2 Skeleton (Popular Movies & Series)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(shimmerBrush)
                    )
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(shimmerBrush)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(3) {
                        Column(modifier = Modifier.width(135.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(2f / 3f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(shimmerBrush)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(shimmerBrush)
                            )
                        }
                    }
                }
            }
        }
    }
}
