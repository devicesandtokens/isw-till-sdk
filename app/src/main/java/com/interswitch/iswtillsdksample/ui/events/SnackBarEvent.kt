package com.interswitch.iswtillsdksample.ui.events

sealed class SnackBarEvent {
    data class ShowSnackBar(val message: String) : SnackBarEvent()
}
