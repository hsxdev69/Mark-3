package com.movievortex.android.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OttLogoBadge(
    name: String,
    modifier: Modifier = Modifier
) {
    when (name.lowercase()) {
        "prime video", "prime", "amazon" -> PrimeVideoLogo(modifier = modifier)
        "netflix" -> NetflixLogo(modifier = modifier)
        "sony liv", "sonyliv", "sony" -> SonyLivLogo(modifier = modifier)
        "crunchyroll" -> CrunchyrollLogo(modifier = modifier)
        "jiohotstar", "hotstar", "disney+ hotstar", "disney" -> JioHotstarLogo(modifier = modifier)
        "zee5" -> Zee5Logo(modifier = modifier)
        else -> DefaultOttLogo(name = name, modifier = modifier)
    }
}

@Composable
fun PrimeVideoLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(Color(0xFF00A8E1)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text(
                text = "prime",
                color = Color(0xFF002742),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                lineHeight = 12.sp
            )

            // Prime curved smile arrow
            Canvas(modifier = Modifier.size(width = 30.dp, height = 5.dp)) {
                val w = size.width
                val h = size.height
                val smileColor = Color(0xFF002742)

                val path = Path().apply {
                    moveTo(2f, 1f)
                    quadraticBezierTo(w * 0.5f, h * 1.5f, w - 4f, 1.5f)
                }
                drawPath(
                    path = path,
                    color = smileColor,
                    style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
                )

                // Arrow head
                val arrowHead = Path().apply {
                    moveTo(w - 7f, 0f)
                    lineTo(w - 2f, 1.5f)
                    lineTo(w - 4f, 4f)
                }
                drawPath(
                    path = arrowHead,
                    color = smileColor,
                    style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            Text(
                text = "video",
                color = Color(0xFF002742),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.2).sp,
                lineHeight = 9.sp,
                modifier = Modifier.offset(y = (-1).dp)
            )
        }
    }
}

@Composable
fun NetflixLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(Color(0xFF111111)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(width = 22.dp, height = 34.dp)) {
            val w = size.width
            val h = size.height
            val colW = w * 0.32f
            val darkRed = Color(0xFFB81D24)
            val brightRed = Color(0xFFE50914)

            // Left vertical column
            drawRect(
                color = darkRed,
                topLeft = Offset(0f, 0f),
                size = Size(colW, h)
            )

            // Right vertical column
            drawRect(
                color = darkRed,
                topLeft = Offset(w - colW, 0f),
                size = Size(colW, h)
            )

            // Center diagonal ribbon
            val ribbon = Path().apply {
                moveTo(0f, 0f)
                lineTo(colW, 0f)
                lineTo(w, h)
                lineTo(w - colW, h)
                close()
            }
            drawPath(path = ribbon, color = brightRed)
        }
    }
}

@Composable
fun SonyLivLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(Color(0xFF141424)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SONY",
                color = Color.White,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )

            Box(contentAlignment = Alignment.Center) {
                // Gradient arc ribbon
                Canvas(modifier = Modifier.size(width = 38.dp, height = 22.dp)) {
                    val w = size.width
                    val h = size.height
                    val gradient = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFFD600), // Yellow
                            Color(0xFFFF5722), // Orange
                            Color(0xFFE91E63), // Pink
                            Color(0xFF2979FF)  // Blue
                        )
                    )
                    drawArc(
                        brush = gradient,
                        startAngle = 0f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(1f, 1f),
                        size = Size(w - 2f, h * 1.6f),
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Text(
                    text = "liv",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.offset(y = 2.dp)
                )
            }
        }
    }
}

@Composable
fun CrunchyrollLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(Color(0xFFFF6400)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(32.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.width * 0.44f

            // Outer white crescent
            drawCircle(
                color = Color.White,
                radius = outerRadius,
                center = center
            )
            // Orange cutout circle slightly offset to right
            drawCircle(
                color = Color(0xFFFF6400),
                radius = outerRadius * 0.72f,
                center = Offset(center.x + outerRadius * 0.28f, center.y)
            )
            // Inner white pupil
            drawCircle(
                color = Color.White,
                radius = outerRadius * 0.38f,
                center = Offset(center.x + outerRadius * 0.16f, center.y)
            )
        }
    }
}

@Composable
fun JioHotstarLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1B4DFF), Color(0xFF6B26F2))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Sparkle / Star Icon
            Canvas(modifier = Modifier.size(16.dp)) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val r = size.width / 2f

                val star = Path().apply {
                    // 4 main rays
                    moveTo(cx, cy - r)
                    quadraticBezierTo(cx, cy, cx + r, cy)
                    quadraticBezierTo(cx, cy, cx, cy + r)
                    quadraticBezierTo(cx, cy, cx - r, cy)
                    quadraticBezierTo(cx, cy, cx, cy - r)
                }
                drawPath(path = star, color = Color.White)
                // Center glow dot
                drawCircle(color = Color(0xFFFFD700), radius = 2.dp.toPx(), center = Offset(cx, cy))
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "JioHotstar",
                color = Color.White,
                fontSize = 7.5.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.3).sp
            )
        }
    }
}

@Composable
fun Zee5Logo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        // Multi-color rainbow ring
        Canvas(modifier = Modifier.size(50.dp)) {
            val rainbowBrush = Brush.sweepGradient(
                colors = listOf(
                    Color(0xFFE50914), // Red
                    Color(0xFFFF9900), // Orange
                    Color(0xFFFFEE00), // Yellow
                    Color(0xFF00E676), // Green
                    Color(0xFF00E5FF), // Cyan
                    Color(0xFF2979FF), // Blue
                    Color(0xFFD500F9), // Magenta
                    Color(0xFFE50914)  // Red
                )
            )
            drawCircle(
                brush = rainbowBrush,
                style = Stroke(width = 3.dp.toPx())
            )
        }

        Text(
            text = "ZEE5",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun DefaultOttLogo(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(Color(0xFF222222)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.take(3).uppercase(),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun DisneyPlusBadgeCard(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(105.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF091C3E))
            .border(1.dp, Color(0x334B7BEC), RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Disney",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
            Text(
                text = "+",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun NetflixBadgeCard(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(105.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "NETFLIX",
            color = Color(0xFFE50914),
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun PrimeVideoBadgeCard(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(105.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF071E34))
            .border(1.dp, Color(0x3300A8E1), RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "prime video",
            color = Color(0xFF00A8E1),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
