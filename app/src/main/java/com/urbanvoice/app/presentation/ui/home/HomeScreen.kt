package com.urbanvoice.app.presentation.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.urbanvoice.app.domain.model.IncidentReport
import com.urbanvoice.app.presentation.ui.components.AppDrawer
import com.urbanvoice.app.presentation.viewmodel.AuthViewModel
import com.urbanvoice.app.presentation.viewmodel.LocationViewModel
import com.urbanvoice.app.presentation.viewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToReport: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToMyReports: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    onLogout: () -> Unit,
    locationViewModel: LocationViewModel = hiltViewModel(),
    reportViewModel: ReportViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val locationState by locationViewModel.state.collectAsStateWithLifecycle()
    val reportState by reportViewModel.state.collectAsStateWithLifecycle()
    val authState by authViewModel.state.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val defaultLatLng = LatLng(-12.0464, -77.0428)

    LaunchedEffect(Unit) {
        locationViewModel.getAllLocations()
        reportViewModel.getNearbyReports(-12.0464, -77.0428)
    }

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
                    cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(defaultLatLng, 12f)
                    },
                    uiSettings = MapUiSettings(
                        myLocationButtonEnabled = true,
                        zoomControlsEnabled = true
                    )
                ) {
                    locationState.locations.forEach { loc ->
                        MarkerInfoWindow(
                            state = rememberMarkerState(
                                position = LatLng(loc.latitude, loc.longitude)
                            ),
                            icon = BitmapDescriptorFactory.defaultMarker(
                                getRiskHue(loc.riskLevel)
                            ),
                            title = loc.district,
                            snippet = "Riesgo: ${loc.riskCategory} - Incidentes: ${loc.incidentCount}"
                        )
                    }
                    reportState.reports.forEach { report ->
                        MarkerInfoWindow(
                            state = rememberMarkerState(
                                position = LatLng(report.latitude, report.longitude)
                            ),
                            icon = BitmapDescriptorFactory.defaultMarker(
                                getReportHue(report.incidentType)
                            ),
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
}

private fun getRiskHue(riskLevel: Int): Float = when (riskLevel) {
    4, 5 -> BitmapDescriptorFactory.HUE_RED
    3 -> BitmapDescriptorFactory.HUE_ORANGE
    2 -> BitmapDescriptorFactory.HUE_YELLOW
    else -> BitmapDescriptorFactory.HUE_GREEN
}

private fun getReportHue(type: String): Float = when (type) {
    "ROBBERY" -> BitmapDescriptorFactory.HUE_VIOLET
    "ASSAULT" -> BitmapDescriptorFactory.HUE_RED
    "HARASSMENT" -> BitmapDescriptorFactory.HUE_ORANGE
    "VANDALISM" -> BitmapDescriptorFactory.HUE_CYAN
    "ACCIDENT" -> BitmapDescriptorFactory.HUE_BLUE
    else -> BitmapDescriptorFactory.HUE_ROSE
}
