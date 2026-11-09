package com.movievortex.android.ui.library

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lagradost.cloudstream3.utils.DataStoreHelper
import com.movievortex.android.model.MovieVortexMedia
import com.movievortex.android.ui.components.PosterCard
import com.movievortex.android.ui.components.VortexIcons
import com.movievortex.android.ui.theme.VortexColors

@Composable
fun HistoryScreen(
    onMediaClick: (MovieVortexMedia) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val historyItems = remember {
        val resumeMap = DataStoreHelper.getAllResumeStateIds() ?: emptyList()
        val list = mutableListOf<MovieVortexMedia>()
        resumeMap.forEach { resumeItem ->
            val pos = DataStoreHelper.getViewPos(resumeItem)
            val bookmarked = DataStoreHelper.getBookmarkedData(resumeItem)
            val title = bookmarked?.name ?: "Watched Item"
            val poster = bookmarked?.posterUrl
            val progress = if (pos != null && pos.duration > 0) {
                (pos.position.toFloat() / pos.duration.toFloat()).coerceIn(0f, 1f)
            } else 0f

            list.add(
                MovieVortexMedia(
                    id = resumeItem.toString(),
                    title = title,
                    poster = poster,
                    backdrop = poster,
                    watchProgress = progress,
                    rawUrl = bookmarked?.url ?: resumeItem.toString(),
                    providerApiName = bookmarked?.apiName ?: ""
                )
            )
        }
        list
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VortexColors.BackgroundOled)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = VortexIcons.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "Watch History",
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${historyItems.size} watched",
                color = VortexColors.TextMuted,
                fontSize = 13.sp,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(end = 12.dp)
            )
        }

        if (historyItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = VortexIcons.Refresh,
                        contentDescription = null,
                        tint = VortexColors.TextMuted,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Watch History",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Titles you start streaming will appear here with your playback progress.",
                        color = VortexColors.TextMuted,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(historyItems, key = { it.id }) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(VortexColors.CardSurface)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PosterCard(
                            media = item,
                            onClick = { onMediaClick(item) },
                            width = 75.dp,
                            showDetails = false
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp)
                        ) {
                            Text(
                                text = item.title,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val pct = (item.watchProgress * 100).toInt()
                            Text(
                                text = if (pct > 0) "$pct% completed" else "Started watching",
                                color = VortexColors.NeonCrimson,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }
                }
            }
        }
    }
}
