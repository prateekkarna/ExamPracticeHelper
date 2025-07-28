package com.example.exampractisehelper.ui.screens.timer

import android.content.Context
import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.exampractisehelper.R
import com.example.exampractisehelper.data.database.PracticeDatabase
import java.util.Locale
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.room.Room
import com.example.exampractisehelper.data.database.MIGRATION_2_3

@Composable
fun TimerScreen() {
    var timerValue by remember { mutableStateOf(0L) }
    var running by remember { mutableStateOf(false) }
    var isStopwatch by remember { mutableStateOf(true) }
    var inputMinutes by remember { mutableStateOf(0) }
    var inputSeconds by remember { mutableStateOf(0) }
    var inputHours by remember { mutableStateOf(0) } // Added for hours
    var showInput by remember { mutableStateOf(false) }
    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", timerValue / 3600, (timerValue % 3600) / 60, timerValue % 60)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val db = remember {
        Room.databaseBuilder(
            context,
            PracticeDatabase::class.java,
            "exam_practise_helper_db_v2"
        )
        .addMigrations(MIGRATION_2_3)
        .fallbackToDestructiveMigration()
        .fallbackToDestructiveMigrationOnDowngrade()
        .build()
    }
    val settingsDao = remember { db.settingsDao() }
    var alertType by remember { mutableStateOf("audio") }
    LaunchedEffect(Unit) {
        val settings = withContext(Dispatchers.IO) { settingsDao.getSettings() }
        alertType = settings?.alertType ?: "audio"
    }
    fun shouldVibrate() = alertType == "vibration" || alertType == "both"
    fun shouldPlayAudio() = alertType == "audio" || alertType == "both"
    val vibrator = remember {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            context.getSystemService(android.os.VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
    val mediaPlayer = remember { mutableStateOf<MediaPlayer?>(null) }
    fun playAlarm() {
        mediaPlayer.value?.release()
        val mp = MediaPlayer.create(context, R.raw.alarm)
        mp?.setOnCompletionListener { it.release() }
        mp?.start()
        mediaPlayer.value = mp
    }
    val timerShake = remember { Animatable(0f) }
    var shakeActive by remember { mutableStateOf(false) }
    var prevTimerValue by remember { mutableStateOf(timerValue) }
    var alertTriggered by remember { mutableStateOf(false) }
    LaunchedEffect(running, isStopwatch, timerValue) {
        if (running && !isStopwatch && !alertTriggered && prevTimerValue > 5 && timerValue <= 5 && timerValue > 0) {
            if (shouldVibrate()) {
                vibrator?.let {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        it.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        it.vibrate(5000)
                    }
                }
            }
            if (shouldPlayAudio()) {
                playAlarm()
            }
            shakeActive = true
            coroutineScope.launch {
                val shakeDuration = 5000L // ms (5 seconds)
                val shakeStep = 50 // ms
                val shakeTimes = (shakeDuration / shakeStep).toInt()
                repeat(shakeTimes) {
                    timerShake.snapTo(20f)
                    timerShake.animateTo(0f, animationSpec = tween(shakeStep))
                }
                shakeActive = false
            }
            alertTriggered = true
        }
        if (!running || timerValue == 0L) {
            alertTriggered = false
        }
        prevTimerValue = timerValue
    }

    // Timer running logic
    LaunchedEffect(running, isStopwatch) {
        while (running) {
            kotlinx.coroutines.delay(1000)
            if (isStopwatch) {
                timerValue += 1
            } else {
                if (timerValue > 0) timerValue -= 1
                if (timerValue == 0L) running = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp), // Move timer to top
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Timer/Stopwatch toggle at the very top
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        running = false
                        timerValue = 0L
                        isStopwatch = true
                    },
                    colors = if (isStopwatch) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                             else ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Stopwatch", color = if (isStopwatch) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                }
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = {
                        running = false
                        timerValue = 0L
                        isStopwatch = false
                        showInput = true
                    },
                    colors = if (!isStopwatch) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                              else ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Timer", color = if (!isStopwatch) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = if (isStopwatch) "Stopwatch" else "Timer",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.displayLarge.copy(fontSize = MaterialTheme.typography.displayLarge.fontSize * 1.5),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(16.dp)
                    .graphicsLayer {
                        translationX = if (shakeActive) timerShake.value else 0f
                    }
            )
            Row(Modifier.padding(vertical = 16.dp)) {
                Button(
                    onClick = {
                        if (!running) {
                            if (!isStopwatch && timerValue == 0L) {
                                showInput = true
                            } else {
                                running = true
                            }
                        } else {
                            running = false
                        }
                    },
                    modifier = Modifier.height(56.dp).width(120.dp)
                ) {
                    Text(if (running) "Pause" else "Start", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = {
                        running = false
                        timerValue = 0L
                    },
                    modifier = Modifier.height(56.dp).width(120.dp)
                ) {
                    Text("Reset", style = MaterialTheme.typography.titleMedium)
                }
            }
            if (showInput) {
                AlertDialog(
                    onDismissRequest = { showInput = false },
                    title = { Text("Set Timer") },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = inputHours.toString(),
                                onValueChange = { inputHours = it.toIntOrNull() ?: 0 },
                                label = { Text("Hours") },
                                singleLine = true,
                                modifier = Modifier.width(80.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            OutlinedTextField(
                                value = inputMinutes.toString(),
                                onValueChange = { inputMinutes = it.toIntOrNull() ?: 0 },
                                label = { Text("Minutes") },
                                singleLine = true,
                                modifier = Modifier.width(80.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            OutlinedTextField(
                                value = inputSeconds.toString(),
                                onValueChange = { inputSeconds = it.toIntOrNull() ?: 0 },
                                label = { Text("Seconds") },
                                singleLine = true,
                                modifier = Modifier.width(80.dp)
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            timerValue = (inputHours * 3600 + inputMinutes * 60 + inputSeconds).toLong()
                            // Do not auto start, just set the value
                            showInput = false
                        }) {
                            Text("Set")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showInput = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
