package com.example.myapplication.ui.events

sealed class SnackBarEvent {
    data class ShowSnackBar(val message: String) : SnackBarEvent()
}

