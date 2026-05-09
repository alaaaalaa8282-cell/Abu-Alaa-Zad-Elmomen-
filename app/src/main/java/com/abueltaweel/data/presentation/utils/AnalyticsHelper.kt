package com.abueltaweel.presentation.utils

class AnalyticsHelper {

    fun logEvent(
        name: String,
        params: Map<String, String> = emptyMap()
    ) {
        // Analytics removed
    }

    fun logScreen(screenName: String) {
        logEvent(
            name = "screen_view",
            params = mapOf("screen_name" to screenName)
        )
    }
}
