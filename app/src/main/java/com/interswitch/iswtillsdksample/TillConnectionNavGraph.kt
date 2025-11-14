package com.interswitch.iswtillsdksample

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.home.HomeScreen
import com.interswitch.iswtillsdksample.ui.settings.SettingsScreen

@Composable
fun TillAppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    snackbarHostState: (String) -> Unit,
    startDestination: String = TillNavDestinations.HOME_ROUTE,
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            route = TillNavDestinations.HOME_ROUTE
        ) {
            HomeScreen(
                modifier = modifier,
                snackbarHostState = {
                    snackbarHostState.invoke(it)
                }
            )
        }

        composable(
           route = TillNavDestinations.SETTINGS_ROUTE
        ) {
            SettingsScreen(modifier = modifier)
        }
    }

}