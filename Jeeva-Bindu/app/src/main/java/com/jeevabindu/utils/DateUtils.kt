package com.jeevabindu.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {
    private const val DONATION_GAP_DAYS = 90L
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun isEligible(lastDonationMillis: Long): Boolean {
        if (lastDonationMillis <= 0L) return true
        val days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastDonationMillis)
        return days >= DONATION_GAP_DAYS
    }

    fun nextEligibleDateMillis(lastDonationMillis: Long): Long =
        lastDonationMillis + TimeUnit.DAYS.toMillis(DONATION_GAP_DAYS)

    fun remainingMillis(lastDonationMillis: Long): Long {
        if (lastDonationMillis <= 0L) return 0L
        val next = nextEligibleDateMillis(lastDonationMillis)
        return (next - System.currentTimeMillis()).coerceAtLeast(0L)
    }

    fun formatCountdown(remainingMillis: Long): String {
        if (remainingMillis <= 0L) return "Eligible now"
        val days = TimeUnit.MILLISECONDS.toDays(remainingMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(remainingMillis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % 60
        return "${days}d ${hours}h ${minutes}m"
    }

    fun formatDate(millis: Long): String {
        if (millis <= 0L) return "Not donated yet"
        return dateFormat.format(Date(millis))
    }
}
