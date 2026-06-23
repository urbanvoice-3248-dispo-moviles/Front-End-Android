package com.urbanvoice.app.presentation.ui.report

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import com.urbanvoice.app.presentation.theme.DangerColor
import com.urbanvoice.app.presentation.ui.components.LoadingOverlay
import com.urbanvoice.app.presentation.viewmodel.AuthViewModel
import com.urbanvoice.app.presentation.viewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIncidentScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: ReportViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val authState by authViewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("ROBBERY") }
    var isAnonymous by remember { mutableStateOf(false) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var mediaPath by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val incidentTypes = listOf(
        "ROBBERY" to "Robo", "ASSAULT" to "Asalto", "HARASSMENT" to "Acoso",
        "VANDALISM" to "Vandalismo", "ACCIDENT" to "Accidente", "OTHER" to "Otro"
    )

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            fusedClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                }
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        mediaPath = uri?.toString()
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            fusedClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                }
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(state.isCreated) {
        if (state.isCreated) {
            viewModel.clearState()
            onNavigateToHome()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportar Incidente") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LoadingOverlay(isLoading = state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Describe el incidente",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = incidentTypes.find { it.first == selectedType }?.second ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de incidente") },
                        leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        incidentTypes.forEach { (value, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    selectedType = value
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("Título") },
                    leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text("Descripción") },
                    minLines = 4, modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (latitude != null) "Ubicación capturada"
                        else "Obteniendo ubicación...",
                        color = if (latitude != null) Color(0xFF388E3C) else Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (latitude == null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") }
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Agregar evidencia")
                    }
                    if (mediaPath != null) {
                        AssistChip(
                            onClick = {},
                            label = { Text("1 archivo") },
                            trailingIcon = {
                                IconButton(
                                    onClick = { mediaPath = null },
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Quitar", modifier = Modifier.size(14.dp))
                                }
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VisibilityOff, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Reporte anónimo", style = MaterialTheme.typography.bodyLarge)
                            Text("Ocultar mi identidad", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Switch(checked = isAnonymous, onCheckedChange = { isAnonymous = it })
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val profile = authState.profile
                        if (profile != null && latitude != null && longitude != null) {
                            viewModel.createReport(
                                userId = profile.id,
                                title = title, description = description,
                                incidentType = selectedType,
                                latitude = latitude!!, longitude = longitude!!,
                                address = null, mediaUrl = mediaPath,
                                isAnonymous = isAnonymous
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DangerColor),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enviar Reporte")
                }

                if (state.error != null) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
