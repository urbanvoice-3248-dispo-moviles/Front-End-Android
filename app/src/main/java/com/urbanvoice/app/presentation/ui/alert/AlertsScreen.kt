package com.urbanvoice.app.presentation.ui.alert

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.urbanvoice.app.presentation.viewmodel.AlertViewModel
import com.urbanvoice.app.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AlertViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val authState by authViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        val profile = authState.profile
        if (profile != null) {
            viewModel.getAlertsByUser(profile.id)
        } else {
            viewModel.getAllAlerts()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alertas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.error!!, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            val profile = authState.profile
                            if (profile != null) viewModel.getAlertsByUser(profile.id)
                            else viewModel.getAllAlerts()
                        }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            state.alerts.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.NotificationsOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No tienes alertas", color = Color.Gray)
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        TextButton(
                            onClick = {
                                val profile = authState.profile
                                if (profile != null) viewModel.getAlertsByUser(profile.id)
                                else viewModel.getAllAlerts()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Actualizar")
                        }
                    }
                    items(state.alerts) { alert ->
                        Card(
                            shape = MaterialTheme.shapes.small
                        ) {
                            ListItem(
                                leadingContent = {
                                    Icon(
                                        imageVector = getAlertIcon(alert.type),
                                        contentDescription = null,
                                        tint = if (alert.isRead) Color.Gray else Color.Red,
                                        modifier = Modifier.size(32.dp)
                                    )
                                },
                                headlineContent = {
                                    Text(
                                        text = alert.title,
                                        fontWeight = if (alert.isRead) FontWeight.Normal else FontWeight.Bold
                                    )
                                },
                                supportingContent = {
                                    Column {
                                        Text(
                                            text = alert.message,
                                            maxLines = 2,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = alert.createdAt.take(16).replace("T", " "),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                    }
                                },
                                trailingContent = {
                                    if (!alert.isRead) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .padding(end = 8.dp)
                                        ) {
                                            Surface(
                                                modifier = Modifier.size(12.dp),
                                                shape = CircleShape,
                                                color = Color.Red
                                            ) {}
                                        }
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

private fun getAlertIcon(type: String): ImageVector {
    return when (type) {
        "INCIDENT_NEARBY" -> Icons.Default.LocationOn
        "HIGH_RISK_ZONE" -> Icons.Default.Warning
        "EMERGENCY" -> Icons.Default.Emergency
        else -> Icons.Default.Notifications
    }
}
