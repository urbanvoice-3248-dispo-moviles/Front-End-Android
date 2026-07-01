package com.urbanvoice.app.presentation.ui.locationsharing

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.urbanvoice.app.domain.model.UserLiveLocation
import com.urbanvoice.app.presentation.viewmodel.AuthViewModel
import com.urbanvoice.app.presentation.viewmodel.LocationSharingViewModel
import com.urbanvoice.app.presentation.viewmodel.LocationSharingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSharingScreen(
    authViewModel: AuthViewModel,
    locationSharingViewModel: LocationSharingViewModel,
    onNavigateBack: () -> Unit,
    locationPermissionGranted: Boolean,
    userLatitude: Double,
    userLongitude: Double
) {
    val authState by authViewModel.state.collectAsState()
    val sharingState by locationSharingViewModel.state.collectAsState()
    val userId = authState.profile?.id ?: return

    var showStartSharingDialog by remember { mutableStateOf(false) }
    var targetUserIdText by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        locationSharingViewModel.startPolling(userId)
        locationSharingViewModel.loadMyShares(userId)
    }

    LaunchedEffect(userLatitude, userLongitude) {
        if (userLatitude != 0.0 && userLongitude != 0.0) {
            locationSharingViewModel.publishLocation(userId, userLatitude, userLongitude)
        }
    }

    DisposableEffect(Unit) {
        onDispose { locationSharingViewModel.stopPolling() }
    }

    val defaultLatLng = LatLng(userLatitude.takeIf { it != 0.0 } ?: -12.0464, userLongitude.takeIf { it != 0.0 } ?: -77.0428)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLatLng, 13f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compartir Ubicación") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    if (!sharingState.isSharingActive) {
                        IconButton(onClick = { showStartSharingDialog = true }) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Compartir con")
                        }
                    }
                }
            )
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
                if (userLatitude != 0.0 && userLongitude != 0.0) {
                    MarkerInfoWindow(
                        state = rememberMarkerState(position = LatLng(userLatitude, userLongitude)),
                        icon = remember { BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE) },
                        title = "Mi ubicación",
                        snippet = "Actualizada ahora"
                    )
                }
                sharingState.friendsLocations.forEach { friend ->
                    MarkerInfoWindow(
                        state = rememberMarkerState(
                            position = LatLng(friend.latitude, friend.longitude)
                        ),
                        icon = remember { BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE) },
                        title = "Usuario #${friend.userId}",
                        snippet = "Actualizado: ${friend.updatedAt}"
                    )
                }
            }

            if (sharingState.myShares.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .fillMaxWidth(0.9f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Compartiendo con:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        sharingState.myShares.forEach { targetId ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("#$targetId", modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = {
                                        locationSharingViewModel.stopSharing(userId, targetId)
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Dejar de compartir",
                                        tint = Color.Red,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (sharingState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    if (showStartSharingDialog) {
        AlertDialog(
            onDismissRequest = { showStartSharingDialog = false },
            title = { Text("Compartir ubicación") },
            text = {
                Column {
                    Text("Ingresa el ID del usuario con quien quieres compartir tu ubicación:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = targetUserIdText,
                        onValueChange = { targetUserIdText = it },
                        label = { Text("ID de usuario") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val targetId = targetUserIdText.toIntOrNull()
                        if (targetId != null && targetId > 0) {
                            locationSharingViewModel.startSharing(userId, targetId)
                        }
                        showStartSharingDialog = false
                        targetUserIdText = ""
                    }
                ) { Text("Compartir") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showStartSharingDialog = false
                    targetUserIdText = ""
                }) { Text("Cancelar") }
            }
        )
    }

    if (sharingState.error != null) {
        LaunchedEffect(sharingState.error) {
            kotlinx.coroutines.delay(3000)
            locationSharingViewModel.clearError()
        }
    }
}
