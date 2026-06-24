package com.urbanvoice.app.presentation.ui.reports

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.urbanvoice.app.presentation.viewmodel.AuthViewModel
import com.urbanvoice.app.presentation.viewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReportsScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val authState by authViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        authState.profile?.let { viewModel.getReportsByUser(it.id) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Reportes") },
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
                        Text(state.error!!, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            authState.profile?.let { viewModel.getReportsByUser(it.id) }
                        }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            state.reports.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No has realizado reportes aún", style = MaterialTheme.typography.bodyLarge)
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
                            onClick = { authState.profile?.let { viewModel.getReportsByUser(it.id) } },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Actualizar")
                        }
                    }
                    items(state.reports) { report ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToDetail(report.id) },
                            shape = MaterialTheme.shapes.small
                        ) {
                            ListItem(
                                leadingContent = {
                                    Icon(
                                        imageVector = getIncidentIcon(report.incidentType),
                                        contentDescription = null,
                                        tint = Color.Red,
                                        modifier = Modifier.size(32.dp)
                                    )
                                },
                                headlineContent = {
                                    Text(
                                        report.title,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                supportingContent = {
                                    Column {
                                        Text(
                                            report.description,
                                            maxLines = 2,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = report.reportedAt.take(16).replace("T", " "),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                    }
                                },
                                trailingContent = {
                                    if (report.isAnonymous) {
                                        SuggestionChip(
                                            onClick = {},
                                            label = { Text("Anónimo", style = MaterialTheme.typography.labelSmall) }
                                        )
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

private fun getIncidentIcon(type: String): ImageVector {
    return when (type) {
        "ROBBERY" -> Icons.Default.MoneyOff
        "ASSAULT" -> Icons.Default.PersonOff
        "HARASSMENT" -> Icons.Default.Warning
        "VANDALISM" -> Icons.Default.BrokenImage
        "ACCIDENT" -> Icons.Default.CarCrash
        else -> Icons.Default.ReportProblem
    }
}
