package com.example.exampractisehelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.exampractisehelper.navigation.AppRootNavigation
import com.example.exampractisehelper.ui.screens.home.HomeScreen
import com.example.exampractisehelper.ui.theme.ExamPractiseHelperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ExamPractiseHelperTheme {
                AppRootNavigation()
                }
            }
        }
    }