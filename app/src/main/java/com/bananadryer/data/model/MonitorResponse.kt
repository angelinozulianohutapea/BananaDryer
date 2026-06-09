package com.bananadryer.data.model

data class MonitorResponse(
    val temperature: Float = 0f,
    val humidity: Float = 0f,
    val status: String = "IDLE"
)