package com.abueltaweel.domain.model

data class BugReportRequest(
    val title: String,
    val description: String,
    val featureArea: String,
    val imageUrl: String? = null
)