package com.jeevabindu.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jeevabindu.domain.model.AppNotification
import com.jeevabindu.domain.repository.DonorRepository
import com.jeevabindu.domain.repository.NotificationRepository
import com.jeevabindu.utils.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class JeevaBinduFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var notificationRepository: NotificationRepository
    @Inject lateinit var donorRepository: DonorRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"] ?: "Emergency alert"
        val body = message.notification?.body ?: message.data["body"] ?: "New blood request nearby"
        val userId = message.data["userId"] ?: FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val relatedId = message.data["relatedId"].orEmpty()

        NotificationHelper.showEmergencyNotification(this, title, body)

        if (userId.isNotBlank()) {
            scope.launch {
                notificationRepository.saveNotification(
                    AppNotification(
                        userId = userId,
                        title = title,
                        body = body,
                        type = message.data["type"] ?: "emergency",
                        relatedId = relatedId
                    )
                )
            }
        }
    }

    override fun onNewToken(token: String) {
        scope.launch { donorRepository.updateFcmToken(token) }
    }
}
