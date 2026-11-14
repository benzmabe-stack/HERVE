package com.example.browser

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.UUID

data class BrowserTab(
    val id: String = UUID.randomUUID().toString(),
    var url: String = "https://www.google.com",
    var title: String = "Google",
    var progress: Int = 0,
    var isLoading: Boolean = false,
    val isIncognito: Boolean = false,
    var canGoBack: Boolean = false,
    var canGoForward: Boolean = false
)

class BrowserController {
    var tabs = mutableStateOf(listOf(BrowserTab()))
        private set

    var activeTabId = mutableStateOf(tabs.value.first().id)
        private set

    var isDesktopMode = mutableStateOf(false)
        private set

    var isDarkModeWebView = mutableStateOf(true)
        private set

    val activeTab: BrowserTab?
        get() = tabs.value.find { it.id == activeTabId.value }

    fun openNewTab(url: String = "https://www.google.com", isIncognito: Boolean = false) {
        val newTab = BrowserTab(url = url, isIncognito = isIncognito, title = if (url.contains("google")) "Google" else url)
        tabs.value = tabs.value + newTab
        activeTabId.value = newTab.id
    }

    fun closeTab(id: String) {
        val currentTabs = tabs.value
        if (currentTabs.size <= 1) {
            // Keep at least one tab open or reset
            tabs.value = listOf(BrowserTab())
            activeTabId.value = tabs.value.first().id
            return
        }

        val index = currentTabs.indexOfFirst { it.id == id }
        val newTabs = currentTabs.filter { it.id != id }
        tabs.value = newTabs

        if (activeTabId.value == id) {
            val newIndex = if (index > 0) index - 1 else 0
            activeTabId.value = newTabs[newIndex].id
        }
    }

    fun closeAllTabs(incognitoOnly: Boolean = false) {
        if (incognitoOnly) {
            val remaining = tabs.value.filter { !it.isIncognito }
            if (remaining.isEmpty()) {
                tabs.value = listOf(BrowserTab())
            } else {
                tabs.value = remaining
            }
        } else {
            tabs.value = listOf(BrowserTab())
        }
        activeTabId.value = tabs.value.first().id
    }

    fun selectTab(id: String) {
        activeTabId.value = id
    }

    fun updateActiveTabUrl(url: String) {
        tabs.value = tabs.value.map {
            if (it.id == activeTabId.value) it.copy(url = url) else it
        }
    }

    fun updateActiveTabTitle(title: String) {
        tabs.value = tabs.value.map {
            if (it.id == activeTabId.value) it.copy(title = title) else it
        }
    }

    fun updateActiveTabProgress(progress: Int, isLoading: Boolean) {
        tabs.value = tabs.value.map {
            if (it.id == activeTabId.value) it.copy(progress = progress, isLoading = isLoading) else it
        }
    }

    fun toggleDesktopMode() {
        isDesktopMode.value = !isDesktopMode.value
    }

    fun toggleDarkModeWebView() {
        isDarkModeWebView.value = !isDarkModeWebView.value
    }
}
