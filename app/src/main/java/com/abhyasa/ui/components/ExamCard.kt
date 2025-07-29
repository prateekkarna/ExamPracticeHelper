package com.abhyasa.ui.components

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abhyasa.data.entities.Exam
import androidx.compose.foundation.layout.*

@Composable
fun ExamCard(exam: Exam, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = exam.name)
            if (!exam.description.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(text = exam.description, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
            }
        }
    }
}

