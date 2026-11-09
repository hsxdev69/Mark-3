package com.movievortex.android.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lagradost.cloudstream3.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MovieVortexSplashScreen(
    onSplashFinished: () -> Unit
) {
    // Animatable values for cinematic zoom & fade entrance
    val splashAlpha = remember { Animatable(0f) }
    val splashScale = remember { Animatable(1.08f) }
    val loaderAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Parallel launch of cinematic entrance animation
        launch {
            splashAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        }
        launch {
            splashScale.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing)
            )
        }
        launch {
            delay(300)
            loaderAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = LinearEasing)
            )
        }

        // Total splash display duration before seamless transition
        delay(2400)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Animated Full-Screen Splash Artwork (Ken-Burns smooth subtle zoom-out & fade-in)
        Image(
            painter = painterResource(id = R.drawable.vortex_splash_bg),
            contentDescription = "MovieVortex Splash",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .scale(splashScale.value)
                .alpha(splashAlpha.value)
        )

        // Subtle gradient overlay at bottom for optimal loader contrast
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x99000000),
                            Color(0xE6000000)
                        )
                    )
                )
        )

        // Sabse bottom mein 3 loading animated dots
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 36.dp)
                .alpha(loaderAlpha.value),
            contentAlignment = Alignment.Center
        ) {
            ThreeDotLoader(
                dotColor = Color.White,
                dotSize = 10.dp
            )
        }
    }
}

@Composable
fun ThreeDotLoader(
    modifier: Modifier = Modifier,
    dotColor: Color = Color.White,
    dotSize: Dp = 10.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "three_dot_loader")

    // Smooth physics-like staggered bounce
    val dot1Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -14f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1_offset"
    )

    val dot2Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -14f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, delayMillis = 130, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2_offset"
    )

    val dot3Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -14f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, delayMillis = 260, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3_offset"
    )

    // Pulsing subtle glow scale
    val dot1Scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1_scale"
    )

    val dot2Scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, delayMillis = 130, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2_scale"
    )

    val dot3Scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, delayMillis = 260, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3_scale"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .offset(y = dot1Offset.dp)
                .scale(dot1Scale)
                .size(dotSize)
                .clip(CircleShape)
                .background(dotColor)
        )
        Box(
            modifier = Modifier
                .offset(y = dot2Offset.dp)
                .scale(dot2Scale)
                .size(dotSize)
                .clip(CircleShape)
                .background(dotColor)
        )
        Box(
            modifier = Modifier
                .offset(y = dot3Offset.dp)
                .scale(dot3Scale)
                .size(dotSize)
                .clip(CircleShape)
                .background(dotColor)
        )
    }
}
