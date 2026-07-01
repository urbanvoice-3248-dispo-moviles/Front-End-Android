package com.urbanvoice.app.presentation.ui.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

data class LocationPermissionState(
    val granted: State<Boolean>,
    val requestPermission: () -> Unit
)

@Composable
fun rememberLocationPermissionState(
    onLocationFound: (Double, Double) -> Unit
): LocationPermissionState {
    val context = LocalContext.current
    val isGrantedState = remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isGrantedState.value = granted
        if (granted) {
            requestLastLocation(context, onLocationFound)
        }
    }

    val requestPermission = remember(launcher, context) {
        {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                isGrantedState.value = true
                requestLastLocation(context, onLocationFound)
            } else {
                launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    return LocationPermissionState(granted = isGrantedState, requestPermission = requestPermission)
}

private fun requestLastLocation(
    context: Context,
    onLocationFound: (Double, Double) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onLocationFound(location.latitude, location.longitude)
        }
    }
}
