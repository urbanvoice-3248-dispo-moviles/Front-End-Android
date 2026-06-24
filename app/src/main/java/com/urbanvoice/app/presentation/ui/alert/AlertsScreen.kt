package com.urbanvoice.app.presentation.ui.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.urbanvoice.app.presentation.viewmodel.AlertViewModel
import com.urbanvoice.app.presentation.viewmodel.AuthViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    viewModel: AlertViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val authState by authViewModel.state.collectAsStateWithLifecycle()
    var selectedAlert by remember { mutableStateOf<com.urbanvoice.app.domain.model.Alert?>(null) }

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.Red.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            state.error!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
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
                            modifier = Modifier.size(72.dp),
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No tienes alertas",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Las alertas aparecerán aquí cuando haya\nincidentes cerca de tu ubicación",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    items(state.alerts, key = { it.id }) { alert ->
                        AlertCard(
                            alert = alert,
                            onClick = { selectedAlert = alert }
                        )
                    }
                }
            }
        }
    }

    selectedAlert?.let { alert ->
        AlertDialog(
            onDismissRequest = { selectedAlert = null },
            title = { Text(alert.title, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(alert.message, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Tipo: ${alert.type}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    if (alert.latitude != null && alert.longitude != null) {
                        Text(
                            "Ubicación: ${"%.4f".format(alert.latitude)}, ${"%.4f".format(alert.longitude)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Text(
                        "Recibida: ${formatAlertTime(alert.createdAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedAlert = null }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
private fun AlertCard(
    alert: com.urbanvoice.app.domain.model.Alert,
    onClick: () -> Unit = {}
) {
    val (cardColor, iconColor) = getAlertColors(alert.type)
    val formattedTime = formatAlertTime(alert.createdAt)

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        ListItem(
            leadingContent = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getAlertIcon(alert.type),
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            },
            headlineContent = {
                Text(
                    text = alert.title,
                    fontWeight = if (alert.isRead) FontWeight.Medium else FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            supportingContent = {
                Column {
                    Text(
                        text = alert.message,
                        maxLines = 2,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            },
            trailingContent = {
                if (!alert.isRead) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(iconColor)
                    )
                }
            }
        )
    }
}

private fun getAlertIcon(type: String): ImageVector = when (type) {
    "INCIDENT_NEARBY" -> Icons.Default.LocationOn
    "HIGH_RISK_ZONE" -> Icons.Default.Warning
    "EMERGENCY" -> Icons.Default.Emergency
    else -> Icons.Default.Notifications
}

private fun getAlertColors(type: String): Pair<Color, Color> = when (type) {
    "INCIDENT_NEARBY" -> Color(0xFFFFF3E0) to Color(0xFFE65100)
    "HIGH_RISK_ZONE" -> Color(0xFFFFEBEE) to Color(0xFFC62828)
    "EMERGENCY" -> Color(0xFFFCE4EC) to Color(0xFFB71C1C)
    else -> Color(0xFFE8EAF6) to Color(0xFF283593)
}

private fun formatAlertTime(isoString: String): String {
    return try {
        val dateTime = LocalDateTime.parse(isoString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val now = LocalDateTime.now()
        val daysBetween = ChronoUnit.DAYS.between(dateTime.toLocalDate(), now.toLocalDate())
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        when {
            daysBetween == 0L -> "hoy, ${dateTime.format(timeFormatter)}"
            daysBetween == 1L -> "ayer, ${dateTime.format(timeFormatter)}"
            daysBetween <= 7 -> "hace $daysBetween días"
            else -> dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }
    } catch (_: Exception) {
        isoString.take(16).replace("T", " ")
    }
}
