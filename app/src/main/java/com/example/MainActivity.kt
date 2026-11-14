package com.example

import android.os.Bundle
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ai.HpAiAssistantViewModel
import com.example.browser.BrowserController
import com.example.security.configureAppCheck
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    private val aiViewModel: HpAiAssistantViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // MANDATORY: Configure App Check before any UI or Firebase operations
        configureAppCheck(this, intent)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val browserController = remember { BrowserController() }

                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") {
                            SplashScreen(
                                onSplashFinished = {
                                    navController.navigate("home") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("home") {
                            HomeScreen(
                                browserController = browserController,
                                onNavigateToBrowser = { url ->
                                    val encoded = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                    navController.navigate("browser/$encoded")
                                },
                                onNavigateToTabs = { navController.navigate("tabs") },
                                onNavigateToDownloads = { navController.navigate("downloads") },
                                onNavigateToHistory = { navController.navigate("history") },
                                onNavigateToSettings = { navController.navigate("settings") },
                                onNavigateToQrScanner = { navController.navigate("qr_scanner") },
                                onStartVoiceSearch = {
                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Parlez pour rechercher...")
                                    }
                                    try {
                                        startActivity(intent)
                                    } catch (e: Exception) {
                                        // Handle if speech recognition not available
                                    }
                                }
                            )
                        }
                        composable(
                            route = "browser/{url}",
                            arguments = listOf(navArgument("url") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val encodedUrl = backStackEntry.arguments?.getString("url") ?: "https://www.google.com"
                            val url = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())
                            BrowserScreen(
                                initialUrl = url,
                                browserController = browserController,
                                aiViewModel = aiViewModel,
                                onNavigateBackToHome = { navController.popBackStack("home", inclusive = false) },
                                onNavigateToTabs = { navController.navigate("tabs") }
                            )
                        }
                        composable("tabs") {
                            TabsScreen(
                                browserController = browserController,
                                onTabSelected = { url ->
                                    val encoded = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                    navController.navigate("browser/$encoded")
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("downloads") {
                            DownloadsScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("history") {
                            HistoryScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onOpenUrl = { url ->
                                    val encoded = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                    navController.navigate("browser/$encoded")
                                }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("qr_scanner") {
                            QrScannerScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onQrScanned = { url ->
                                    val encoded = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                    navController.navigate("browser/$encoded") {
                                        popUpTo("home")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
