package com.komikta.app.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.komikta.app.R
import com.komikta.app.ui.theme.Background
import com.komikta.app.ui.theme.SurfaceElevated
import com.komikta.app.ui.theme.TextMuted
import com.komikta.app.ui.theme.TextPrimary
import com.komikta.app.ui.theme.TextSecondary

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        Image(
            painter = painterResource(R.drawable.ic_logo_k),
            contentDescription = "Komikta",
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Komikta",
            color = TextPrimary,
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Baca cerita, temukan dunia.",
            color = TextMuted,
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(32.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceElevated)
                .padding(16.dp)
        ) {
            Text("Versi 1.0.0", color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Aplikasi pembaca manhwa, manhua, dan manga. Data dari API multi-sumber.",
                color = TextMuted,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall
            )
        }
    }
}
