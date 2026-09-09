package org.sightguide.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.sightguide.SightGuideApplication
import org.sightguide.feature.camera.ui.CameraScreen
import org.sightguide.feature.camera.viewmodel.CameraViewModel
import org.sightguide.feature.caregiver.ui.CaregiverScreen
import org.sightguide.feature.caregiver.viewmodel.CaregiverViewModel
import org.sightguide.feature.dashboard.ui.DashboardScreen
import org.sightguide.feature.dashboard.viewmodel.DashboardViewModel
import org.sightguide.feature.navigation.ui.NavigationScreen
import org.sightguide.feature.navigation.viewmodel.NavigationViewModel
import org.sightguide.feature.nearby.ui.NearbyScreen
import org.sightguide.feature.nearby.viewmodel.NearbyViewModel
import org.sightguide.feature.reader.ui.ReaderScreen
import org.sightguide.feature.reader.viewmodel.ReaderViewModel
import org.sightguide.feature.safety.ui.SafetyScreen
import org.sightguide.feature.safety.viewmodel.SafetyViewModel
import org.sightguide.feature.settings.ui.SettingsScreen
import org.sightguide.feature.settings.viewmodel.SettingsViewModel

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object Navigation : Screen("navigation")
    data object Camera : Screen("camera")
    data object Reader : Screen("reader")
    data object Nearby : Screen("nearby")
    data object Safety : Screen("safety")
    data object Caregiver : Screen("caregiver")
    data object Settings : Screen("settings")
}

@Composable
fun SightGuideNavGraph(
    app: SightGuideApplication,
    navController: NavHostController,
    onOpenVoiceTrigger: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dashboardVm = remember {
        DashboardViewModel(
            locationClient = app.locationClient,
            userSettingsDataStore = app.userSettingsDataStore,
            ttsManager = app.ttsManager,
            hapticManager = app.hapticManager
        )
    }

    val navigationVm = remember {
        NavigationViewModel(
            locationClient = app.locationClient,
            compassSensorManager = app.compassSensorManager,
            ttsManager = app.ttsManager,
            hapticManager = app.hapticManager,
            earconPlayer = app.earconPlayer
        )
    }

    val cameraVm = remember {
        CameraViewModel(
            ttsManager = app.ttsManager,
            hapticManager = app.hapticManager,
            earconPlayer = app.earconPlayer
        )
    }

    val readerVm = remember {
        ReaderViewModel(
            ttsManager = app.ttsManager,
            hapticManager = app.hapticManager,
            earconPlayer = app.earconPlayer
        )
    }

    val nearbyVm = remember {
        NearbyViewModel(
            repository = app.nearbyPlacesRepository,
            locationClient = app.locationClient,
            compassSensorManager = app.compassSensorManager,
            ttsManager = app.ttsManager,
            hapticManager = app.hapticManager
        )
    }

    val safetyVm = remember {
        SafetyViewModel(
            contactDao = app.database.emergencyContactDao(),
            emergencyDispatcher = app.emergencyDispatcher,
            locationClient = app.locationClient,
            userSettingsDataStore = app.userSettingsDataStore,
            ttsManager = app.ttsManager,
            hapticManager = app.hapticManager
        )
    }

    val caregiverVm = remember {
        CaregiverViewModel(
            userSettingsDataStore = app.userSettingsDataStore,
            auditTrailDao = app.database.auditTrailDao(),
            ttsManager = app.ttsManager,
            hapticManager = app.hapticManager
        )
    }

    val settingsVm = remember {
        SettingsViewModel(
            userSettingsDataStore = app.userSettingsDataStore,
            ttsManager = app.ttsManager,
            hapticManager = app.hapticManager,
            earconPlayer = app.earconPlayer
        )
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = dashboardVm,
                onNavigateClick = { navController.navigate(Screen.Navigation.route) },
                onCameraClick = { navController.navigate(Screen.Camera.route) },
                onReadClick = { navController.navigate(Screen.Reader.route) },
                onNearbyClick = { navController.navigate(Screen.Nearby.route) },
                onSafetyClick = { navController.navigate(Screen.Safety.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Navigation.route) {
            NavigationScreen(viewModel = navigationVm)
        }

        composable(Screen.Camera.route) {
            CameraScreen(viewModel = cameraVm)
        }

        composable(Screen.Reader.route) {
            ReaderScreen(viewModel = readerVm)
        }

        composable(Screen.Nearby.route) {
            NearbyScreen(
                viewModel = nearbyVm,
                onNavigateToPlace = { place ->
                    navigationVm.startNavigation(place.name, place.coordinates)
                    navController.navigate(Screen.Navigation.route)
                }
            )
        }

        composable(Screen.Safety.route) {
            SafetyScreen(viewModel = safetyVm)
        }

        composable(Screen.Caregiver.route) {
            CaregiverScreen(viewModel = caregiverVm)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(viewModel = settingsVm)
        }
    }
}
