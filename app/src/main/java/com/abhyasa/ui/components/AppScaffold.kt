package com.abhyasa.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.navigation.NavController
import com.abhyasa.navigation.Screen
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun AppScaffold(
    navController: NavController,
    drawerItems: List<String>,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
                modifier = Modifier.background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1976D2), Color(0xFF63A4FF))
                    )
                )
            ) {
                // Drawer Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1976D2))
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TrackChanges,
                            contentDescription = "App Icon",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Prayatna",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                // Drawer Items with icons
                drawerItems.forEach { label ->
                    val route = label.lowercase()
                    val icon = when (route) {
                        "home" -> Icons.Default.Home
                        "settings" -> Icons.Default.Settings
                        "about" -> Icons.Default.Info
                        "activity" -> Icons.Default.Timer
                        "sessions" -> Icons.Default.ListAlt
                        else -> null
                    }
                    val isSelected = navController.currentBackStackEntry?.destination?.route == route ||
                        (route == "sessions" && navController.currentBackStackEntry?.destination?.route == "home")
                    NavigationDrawerItem(
                        label = { Text(label, fontWeight = FontWeight.Medium) },
                        selected = isSelected,
                        onClick = {
                            if (route == "sessions") {
                                navController.navigate("home")
                            } else {
                                navController.navigate(route)
                            }
                            scope.launch { drawerState.close() }
                        },
                        icon = icon?.let {
                            {
                                Icon(
                                    it,
                                    contentDescription = label,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else LocalContentColor.current
                                )
                            }
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                // Move About below Settings
                /*
                val aboutIndex = drawerItems.indexOfFirst { it.equals("about", ignoreCase = true) }
                val settingsIndex = drawerItems.indexOfFirst { it.equals("settings", ignoreCase = true) }
                if (aboutIndex >= 0 && settingsIndex >= 0 && aboutIndex < settingsIndex) {
                    // If About is above Settings, swap them
                    val mutableDrawerItems = drawerItems.toMutableList()
                    val about = mutableDrawerItems.removeAt(aboutIndex)
                    mutableDrawerItems.add(settingsIndex, about)
                }
                */
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrackChanges,
                                contentDescription = "App Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Prayatna")
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
//                    actions = {
//                        IconButton(onClick = {
//                            navController.navigate(Screen.Profile.route)
//                        }) {
//                            Icon(Icons.Default.Person, contentDescription = "Profile")
//                        }
//                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                content()
            }
        }
    }
}
