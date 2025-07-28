package com.example.exampractisehelper.ui.screens.runsession

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.exampractisehelper.data.entities.PracticeSession
import com.example.exampractisehelper.data.entities.Task
import com.example.exampractisehelper.data.entities.Subtask
import kotlinx.coroutines.delay
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Stop
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.exampractisehelper.ui.theme.ExamPractiseHelperTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.keepScreenOn
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import com.example.exampractisehelper.data.database.PracticeDatabase
import com.example.exampractisehelper.data.repository.PracticeSessionRepositoryImpl
import com.example.exampractisehelper.data.database.MIGRATION_2_3
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun RunSessionScreen(
    sessionId: Int,
    navController: NavController
) {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(
            context,
            PracticeDatabase::class.java,
            "exam_practise_helper_db_v2"
        )
        .addMigrations(MIGRATION_2_3)
        .build()
    }
    val sessionRepository = remember { PracticeSessionRepositoryImpl(db.practiceSessionDao()) }
    val taskDao = remember { db.taskDao() }
    val subTaskDao = remember { db.subTaskDao() }
    val settingsDao = remember { db.settingsDao() }
    var alertType by remember { mutableStateOf("audio") }
    LaunchedEffect(Unit) {
        val settings = withContext(Dispatchers.IO) { settingsDao.getSettings() }
        alertType = settings?.alertType ?: "audio"
    }
    fun shouldVibrate(): Boolean = alertType == "vibration" || alertType == "both"
    fun shouldPlayAudio(): Boolean = alertType == "audio" || alertType == "both"
    // If alertType is empty or not set, neither should trigger
    if (alertType.isBlank()) {
        fun shouldVibrate(): Boolean = false
        fun shouldPlayAudio(): Boolean = false
    }

    val sessionState = produceState<Triple<com.example.exampractisehelper.data.entities.PracticeSession?, List<com.example.exampractisehelper.data.entities.Task>, Map<Int, List<com.example.exampractisehelper.data.entities.Subtask>>>?>(initialValue = null, sessionId) {
        val session = sessionRepository.getAllSessions().find { it.sessionId == sessionId }
        val tasks = taskDao.getTasksForSession(sessionId)
        val subtasksMap = tasks.associate { it.taskId to subTaskDao.getSubtasksForTask(it.taskId) }
        value = Triple(session, tasks, subtasksMap)
    }
    if (sessionState.value == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val (session, tasks, subtasksMap) = sessionState.value!!

    // Session timer
    val sessionDuration = session?.totalDuration ?: 0
    var sessionTimer by remember { mutableStateOf(if (sessionDuration > 0) sessionDuration else 0) }
    var sessionRunning by remember { mutableStateOf(false) }
    // Track if timer should count up or down
    val sessionCountsDown = sessionDuration > 0

    // Subtask timer (move these above LaunchedEffect)
    var currentSubtaskIndex by remember { mutableStateOf<Int?>(null) }
    var subtaskTimer by remember { mutableStateOf(0) }
    var subtaskRunning by remember { mutableStateOf(false) }

    // Task timer
    var currentTaskIndex by remember { mutableStateOf<Int?>(null) }
    // Reset subtask state when task changes
    LaunchedEffect(currentTaskIndex) {
        if (currentTaskIndex != null) {
            val newSubtasks = tasks.getOrNull(currentTaskIndex!!)?.let { subtasksMap[it.taskId] } ?: emptyList()
            currentSubtaskIndex = if (newSubtasks.isNotEmpty()) 0 else null
            subtaskTimer = if (newSubtasks.isNotEmpty()) newSubtasks[0].duration ?: 0 else 0
            subtaskRunning = newSubtasks.isNotEmpty() // Start subtask timer if there are subtasks
        } else {
            currentSubtaskIndex = null
            subtaskTimer = 0
            subtaskRunning = false
        }
    }
    val currentTask = currentTaskIndex?.let { tasks.getOrNull(it) }
    val taskName = currentTask?.text ?: "Select a Task"
    val taskDuration = currentTask?.taskDuration ?: 0
    var taskTimer by remember { mutableStateOf(0) }
    var taskRunning by remember { mutableStateOf(false) }

    // Subtask timer
    val subtasks = currentTask?.let { subtasksMap[it.taskId] } ?: emptyList()
    val currentSubtask = currentSubtaskIndex?.let { subtasks.getOrNull(it) }
    val nextSubtask = currentSubtaskIndex?.let { subtasks.getOrNull(it + 1) }
    val subtaskDuration = currentSubtask?.duration ?: 0

    // Add state for selected task
    var selectedTaskIndex by remember { mutableStateOf<Int?>(null) }
    // Add state for tooltip visibility
    var showSelectTaskTooltip by remember { mutableStateOf(false) }

    // Vibration logic
    val vibrator = remember {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            context.getSystemService(android.os.VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as android.os.Vibrator
        }
    }
    var sessionPrevTimer by remember { mutableStateOf(sessionTimer) }
    var taskPrevTimer by remember { mutableStateOf(taskTimer) }
    var subtaskPrevTimer by remember { mutableStateOf(subtaskTimer) }

    // Audio logic
    val mediaPlayer = remember { mutableStateOf<android.media.MediaPlayer?>(null) }
    fun playAlarm() {
        mediaPlayer.value?.release()
        val mp = android.media.MediaPlayer.create(context, com.example.exampractisehelper.R.raw.alarm)
        mp?.setOnCompletionListener { it.release() }
        mp?.start()
        mediaPlayer.value = mp
    }

    // Shake animation states for timer text
    val sessionTimerShake = remember { Animatable(0f) }
    val taskTimerShake = remember { Animatable(0f) }
    val subtaskTimerShake = remember { Animatable(0f) }
    var sessionShakeActive by remember { mutableStateOf(false) }
    var taskShakeActive by remember { mutableStateOf(false) }
    var subtaskShakeActive by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Synchronized Session, Task, and Subtask timer logic
    LaunchedEffect(sessionRunning, taskRunning, subtaskRunning, currentSubtaskIndex) {
        var lastUpdate = System.currentTimeMillis()
        while (sessionRunning || taskRunning) {
            val now = System.currentTimeMillis()
            val elapsed = ((now - lastUpdate) / 1000).toInt()

            if (elapsed > 0) {
                lastUpdate += elapsed * 1000 // increment by elapsed seconds
                // Update session timer
                if (sessionRunning) {
                    if (sessionCountsDown) {
                        sessionTimer -= elapsed
                    } else {
                        sessionTimer += elapsed
                    }
                }
                // Vibrate and play audio for session timer
                if (sessionPrevTimer > 5 && sessionTimer <= 5 && sessionTimer > 0) {
                    if (shouldVibrate()) {
                        vibrator?.let {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                it.vibrate(android.os.VibrationEffect.createOneShot(5000, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
                            } else {
                                @Suppress("DEPRECATION")
                                it.vibrate(5000)
                            }
                        }
                    }
                    if (shouldPlayAudio()) {
                        playAlarm()
                    }
                    sessionShakeActive = true
                    coroutineScope.launch {
                        val shakeDuration = 5000 // ms
                        val shakeStep = 50 // ms
                        val shakeTimes = shakeDuration / shakeStep
                        repeat(shakeTimes) {
                            sessionTimerShake.snapTo(20f)
                            sessionTimerShake.animateTo(0f, animationSpec = tween(shakeStep))
                        }
                        sessionShakeActive = false
                    }
                }
                sessionPrevTimer = sessionTimer
                // Update task timer
                if (taskRunning) {
                    if (currentTask?.taskDuration != null && currentTask.taskDuration > 0) {
                        taskTimer -= elapsed
                    } else {
                        taskTimer += elapsed
                    }
                    // Vibrate and play audio for task timer
                    if (taskPrevTimer > 5 && taskTimer <= 5 && taskTimer > 0) {
                        if (shouldVibrate()) {
                            vibrator?.let {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    it.vibrate(android.os.VibrationEffect.createOneShot(5000, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
                            } else {
                                @Suppress("DEPRECATION")
                                it.vibrate(5000)
                            }
                        }
                        }
                        if (shouldPlayAudio()) {
                            playAlarm()
                        }
                        taskShakeActive = true
                        coroutineScope.launch {
                            val shakeDuration = 5000 // ms
                            val shakeStep = 50 // ms
                            val shakeTimes = shakeDuration / shakeStep
                            repeat(shakeTimes) {
                                taskTimerShake.snapTo(20f)
                                taskTimerShake.animateTo(0f, animationSpec = tween(shakeStep))
                            }
                            taskShakeActive = false
                        }
                    }
                    taskPrevTimer = taskTimer
                }
                // Update subtask timer if running
                if (subtaskRunning && currentSubtaskIndex != null && subtasks.isNotEmpty()) {
                    subtaskTimer -= elapsed
                    if (subtaskPrevTimer > 5 && subtaskTimer <= 5 && subtaskTimer > 0) {
                        if (shouldVibrate()) {
                            vibrator?.let {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    it.vibrate(android.os.VibrationEffect.createOneShot(5000, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
                                } else {
                                    @Suppress("DEPRECATION")
                                    it.vibrate(5000)
                                }
                            }
                        }
                        if (shouldPlayAudio()) {
                            playAlarm()
                        }
                        subtaskShakeActive = true
                        coroutineScope.launch {
                            val shakeDuration = 5000 // ms
                            val shakeStep = 50 // ms
                            val shakeTimes = shakeDuration / shakeStep
                            repeat(shakeTimes) {
                                subtaskTimerShake.snapTo(20f)
                                subtaskTimerShake.animateTo(0f, animationSpec = tween(shakeStep))
                            }
                            subtaskShakeActive = false
                        }
                    }
                    subtaskPrevTimer = subtaskTimer
                    if (subtaskTimer <= 0) {
                        val nextIndex = currentSubtaskIndex!! + 1
                        if (nextIndex < subtasks.size) {
                            currentSubtaskIndex = nextIndex
                            subtaskTimer = subtasks[nextIndex].duration ?: 0
                            lastUpdate = System.currentTimeMillis() // reset for new subtask
                        } else {
                            subtaskRunning = false
                        }
                    }
                }
            }
            delay(100)
        }
    }

    // Timer font sizes
    val smallestTimerSize = MaterialTheme.typography.displaySmall.fontSize
    val mediumTimerSize = MaterialTheme.typography.displayMedium.fontSize * 1.3f
    val largestTimerSize = if (sessionTimer < 0 || taskTimer < 0) MaterialTheme.typography.displayLarge.fontSize * 1.2f else MaterialTheme.typography.displayLarge.fontSize * 1.3f

    // Determine which timers are visible
    val showSubtaskTimer = subtasks.isNotEmpty()
    val showTaskTimer = tasks.isNotEmpty()
    val showSessionTimer = true

    // Assign font sizes based on preference
    val sessionTimerFontSize = when {
        showSubtaskTimer && showTaskTimer -> smallestTimerSize
        showTaskTimer -> mediumTimerSize
        else -> largestTimerSize
    }
    val taskTimerFontSize = when {
        showSubtaskTimer -> mediumTimerSize
        showTaskTimer -> largestTimerSize
        else -> mediumTimerSize
    }
    val subtaskTimerFontSize = if (showSubtaskTimer) largestTimerSize else mediumTimerSize

    // Timer color logic
    val sessionTimerColor = if (sessionTimer < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val taskTimerColor = if (taskTimer < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary

    // Determine if any timer is running
    val anyTimerRunning = sessionRunning || taskRunning || subtaskRunning

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .then(if (anyTimerRunning) Modifier.keepScreenOn() else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (tasks.isNotEmpty()) {
            // Show main session timer card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                ,
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (currentTaskIndex == null) {
                                    showSelectTaskTooltip = true
                                } else {
                                    sessionRunning = !sessionRunning
                                    taskRunning = !taskRunning
                                    if (subtasks.isNotEmpty()) {
                                        subtaskRunning = sessionRunning // Always start subtask timer when session starts
                                    }
                                }
                            },
                            modifier = Modifier.size(56.dp)
                        ) {
                            val playTint = if (currentTaskIndex != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            if (sessionRunning) {
                                Icon(
                                    imageVector = Icons.Filled.Pause,
                                    contentDescription = "Pause",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(80.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Play",
                                    tint = playTint,
                                    modifier = Modifier.size(80.dp)
                                )
                            }
                        }
                        Spacer(Modifier.weight(1f))
                        Text("Session Timer", style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.weight(1f))
                        IconButton(
                            onClick = {
                                // Reset all data to initial value
                                sessionRunning = false
                                taskRunning = false
                                subtaskRunning = false
                                sessionTimer = if (sessionCountsDown) sessionDuration else 0
                                currentTaskIndex = null
                                taskTimer = 0
                                currentSubtaskIndex = null
                                subtaskTimer = 0
                                selectedTaskIndex = null
                                // Stop all animation, audio, and vibration
                                sessionShakeActive = false
                                taskShakeActive = false
                                subtaskShakeActive = false
                                // Launch coroutine to snap animations
                                coroutineScope.launch {
                                    sessionTimerShake.snapTo(0f)
                                    taskTimerShake.snapTo(0f)
                                    subtaskTimerShake.snapTo(0f)
                                }
                                mediaPlayer.value?.let { mp ->
                                    try {
                                        if (mp.isPlaying) {
                                            mp.stop()
                                        }
                                    } catch (e: IllegalStateException) {
                                        // Ignore, safe to release
                                    } finally {
                                        mp.release()
                                    }
                                }
                                mediaPlayer.value = null
                                vibrator?.cancel()
                            },
                            modifier = Modifier.size(56.dp),
                            enabled = currentTaskIndex != null
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Stop,
                                contentDescription = "Stop",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    // Session timer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = String.format(Locale.getDefault(), "%s%02d:%02d:%02d",
                                if (sessionTimer < 0) "-" else "",
                                kotlin.math.abs(sessionTimer) / 3600,
                                (kotlin.math.abs(sessionTimer) % 3600) / 60,
                                kotlin.math.abs(sessionTimer) % 60),
                            style = MaterialTheme.typography.displayMedium.copy(fontSize = sessionTimerFontSize, color = sessionTimerColor),
                            modifier = if (sessionShakeActive) Modifier.graphicsLayer { translationX = sessionTimerShake.value * kotlin.math.sin(System.currentTimeMillis().toDouble() / 20).toFloat() } else Modifier
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
            // Only show the task timer card if there is at least one task
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                ,
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (currentTask != null) {
                        Text(
                            "Current Task: $taskName",
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            "Select a Task",
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    // Fancy timer for task (smaller than session timer)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = String.format(Locale.getDefault(), "%s%02d:%02d:%02d",
                                if (taskTimer < 0) "-" else "",
                                kotlin.math.abs(taskTimer) / 3600,
                                (kotlin.math.abs(taskTimer) % 3600) / 60,
                                kotlin.math.abs(taskTimer) % 60),
                            style = MaterialTheme.typography.displaySmall.copy(fontSize = taskTimerFontSize, color = taskTimerColor),
                            modifier = if (taskShakeActive) Modifier.graphicsLayer { translationX = taskTimerShake.value * kotlin.math.sin(System.currentTimeMillis().toDouble() / 20).toFloat() } else Modifier
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    if (subtasks.isNotEmpty()) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Current Subtask",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text(
                                        currentSubtask?.name ?: "-",
                                        style = MaterialTheme.typography.titleLarge,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Next Subtask",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text(
                                        nextSubtask?.name ?: "-",
                                        style = MaterialTheme.typography.titleLarge,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        // Show current running subtask timer below the row, slightly less than before but still prominent
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = String.format(Locale.getDefault(), "%02d:%02d:%02d", subtaskTimer / 3600, (subtaskTimer % 3600) / 60, subtaskTimer % 60),
                                style = MaterialTheme.typography.displayLarge.copy(fontSize = subtaskTimerFontSize, color = MaterialTheme.colorScheme.primary),
                                modifier = if (subtaskShakeActive) Modifier.graphicsLayer { translationX = subtaskTimerShake.value * kotlin.math.sin(System.currentTimeMillis().toDouble() / 20).toFloat() } else Modifier
                            )
                        }
                    }
                }
            }
            // AlertDialog for play button tooltip
            if (showSelectTaskTooltip) {
                AlertDialog(
                    onDismissRequest = { showSelectTaskTooltip = false },
                    confirmButton = {
                        TextButton(onClick = { showSelectTaskTooltip = false }) {
                            Text("OK")
                        }
                    },
                    title = { Text("No Task Selected") },
                    text = { Text("Please select a task to start the session.") }
                )
            }
            // Move available tasks card below the task timer card
            Text("Select to start or switch between tasks", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            tasks.chunked(2).forEach { rowTasks ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    rowTasks.forEachIndexed { idx, task ->
                        val globalIndex = tasks.indexOf(task)
                        Card(
                            modifier = Modifier
                                .heightIn(min = 60.dp, max = 200.dp)
                                .weight(1f)
                                .padding(4.dp)
                                .clickable {
                                    currentTaskIndex = globalIndex
                                    // Reset timers for selected task
                                    taskTimer = task.taskDuration ?: 0
                                    val subList = subtasksMap[task.taskId]
                                    currentSubtaskIndex = if (subList?.isNotEmpty() == true) 0 else null
                                    subtaskTimer = subList?.getOrNull(0)?.duration ?: 0
                                    subtaskRunning = false // Do not start timers automatically
                                    // sessionRunning and taskRunning should NOT start here
                                },
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    task.text,
                                    style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                                val h = (task.taskDuration ?: 0) / 3600
                                val m = ((task.taskDuration ?: 0) % 3600) / 60
                                val s = (task.taskDuration ?: 0) % 60
                                Text(
                                    text = String.format("%dh %dm %ds", h, m, s),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Only session timer, show big timer and play/stop below
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Session Timer", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = String.format(Locale.getDefault(), "%s%02d:%02d:%02d",
                                if (sessionTimer < 0) "-" else "",
                                kotlin.math.abs(sessionTimer) / 3600,
                                (kotlin.math.abs(sessionTimer) % 3600) / 60,
                                kotlin.math.abs(sessionTimer) % 60),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = largestTimerSize,
                                color = if (sessionTimer < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            ),
                            modifier = if (sessionShakeActive) Modifier.graphicsLayer { translationX = sessionTimerShake.value * kotlin.math.sin(System.currentTimeMillis().toDouble() / 20).toFloat() } else Modifier
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {
                                sessionRunning = !sessionRunning
                            },
                            modifier = Modifier.size(72.dp)
                        ) {
                            if (sessionRunning) {
                                Icon(
                                    imageVector = Icons.Filled.Pause,
                                    contentDescription = "Pause",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(56.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Play",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(56.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(32.dp))
                        IconButton(
                            onClick = {
                                // Reset all data to initial value
                                sessionRunning = false
                                taskRunning = false
                                subtaskRunning = false
                                sessionTimer = if (sessionCountsDown) sessionDuration else 0
                                currentTaskIndex = null
                                taskTimer = 0
                                currentSubtaskIndex = null
                                subtaskTimer = 0
                                selectedTaskIndex = null
                                // Stop all animation, audio, and vibration
                                sessionShakeActive = false
                                taskShakeActive = false
                                subtaskShakeActive = false
                                // Launch coroutine to snap animations
                                coroutineScope.launch {
                                    sessionTimerShake.snapTo(0f)
                                    taskTimerShake.snapTo(0f)
                                    subtaskTimerShake.snapTo(0f)
                                }
                                mediaPlayer.value?.let { mp ->
                                    try {
                                        if (mp.isPlaying) {
                                            mp.stop()
                                        }
                                    } catch (e: IllegalStateException) {
                                        // Ignore, safe to release
                                    } finally {
                                        mp.release()
                                    }
                                }
                                mediaPlayer.value = null
                                vibrator?.cancel()
                            },
                            modifier = Modifier.size(72.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Stop,
                                contentDescription = "Stop",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(56.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RunSessionScreenPreview() {
    ExamPractiseHelperTheme {
        // Preview with dummy data
        val dummySession = PracticeSession(
            sessionId = 1,
            name = "Dummy Session",
            isTimed = true,
            totalDuration = 3600,
            loopEnabled = false,
            loopCount = 1,
            isSimpleSession = false
        )
        val dummyTasks = listOf(
            Task(
                taskId = 1,
                sessionId = 1,
                text = "Task 1",
                hasSubtasks = true,
                taskDuration = 600,
                typeLabel = "Type A"
            ),
            Task(
                taskId = 2,
                sessionId = 1,
                text = "Task 2",
                hasSubtasks = false,
                taskDuration = 1200,
                typeLabel = "Type B"
            ),
            Task(
                taskId = 3,
                sessionId = 1,
                text = "Task 3",
                hasSubtasks = true,
                taskDuration = 900,
                typeLabel = "Type C"
            )
        )
        val dummySubtasksMap = mapOf(
            1 to listOf(
                Subtask(
                    subtaskId = 1,
                    taskId = 1,
                    name = "Subtask 1.1",
                    duration = 300
                ),
                Subtask(
                    subtaskId = 2,
                    taskId = 1,
                    name = "Subtask 1.2",
                    duration = 300
                )
            ),
            3 to listOf(
                Subtask(
                    subtaskId = 3,
                    taskId = 3,
                    name = "Subtask 3.1",
                    duration = 450
                ),
                Subtask(
                    subtaskId = 4,
                    taskId = 3,
                    name = "Subtask 3.2",
                    duration = 450
                )
            )
        )
        RunSessionScreen(
            sessionId = 1,
            navController = NavController(LocalContext.current)
        )
    }
}
