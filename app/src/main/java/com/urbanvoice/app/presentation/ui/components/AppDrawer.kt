package com.urbanvoice.app.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.urbanvoice.app.domain.model.UserProfile
import com.urbanvoice.app.presentation.theme.PrimaryColor

@Composable
fun AppDrawer(
    profile: UserProfile?,
    onMapaDeRiesgo: () -> Unit,
    onReportarIncidente: () -> Unit,
    onMisReportes: () -> Unit,
    onAlertas: () -> Unit,
    onMiPerfil: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val initials = profile?.let {
        listOf(it.name, it.lastName)
            .mapNotNull { namePart -> namePart.firstOrNull()?.uppercaseChar() }
            .joinToString("")
            .ifBlank { "?" }
    } ?: "?"

    ModalDrawerSheet {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = PrimaryColor
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = initials,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = profile?.let { "${it.name} ${it.lastName}" } ?: "Invitado",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = profile?.email ?: "Sin sesión",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        HorizontalDivider()
        DrawerItem(Icons.Default.Map, "Mapa de Riesgo", onMapaDeRiesgo)
        DrawerItem(Icons.Default.AddCircle, "Reportar Incidente", onReportarIncidente, Color.Red)
        DrawerItem(Icons.Default.ListAlt, "Mis Reportes", onMisReportes)
        DrawerItem(Icons.Default.Notifications, "Alertas", onAlertas)
        HorizontalDivider()
        DrawerItem(Icons.Default.Person, "Mi Perfil", onMiPerfil)
        if (profile != null) {
            DrawerItem(Icons.Default.Logout, "Cerrar Sesión", onCerrarSesion, Color.Red)
        }
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    NavDrawerItem(
        icon = { Icon(icon, contentDescription = label, tint = tint) },
        label = { Text(label, color = tint) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}
