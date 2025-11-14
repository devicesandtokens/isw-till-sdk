package com.interswitch.iswtillsdksample.data.repository

import com.example.isw_smart_till.interfaces.TillCommand

sealed interface TillCalls{

    data class MessageReceived(val message: String): TillCalls

    data class ErrorReceived(val errorMessage: String, val code: Int): TillCalls

    data class Connected(val device: String): TillCalls

    data class CommandReceived(val command: TillCommand): TillCalls

    object Disconnect: TillCalls

}