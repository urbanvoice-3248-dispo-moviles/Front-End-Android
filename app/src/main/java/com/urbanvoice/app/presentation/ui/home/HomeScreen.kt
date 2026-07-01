package com.urbanvoice.app.presentation.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

private const val DEFAULT_LIMA_LATITUDE = -12.0464
private const val DEFAULT_LIMA_LONGITUDE = -77.0428
private const val DEFAULT_MAP_ZOOM = 12f
private const val DEFAULT_NEARBY_RADIUS_KM = 5.0

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
    val mapError = locationState.error ?: reportState.error

    val defaultLatLng = LatLng(DEFAULT_LIMA_LATITUDE, DEFAULT_LIMA_LONGITUDE)

    LaunchedEffect(Unit) {
        locationViewModel.getAllLocations()
        reportViewModel.getNearbyReports(
            DEFAULT_LIMA_LATITUDE,
            DEFAULT_LIMA_LONGITUDE,
            DEFAULT_NEARBY_RADIUS_KM
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                profile = authState.profile,
                onMapaDeRiesgo = { drawerState.close() },
                onReportarIncidente = {
                    drawerState.close()
                    onNavigateToReport()
                },
                onMisReportes = {
                    drawerState.close()
                    onNavigateToMyReports()
                },
                onAlertas = {
                    drawerState.close()
                    onNavigateToAlerts()
                },
                onMiPerfil = {
                    drawerState.close()
                    onNavigateToProfile()
                },
                onCerrarSesion = {
                    drawerState.close()
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
                        IconButton(onClick = { drawerState.open() }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
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
                        position = CameraPosition.fromLatLngZoom(defaultLatLng, DEFAULT_MAP_ZOOM)
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

                if (mapError != null) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = mapError,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
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
