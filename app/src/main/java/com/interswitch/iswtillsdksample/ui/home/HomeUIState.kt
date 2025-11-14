package com.interswitch.iswtillsdksample.ui.home

import com.example.isw_smart_till.enums.TillConnectionModes

data class HomeUIState(
    val receivedMessage: String = "",
    val isConnected: Boolean = false,
    val connectedDevice: String = "",
    val connectionType: TillConnectionModes = TillConnectionModes.USB
)



