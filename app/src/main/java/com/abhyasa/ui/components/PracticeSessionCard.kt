package com.abhyasa.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abhyasa.data.entities.PracticeSession
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

@Composable
fun PracticeSessionCard(session: PracticeSession, modifier: Modifier = Modifier, onStartClick: (() -> Unit)? = null) {
    fun formatDuration(seconds: Int?): String {
        if (seconds == null || seconds <= 0) return "No timer"
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return listOf(
            if (h > 0) "${h}h" else null,
            if (m > 0) "${m}m" else null,
            if (s > 0 || (h == 0 && m == 0)) "${s}s" else null
        ).filterNotNull().joinToString(" ")
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = session.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                if (onStartClick != null) {
                    IconButton(onClick = onStartClick) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Start Session", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (session.isTimed) {
                    Icon(Icons.Default.Timer, contentDescription = "Timed", tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(4.dp))
                    Text(text = formatDuration(session.totalDuration), style = MaterialTheme.typography.bodyMedium)
                } else {
                    Icon(Icons.Default.EventNote, contentDescription = "Untimed", tint = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.width(4.dp))
                    Text(text = "Untimed", style = MaterialTheme.typography.bodyMedium)
                }
                if (session.loopEnabled) {
                    Spacer(Modifier.width(12.dp))
                    Icon(Icons.Default.Repeat, contentDescription = "Loop", tint = MaterialTheme.colorScheme.tertiary)
                    Spacer(Modifier.width(4.dp))
                    Text(text = "Loops: ${session.loopCount}", style = MaterialTheme.typography.bodyMedium)
                }
                if (session.isSimpleSession == true) {
                    Spacer(Modifier.width(12.dp))
                    Icon(Icons.Default.CheckCircle, contentDescription = "Simple", tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(4.dp))
                    Text(text = "Simple", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
