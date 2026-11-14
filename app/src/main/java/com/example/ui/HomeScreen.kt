package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.browser.BrowserController

data class FavoriteSite(val title: String, val url: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    browserController: BrowserController,
    onNavigateToBrowser: (String) -> Unit,
    onNavigateToTabs: () -> Unit,
    onNavigateToDownloads: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToQrScanner: () -> Unit,
    onStartVoiceSearch: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val favorites = listOf(
        FavoriteSite("Google", "https://www.google.com", Icons.Default.Search),
        FavoriteSite("YouTube", "https://www.youtube.com", Icons.Default.PlayArrow),
        FavoriteSite("Wikipedia", "https://www.wikipedia.org", Icons.Default.MenuBook),
        FavoriteSite("GitHub", "https://www.github.com", Icons.Default.Code),
        FavoriteSite("ChatGPT", "https://chatgpt.com", Icons.Default.SmartToy),
        FavoriteSite("Actualités", "https://news.google.com", Icons.Default.Newspaper),
        FavoriteSite("Reddit", "https://www.reddit.com", Icons.Default.Forum),
        FavoriteSite("LinkedIn", "https://www.linkedin.com", Icons.Default.Work)
    )

    fun handleSearchSubmit(query: String) {
        if (query.isBlank()) return
        val formattedUrl = if (query.startsWith("http://") || query.startsWith("https://")) {
            query
        } else if (query.contains(".") && !query.contains(" ") && !query.startsWith("www.")) {
            "https://$query"
        } else if (query.startsWith("www.")) {
            "https://$query"
        } else {
            "https://www.google.com/search?q=${java.net.URLEncoder.encode(query, "UTF-8")}"
        }
        browserController.updateActiveTabUrl(formattedUrl)
        onNavigateToBrowser(formattedUrl)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "HP NAVIGATE",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateDownloadsInternal@{ onNavigateToDownloads() }, modifier = Modifier.testTag("downloads_button")) {
                        Icon(imageVector = Icons.Outlined.Download, contentDescription = "Téléchargements")
                    }
                    IconButton(onClick = { onNavigateToTabs() }, modifier = Modifier.testTag("tabs_button")) {
                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                            Text(text = "${browserController.tabs.value.size}")
                        }
                        Icon(imageVector = Icons.Outlined.Tab, contentDescription = "Onglets")
                    }
                    IconButton(onClick = { onNavigateToSettings() }, modifier = Modifier.testTag("settings_button")) {
                        Icon(imageVector = Icons.Outlined.Settings, contentDescription = "Paramètres")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Accueil") },
                    label = { Text("Accueil") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToTabs,
                    icon = { Icon(Icons.Default.Tab, contentDescription = "Onglets") },
                    label = { Text("Onglets") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToDownloads,
                    icon = { Icon(Icons.Default.Download, contentDescription = "Fichiers") },
                    label = { Text("Fichiers") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHistory,
                    icon = { Icon(Icons.Default.History, contentDescription = "Historique") },
                    label = { Text("Historique") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Votre porte d'entrée sur Internet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("search_bar"),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Rechercher ou saisir une URL...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                            disabledBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                        ),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                            onSearch = { handleSearchSubmit(searchQuery) }
                        )
                    )
                    IconButton(onClick = onStartVoiceSearch, modifier = Modifier.testTag("voice_search_button")) {
                        Icon(imageVector = Icons.Default.Mic, contentDescription = "Recherche vocale", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onNavigateToQrScanner, modifier = Modifier.testTag("qr_scanner_button")) {
                        Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "Scanner QR", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sites Favoris",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToHistory) {
                    Text("Historique")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favorites) { site ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                browserController.updateActiveTabUrl(site.url)
                                onNavigateToBrowser(site.url)
                            }
                            .padding(8.dp)
                            .testTag("favorite_${site.title.lowercase()}")
                    ) {
                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = site.icon,
                                    contentDescription = site.title,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = site.title,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
