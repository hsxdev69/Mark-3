package com.movievortex.android.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import com.movievortex.android.ui.components.VortexIcons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movievortex.android.model.MovieVortexMedia
import com.movievortex.android.model.StreamingPlatform
import com.movievortex.android.ui.components.PosterCard
import com.movievortex.android.ui.theme.VortexColors

@Composable
fun PlatformCatalogScreen(
    platform: StreamingPlatform,
    catalogItems: List<MovieVortexMedia>,
    onMediaClick: (MovieVortexMedia) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VortexColors.PureBlack)
    ) {
        // Platform Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = VortexIcons.ArrowBack,
                    contentDescription = "Back",
                    tint = VortexColors.PrimaryText
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Platform Badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(VortexColors.CardSurface)
                    .border(1.dp, VortexColors.BorderSubtle, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = platform.name.take(3).uppercase(),
                    color = Color(platform.primaryColor),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = platform.name,
                    color = VortexColors.PrimaryText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = platform.description,
                    color = VortexColors.MutedText,
                    fontSize = 11.sp
                )
            }
        }

        // Responsive Poster Grid (3 columns chunked)
        val chunked = catalogItems.chunked(3)
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chunked) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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
