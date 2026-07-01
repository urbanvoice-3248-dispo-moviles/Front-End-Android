package com.urbanvoice.app.presentation.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Términos y Condiciones") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Términos y Condiciones de uso",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Última actualización: Julio 2026",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "1. Aceptación de los términos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Al utilizar UrbanVoice, usted acepta los presentes términos y condiciones. " +
                            "Si no está de acuerdo con alguno de ellos, no debe utilizar la aplicación.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "2. Descripción del servicio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "UrbanVoice es una plataforma que permite a los usuarios reportar incidentes " +
                            "de seguridad ciudadana, visualizar zonas de riesgo en un mapa interactivo, " +
                            "y recibir alertas relacionadas con la seguridad en su distrito.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "3. Uso de datos de ubicación",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "La aplicación requiere acceso a su ubicación GPS para:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "- Capturar la ubicación actual al reportar un incidente.\n" +
                            "- Mostrar incidentes y zonas de riesgo cercanos.\n" +
                            "- Geolocalizar alertas en el mapa de riesgo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Su ubicación no es almacenada ni compartida sin su consentimiento explícito.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "4. Reportes y contenido generado por el usuario",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Los reportes que publique pueden incluir:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "- Descripción del incidente.\n" +
                            "- Tipo de incidente (robo, asalto, acoso, etc.).\n" +
                            "- Ubicación geográfica.\n" +
                            "- Fotografías (opcional).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Usted es responsable del contenido que publique. No debe publicar " +
                            "información falsa, difamatoria, o que vulnere la privacidad de terceros. " +
                            "UrbanVoice se reserva el derecho de eliminar contenido inapropiado.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "5. Reportes anónimos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Los reportes anónimos no muestran su nombre ni información personal " +
                            "asociada. Sin embargo, su identidad permanece registrada internamente " +
                            "para fines de moderación y prevención de abusos.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "6. Privacidad y protección de datos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Los datos personales recopilados incluyen: nombre, correo electrónico, " +
                            "número de teléfono y ubicación. Estos datos se utilizan únicamente para " +
                            "el funcionamiento de la aplicación y no se comparten con terceros sin " +
                            "su consentimiento, excepto cuando sea requerido por ley.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "7. Almacenamiento de imágenes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Las fotografías adjuntas a los reportes se almacenan de forma segura " +
                            "y pueden ser visibles para otros usuarios de la aplicación. No debe " +
                            "subir imágenes que contengan datos personales sensibles, menores de " +
                            "edad sin autorización, o contenido protegido por derechos de autor.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "8. Responsabilidad",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "UrbanVoice no se hace responsable por la veracidad de los reportes " +
                            "publicados por los usuarios. La información mostrada en el mapa de " +
                            "riesgo es referencial y no debe ser considerada como fuente oficial " +
                            "de seguridad. Siempre verifique la información con las autoridades " +
                            "competentes.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "9. Modificaciones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "UrbanVoice se reserva el derecho de modificar estos términos en " +
                            "cualquier momento. Los cambios serán notificados a través de la " +
                            "aplicación. El uso continuado de la aplicación después de dichas " +
                            "modificaciones constituye la aceptación de los nuevos términos.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "10. Contacto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Si tiene preguntas sobre estos términos, puede contactarnos a través " +
                            "del correo electrónico registrado en la aplicación.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Al aceptar, confirma que ha leído y comprendido estos términos y condiciones.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showConfirmDialog = true },
                        modifier = Modifier.weight(1f).height(52.dp)
                    ) {
                        Text("Rechazar")
                    }
                    Button(
                        onClick = {
                            onAccept()
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Aceptar")
                    }
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("¿Está seguro?") },
            text = {
                Text("Si rechaza los términos y condiciones no podrá acceder a la aplicación. " +
                        "Su cuenta será cerrada y deberá registrarse nuevamente si desea usar UrbanVoice en el futuro.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    onDecline()
                }) {
                    Text("Rechazar y salir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Volver")
                }
            }
        )
    }
}
