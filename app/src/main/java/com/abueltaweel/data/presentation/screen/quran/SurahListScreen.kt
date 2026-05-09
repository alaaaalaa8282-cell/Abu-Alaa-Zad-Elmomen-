package com.abueltaweel.presentation.screen.quran

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.abueltaweel.R
import com.abueltaweel.design_system.theme.Theme
import com.abueltaweel.presentation.base.localizedString
import com.abueltaweel.presentation.components.AppBarAction
import com.abueltaweel.presentation.components.LoadingContainer
import com.abueltaweel.presentation.components.QuranAppBar
import com.abueltaweel.presentation.navigation.Route
import com.abueltaweel.presentation.navigation.Route.SearchAyahScreen
import com.abueltaweel.presentation.navigation.Route.SurahAyatScreen
import com.abueltaweel.presentation.screen.SearchAyah.SearchType
import com.abueltaweel.presentation.screen.quran.components.SurahGrid
import com.abueltaweel.presentation.utils.CollectEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun SurahListScreen(
    navController: NavController,
    viewModel: SurahListViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is SurahListEffect.NavigateToSurahAyat ->
                navController.navigate(
                    SurahAyatScreen(
                        effect.surahId,
                        effect.arabicName,
                        effect.englishName
                    )
                )

            SurahListEffect.NavigateToQuranSearch -> {
                navController.navigate(
                    SearchAyahScreen(
                        type = SearchType.QURAN
                    )
                )
            }

            SurahListEffect.NavigateToBookmarksList -> {
                navController.navigate(Route.BookmarksListScreen)
            }
        }
    }

    SurahListContent(
        state = state,
        listener = viewModel,
        onBack = navController::popBackStack
    )
}

@Composable
private fun SurahListContent(
    state: SurahListUiState,
    listener: SurahListInteractionListener,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QuranAppBar(
            onBackClick = onBack,
            titleColor = Theme.color.primary.shadePrimary,
            title = localizedString(R.string.quran), actions = listOf(
                AppBarAction(
                    icon = painterResource(R.drawable.ic_search),
                    onClick = {
                        listener.onSearchClick()
                    },
                ),
                AppBarAction(
                    icon = painterResource(R.drawable.ic_all_bookmark),
                    onClick = { listener.onBookmarksClick() },
                )
            )
        )
        AnimatedContent(
            targetState = state.isLoading,
            transitionSpec = {

                fadeIn(animationSpec = tween(400)) + scaleIn(initialScale = 0.92f) togetherWith
                        fadeOut(animationSpec = tween(300))
            },
            label = "LoadingToContentAnimation"
        ) { isLoading ->
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingContainer()
                }
            } else {
                SurahGrid(
                    sur = state.surahList,
                    onSurahClick = listener::onSurahClick
                )
            }
        }
    }
}