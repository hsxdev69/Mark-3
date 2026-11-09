package com.movievortex.android.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movievortex.android.ui.theme.VortexColors

/**
 * Geometric, cinematic brand mark for MovieVortex:
 * An interconnected vortex diamond prism with sharp cinematic angles and a glowing red apex.
 */
@Composable
fun MovieVortexMark(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    showGlow: Boolean = true
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Subtle glow aura behind mark
        if (showGlow) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        VortexColors.CinematicRedGlow,
                        Color.Transparent
                    ),
                    center = Offset(w / 2f, h / 2f),
                    radius = w * 0.7f
                ),
                radius = w * 0.7f,
                center = Offset(w / 2f, h / 2f)
            )
        }

        // Left wing facet (Sleek dark silver to white)
        val leftPath = Path().apply {
            moveTo(w * 0.12f, h * 0.28f)
            lineTo(w * 0.48f, h * 0.08f)
            lineTo(w * 0.40f, h * 0.88f)
            lineTo(w * 0.20f, h * 0.74f)
            close()
        }
        drawPath(
            path = leftPath,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFFFFFF), Color(0xFF888899)),
                start = Offset(0f, 0f),
                end = Offset(w * 0.5f, h)
            )
        )

        // Right wing facet (Deep violet-silver)
        val rightPath = Path().apply {
            moveTo(w * 0.88f, h * 0.28f)
            lineTo(w * 0.52f, h * 0.08f)
            lineTo(w * 0.60f, h * 0.88f)
            lineTo(w * 0.80f, h * 0.74f)
            close()
        }
        drawPath(
            path = rightPath,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFE2E8F0), Color(0xFF64748B)),
                start = Offset(w, 0f),
                end = Offset(w * 0.5f, h)
            )
        )

        // Center Vortex Core (Cinematic Red Diamond Spike)
        val centerVortex = Path().apply {
            moveTo(w * 0.50f, h * 0.05f)
            lineTo(w * 0.66f, h * 0.50f)
            lineTo(w * 0.50f, h * 0.95f)
            lineTo(w * 0.34f, h * 0.50f)
            close()
        }
        drawPath(
            path = centerVortex,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFF3344),
                    VortexColors.CinematicRed,
                    Color(0xFF990008)
                )
            )
        )

        // Center focal accent line
        drawLine(
            color = Color.White.copy(alpha = 0.8f),
            start = Offset(w * 0.50f, h * 0.15f),
            end = Offset(w * 0.50f, h * 0.75f),
            strokeWidth = w * 0.04f
        )
    }
}

@Composable
fun MovieVortexHeaderLogo(
    modifier: Modifier = Modifier,
    markSize: Dp = 28.dp,
    fontSize: TextUnit = 20.sp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MovieVortexMark(size = markSize)
        Row {
            Text(
                text = "MOVIE",
                fontWeight = FontWeight.Black,
                fontSize = fontSize,
                letterSpacing = 1.2.sp,
                color = VortexColors.PrimaryText
            )
            Text(
                text = "VORTEX",
                fontWeight = FontWeight.Black,
                fontSize = fontSize,
                letterSpacing = 1.2.sp,
                color = VortexColors.CinematicRed
            )
        }
    }
}
