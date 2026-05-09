package com.abueltaweel.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.abueltaweel.R
import com.abueltaweel.design_system.component.BottomNavigationBar
import com.abueltaweel.design_system.component.NavItem
import com.abueltaweel.design_system.theme.Theme
import com.abueltaweel.presentation.base.localizedString
import com.abueltaweel.presentation.screen.azkar.AzkarScreen
import com.abueltaweel.presentation.screen.dhikr.DhikrScreen
import com.abueltaweel.presentation.screen.home.HomeScreen
import com.abueltaweel.presentation.screen.prayers.FullPrayerTimesViewScreen
import com.abueltaweel.presentation.screen.radio.RadioScreen
import com.abueltaweel.presentation.screen.settings.SettingsScreen

@OptIn(kotlin.time.ExperimentalTime::class)
@Composable
fun MainContainer(
    rootNavController: NavHostController,
    modifier: Modifier = Modifier
) {
    val bottomNavController = rememberNavController()

    val navItems = listOf(
        Route.HomeScreen,
        Route.FullPrayerTimeView,
        Route.AzkarScreen,
        Route.DhikrScreen,
        Route.RadioScreen,
        Route.SettingsScreen
    )

    val bottomItems = listOf(
        NavItem(
            title = localizedString(R.string.home),
            selectedIcon = painterResource(R.drawable.ic_home_selected),
            unselectedIcon = painterResource(R.drawable.ic_home_not_selected)
        ),
        NavItem(
            title = localizedString(R.string.prayer),
            selectedIcon = painterResource(R.drawable.ic_prayer_times_selected),
            unselectedIcon = painterResource(R.drawable.ic_prayer_times_not_selected)
        ),
        NavItem(
            title = localizedString(R.string.azkar),
            selectedIcon = painterResource(R.drawable.ic_azkar_selected),
            unselectedIcon = painterResource(R.drawable.ic_azkar_not_selected)
        ),
        NavItem(
            title = "أذكاري",
            selectedIcon = painterResource(R.drawable.ic_tasbih),
            unselectedIcon = painterResource(R.drawable.ic_tasbih1)
        ),
        NavItem(
            title = localizedString(R.string.radio),
            selectedIcon = painterResource(R.drawable.ic_radio_selected),
            unselectedIcon = painterResource(R.drawable.ic_radio_not_selected)
        ),
        NavItem(
            title = localizedString(R.string.settings),
            selectedIcon = painterResource(R.drawable.ic_settings_selected),
            unselectedIcon = painterResource(R.drawable.ic_settings_not_selected)
        )
    )

    val currentBackStack by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route
    val selectedIndex = navItems.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        NavHost(
            navController = bottomNavController,
            startDestination = Route.HomeScreen.route,
            modifier = Modifier.fillMaxSize().padding(bottom = 74.dp)
        ) {
            composable(Route.HomeScreen.route)         { HomeScreen(rootNavController) }
            composable(Route.FullPrayerTimeView.route) { FullPrayerTimesViewScreen(rootNavController) }
            composable(Route.AzkarScreen.route)        { AzkarScreen(rootNavController) }
            composable(Route.DhikrScreen.route)        { DhikrScreen() }
            composable(Route.RadioScreen.route)        { RadioScreen(rootNavController) }
            composable(Route.SettingsScreen.route)     { SettingsScreen(rootNavController) }
        }

        BottomNavigationBar(
            items = bottomItems,
            selectedIndex = selectedIndex,
            onItemSelected = { index ->
                val route = navItems[index].route
                bottomNavController.navigate(route) {
                    popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
