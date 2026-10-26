package com.komikta.app.ui.screens.home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.komikta.app.R
import com.komikta.app.domain.model.Comic
import com.komikta.app.domain.model.UiState
import com.komikta.app.ui.components.ErrorState
import com.komikta.app.ui.components.HorizontalComicList
import com.komikta.app.ui.components.LoadingState
import com.komikta.app.ui.components.SectionHeader
import com.komikta.app.ui.components.UpdateListItem
import com.komikta.app.ui.theme.Accent
import com.komikta.app.ui.theme.Background
import com.komikta.app.ui.theme.OnAccent
import com.komikta.app.ui.theme.Surface
import com.komikta.app.ui.theme.SurfaceElevated
import com.komikta.app.ui.theme.TextMuted
import com.komikta.app.ui.theme.TextPrimary
import com.komikta.app.ui.theme.TextSecondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onComicClick: (Comic) -> Unit,
    onSeeAllLatest: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "KOMIKTA",
                    color = TextPrimary,
                    style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Baca cerita, temukan dunia.",
                    color = TextMuted,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                )
            }
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SurfaceElevated)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cari",
                    tint = TextPrimary
                )
            }
        }

        when (val s = state) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(message = s.message, onRetry = { viewModel.load() })
            is UiState.Empty -> ErrorState(message = "Tidak ada data", onRetry = { viewModel.load() })
            is UiState.Success -> {
                val content = s.data
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Hero promo card
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Surface)
                                .padding(20.dp)
                        ) {
                            Column {
                                Text(
                                    text = "DISCOVER YOUR NEXT STORY",
                                    color = TextMuted,
                                    style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                                    modifier = Modifier
                                        .background(SurfaceElevated, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = "Satu chapter lagi.\nSatu dunia baru.",
                                    color = TextPrimary,
                                    style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Temukan manga, manhwa, dan manhua pilihanmu.",
                                    color = TextSecondary,
                                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                                )
                                Spacer(Modifier.height(16.dp))
                                Button(
                                    onClick = onSeeAllLatest,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = OnAccent,
                                        contentColor = Background
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                ) {
                                    Text("Jelajahi koleksi →")
                                }
                            }
                        }
                    }

                    // Recommendations (using popular)
                    if (content.popular.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Pilihan untukmu",
                                actionLabel = "Lihat semua",
                                onAction = onSeeAllLatest
                            )
                        }
                        item {
                            HorizontalComicList(
                                comics = content.popular,
                                onComicClick = onComicClick
                            )
                        }
                    }

                    // Latest updates (vertical list style like screenshot)
                    if (content.latest.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Update terbaru",
                                actionLabel = "Eksplorasi",
                                onAction = onSeeAllLatest
                            )
                        }
                        items(content.latest.take(15), key = { it.id }) { comic ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                                UpdateListItem(
                                    comic = comic,
                                    onClick = { onComicClick(comic) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
