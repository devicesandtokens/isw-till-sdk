package com.interswitch.iswtillsdksample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var expanded = remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Column(modifier = Modifier
                    .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Till Test Application",
                        textAlign = TextAlign.Center
                    )
                }
            },
            actions = {

                   IconButton(onClick = {
                       expanded.value = true
                   }) {
                       Icon(
                           imageVector = Icons.Filled.Settings,
                           contentDescription = "Localized description"
                       )
                   }
                   DropdownMenu(
                       expanded = expanded.value,
                       onDismissRequest = { expanded.value = false }
                   ) {
                       DropdownMenuItem(
                           text = { Text("Load") },
                           onClick = {  }
                       )
                       DropdownMenuItem(
                           text = { Text("Save") },
                           onClick = {  }
                       )
                   }
            },
            scrollBehavior = scrollBehavior,
        )
}