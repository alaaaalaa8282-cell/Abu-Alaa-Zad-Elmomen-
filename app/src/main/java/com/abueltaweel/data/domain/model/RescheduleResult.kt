package com.abueltaweel.domain.model

sealed class RescheduleResult {
    object Success : RescheduleResult()
    object PermissionRequired : RescheduleResult()
}