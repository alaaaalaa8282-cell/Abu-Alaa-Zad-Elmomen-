package com.abueltaweel.presentation.screen.ReportBug.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.abueltaweel.R
import com.abueltaweel.design_system.component.AppBar
import com.abueltaweel.presentation.base.localizedString

@Composable
fun ReportBugTopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier) {
    AppBar(
        modifier = modifier,
        title = localizedString(R.string.help_feedback),
        onBackClick =onBackClick
    )
}