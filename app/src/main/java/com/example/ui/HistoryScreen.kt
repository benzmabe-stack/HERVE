package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onOpenUrl: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val historyItems = listOf(
        Pair("Google Search", "https://www.google.com"),
        Pair("YouTube - Streaming", "https://www.youtube.com"),
        Pair("Documentation Android", "https://developer.android.com"),
        Pair("GitHub Repositories", "https://github.com")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historique & Favoris", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Historique") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Favoris") })
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(historyItems) { item ->
                    Card(
                        onClick = { onOpenUrl(item.second) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ListItem(
                            headlineContent = { Text(item.first, fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text(item.second) },
                            leadingContent = { Icon(imageVector = Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                        )
                    }
                }
            }
        }
    }
}
