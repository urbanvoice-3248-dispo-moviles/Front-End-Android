package com.urbanvoice.app.presentation.ui.report

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerDialog(
    initialLatitude: Double?,
    initialLongitude: Double?,
    onConfirm: (Double, Double) -> Unit,
    onDismiss: () -> Unit
) {
    val defaultLatLng = LatLng(-12.0464, -77.0428)
    var selectedLatLng by remember {
        mutableStateOf(
            if (initialLatitude != null && initialLongitude != null)
                LatLng(initialLatitude, initialLongitude)
            else null
        )
    }

    val cameraPositionState = rememberCameraPositionState {
        position = if (selectedLatLng != null)
            CameraPosition.fromLatLngZoom(selectedLatLng!!, 15f)
        else
            CameraPosition.fromLatLngZoom(defaultLatLng, 12f)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text("Seleccionar ubicación") },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Cancelar")
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = {
                                selectedLatLng?.let {
                                    onConfirm(it.latitude, it.longitude)
                                }
                            },
                            enabled = selectedLatLng != null
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Confirmar")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Box(modifier = Modifier.weight(1f)) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = true,
                            myLocationButtonEnabled = true
                        ),
                        properties = MapProperties(isMyLocationEnabled = true),
                        onMapClick = { latLng ->
                            selectedLatLng = latLng
                        }
                    ) {
                        selectedLatLng?.let { position ->
                            Marker(
                                state = rememberMarkerState(position = position),
                                title = "Ubicación seleccionada",
                                snippet = "${"%.5f".format(position.latitude)}, ${"%.5f".format(position.longitude)}"
                            )
                        }
                    }

                    if (selectedLatLng == null) {
                        Card(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 16.dp)
                                .padding(horizontal = 32.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.inverseSurface
                            )
                        ) {
                            Text(
                                text = "Toca el mapa para seleccionar una ubicación",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                if (selectedLatLng != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Ubicación seleccionada",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Lat: ${"%.5f".format(selectedLatLng!!.latitude)}" +
                                        " - Lon: ${"%.5f".format(selectedLatLng!!.longitude)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
