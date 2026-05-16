package com.jeevabindu.presentation.screens

import android.app.DatePickerDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.jeevabindu.domain.model.BLOOD_GROUPS
import com.jeevabindu.domain.model.GENDERS
import com.jeevabindu.domain.model.KARNATAKA_DISTRICTS
import com.jeevabindu.presentation.components.AuthErrorText
import com.jeevabindu.presentation.components.AuthGradientBackground
import com.jeevabindu.presentation.components.AvailabilityToggle
import com.jeevabindu.presentation.components.BloodGroupChip
import com.jeevabindu.presentation.components.DonorCard
import com.jeevabindu.presentation.components.EmergencyAlertCard
import com.jeevabindu.presentation.components.EmptyState
import com.jeevabindu.presentation.components.JeevaScaffold
import com.jeevabindu.presentation.components.JeevaTextField
import com.jeevabindu.presentation.components.LoadingShimmerList
import com.jeevabindu.presentation.components.PrimaryActionButton
import com.jeevabindu.presentation.theme.GreenAvailable
import com.jeevabindu.presentation.theme.RedAlert
import com.jeevabindu.presentation.viewmodel.AdminViewModel
import com.jeevabindu.presentation.viewmodel.DonorSearchViewModel
import com.jeevabindu.presentation.viewmodel.EmergencyViewModel
import com.jeevabindu.presentation.viewmodel.HealthViewModel
import com.jeevabindu.presentation.viewmodel.HomeViewModel
import com.jeevabindu.presentation.viewmodel.NotificationViewModel
import com.jeevabindu.presentation.viewmodel.RegistrationViewModel
import com.jeevabindu.presentation.viewmodel.SettingsViewModel
import com.jeevabindu.utils.DateUtils
import com.jeevabindu.utils.IntentUtils
import java.util.Calendar

// ── Home ──────────────────────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    onNavigateEmergency: () -> Unit,
    onNavigateNotifications: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateDonorDetails: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.start() }

    JeevaScaffold(
        title = "Jeeva-Bindu",
        subtitle = state.currentDonor?.name?.let { "Hello, $it" },
        onSettings = onNavigateSettings,
        onNotifications = onNavigateNotifications,
        isRefreshing = state.refreshing,
        onRefresh = { viewModel.refresh() },
        floatingAction = {
            FloatingActionButton(
                onClick = onNavigateEmergency,
                containerColor = RedAlert
            ) { androidx.compose.material3.Icon(Icons.Default.Add, "Emergency", tint = androidx.compose.ui.graphics.Color.White) }
        }
    ) { mod ->
        if (state.loading) {
            LoadingShimmerList(modifier = mod)
            return@JeevaScaffold
        }
        LazyColumn(modifier = mod, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                state.currentDonor?.let { donor ->
                    AvailabilityToggle(
                        available = donor.availability,
                        onToggle = { viewModel.toggleAvailability() }
                    )
                }
            }
            item {
                Text("Blood group quick actions", fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BLOOD_GROUPS.forEach { group ->
                        BloodGroupChip(
                            label = group,
                            selected = false,
                            onClick = { }
                        )
                    }
                }
            }
            item { Text("Live emergencies", fontWeight = FontWeight.SemiBold) }
            if (state.alerts.isEmpty()) item { EmptyState("No active emergencies") }
            items(state.alerts.take(5)) { alert ->
                EmergencyAlertCard(alert = alert)
            }
            item { Text("Nearby donors", fontWeight = FontWeight.SemiBold) }
            if (state.nearbyDonors.isEmpty()) item { EmptyState("No nearby donors found") }
            items(state.nearbyDonors.take(8)) { donor ->
                DonorCard(
                    donor = donor,
                    onCall = { IntentUtils.dialPhone(context, donor.phone) },
                    onOpenMaps = { IntentUtils.openMaps(context, donor.lat, donor.lng, donor.address) },
                    onClick = { onNavigateDonorDetails(donor.uid) }
                )
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// ── Registration ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegistrationScreen(
    phone: String = "",
    onComplete: () -> Unit,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        viewModel.setImageUri(uri)
    }

    LaunchedEffect(phone) {
        if (phone.isNotBlank()) viewModel.bootstrap(phone)
        else viewModel.bootstrap()
    }

    AuthGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                if (state.isEditMode) "Edit profile" else "Donor registration",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            val imageModel = state.pendingImageUri ?: state.profileImageUrl.takeIf { it.isNotBlank() }
            if (imageModel != null) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = null,
                    modifier = Modifier.size(96.dp).clip(CircleShape).align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Crop
                )
            }
            OutlinedButton(onClick = { imagePicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                Text("Upload profile photo")
            }
            JeevaTextField(state.name, { viewModel.update("name", it) }, "Full name")
            JeevaTextField(state.age, { viewModel.update("age", it) }, "Age")
            DropdownField("Gender", GENDERS, state.gender) { viewModel.update("gender", it) }
            DropdownField("Blood group", BLOOD_GROUPS, state.bloodGroup) { viewModel.update("bloodGroup", it) }
            JeevaTextField(state.phone, {}, "Phone", enabled = false)
            JeevaTextField(state.address, { viewModel.update("address", it) }, "Address")
            DropdownField("District", KARNATAKA_DISTRICTS, state.district) { viewModel.update("district", it) }
            OutlinedButton(
                onClick = {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(context, { _, y, m, d ->
                        cal.set(y, m, d)
                        viewModel.setLastDonation(cal.timeInMillis)
                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Last donation: ${DateUtils.formatDate(state.lastDonationDateMillis)}")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Available to donate")
                Switch(state.availability, viewModel::setAvailability)
            }
            AuthErrorText(state.error)
            PrimaryActionButton(
                text = if (state.isEditMode) "Save changes" else "Complete registration",
                onClick = { viewModel.submit(onComplete) },
                isLoading = state.isLoading
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.ifBlank { "Select $label" },
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onSelect(option); expanded = false }
                )
            }
        }
    }
}

