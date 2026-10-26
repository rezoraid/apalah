package com.komikta.app.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.komikta.app.domain.model.Comic
import com.komikta.app.ui.components.ComicCoverCard
import com.komikta.app.ui.components.EmptyState
import com.komikta.app.ui.theme.Accent
import com.komikta.app.ui.theme.Background
import com.komikta.app.ui.theme.TextMuted
import com.komikta.app.ui.theme.TextPrimary
import org.koin.androidx.compose.koinViewModel

@Composable
fun LibraryScreen(
    onComicClick: (Comic) -> Unit,
    onExploreClick: () -> Unit,
    viewModel: LibraryViewModel = koinViewModel()
) {
    val bookmarks by viewModel.bookmarks.collectAsState()
    val history by viewModel.history.collectAsState()
    var tab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Text(
            text = "Library",
            color = TextPrimary,
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        TabRow(
            selectedTabIndex = tab,
            containerColor = Background,
            contentColor = Accent,
            indicator = { positions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(positions[tab]),
                    color = Accent
                )
            }
        ) {
            Tab(
                selected = tab == 0,
                onClick = { tab = 0 },
                text = { Text("Bookmark", color = if (tab == 0) Accent else TextMuted) }
            )
            Tab(
                selected = tab == 1,
                onClick = { tab = 1 },
                text = { Text("Riwayat", color = if (tab == 1) Accent else TextMuted) }
            )
        }

        when (tab) {
            0 -> {
                if (bookmarks.isEmpty()) {
                    EmptyState(
                        message = "Belum ada bookmark",
                        actionLabel = "Jelajahi",
                        onAction = onExploreClick
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(120.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(bookmarks, key = { it.id }) { comic ->
                            ComicCoverCard(comic = comic, onClick = { onComicClick(comic) })
                        }
                    }
                }
            }
            1 -> {
                if (history.isEmpty()) {
                    EmptyState(
                        message = "Belum ada riwayat baca",
                        actionLabel = "Jelajahi",
                        onAction = onExploreClick
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(120.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(history, key = { it.comicId }) { h ->
                            val comic = Comic(
                                id = h.comicId,
                                source = h.source,
                                sourceId = h.sourceId,
                                title = h.title,
                                coverUrl = h.coverUrl,
                                latestChapter = h.chapterNumber
                            )
                            ComicCoverCard(comic = comic, onClick = { onComicClick(comic) })
                        }
                    }
                }
            }
        }
    }
}
