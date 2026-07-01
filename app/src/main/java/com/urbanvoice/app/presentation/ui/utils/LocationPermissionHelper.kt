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

fun decodePolyline(encoded: String): List<Pair<Double, Double>> {
    val poly = mutableListOf<Pair<Double, Double>>()
    var index = 0
    var lat = 0
    var lng = 0
    while (index < encoded.length) {
        var sum = 0
        var shift = 0
        var b: Int
        do {
            b = encoded[index++].code - 63
            sum = sum or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (sum and 1 != 0) (sum shr 1).inv() else sum shr 1
        lat += dlat
        shift = 0
        sum = 0
        do {
            b = encoded[index++].code - 63
            sum = sum or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (sum and 1 != 0) (sum shr 1).inv() else sum shr 1
        lng += dlng
        poly.add(Pair(lat / 1e5, lng / 1e5))
    }
    return poly
}

private fun requestLastLocation(
    context: Context,
    onLocationFound: (Double, Double) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                onLocationFound(location.latitude, location.longitude)
            }
        }
        .addOnFailureListener { exception ->
            android.util.Log.e("LocationPermissionHelper", "Error getting last location", exception)
        }
}