// ── Donor Search ──────────────────────────────────────────────────────────────

@Composable
fun DonorSearchScreen(
    onNavigateDonorDetails: (String) -> Unit,
    viewModel: DonorSearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.start() }

    JeevaScaffold(title = "Find donors", subtitle = "${state.donors.size} results") { mod ->
        Column(modifier = mod, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                (listOf("All") + BLOOD_GROUPS).forEach { g ->
                    BloodGroupChip(
                        label = g,
                        selected = state.bloodGroup == g,
                        onClick = { viewModel.setBloodGroup(g) }
                    )
                }
            }
            DropdownField("District", listOf("All") + KARNATAKA_DISTRICTS, state.district) {
                viewModel.setDistrict(it)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Available only", modifier = Modifier.weight(1f))
                Switch(state.availableOnly, viewModel::setAvailableOnly)
            }
            if (state.loading) LoadingShimmerList()
            else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.donors) { donor ->
                        DonorCard(
                            donor = donor,
                            onCall = { IntentUtils.dialPhone(context, donor.phone) },
                            onOpenMaps = { IntentUtils.openMaps(context, donor.lat, donor.lng, donor.address) },
                            onClick = { onNavigateDonorDetails(donor.uid) }
                        )
                    }
                }
            }
        }
    }
}

// ── Emergency ─────────────────────────────────────────────────────────────────

@Composable
fun EmergencyAlertScreen(
    onBack: () -> Unit,
    viewModel: EmergencyViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    AuthGradientBackground {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Emergency request", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            DropdownField("Blood group", BLOOD_GROUPS, state.bloodGroup) { viewModel.update("bloodGroup", it) }
            JeevaTextField(state.hospitalName, { viewModel.update("hospital", it) }, "Hospital / location name")
            JeevaTextField(state.patientCondition, { viewModel.update("condition", it) }, "Patient condition")
            JeevaTextField(state.unitsRequired, { viewModel.update("units", it) }, "Units required")
            JeevaTextField(state.contactNumber, { viewModel.update("contact", it) }, "Contact number")
            JeevaTextField(state.locationText, { viewModel.update("location", it) }, "Address details")
            AuthErrorText(state.error)
            PrimaryActionButton(
                text = "Post emergency",
                onClick = { viewModel.createAlert(onBack) },
                isLoading = state.isLoading
            )
            Text("Active feed", fontWeight = FontWeight.SemiBold)
            state.alerts.forEach { alert ->
                EmergencyAlertCard(
                    alert = alert,
                    onAccept = { viewModel.respond(alert.id, true) },
                    onReject = { viewModel.respond(alert.id, false) }
                )
            }
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
        }
    }
}

// ── Health ────────────────────────────────────────────────────────────────────

