package com.jeevabindu
import com.jeevabindu.presentation.nav.AppNavGraph
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.messaging.FirebaseMessaging
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.jeevabindu.presentation.nav.AppNavGraph
import com.jeevabindu.presentation.theme.JeevaBinduTheme
import com.jeevabindu.presentation.viewmodel.SettingsViewModel
import com.jeevabindu.utils.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createChannels(this)
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            // Token sync handled when donor profile loads via DonorRepository
        }
        setContent {
            JeevaBinduAppContent()
        }
    }
}

@Composable
private fun JeevaBinduAppContent() {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val darkPref by settingsViewModel.isDarkMode.collectAsStateWithLifecycle()
    val darkTheme = darkPref ?: androidx.compose.foundation.isSystemInDarkTheme()
    JeevaBinduTheme(darkTheme = darkTheme) {
        AppNavGraph()
    }
}
