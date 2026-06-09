package com.bananadryer.utils

import android.content.Context

class PrefsManager(context: Context) {
    private val prefs = context.getSharedPreferences("banana_prefs", Context.MODE_PRIVATE)

    var espIp: String
        get() = prefs.getString("esp_ip", "192.168.1.1") ?: "192.168.1.1"
        set(value) = prefs.edit().putString("esp_ip", value).apply()

    var lastTb1: Long
        get() = prefs.getLong("last_tb1", 5000L)
        set(value) = prefs.edit().putLong("last_tb1", value).apply()

    var lastTb2: Long
        get() = prefs.getLong("last_tb2", 30000L)
        set(value) = prefs.edit().putLong("last_tb2", value).apply()
}