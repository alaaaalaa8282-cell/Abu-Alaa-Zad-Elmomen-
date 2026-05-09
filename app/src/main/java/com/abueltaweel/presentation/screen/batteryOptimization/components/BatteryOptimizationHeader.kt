package com.abueltaweel.presentation.screen.batteryOptimization.components

import androidx.compose.runtime.Composable
import com.abueltaweel.R
import com.abueltaweel.design_system.component.AppBar
import com.abueltaweel.presentation.base.localizedString
import com.abueltaweel.presentation.screen.batteryOptimization.BatteryOptimizationInteractionListener

@Composable
fun BatteryOptimizationHeader(listener: BatteryOptimizationInteractionListener) {
    AppBar(
        title = localizedString(R.string.battery_optimization),
        onBackClick = listener::onBackClicked
    )
}