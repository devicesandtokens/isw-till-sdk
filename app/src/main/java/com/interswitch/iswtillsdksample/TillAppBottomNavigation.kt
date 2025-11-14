package com.interswitch.iswtillsdksample

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun BottomNavigationBar(
    navigateToHome: () -> Unit,
    navigateToSettings: () -> Unit,
) {

    var selectedTabIndex = rememberSaveable { mutableStateOf(0) }


    NavigationBar {
        NavigationBarItem(
            selected = selectedTabIndex.value == 0,
            onClick = {
                selectedTabIndex.value = 0
                navigateToHome.invoke()
            },
            icon = {
                TabBarIconView(
                    isSelected = selectedTabIndex.value == 0,
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Filled.Home,
                    title = "Home"
                )
            },
            label = {Text("Home")}
        )

        NavigationBarItem(
            selected = selectedTabIndex.value == 1,
            onClick = {
                selectedTabIndex.value = 1
                navigateToSettings.invoke()
            },
            icon = {
                TabBarIconView(
                    isSelected = selectedTabIndex.value == 1,
                    selectedIcon = Icons.Filled.Settings,
                    unselectedIcon = Icons.Filled.Settings,
                    title = "Settings"
                )
            },
            label = {Text("Settings")}
        )

    }
}

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.M)
    @Composable
    fun TabBarIconView(
        isSelected: Boolean,
        selectedIcon: ImageVector,
        unselectedIcon: ImageVector,
        title: String
    ) {
        Box {
            Icon(
                imageVector = if (isSelected) {selectedIcon} else {unselectedIcon},
                contentDescription = title
            )
        }
    }

