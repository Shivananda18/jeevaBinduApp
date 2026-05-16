package com.jeevabindu.presentation.nav

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppNavGraph() {

    var currentScreen by remember {
        mutableStateOf("splash")
    }

    when (currentScreen) {

        "splash" -> SplashScreen {
            currentScreen = "login"
        }

        "login" -> LoginScreen {
            currentScreen = "register"
        }

        "register" -> RegistrationScreen {
            currentScreen = "home"
        }

        "home" -> HomeScreen(
            onProfile = {
                currentScreen = "profile"
            },
            onSettings = {
                currentScreen = "settings"
            }
        )

        "profile" -> ProfileScreen {
            currentScreen = "home"
        }

        "settings" -> SettingsScreen {
            currentScreen = "home"
        }
    }
}

@Composable
fun SplashScreen(onContinue: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Jeeva-Bindu",
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Rapid blood donor response"
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onContinue
        ) {
            Text("Continue")
        }
    }
}

@Composable
fun LoginScreen(onLogin: () -> Unit) {

    var phoneNumber by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Login Screen",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "User authentication"
        )

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
            },
            label = {
                Text("Phone Number")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onLogin
        ) {
            Text("Login")
        }
    }
}

@Composable
fun RegistrationScreen(onRegister: () -> Unit) {

    var fullName by remember {
        mutableStateOf("")
    }

    var bloodGroup by remember {
        mutableStateOf("")
    }

    var city by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Registration",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Complete donor profile"
        )

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = {
                fullName = it
            },
            label = {
                Text("Full Name")
            }
        )

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = bloodGroup,
            onValueChange = {
                bloodGroup = it
            },
            label = {
                Text("Blood Group")
            }
        )

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = city,
            onValueChange = {
                city = it
            },
            label = {
                Text("City")
            }
        )

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = onRegister
        ) {
            Text("Register")
        }
    }
}

@Composable
fun HomeScreen(
    onProfile: () -> Unit,
    onSettings: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Welcome to Jeeva-Bindu",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Blood donor emergency response app"
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = onProfile) {
            Text("Profile")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = onSettings) {
            Text("Settings")
        }
    }
}

@Composable
fun ProfileScreen(onBack: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Profile Screen",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "User profile details"
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}

@Composable
fun SettingsScreen(onBack: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Settings Screen",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "App settings"
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}