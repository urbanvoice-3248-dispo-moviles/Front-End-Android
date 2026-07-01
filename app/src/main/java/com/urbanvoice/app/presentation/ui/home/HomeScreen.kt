package com.urbanvoice.app.presentation.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.urbanvoice.app.presentation.ui.components.AppDrawer
import com.urbanvoice.app.presentation.ui.utils.rememberLocationPermissionState
import com.urbanvoice.app.presentation.viewmodel.AuthViewModel
import com.urbanvoice.app.presentation.viewmodel.DistrictViewModel
import com.urbanvoice.app.presentation.viewmodel.LocationViewModel
import com.urbanvoice.app.presentation.viewmodel.ReportViewModel
import kotlinx.coroutines.launch

private val allIncidentTypes = listOf(
    "ROBBERY" to "Robo", "ASSAULT" to "Asalto", "HARASSMENT" to "Acoso",
    "VANDALISM" to "Vandalismo", "ACCIDENT" to "Accidente", "OTHER" to "Otro"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToReport: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToMyReports: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToModerate: (() -> Unit)? = null,
    onNavigateToDetail: (Int) -> Unit,
    onLogout: () -> Unit,
    locationViewModel: LocationViewModel = hiltViewModel(),
    reportViewModel: ReportViewModel = hiltViewModel(),
    districtViewModel: DistrictViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val locationState by locationViewModel.state.collectAsStateWithLifecycle()
    val reportState by reportViewModel.state.collectAsStateWithLifecycle()
    val districtState by districtViewModel.state.collectAsStateWithLifecycle()
    val authState by authViewModel.state.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val defaultLatLng = LatLng(-12.0464, -77.0428)
    var userLocation by remember { mutableStateOf(defaultLatLng) }
    var locationPermissionGranted by remember { mutableStateOf(false) }

    var selectedTypes by remember { mutableStateOf(allIncidentTypes.map { it.first }.toSet()) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLatLng, 12f)
    }

    val locationPermissionState = rememberLocationPermissionState { latitude, longitude ->
        locationPermissionGranted = true
        userLocation = LatLng(latitude, longitude)
        scope.launch {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(userLocation, 14f))
        }
        reportViewModel.getNearbyReports(latitude, longitude)
    }

    LaunchedEffect(Unit) {
        locationViewModel.getAllLocations()
        districtViewModel.getAllDistricts()
        locationPermissionState.requestPermission()
    }

    val filteredReports = reportState.reports.filter { it.incidentType in selectedTypes }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                profile = authState.profile,
                onMapaDeRiesgo = { scope.launch { drawerState.close() } },
                onReportarIncidente = {
                    scope.launch { drawerState.close() }
                    onNavigateToReport()
                },
                onMisReportes = {
                    scope.launch { drawerState.close() }
                    onNavigateToMyReports()
                },
                onAlertas = {
                    scope.launch { drawerState.close() }
                    onNavigateToAlerts()
                },
                onMiPerfil = {
                    scope.launch { drawerState.close() }
                    onNavigateToProfile()
                },
                onModeracion = {
                    scope.launch { drawerState.close() }
                    onNavigateToModerate?.invoke()
                },
                onCerrarSesion = {
                    scope.launch { drawerState.close() }
                    authViewModel.logout()
                    onLogout()
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("UrbanVoice") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menú")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filtrar",
                                tint = if (selectedTypes.size < allIncidentTypes.size)
                                    MaterialTheme.colorScheme.primary
                                else Color.Gray
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FloatingActionButton(
                        onClick = onNavigateToReport,
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Reportar")
                    }
                    SmallFloatingActionButton(
                        onClick = onNavigateToAlerts,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Alertas")
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        myLocationButtonEnabled = locationPermissionGranted,
                        zoomControlsEnabled = true
                    ),
                    properties = MapProperties(
                        isMyLocationEnabled = locationPermissionGranted
                    )
                ) {
                    districtState.districts.forEach { district ->
                        if (district.boundary.isNotEmpty()) {
                            val avgLat = district.boundary.map { it.latitude }.average()
                            val avgLng = district.boundary.map { it.longitude }.average()
                            MarkerInfoWindow(
                                state = rememberMarkerState(
                                    position = LatLng(avgLat, avgLng)
                                ),
                                icon = remember(district.riskLevel) { getRiskMarkerIcon(district.riskLevel) },
                                title = district.name,
                                snippet = "Riesgo: ${district.riskCategory} - Incidentes: ${district.incidentCount}"
                            )
                        }
                    }
                    locationState.locations.forEach { loc ->
                        MarkerInfoWindow(
                            state = rememberMarkerState(
                                position = LatLng(loc.latitude, loc.longitude)
                            ),
                            icon = remember { getLocationMarkerIcon() },
                            title = loc.address ?: loc.district,
                            snippet = loc.district
                        )
                    }
                    filteredReports.forEach { report ->
                        MarkerInfoWindow(
                            state = rememberMarkerState(
                                position = LatLng(report.latitude, report.longitude)
                            ),
                            icon = remember(report.incidentType) { getReportMarkerIcon(report.incidentType) },
                            title = report.title,
                            snippet = report.description,
                            onClick = {
                                onNavigateToDetail(report.id)
                                true
                            }
                        )
                    }
                }

                if (locationState.isLoading || reportState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false }
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Filtrar incidentes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                allIncidentTypes.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedTypes = if (value in selectedTypes) {
                                    selectedTypes - value
                                } else {
                                    selectedTypes + value
                                }
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = value in selectedTypes,
                            onCheckedChange = {
                                selectedTypes = if (it) {
                                    selectedTypes + value
                                } else {
                                    selectedTypes - value
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .padding(end = 8.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(12.dp),
                                shape = RoundedCornerShape(2.dp),
                        color = getReportColor(value)
                            ) {}
                        }
                        Text(label, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Aplicar filtros")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

private fun getLocationMarkerIcon(): BitmapDescriptor {
    return createCircleMarker(android.graphics.Color.parseColor("#757575"), android.graphics.Color.WHITE, "•")
}

private fun getRiskMarkerIcon(riskLevel: Int): BitmapDescriptor {
    val (bgColor, letter) = when (riskLevel) {
        4, 5 -> android.graphics.Color.parseColor("#D32F2F") to "!"
        3 -> android.graphics.Color.parseColor("#F57C00") to "!"
        2 -> android.graphics.Color.parseColor("#FBC02D") to "~"
        else -> android.graphics.Color.parseColor("#388E3C") to "~"
    }
    return createCircleMarker(bgColor, android.graphics.Color.WHITE, letter)
}

private fun getReportMarkerIcon(type: String): BitmapDescriptor {
    val (bgColor, letter) = when (type) {
        "ROBBERY" -> android.graphics.Color.parseColor("#9C27B0") to "R"
        "ASSAULT" -> android.graphics.Color.parseColor("#D32F2F") to "A"
        "HARASSMENT" -> android.graphics.Color.parseColor("#F57C00") to "H"
        "VANDALISM" -> android.graphics.Color.parseColor("#0097A7") to "V"
        "ACCIDENT" -> android.graphics.Color.parseColor("#1976D2") to "C"
        else -> android.graphics.Color.parseColor("#C2185B") to "O"
    }
    return createCircleMarker(bgColor, android.graphics.Color.WHITE, letter)
}

private fun createCircleMarker(bgColor: Int, textColor: Int, letter: String): BitmapDescriptor {
    val size = 56
    val bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
    val center = size / 2f
    val radius = 24f

    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(center, center, radius + 3f, paint)

    paint.color = bgColor
    canvas.drawCircle(center, center, radius, paint)

    paint.color = textColor
    paint.textSize = 28f
    paint.textAlign = android.graphics.Paint.Align.CENTER
    paint.isFakeBoldText = true
    val yOffset = -(paint.descent() + paint.ascent()) / 2
    canvas.drawText(letter, center, center + yOffset, paint)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

private fun getReportColor(type: String): Color = when (type) {
    "ROBBERY" -> Color(0xFF9C27B0)
    "ASSAULT" -> Color(0xFFF44336)
    "HARASSMENT" -> Color(0xFFFF9800)
    "VANDALISM" -> Color(0xFF00BCD4)
    "ACCIDENT" -> Color(0xFF2196F3)
    else -> Color(0xFFE91E63)
}
