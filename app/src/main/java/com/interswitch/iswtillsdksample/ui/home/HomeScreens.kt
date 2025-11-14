package com.example.myapplication.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.isw_smart_till.enums.TillConnectionModes
import com.interswitch.iswtillsdksample.data.models.MessageModalType
import com.interswitch.iswtillsdksample.ui.components.ButtonPill
import com.interswitch.iswtillsdksample.ui.components.DisabledLongTextField
import com.interswitch.iswtillsdksample.ui.components.MessageModalSheet
import com.interswitch.iswtillsdksample.ui.components.singleLineTextField
import com.interswitch.iswtillsdksample.ui.home.HomeViewModel
import kotlinx.coroutines.CoroutineScope


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier,
    snackbarHostState: (String) -> Unit,
    homeViewModel: HomeViewModel =  hiltViewModel()
) {

    val homeUIState by homeViewModel.uiState.collectAsState()

    val showSheet = remember { mutableStateOf(false) }
    val type = remember { mutableStateOf(MessageModalType.TRANSACTION) }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val uiState = homeViewModel.uiState.collectAsState()
    val snackBarEvent by homeViewModel.snackBarEvents.collectAsState()

    LaunchedEffect(false) {
        homeViewModel.initializeTill(TillConnectionModes.WIFI)
    }

    if (showSheet.value) {
        MessageModalSheet(type.value, showSheet = showSheet, onDismiss = {}) {

        }
    }

    Column(
        modifier = Modifier.padding(20.dp)
    ) {

        if (uiState.value.isConnected) {
            Text(
                text = "Connected to: ${uiState.value.connectedDevice}",
            )

            ButtonPill(text = "Disconnect") {
                homeViewModel.disconnect()
            }
        }


        Text(
            text = "Connecting IP",
        )

        singleLineTextField {
            println("I want to connect")
            homeViewModel.connect(it)
        }

        Text(
            text = "Incoming message",
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(8.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ){
            homeUIState?.let { DisabledLongTextField(text = it.receivedMessage) }
        }

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "Send a message",
        )

        FlowRow(
            modifier = Modifier.fillMaxSize(),

        ) {
            ButtonPill("Transaction Test Call") {
                homeViewModel.sendTestRequest()
//                type.value = MessageModalType.TRANSACTION
//                showSheet.value = true
            }

            ButtonPill("Transaction Test Status") {
//                type.value = MessageModalType.STATUS
//                showSheet.value = true
                homeViewModel.showSnackBar("test")
            }

            ButtonPill("Transaction Response") {
                type.value = MessageModalType.RESPONSE
                showSheet.value = true
            }
        }
    }

}