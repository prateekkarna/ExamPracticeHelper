package com.example.exampractisehelper.ui.components

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.exampractisehelper.data.entities.PracticeSession
import androidx.compose.foundation.layout.*

@Composable
fun PracticeSessionCard(session: PracticeSession, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = session.name)
            if (session.isTimed) {
                Spacer(Modifier.height(4.dp))
                Text(text = "Timed: ${session.totalDuration ?: 0} min")
            }
            if (session.loopEnabled) {
                Spacer(Modifier.height(4.dp))
                Text(text = "Loops: ${session.loopCount}")
            }
        }
    }
}

