package com.movievortex.android.ui.mylist

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
import com.movievortex.android.ui.components.VortexIcons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movievortex.android.controller.LibraryController
import com.movievortex.android.model.MovieVortexMedia
import com.movievortex.android.ui.components.PosterCard
import com.movievortex.android.ui.theme.VortexColors

@Composable
fun MyListScreen(
    libraryController: LibraryController,
    onMediaClick: (MovieVortexMedia) -> Unit,
    onBrowseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by libraryController.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VortexColors.PureBlack)
            .padding(top = 16.dp)
    ) {
        // Heading & Saved count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "My List",
                color = VortexColors.PrimaryText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            if (state.items.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .background(VortexColors.CardSurface, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${state.items.size} titles",
                        color = VortexColors.SecondaryText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.items.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = VortexIcons.BookmarkBorder,
                        contentDescription = null,
                        tint = VortexColors.MutedText,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your list is empty",
                        color = VortexColors.PrimaryText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Save movies and TV shows to watch them later",
                        color = VortexColors.MutedText,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onBrowseClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Explore Movies & Shows", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Responsive 3-column Poster Grid (chunked)
            val chunked = state.items.chunked(3)
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
}
