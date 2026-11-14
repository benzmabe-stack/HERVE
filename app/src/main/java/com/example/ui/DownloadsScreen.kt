package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class DownloadItem(
    val id: Long,
    val fileName: String,
    val category: String,
    val fileSize: String,
    val progress: Int,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(onNavigateBack: () -> Unit) {
    var selectedCategory by remember { mutableStateOf("Tous") }
    val categories = listOf("Tous", "Images", "Vidéos", "Musique", "PDF", "Documents", "Archives")

    val sampleDownloads = listOf(
        DownloadItem(1, "Rapport_Professionnel_2026.pdf", "PDF", "4.2 Mo", 100, "Terminé"),
        DownloadItem(2, "Presentation_HP_V2.pptx", "Documents", "12.8 Mo", 75, "Téléchargement..."),
        DownloadItem(3, "Logo_HP_Vector.zip", "Archives", "1.5 Mo", 100, "Terminé"),
        DownloadItem(4, "Tutoriel_Android_Kotlin.mp4", "Vidéos", "240 Mo", 30, "En pause")
    )

    val filteredDownloads = if (selectedCategory == "Tous") {
        sampleDownloads
    } else {
        sampleDownloads.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionnaire de Téléchargements V2", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredDownloads.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucun fichier téléchargé dans cette catégorie.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredDownloads) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Icon(
                                            imageVector = when (item.category) {
                                                "PDF", "Documents" -> Icons.Default.Description
                                                "Vidéos" -> Icons.Default.VideoFile
                                                "Images" -> Icons.Default.Image
                                                else -> Icons.Default.InsertDriveFile
                                            },
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(text = item.fileName, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                            Text(text = "${item.fileSize} • ${item.status}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    IconButton(onClick = { /* Handle action */ }) {
                                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Options")
                                    }
                                }

                                if (item.progress < 100) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = { item.progress / 100f },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
