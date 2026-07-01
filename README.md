# UrbanVoice - Frontend Android

Este repositorio contiene la aplicación móvil Android de **UrbanVoice**, una solución orientada al reporte ciudadano de incidentes urbanos. La app permite a los usuarios registrar reportes, visualizar incidentes, consultar alertas y gestionar su perfil desde un dispositivo móvil.

## Descripción

UrbanVoice Android es el frontend móvil del sistema UrbanVoice. Está desarrollado en **Kotlin** y utiliza **Jetpack Compose** para construir una interfaz moderna, declarativa y adaptable.

La aplicación se conecta con el backend de UrbanVoice mediante servicios REST, consumiendo endpoints para perfiles, reportes, ubicaciones y alertas.

## Tecnologías utilizadas

* Kotlin
* Android SDK
* Jetpack Compose
* Material Design 3
* Navigation Compose
* Retrofit
* OkHttp
* Gson
* Hilt
* ViewModel
* Coroutines
* Gradle Kotlin DSL

## Arquitectura del frontend

El proyecto está organizado por capas para mantener una estructura limpia, escalable y fácil de mantener.

```text
app/
└── src/
    └── main/
        └── java/
            └── com/
                └── urbanvoice/
                    └── app/
                        ├── data/
                        ├── di/
                        ├── domain/
                        ├── presentation/
                        └── ui/
```

### Capas principales

#### data

Contiene la lógica relacionada con el acceso a datos externos. Aquí se encuentran los servicios remotos, DTOs, implementaciones de repositorios y configuración para el consumo de la API.

#### domain

Contiene los modelos principales del negocio y las interfaces de repositorio. Esta capa define las entidades que usa la aplicación sin depender directamente de Retrofit u otras tecnologías externas.

#### di

Contiene la configuración de inyección de dependencias mediante Hilt. Aquí se declaran los módulos que permiten inyectar servicios, repositorios y dependencias necesarias para la app.

#### presentation

Contiene las pantallas, componentes visuales y ViewModels. Esta capa se encarga de manejar el estado de la interfaz y conectar la vista con la lógica de negocio.

#### ui

Contiene elementos relacionados con el tema visual de la aplicación, como colores, tipografía y configuración de Material Design.

## Funcionalidades implementadas

* Pantallas de autenticación.
* Gestión de perfil de usuario.
* Registro de reportes ciudadanos.
* Visualización de reportes.
* Consulta de reportes por usuario.
* Consulta de reportes cercanos.
* Visualización de ubicaciones.
* Consulta de alertas.
* Navegación entre pantallas.
* Consumo de servicios REST mediante Retrofit.
* Manejo de estado con ViewModels.
* Inyección de dependencias con Hilt.

## Conexión con el backend

La aplicación consume los servicios REST del backend UrbanVoice.

Para pruebas en emulador Android, la URL base configurada es:

```text
http://10.0.2.2:8080/api/v1/
```

Esta dirección permite que el emulador Android se conecte al backend ejecutándose localmente en la computadora.

Repositorio del backend:

```text
https://github.com/urbanvoice-3248-dispo-moviles/Backend-UrbanVoice
```

## Endpoints consumidos

La aplicación Android consume servicios relacionados con:

* Perfiles de usuario.
* Reportes de incidentes.
* Ubicaciones.
* Alertas.
* Autenticación.

Estos servicios permiten que la app móvil se comunique con el backend para registrar, consultar y mostrar información al usuario.

## Flujo principal de la aplicación

1. El usuario ingresa a la aplicación.
2. La app permite acceder a las pantallas principales mediante navegación.
3. El usuario puede gestionar su perfil.
4. El usuario puede registrar un nuevo reporte ciudadano.
5. La aplicación envía el reporte al backend mediante Retrofit.
6. El usuario puede visualizar reportes existentes.
7. La app permite consultar ubicaciones, incidentes cercanos y alertas.

## Instalación y ejecución

### Requisitos previos

* Android Studio instalado.
* JDK configurado.
* Emulador Android o dispositivo físico.
* Backend UrbanVoice ejecutándose localmente o desplegado.
* Conexión a internet si se consume una API remota.

### Pasos para ejecutar el proyecto

1. Clonar el repositorio:

```bash
git clone https://github.com/urbanvoice-3248-dispo-moviles/Front-End-Android.git
```

2. Abrir el proyecto en Android Studio.

3. Esperar a que Gradle sincronice las dependencias.

4. Verificar que el backend esté ejecutándose.

5. Ejecutar la aplicación en un emulador o dispositivo Android.

## Estado actual del frontend

Actualmente el frontend Android cuenta con una estructura base funcional, organizada por capas y preparada para consumir los servicios principales del backend. Se han implementado pantallas, modelos, repositorios, ViewModels y configuración de red para conectar la aplicación con la API REST.

Entre los avances principales se encuentran:

* Estructura del proyecto en capas.
* Uso de Jetpack Compose para la interfaz.
* Configuración de Retrofit para consumo de API.
* Configuración de Hilt para inyección de dependencias.
* Implementación de ViewModels.
* Organización de pantallas por módulos.
* Integración inicial con servicios del backend.

## Mejoras futuras

* Agregar capturas de pantalla de las pantallas principales.
* Mejorar la documentación de cada módulo.
* Agregar validaciones más completas en formularios.
* Configurar ambientes para desarrollo y producción.
* Implementar manejo avanzado de errores.
* Agregar pruebas unitarias y de interfaz.
* Mejorar el diseño visual de las pantallas.
* Optimizar la experiencia de usuario.
* Documentar el flujo completo de navegación.

## Conclusión

El frontend Android de UrbanVoice representa la aplicación móvil del sistema, permitiendo a los usuarios interactuar con las funcionalidades principales del proyecto. Su estructura en capas, el uso de Jetpack Compose, Retrofit, Hilt y ViewModels permite que la aplicación sea escalable, mantenible y preparada para futuras mejoras.
