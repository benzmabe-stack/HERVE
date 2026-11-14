package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateBack: () -> Unit) {
    var safeBrowsingEnabled by remember { mutableStateOf(true) }
    var adBlockerEnabled by remember { mutableStateOf(true) }
    var httpsOnlyEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paramètres HP Navigate", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(text = "A. Général", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                ListItem(
                    headlineContent = { Text("Moteur de recherche") },
                    supportingContent = { Text("Google (Par défaut)") },
                    leadingContent = { Icon(Icons.Default.Search, contentDescription = null) }
                )
                ListItem(
                    headlineContent = { Text("Langue de l'application") },
                    supportingContent = { Text("Français") },
                    leadingContent = { Icon(Icons.Default.Language, contentDescription = null) }
                )
            }

            item {
                Text(text = "B. Confidentialité & Sécurité", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                ListItem(
                    headlineContent = { Text("Safe Browsing") },
                    supportingContent = { Text("Détecter les sites dangereux") },
                    leadingContent = { Icon(Icons.Default.Security, contentDescription = null) },
                    trailingContent = {
                        Switch(checked = safeBrowsingEnabled, onCheckedChange = { safeBrowsingEnabled = it })
                    }
                )
                ListItem(
                    headlineContent = { Text("Blocage des Pop-ups & Ads") },
                    supportingContent = { Text("Limiter les fenêtres intrusives") },
                    leadingContent = { Icon(Icons.Default.Block, contentDescription = null) },
                    trailingContent = {
                        Switch(checked = adBlockerEnabled, onCheckedChange = { adBlockerEnabled = it })
                    }
                )
                ListItem(
                    headlineContent = { Text("Protection HTTPS stricte") },
                    supportingContent = { Text("Signaler les connexions non sécurisées") },
                    leadingContent = { Icon(Icons.Default.Https, contentDescription = null) },
                    trailingContent = {
                        Switch(checked = httpsOnlyEnabled, onCheckedChange = { httpsOnlyEnabled = it })
                    }
                )
            }

            item {
                Text(text = "C. Données & Cache", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                ListItem(
                    headlineContent = { Text("Effacer l'historique et les cookies") },
                    supportingContent = { Text("Libérer de l'espace de stockage") },
                    leadingContent = { Icon(Icons.Default.DeleteForever, contentDescription = null) }
                )
            }
        }
    }
}
