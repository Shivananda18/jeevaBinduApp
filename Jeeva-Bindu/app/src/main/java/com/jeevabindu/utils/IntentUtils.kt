package com.jeevabindu.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

object IntentUtils {
    fun dialPhone(context: Context, phone: String) {
        val normalized = phone.filter { it.isDigit() || it == '+' }
        val intent = Intent(Intent.ACTION_DIAL, "tel:$normalized".toUri())
        context.startActivity(intent)
    }

    fun openMaps(context: Context, lat: Double, lng: Double, label: String = "") {
        val query = if (lat != 0.0 && lng != 0.0) "$lat,$lng" else label
        val uri = Uri.parse("geo:0,0?q=${Uri.encode(query)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }
}
