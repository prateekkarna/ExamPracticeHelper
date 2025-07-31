package com.abhyasa.ui.screens.about

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen() {
    val context = LocalContext.current
    val versionName = "1.0" // Hardcoded version to avoid BuildConfig error
    val appName = context.getString(com.abhyasa.exampractisehelper.R.string.app_name)
    Box(modifier = Modifier.fillMaxSize()) {
        Card(modifier = Modifier.padding(24.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Prayatna", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                Text("Version $versionName", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))
                Text(
                    "Prayatna helps you create, organize, and manage sessions, tasks, and routines for any purpose—study, work, fitness, or personal growth. Track your progress, stay productive, and achieve your goals with flexible session and task management tools.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(24.dp))
                Text("Developed by Pride Of Mithila", style = MaterialTheme.typography.bodySmall)
                Text("© 2025", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
