package com.abueltaweel.presentation.screen.SurahAyat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.abueltaweel.R
import com.abueltaweel.design_system.theme.Theme
import com.abueltaweel.presentation.components.AppBarAction
import com.abueltaweel.presentation.components.QuranAppBar

@Composable
fun SurahAppBarSection(
    surahName: String,
    onBack: () -> Unit,
    onSearch: () -> Unit
) {
    QuranAppBar(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .background(Theme.color.surfaces.surface),
        title = surahName,
        onBackClick = onBack,
        titleColor = Theme.color.primary.shadePrimary,
        actions = listOf(
            AppBarAction(
                icon = painterResource(R.drawable.ic_search),
                onClick = onSearch
            )
        )
    )
}