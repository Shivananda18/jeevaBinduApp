package com.jeevabindu.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jeevabindu.presentation.nav.Routes

private sealed class MainTab(val route: String, val label: String) {
    data object Home : MainTab("main_home", "Home")
    data object Search : MainTab("main_search", "Search")
    data object Health : MainTab("main_health", "Health")
    data object Profile : MainTab("main_profile", "Profile")
}

@Composable
fun MainScreen(
    onNavigateEmergency: () -> Unit,
    onNavigateNotifications: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateAdmin: () -> Unit,
    onNavigateDonorDetails: (String) -> Unit,
    onNavigateRegister: () -> Unit
) {
    val navController = rememberNavController()
    val tabs = listOf(MainTab.Home, MainTab.Search, MainTab.Health, MainTab.Profile)
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                when (tab) {
                                    MainTab.Home -> Icons.Default.Home
                                    MainTab.Search -> Icons.Default.Search
                                    MainTab.Health -> Icons.Default.Favorite
                                    MainTab.Profile -> Icons.Default.Person
                                },
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = MainTab.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(MainTab.Home.route) {
                HomeScreen(
                    onNavigateEmergency = onNavigateEmergency,
                    onNavigateNotifications = onNavigateNotifications,
                    onNavigateSettings = onNavigateSettings,
                    onNavigateDonorDetails = onNavigateDonorDetails
                )
            }
            composable(MainTab.Search.route) {
                DonorSearchScreen(onNavigateDonorDetails = onNavigateDonorDetails)
            }
            composable(MainTab.Health.route) { HealthTrackerScreen() }
            composable(MainTab.Profile.route) {
                ProfileScreen(
                    onEditProfile = onNavigateRegister,
                    onNavigateSettings = onNavigateSettings,
                    onNavigateAdmin = onNavigateAdmin
                )
            }
        }
    }
}
