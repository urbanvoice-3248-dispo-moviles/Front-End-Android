package com.urbanvoice.app.presentation.ui.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.urbanvoice.app.presentation.viewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentDetailScreen(
    reportId: Int,
    onNavigateBack: () -> Unit,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(reportId) {
        viewModel.getReportById(reportId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Incidente") },
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
            state.selectedReport != null -> {
                val report = state.selectedReport!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = rememberCameraPositionState {
                                position = CameraPosition.fromLatLngZoom(
                                    LatLng(report.latitude, report.longitude), 15f
                                )
                            },
                            uiSettings = MapUiSettings(zoomControlsEnabled = true)
                        )
                        Marker(
                            state = MarkerState(
                                position = LatLng(report.latitude, report.longitude)
                            ),
                            title = report.title
                        )
                    }
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = getIncidentIcon(report.incidentType),
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = report.title,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SuggestionChip(
                                onClick = {},
                                label = { Text(report.incidentType) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = Color(0xFFFFCDD2)
                                )
                            )
                            if (report.isAnonymous) {
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text("Reporte Anónimo") }
                                )
                            }
                        }
                        Text(
                            text = report.description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = report.address ?: "Sin dirección",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.Gray)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = report.reportedAt.take(16).replace("T", " "),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                        if (report.mediaUrl != null) {
                            Text("Evidencia adjunta:", style = MaterialTheme.typography.bodyMedium)
                            Surface(
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                color = Color(0xFFEEEEEE)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Image,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = Color.Gray
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

private fun getIncidentIcon(type: String) = when (type) {
    "ROBBERY" -> Icons.Default.MoneyOff
    "ASSAULT" -> Icons.Default.PersonOff
    "HARASSMENT" -> Icons.Default.Warning
    "VANDALISM" -> Icons.Default.BrokenImage
    "ACCIDENT" -> Icons.Default.CarCrash
    else -> Icons.Default.ReportProblem
}
