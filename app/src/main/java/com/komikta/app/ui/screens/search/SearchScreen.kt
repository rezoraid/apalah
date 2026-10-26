package com.komikta.app.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.komikta.app.domain.model.Comic
import com.komikta.app.domain.model.UiState
import com.komikta.app.ui.components.ComicCoverCard
import com.komikta.app.ui.components.EmptyState
import com.komikta.app.ui.components.ErrorState
import com.komikta.app.ui.components.LoadingState
import com.komikta.app.ui.theme.Accent
import com.komikta.app.ui.theme.Background
import com.komikta.app.ui.theme.Border
import com.komikta.app.ui.theme.SurfaceElevated
import com.komikta.app.ui.theme.TextMuted
import com.komikta.app.ui.theme.TextPrimary
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onComicClick: (Comic) -> Unit,
    viewModel: SearchViewModel = koinViewModel()
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val recent by viewModel.recent.collectAsState()
    val keyboard = LocalSoftwareKeyboardController.current

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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = TextPrimary)
            }
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Cari judul...", color = TextMuted) },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, null, tint = TextMuted) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(Icons.Default.Clear, null, tint = TextMuted)
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.searchNow()
                    keyboard?.hide()
                }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = Border,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = Accent,
                    focusedContainerColor = SurfaceElevated,
                    unfocusedContainerColor = SurfaceElevated
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        when (val s = results) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(s.message) { viewModel.searchNow() }
            is UiState.Empty -> {
                if (query.isBlank() && recent.isNotEmpty()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Pencarian terakhir", color = TextMuted)
                        recent.forEach { q ->
                            Text(
                                text = q,
                                color = TextPrimary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.useRecent(q) }
                                    .padding(vertical = 10.dp)
                            )
                        }
                    }
                } else if (query.isNotBlank()) {
                    EmptyState("Tidak ada hasil untuk \"$query\"")
                }
            }
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
