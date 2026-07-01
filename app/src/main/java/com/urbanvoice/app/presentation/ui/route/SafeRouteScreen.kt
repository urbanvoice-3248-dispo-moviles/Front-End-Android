package com.urbanvoice.app.presentation.ui.route

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.urbanvoice.app.presentation.viewmodel.RouteState
import com.urbanvoice.app.presentation.viewmodel.RouteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeRouteScreen(
    routeViewModel: RouteViewModel,
    onNavigateBack: () -> Unit,
    userLatitude: Double,
    userLongitude: Double
) {
    val routeState by routeViewModel.state.collectAsState()
    var destinationText by remember { mutableStateOf("") }
    var showDestinationDialog by remember { mutableStateOf(false) }
    var showRouteList by remember { mutableStateOf(false) }

    val defaultLatLng = LatLng(
        userLatitude.takeIf { it != 0.0 } ?: -12.0464,
        userLongitude.takeIf { it != 0.0 } ?: -77.0428
    )
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLatLng, 13f)
    }

    // Inicializar el origen con la ubicación del usuario
    LaunchedEffect(Unit) {
        val validLat = userLatitude.takeIf { it != 0.0 } ?: -12.0464
        val validLng = userLongitude.takeIf { it != 0.0 } ?: -77.0428
        routeViewModel.setOrigin(validLat, validLng)
    }

    LaunchedEffect(routeState.destination) {
        routeState.destination?.let {
            if (routeState.origin != null) {
                routeViewModel.findSafeRoute(
                    routeState.origin!!.first, routeState.origin!!.second,
                    it.first, it.second
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ruta Segura") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    if (routeState.routes.isNotEmpty()) {
                        IconButton(onClick = { showRouteList = !showRouteList }) {
                            Icon(Icons.Default.Route, contentDescription = "Ver rutas")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (routeState.routes.isNotEmpty()) {
                val selected = routeState.routes.getOrNull(routeState.selectedRouteIndex)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Ruta recomendada (más segura)",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "${selected?.distance ?: ""} - ${selected?.duration ?: ""}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "Safety Score:",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    "%.1f".format(selected?.safetyScore ?: 0.0),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        (selected?.safetyScore ?: 5.0) <= 1.5 -> Color(0xFF388E3C)
                                        (selected?.safetyScore ?: 5.0) <= 3.0 -> Color(0xFFF57C00)
                                        else -> Color(0xFFD32F2F)
                                    }
                                )
                            }
                        }
                        if (routeState.routes.size > 1) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "${routeState.routes.size} rutas encontradas - Seleccionada la más segura",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (routeState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Calculando ruta segura...")
                    }
                }
            } else {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        myLocationButtonEnabled = true
                    ),
                    properties = MapProperties(isMyLocationEnabled = true),
                    onMapClick = { latLng ->
                        if (routeState.destination == null) {
                            destinationText = "${latLng.latitude},${latLng.longitude}"
                            routeViewModel.findSafeRoute(
                                userLatitude, userLongitude,
                                latLng.latitude, latLng.longitude
                            )
                        }
                    }
                ) {
                    routeState.origin?.let {
                        MarkerInfoWindow(
                            state = rememberMarkerState(position = LatLng(it.first, it.second)),
                            icon = remember { BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN) },
                            title = "Origen",
                            snippet = "Tu ubicación"
                        )
                    }
                    routeState.destination?.let {
                        MarkerInfoWindow(
                            state = rememberMarkerState(position = LatLng(it.first, it.second)),
                            icon = remember { BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED) },
                            title = "Destino",
                            snippet = destinationText
                        )
                    }
                    routeState.routes.forEachIndexed { index, route ->
                        val path = route.decodedPath.map { LatLng(it.first, it.second) }
                        val isSelected = index == routeState.selectedRouteIndex
                        val color = when {
                            route.safetyScore <= 1.5 -> Color(0xFF388E3C)
                            route.safetyScore <= 3.0 -> Color(0xFFF57C00)
                            else -> Color(0xFFD32F2F)
                        }
                        Polyline(
                            points = path,
                            color = if (isSelected) color else color.copy(alpha = 0.3f),
                            width = if (isSelected) 8f else 4f,
                            jointType = JointType.ROUND,
                            clickable = false
                        )
                    }
                }
            }

            if (routeState.error != null) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(routeState.error ?: "", color = Color.Red)
                    }
                }
            }

            if (!routeState.isLoading && routeState.routes.isEmpty()) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Buscar ruta segura",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Toca en el mapa para seleccionar un destino\ny se calculará la ruta más segura.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = destinationText,
                            onValueChange = { destinationText = it },
                            label = { Text("O ingresa coordenadas (lat,lng)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val parts = destinationText.split(",").map { it.trim().toDoubleOrNull() }
                                if (parts.size == 2 && parts[0] != null && parts[1] != null) {
                                    routeViewModel.findSafeRoute(
                                        userLatitude, userLongitude,
                                        parts[0]!!, parts[1]!!
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Route, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Calcular ruta segura")
                        }
                    }
                }
            }

            if (showRouteList && routeState.routes.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth(0.95f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Rutas disponibles",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        routeState.routes.forEachIndexed { index, route ->
                            val isSelected = index == routeState.selectedRouteIndex
                            val scoreColor = when {
                                route.safetyScore <= 1.5 -> Color(0xFF388E3C)
                                route.safetyScore <= 3.0 -> Color(0xFFF57C00)
                                else -> Color(0xFFD32F2F)
                            }
                            Surface(
                                onClick = { routeViewModel.selectRoute(index); showRouteList = false },
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "Ruta #${index + 1} - ${route.distance}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            "${route.duration}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            "%.1f".format(route.safetyScore),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = scoreColor
                                        )
                                        Text(
                                            "riesgo",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                    if (index == 0) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = "Más segura",
                                            tint = Color(0xFF388E3C)
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
}
