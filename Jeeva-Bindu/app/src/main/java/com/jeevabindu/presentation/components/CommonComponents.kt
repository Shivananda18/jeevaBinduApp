package com.jeevabindu.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.jeevabindu.domain.model.EmergencyAlert
import com.jeevabindu.domain.model.User
import com.jeevabindu.presentation.theme.GreenAvailable
import com.jeevabindu.presentation.theme.RedAlert

@Composable
fun ShimmerBox(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                )
            )
    )
}

@Composable
fun LoadingShimmerList(count: Int = 4, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(count) {
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(88.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JeevaTopBar(
    title: String,
    subtitle: String? = null,
    onSettings: (() -> Unit)? = null,
    onNotifications: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                if (subtitle != null) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        },
        actions = {
            if (onNotifications != null) {
                IconButton(onClick = onNotifications) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                }
            }
            if (onSettings != null) {
                IconButton(onClick = onSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
fun BloodGroupChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        modifier = modifier
    )
}

@Composable
fun DonorCard(
    donor: User,
    onCall: () -> Unit,
    onOpenMaps: () -> Unit,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (donor.profileImageUrl.isNotBlank()) {
                AsyncImage(
                    model = donor.profileImageUrl,
                    contentDescription = null,
                    modifier = Modifier.size(52.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = CircleShape,
                    color = RedAlert.copy(alpha = 0.15f)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp),
                        tint = RedAlert
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(donor.name, fontWeight = FontWeight.SemiBold)
                Text(
                    "${donor.bloodGroup} • ${donor.district}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    if (donor.availability) "Available" else "Unavailable",
                    color = if (donor.availability) GreenAvailable else Color.Gray,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            IconButton(onClick = onOpenMaps) {
                Icon(Icons.Default.LocationOn, contentDescription = "Maps", tint = RedAlert)
            }
            IconButton(onClick = onCall) {
                Icon(Icons.Default.Call, contentDescription = "Call", tint = GreenAvailable)
            }
        }
    }
}

@Composable
fun EmergencyAlertCard(
    alert: EmergencyAlert,
    onAccept: (() -> Unit)? = null,
    onReject: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = RedAlert.copy(alpha = 0.08f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    alert.bloodGroup,
                    fontWeight = FontWeight.Bold,
                    color = RedAlert,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    "${alert.acceptedCount} responding",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Text(alert.hospitalName, fontWeight = FontWeight.SemiBold)
            Text(
                alert.patientCondition,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "${alert.locationText} • ${alert.district}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            if (onAccept != null && onReject != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PrimaryActionButton(
                        text = "I'm Coming",
                        onClick = onAccept,
                        modifier = Modifier.weight(1f)
                    )
                    androidx.compose.material3.OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f)
                    ) { Text("Can't") }
                }
            }
        }
    }
}

@Composable
fun AvailabilityToggle(
    available: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Donor availability", fontWeight = FontWeight.SemiBold)
                Text(
                    if (available) "You can receive emergency alerts" else "You are marked unavailable",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Switch(checked = available, onCheckedChange = onToggle)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JeevaScaffold(
    title: String,
    subtitle: String? = null,
    onSettings: (() -> Unit)? = null,
    onNotifications: (() -> Unit)? = null,
    isRefreshing: Boolean = false,
    onRefresh: (() -> Unit)? = null,
    floatingAction: (@Composable () -> Unit)? = null,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        topBar = {
            JeevaTopBar(title, subtitle, onSettings, onNotifications)
        },
        floatingActionButton = { floatingAction?.invoke() }
    ) { padding ->
        val mod = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp)
        if (onRefresh != null) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = mod
            ) { content(Modifier.fillMaxSize()) }
        } else {
            content(mod)
        }
    }
}

@Composable
fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}
