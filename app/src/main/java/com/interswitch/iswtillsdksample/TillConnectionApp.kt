package com.interswitch.iswtillsdksample

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.events.SnackBarEvent
import com.interswitch.iswtillsdksample.ui.home.HomeViewModel
import com.interswitch.iswtillsdksample.ui.theme.ISWTillSDKSampleTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun TillTestApp() {
    ISWTillSDKSampleTheme {
        val navController = rememberNavController()
        val navigationActions = remember(navController) {
            TillConnectionNavActions(navController)
        }
        val homeViewModel: HomeViewModel = hiltViewModel()
        val snackBarEvent by homeViewModel.snackBarEvents.collectAsState()
        val snackBarHost = remember { SnackbarHostState() }
        val coroutineScope: CoroutineScope = rememberCoroutineScope()

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = { TopAppBar() },
                snackbarHost = {
                    SnackbarHost(hostState = snackBarHost)
                },
                bottomBar = {
                    BottomNavigationBar(
                        navigateToHome = navigationActions.navigateToHome,
                        navigateToSettings = navigationActions.navigateToSettings
                    )
                },
                content = { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        LaunchedEffect(snackBarEvent) {
                            snackBarEvent?.let { event ->
                                println("event show ooo")

                            }
                        }

                        DisposableEffect(snackBarEvent) {

                            snackBarEvent?.let {
                                println("An event")
                                homeViewModel.clearSnackbarEvent()
                            }

                            onDispose {  }
                        }
                        TillAppNavGraph(
                            navController = navController,
                            snackbarHostState = {
                                coroutineScope.launch {
                                    snackBarHost.showSnackbar("Test")
                                }
                            }
                        )

                        coroutineScope.launch {
                            println("scope launched")
                            snackBarEvent?.let { event ->
                                println("Triggered")
                                when (event) {
                                    is SnackBarEvent.ShowSnackBar -> {
                                        snackBarHost.showSnackbar(event.message)
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
        
    }
}