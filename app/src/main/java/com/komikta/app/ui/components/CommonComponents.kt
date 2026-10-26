package com.komikta.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.komikta.app.domain.model.Comic
import com.komikta.app.ui.theme.Accent
import com.komikta.app.ui.theme.ChipBg
import com.komikta.app.ui.theme.OnAccent
import com.komikta.app.ui.theme.SurfaceElevated
import com.komikta.app.ui.theme.TextMuted
import com.komikta.app.ui.theme.TextPrimary
import com.komikta.app.ui.theme.TextSecondary

@Composable
fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextPrimary,
            style = androidx.compose.material3.MaterialTheme.typography.titleLarge
        )
        if (actionLabel != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(text = actionLabel, color = TextSecondary)
            }
        }
    }
}

@Composable
fun ComicCoverCard(
    comic: Comic,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showStatus: Boolean = true
) {
    Column(
        modifier = modifier
            .width(120.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.7f)
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceElevated)
        ) {
            AsyncImage(
                model = comic.coverUrl,
                contentDescription = comic.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            if (showStatus && !comic.status.isNullOrBlank()) {
                Text(
                    text = comic.status.uppercase(),
                    color = OnAccent,
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .background(Accent.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = comic.title,
            color = TextPrimary,
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (!comic.latestChapter.isNullOrBlank()) {
            Text(
                text = "Ch. ${comic.latestChapter}",
                color = TextMuted,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }
    }
}

@Composable
fun HorizontalComicList(
    comics: List<Comic>,
    onComicClick: (Comic) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(comics, key = { it.id }) { comic ->
            ComicCoverCard(comic = comic, onClick = { onComicClick(comic) })
        }
    }
}

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Accent)
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, color = TextSecondary)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Accent)
        ) {
            Text("Coba lagi")
        }
    }
}

@Composable
fun EmptyState(
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, color = TextMuted)
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                Text(actionLabel)
            }
        }
    }
}

@Composable
fun UpdateListItem(
    comic: Comic,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceElevated)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = comic.coverUrl,
            contentDescription = comic.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ChipBg)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = comic.title,
                color = TextPrimary,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = comic.latestChapter ?: "-",
                color = TextSecondary,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall
            )
            if (!comic.updatedAt.isNullOrBlank()) {
                Text(
                    text = comic.updatedAt.take(19).replace('T', ' '),
                    color = TextMuted,
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
