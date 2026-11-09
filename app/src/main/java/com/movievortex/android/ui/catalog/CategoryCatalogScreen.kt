package com.movievortex.android.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movievortex.android.model.MovieVortexMedia
import com.movievortex.android.ui.components.PosterCard
import com.movievortex.android.ui.components.VortexIcons
import com.movievortex.android.ui.theme.ManropeFontFamily

@Composable
fun CategoryCatalogScreen(
    title: String,
    catalogItems: List<MovieVortexMedia>,
    onMediaClick: (MovieVortexMedia) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF08090B))
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0x262A3442))
                    .border(1.dp, Color(0x444A5462), CircleShape)
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

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    color = Color(0xFFFFFFFF),
                    fontSize = 20.sp,
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.4).sp
                )
                Text(
                    text = "${catalogItems.size} Titles Available",
                    color = Color(0xFF8E95A3),
                    fontSize = 12.sp,
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (catalogItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No content available in $title right now.",
                    color = Color(0xFF8E95A3),
                    fontSize = 14.sp,
                    fontFamily = ManropeFontFamily
                )
            }
        } else {
            // Responsive 3-column chunked grid of PosterCards
            val chunked = catalogItems.chunked(3)
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(chunked) { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { media ->
                            PosterCard(
                                media = media,
                                onClick = { onMediaClick(media) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
