package com.example.ui

import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.graphics.Bitmap
import android.print.PrintAttributes
import android.print.PrintManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ai.HpAiAssistantViewModel
import com.example.browser.BrowserController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserScreen(
    initialUrl: String,
    browserController: BrowserController,
    aiViewModel: HpAiAssistantViewModel,
    onNavigateBackToHome: () -> Unit,
    onNavigateToTabs: () -> Unit
) {
    val context = LocalContext.current
    val activeTab = browserController.activeTab
    var currentUrlInput by remember { mutableStateOf(initialUrl) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var showAiSheet by remember { mutableStateOf(false) }

    LaunchedEffect(activeTab?.url) {
        if (activeTab != null && activeTab.url != currentUrlInput) {
            currentUrlInput = activeTab.url
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("url_bar"),
                            shape = MaterialTheme.shapes.extraLarge,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Secure Connection",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = currentUrlInput,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                if (activeTab?.isLoading == true) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBackToHome, modifier = Modifier.testTag("home_button")) {
                            Icon(imageVector = Icons.Default.Home, contentDescription = "Accueil")
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToTabs, modifier = Modifier.testTag("tabs_button")) {
                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                Text("${browserController.tabs.value.size}")
                            }
                            Icon(imageVector = Icons.Outlined.Tab, contentDescription = "Onglets")
                        }
                        IconButton(onClick = { showMenu = true }, modifier = Modifier.testTag("menu_button")) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Assistant HP AI") },
                                onClick = {
                                    showMenu = false
                                    showAiSheet = true
                                },
                                leadingIcon = { Icon(Icons.Default.SmartToy, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text(if (browserController.isDesktopMode.value) "Mode Mobile" else "Mode Ordinateur") },
                                onClick = {
                                    showMenu = false
                                    browserController.toggleDesktopMode()
                                    webViewInstance?.settings?.userAgentString = if (browserController.isDesktopMode.value) {
                                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                                    } else {
                                        null
                                    }
                                    webViewInstance?.reload()
                                },
                                leadingIcon = { Icon(Icons.Default.DesktopMac, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Imprimer / PDF") },
                                onClick = {
                                    showMenu = false
                                    webViewInstance?.let { wv ->
                                        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                                        val printAdapter = wv.createPrintDocumentAdapter("HP_Navigate_Doc")
                                        printManager.print("HP_Navigate_Print", printAdapter, PrintAttributes.Builder().build())
                                    }
                                },
                                leadingIcon = { Icon(Icons.Default.Print, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Partager") },
                                onClick = {
                                    showMenu = false
                                    val sendIntent = android.content.Intent().apply {
                                        action = android.content.Intent.ACTION_SEND
                                        putExtra(android.content.Intent.EXTRA_TEXT, currentUrlInput)
                                        type = "text/plain"
                                    }
                                    val shareIntent = android.content.Intent.createChooser(sendIntent, null)
                                    context.startActivity(shareIntent)
                                },
                                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) }
                            )
                        }
                    }
                )
                if (activeTab?.progress ?: 0 in 1..99) {
                    LinearProgressIndicator(
                        progress = { (activeTab?.progress ?: 0) / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { webViewInstance?.let { if (it.canGoBack()) it.goBack() } },
                        enabled = activeTab?.canGoBack == true,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Précédent")
                    }
                    IconButton(
                        onClick = { webViewInstance?.let { if (it.canGoForward()) it.goForward() } },
                        enabled = activeTab?.canGoForward == true,
                        modifier = Modifier.testTag("forward_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Suivant")
                    }
                    IconButton(
                        onClick = {
                            if (activeTab?.isLoading == true) webViewInstance?.stopLoading()
                            else webViewInstance?.reload()
                        },
                        modifier = Modifier.testTag("refresh_button")
                    ) {
                        Icon(
                            imageVector = if (activeTab?.isLoading == true) Icons.Default.Close else Icons.Default.Refresh,
                            contentDescription = "Actualiser"
                        )
                    }
                    IconButton(
                        onClick = { showAiSheet = true },
                        modifier = Modifier.testTag("ai_assistant_button")
                    ) {
                        Icon(imageVector = Icons.Default.SmartToy, contentDescription = "Assistant AI", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                url?.let {
                                    currentUrlInput = it
                                    browserController.updateActiveTabUrl(it)
                                }
                                browserController.updateActiveTabProgress(10, true)
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                url?.let {
                                    browserController.updateActiveTabUrl(it)
                                    browserController.updateActiveTabTitle(view?.title ?: it)
                                }
                                browserController.updateActiveTabProgress(100, false)
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                browserController.updateActiveTabProgress(newProgress, newProgress < 100)
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                super.onReceivedTitle(view, title)
                                title?.let { browserController.updateActiveTabTitle(it) }
                            }
                        }

                        loadUrl(initialUrl)
                        webViewInstance = this
                    }
                },
                update = { wv ->
                    if (activeTab != null && wv.url != activeTab.url) {
                        wv.loadUrl(activeTab.url)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        if (showAiSheet) {
            AiAssistantSheet(
                currentUrl = currentUrlInput,
                currentTitle = activeTab?.title ?: "Page Web",
                aiViewModel = aiViewModel,
                onDismiss = { showAiSheet = false }
            )
        }
    }
}
