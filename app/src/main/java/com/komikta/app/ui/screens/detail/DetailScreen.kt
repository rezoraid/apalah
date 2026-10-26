package com.komikta.app.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.komikta.app.domain.model.Chapter
import com.komikta.app.domain.model.UiState
import com.komikta.app.ui.components.ErrorState
import com.komikta.app.ui.components.LoadingState
import com.komikta.app.ui.theme.Accent
import com.komikta.app.ui.theme.Background
import com.komikta.app.ui.theme.OnAccent
import com.komikta.app.ui.theme.SurfaceElevated
import com.komikta.app.ui.theme.TextMuted
import com.komikta.app.ui.theme.TextPrimary
import com.komikta.app.ui.theme.TextSecondary
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DetailScreen(
    source: String,
    sourceId: String,
    onBack: () -> Unit,
    onReadChapter: (Chapter) -> Unit,
    viewModel: DetailViewModel = koinViewModel { parametersOf(source, sourceId) }
) {
    val state by viewModel.uiState.collectAsState()
    val bookmarked by viewModel.isBookmarked.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = TextPrimary)
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { viewModel.toggleBookmark() }) {
                Icon(
                    imageVector = if (bookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (bookmarked) Accent else TextPrimary
                )
            }
        }

        when (val s = state) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(s.message) { viewModel.load() }
            is UiState.Empty -> ErrorState("Tidak ada data") { viewModel.load() }
            is UiState.Success -> {
                val comic = s.data.comic
                val chapters = s.data.chapters
                LazyColumn {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            AsyncImage(
                                model = comic.coverUrl,
                                contentDescription = comic.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(120.dp)
                                    .aspectRatio(0.7f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceElevated)
                            )
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = comic.title,
                                    color = TextPrimary,
                                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(6.dp))
                                if (!comic.status.isNullOrBlank()) {
                                    Text(text = comic.status, color = Accent, style = androidx.compose.material3.MaterialTheme.typography.labelMedium)
                                }
                                if (comic.genres.isNotEmpty()) {
                                    Text(
                                        text = comic.genres.take(5).joinToString(" • "),
                                        color = TextMuted,
                                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                                        maxLines = 2
                                    )
                                }
                                Spacer(Modifier.height(12.dp))
                                if (chapters.isNotEmpty()) {
                                    Button(
                                        onClick = { onReadChapter(chapters.first()) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Accent),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Baca", color = OnAccent)
                                    }
                                }
                            }
                        }
                    }
                    if (!comic.synopsis.isNullOrBlank()) {
                        item {
                            Text(
                                text = comic.synopsis,
                                color = TextSecondary,
                                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                    item {
                        Text(
                            text = "Chapter (${chapters.size})",
                            color = TextPrimary,
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    items(chapters, key = { it.id }) { ch ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onReadChapter(ch) }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = ch.title ?: ch.number,
                                color = TextPrimary,
                                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            if (!ch.releaseDate.isNullOrBlank()) {
                                Text(text = ch.releaseDate, color = TextMuted, style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
