package com.komikta.app.ui.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.komikta.app.domain.model.Comic
import com.komikta.app.domain.model.UiState
import com.komikta.app.ui.components.ComicCoverCard
import com.komikta.app.ui.components.ErrorState
import com.komikta.app.ui.components.LoadingState
import com.komikta.app.ui.theme.Accent
import com.komikta.app.ui.theme.Background
import com.komikta.app.ui.theme.ChipBg
import com.komikta.app.ui.theme.SurfaceElevated
import com.komikta.app.ui.theme.TextMuted
import com.komikta.app.ui.theme.TextPrimary
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExploreScreen(
    onComicClick: (Comic) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: ExploreViewModel = koinViewModel()
) {
    val comicsState by viewModel.comics.collectAsState()
    val genres by viewModel.genres.collectAsState()
    val selected by viewModel.selectedGenre.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Explore",
                color = TextPrimary,
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(Icons.Default.Search, contentDescription = "Cari", tint = TextPrimary)
            }
        }

        if (genres.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                item {
                    GenreChip(
                        label = "Semua",
                        selected = selected == null,
                        onClick = { viewModel.selectGenre(null) }
                    )
                }
                items(genres.take(20), key = { it.slug }) { g ->
                    GenreChip(
                        label = g.name,
                        selected = selected == g.slug,
                        onClick = { viewModel.selectGenre(g.slug) }
                    )
                }
            }
        }

        when (val s = comicsState) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(s.message) { viewModel.loadComics() }
            is UiState.Empty -> ErrorState("Tidak ada komik") { viewModel.loadComics() }
            is UiState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(120.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(s.data, key = { it.id }) { comic ->
                        ComicCoverCard(comic = comic, onClick = { onComicClick(comic) })
                    }
                }
            }
        }
    }
}

@Composable
private fun GenreChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        color = if (selected) TextPrimary else TextMuted,
        style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) Accent else ChipBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    )
}
