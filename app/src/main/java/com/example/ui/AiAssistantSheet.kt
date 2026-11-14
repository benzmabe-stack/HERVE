package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ai.AiUiState
import com.example.ai.HpAiAssistantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantSheet(
    currentUrl: String,
    currentTitle: String,
    aiViewModel: HpAiAssistantViewModel,
    onDismiss: () -> Unit
) {
    val aiState by aiViewModel.uiState.collectAsStateWithLifecycle()
    var customQuery by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("French") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Assistant Intelligent HP AI",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Page: $currentTitle",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { aiViewModel.processRequest("summarize", "URL: $currentUrl, Title: $currentTitle") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Résumer")
                }
                Button(
                    onClick = { aiViewModel.processRequest("explain", "URL: $currentUrl, Title: $currentTitle") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Expliquer")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { aiViewModel.processRequest("translate", "URL: $currentUrl, Title: $currentTitle", selectedLanguage) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Traduire ($selectedLanguage)")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = customQuery,
                onValueChange = { customQuery = it },
                label = { Text("Poser une question sur cette page...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (customQuery.isNotBlank()) {
                        aiViewModel.processRequest("qa", "Based on web page $currentUrl ($currentTitle), answer the question: $customQuery")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = customQuery.isNotBlank()
            ) {
                Text("Envoyer à l'Assistant")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Résultat de l'Assistant :",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    when (val state = aiState) {
                        is AiUiState.Idle -> {
                            Text(
                                text = "Sélectionnez une action ci-dessus pour laisser l'Assistant HP analyser la page.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        is AiUiState.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                        is AiUiState.Success -> {
                            Text(text = state.result, style = MaterialTheme.typography.bodyMedium)
                        }
                        is AiUiState.Error -> {
                            Text(text = "Erreur: ${state.message}", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
