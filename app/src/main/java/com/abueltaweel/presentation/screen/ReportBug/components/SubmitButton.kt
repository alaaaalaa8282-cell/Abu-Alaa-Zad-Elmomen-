package com.abueltaweel.presentation.screen.ReportBug.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.abueltaweel.R
import com.abueltaweel.design_system.component.PrimaryButton
import com.abueltaweel.presentation.base.localizedString

@Composable
fun SubmitButton(
    loading: Boolean,
    onClick: () -> Unit
) {
    PrimaryButton(
        text = if (loading) localizedString(R.string.sending) else localizedString(R.string.submit_report),
        isEnabled = !loading,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    )
}