package com.abueltaweel.presentation.screen.radio.player

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentUrl: String? = null,
    val isError: Boolean = false,
    val isBuffering: Boolean = false
)