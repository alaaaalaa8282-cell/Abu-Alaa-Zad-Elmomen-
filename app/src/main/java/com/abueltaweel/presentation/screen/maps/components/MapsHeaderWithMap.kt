package com.abueltaweel.presentation.screen.maps.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abueltaweel.R
import com.abueltaweel.design_system.component.AppBar
import com.abueltaweel.presentation.base.localizedString
import com.abueltaweel.presentation.screen.maps.MapsUiState
import org.maplibre.compose.camera.CameraState

@Composable fun MapsHeaderWithMap(
    cameraState: CameraState,
    onBack: () -> Unit,
    onMapClick: (Double, Double) -> Unit,
    onMapStateChanged: (MapsUiState.MapLoadState) -> Unit,
    modifier: Modifier= Modifier
) {
    Column (modifier = modifier){
        AppBar(
            modifier = Modifier.padding(horizontal = 16.dp),
            title = localizedString(R.string.change_location),
            onBackClick = onBack
        )

        Map(
            modifier = Modifier
                .fillMaxSize(),
            cameraState = cameraState,
            onMapClick = { onMapClick(it.latitude, it.longitude) },
            onMapStateChanged ={onMapStateChanged(it)}
        )
    }
}