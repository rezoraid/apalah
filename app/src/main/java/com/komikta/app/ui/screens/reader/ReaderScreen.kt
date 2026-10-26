package com.komikta.app.ui.screens.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.komikta.app.domain.model.UiState
import com.komikta.app.ui.components.ErrorState
import com.komikta.app.ui.components.LoadingState
import com.komikta.app.ui.theme.Background
import com.komikta.app.ui.theme.Surface
import com.komikta.app.ui.theme.TextPrimary
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ReaderScreen(
    source: String,
    sourceId: String,
    chapter: String,
    onBack: () -> Unit,
    viewModel: ReaderViewModel = koinViewModel { parametersOf(source, sourceId, chapter) }
) {
    val state by viewModel.uiState.collectAsState()
    var showControls by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .clickable { showControls = !showControls }
    ) {
        when (val s = state) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(s.message) { viewModel.load() }
            is UiState.Empty -> ErrorState("Tidak ada halaman") { viewModel.load() }
            is UiState.Success -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(s.data.images, key = { i, _ -> i }) { _, url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        if (showControls) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface.copy(alpha = 0.92f))
                    .padding(8.dp)
                    .align(Alignment.TopCenter),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = TextPrimary)
                }
                Column {
                    Text(
                        text = chapter,
                        color = TextPrimary,
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