@Composable
fun HealthTrackerScreen(viewModel: HealthViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    JeevaScaffold(title = "Health tracker", subtitle = "90-day donation cooldown") { mod ->
        Column(modifier = mod.padding(8.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Last donation", style = MaterialTheme.typography.labelLarge)
            Text(state.lastDonationFormatted, style = MaterialTheme.typography.titleMedium)
            Text(
                if (state.eligible) "You are eligible to donate" else "Next eligible in",
                fontWeight = FontWeight.SemiBold,
                color = if (state.eligible) GreenAvailable else RedAlert
            )
            if (!state.eligible) {
                Text(state.countdown, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                val progress = 1f - (state.remainingMillis.toFloat() / (90 * 24 * 60 * 60 * 1000f)).coerceIn(0f, 1f)
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

// ── Profile ───────────────────────────────────────────────────────────────────

@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateAdmin: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.start() }
    val donor = state.currentDonor

    JeevaScaffold(title = "Profile", subtitle = donor?.bloodGroup, onSettings = onNavigateSettings) { mod ->
        Column(modifier = mod, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (donor != null) {
                if (donor.profileImageUrl.isNotBlank()) {
                    AsyncImage(
                        model = donor.profileImageUrl,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp).clip(CircleShape).align(Alignment.CenterHorizontally),
                        contentScale = ContentScale.Crop
                    )
                }
                Text(donor.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("${donor.age} yrs • ${donor.gender} • ${donor.district}")
                Text(donor.address, style = MaterialTheme.typography.bodyMedium)
                Text(donor.phone, style = MaterialTheme.typography.bodyMedium)
                PrimaryActionButton(text = "Edit profile", onClick = onEditProfile)
                OutlinedButton(onClick = onNavigateAdmin, modifier = Modifier.fillMaxWidth()) {
                    androidx.compose.material3.Icon(Icons.Default.AdminPanelSettings, null)
                    Spacer(Modifier.size(8.dp))
                    Text("Admin panel")
                }
            } else {
                EmptyState("Complete your donor profile")
                PrimaryActionButton(text = "Register now", onClick = onEditProfile)
            }
        }
    }
}

@Composable
fun DonorDetailsScreen(
    donorId: String,
    viewModel: com.jeevabindu.presentation.viewmodel.DonorDetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val donor by viewModel.donor.collectAsStateWithLifecycle()
    LaunchedEffect(donorId) { viewModel.load(donorId) }

    AuthGradientBackground {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Donor details", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            if (donor == null) {
                LoadingShimmerList(count = 2)
            } else {
                val d = donor!!
                Text(d.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text("${d.bloodGroup} • ${d.district}")
                Text(d.address)
                Text(d.phone)
                PrimaryActionButton(
                    text = "Call donor",
                    onClick = { IntentUtils.dialPhone(context, d.phone) }
                )
                OutlinedButton(
                    onClick = { IntentUtils.openMaps(context, d.lat, d.lng, d.address) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Open in Maps") }
            }
        }
    }
}

// ── Notifications ─────────────────────────────────────────────────────────────

@Composable
fun NotificationsScreen(viewModel: NotificationViewModel = hiltViewModel()) {
    val items by viewModel.notifications.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    JeevaScaffold(title = "Notifications", subtitle = "${items.count { !it.read }} unread") { mod ->
        if (loading) LoadingShimmerList(modifier = mod)
        else if (items.isEmpty()) EmptyState("No notifications yet", mod)
        else {
            LazyColumn(modifier = mod, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedButton(onClick = { viewModel.markAllRead() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Mark all read")
                    }
                }
                items(items) { n ->
                    androidx.compose.material3.Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .clickable { viewModel.markRead(n.id) }
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(n.title, fontWeight = FontWeight.SemiBold)
                            Text(n.body, style = MaterialTheme.typography.bodySmall)
                            if (!n.read) Text("New", color = RedAlert, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

// ── Admin ─────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminDashboardScreen(viewModel: AdminViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.load() }

    JeevaScaffold(title = "Admin", subtitle = if (state.isAdmin) "Dashboard" else "Access denied") { mod ->
        if (!state.isAdmin) {
            EmptyState("You are not an admin. Add your UID to Firestore 'admins' collection.", mod)
            return@JeevaScaffold
        }
        LazyColumn(modifier = mod, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Donors", state.stats.totalDonors.toString())
                    StatCard("Emergencies", state.stats.activeEmergencies.toString())
                    StatCard("Available", state.stats.availableDonors.toString())
                    StatCard("Suspended", state.stats.suspendedAccounts.toString())
                }
            }
            item { Text("Donors", fontWeight = FontWeight.Bold) }
            items(state.donors.take(20)) { donor ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text(donor.name, fontWeight = FontWeight.Medium)
                        Text("${donor.bloodGroup} • ${donor.phone}", style = MaterialTheme.typography.bodySmall)
                    }
                    OutlinedButton(onClick = { viewModel.suspendUser(donor.uid, !donor.isSuspended) }) {
                        Text(if (donor.isSuspended) "Restore" else "Suspend")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    androidx.compose.material3.Card(modifier = Modifier.padding(4.dp)) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = RedAlert)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

// ── Settings ──────────────────────────────────────────────────────────────────

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val darkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

    AuthGradientBackground {
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Dark mode")
                Switch(
                    checked = darkMode == true,
                    onCheckedChange = { viewModel.setDarkMode(it) }
                )
            }
            PrimaryActionButton(text = "Log out", onClick = onLogout)
        }
    }
}

@Composable
fun OnboardingScreen(onContinue: () -> Unit = {}) {
    AuthGradientBackground {
        Column(
            Modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Save lives together", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Connect donors with emergencies in real time.", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(24.dp))
            PrimaryActionButton(text = "Get started", onClick = onContinue)
        }
    }
}
