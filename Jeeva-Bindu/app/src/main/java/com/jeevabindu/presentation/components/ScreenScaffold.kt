package com.jeevabindu.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.jeevabindu.presentation.theme.GreenAvailable
import com.jeevabindu.presentation.theme.RedAlert

@Composable
fun ScreenScaffold(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        RedAlert.copy(alpha = 0.12f),
                        GreenAvailable.copy(alpha = 0.06f)
                    )
                )
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Text(text = subtitle, style = MaterialTheme.typography.bodyLarge)
    }
}
