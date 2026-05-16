package com.jeevabindu.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SplashScreen() {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Jeeva-Bindu",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Rapid blood donor response",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun OnboardingScreen() {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Onboarding",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "3-page intro with animations",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun LoginScreen(
    onOtpSent: (String, String) -> Unit = { _, _ -> },
    onAutoVerified: () -> Unit = {}
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Phone OTP authentication",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun OtpScreen(
    verificationId: String = "",
    phone: String = "",
    onVerified: () -> Unit = {},
    onBack: () -> Unit = {}
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "OTP Verification",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Enter and verify OTP",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun RegistrationScreen(
    onComplete: () -> Unit = {}
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Donor Registration",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Complete donor profile",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun HomeScreen(
    onNavigateDonorDetails: () -> Unit = {},
    onNavigateNotifications: () -> Unit = {},
    onNavigateSettings: () -> Unit = {},
    onNavigateEmergency: () -> Unit = {}
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Home Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Alerts, availability, nearby donors",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun EmergencyAlertScreen(
    onBack: () -> Unit = {}
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Emergency Alert",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Create urgent blood request",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun DonorSearchScreen(
    onNavigateDonorDetails: () -> Unit = {}
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Donor Search",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Filter by group, location and status",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun DonorDetailsScreen(
    donorId: String = ""
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Donor Details",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Profile and contact details",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun HealthTrackerScreen() {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Health Tracker",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "90-day donation eligibility",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun NotificationsScreen() {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Notifications",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Real-time emergency updates",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ProfileScreen() {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Edit profile and availability",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun AdminDashboardScreen() {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Admin Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Stats and emergency monitoring",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun SettingsScreen() {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Theme and app preferences",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}