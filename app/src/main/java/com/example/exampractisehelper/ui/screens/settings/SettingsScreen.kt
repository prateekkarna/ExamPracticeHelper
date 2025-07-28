package com.example.exampractisehelper.ui.screens.settings

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.exampractisehelper.data.database.PracticeDatabase
import com.example.exampractisehelper.data.entities.Settings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `settings` (
                    `id` INTEGER NOT NULL PRIMARY KEY,
                    `alertType` TEXT NOT NULL
                )
            """)
        }
    }
    val db = remember {
        Room.databaseBuilder(
            context,
            PracticeDatabase::class.java,
            "exam_practise_helper_db_v2"
        )
        .addMigrations(MIGRATION_2_3)
        .build()
    }
    val settingsDao = remember { db.settingsDao() }
    val scope = rememberCoroutineScope()
    var selectedAlert by remember { mutableStateOf("audio") }
    var settingsLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val settings = settingsDao.getSettings()
        if (settings == null) {
            settingsDao.insertSettings(Settings(alertType = "audio"))
            selectedAlert = "audio"
        } else {
            selectedAlert = settings.alertType
        }
        settingsLoaded = true
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (settingsLoaded) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Settings", style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.height(16.dp))
                        Text("Alert on Timer Completion", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val audioChecked = selectedAlert == "audio" || selectedAlert == "both"
                            val vibrationChecked = selectedAlert == "vibration" || selectedAlert == "both"
                            Checkbox(
                                checked = audioChecked,
                                onCheckedChange = { checked ->
                                    val newValue = when {
                                        checked && vibrationChecked -> "both"
                                        checked -> "audio"
                                        !checked && vibrationChecked -> "vibration"
                                        else -> ""
                                    }
                                    selectedAlert = newValue
                                    scope.launch { settingsDao.insertSettings(Settings(alertType = newValue)) }
                                }
                            )
                            Text("Audio", Modifier.padding(start = 4.dp))
                            Spacer(Modifier.width(16.dp))
                            Checkbox(
                                checked = vibrationChecked,
                                onCheckedChange = { checked ->
                                    val newValue = when {
                                        checked && audioChecked -> "both"
                                        checked -> "vibration"
                                        !checked && audioChecked -> "audio"
                                        else -> ""
                                    }
                                    selectedAlert = newValue
                                    scope.launch { settingsDao.insertSettings(Settings(alertType = newValue)) }
                                }
                            )
                            Text("Vibration", Modifier.padding(start = 4.dp))
                        }
                    }
                }
            }
        }
    }
}
