package com.abueltaweel.presentation.screen.maps.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.abueltaweel.R
import com.abueltaweel.design_system.theme.Theme

@Composable
fun BoxScope.MapsFloatingButton(onClick: () -> Unit) {
    FloatingActionButton(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp),
        containerColor = Theme.color.primary.primary,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_detect_location),
            contentDescription = null,
            tint = Theme.color.primary.onPrimary
        )
    }
}