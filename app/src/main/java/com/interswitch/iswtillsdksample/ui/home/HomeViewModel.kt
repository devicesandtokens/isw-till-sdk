package com.interswitch.iswtillsdksample.ui.home


import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.isw_smart_till.enums.TillConnectionModes
import com.example.isw_smart_till.enums.TillPaymentOptions
import com.example.isw_smart_till.enums.TillStatus
import com.example.isw_smart_till.interfaces.TillCommand
import com.example.isw_smart_till.models.requests.TransactionRequest
import com.example.myapplication.data.MockData
import com.interswitch.iswtillsdksample.data.repository.IAppRepository
import com.interswitch.iswtillsdksample.data.repository.TillCalls
import com.example.myapplication.ui.events.SnackBarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val appRepository: IAppRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUIState>(HomeUIState(receivedMessage = MockData.receivedMessage))
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    private val _snackBarEvents = MutableStateFlow<SnackBarEvent?>(null)
    val snackBarEvents: StateFlow<SnackBarEvent?> = _snackBarEvents

    init {
        viewModelScope.launch {
            appRepository.getTillLiveState().asFlow().collect{
                updateUIState(it)
            }
        }
    }



    fun showSnackBar(message: String) {
        println("I got here to trigger")
        _snackBarEvents.value = SnackBarEvent.ShowSnackBar(message)
    }


    fun clearSnackbarEvent() {
        _snackBarEvents.value = null
    }
    fun initializeTill(mode: TillConnectionModes) = appRepository.initializeTill()

    fun connect(ip: String) {
        appRepository.connect(ip)
    }

    fun connect(device: BluetoothDevice) {

    }

    fun sendTestRequest(amount: Int = 1000) {
        val command = TillCommand.TransactionRequestCommand(TransactionRequest(amount= "1000", TillPaymentOptions.TRANSFER, accountType = "savings", command = "purchase"))
        appRepository.sendMessage(command)
    }

    fun sendTestStatus(status: TillStatus = TillStatus.PROCESSING) {
        val command = TillCommand.TillStatusCommand(status)
        appRepository.sendMessage(command)
    }

    fun disconnect() {
        appRepository.disconnect()
    }

    private fun updateUIState(newDataState: TillCalls) {
        // Update the _uiState
        _uiState.value = transformTillStateToUIState(newDataState)

    }

    private fun transformTillStateToUIState(dataState: TillCalls): HomeUIState {
        // Perform transformation logic to convert DataState to HomeUIState
        return when(dataState) {
            is TillCalls.Connected -> {
                showSnackBar("Device connected to ${dataState.device}")
                _uiState.value.copy(isConnected = true, connectedDevice = dataState.device)
            }
            is TillCalls.MessageReceived -> {
                println("Device got a message ${dataState.message}")
                _uiState.value.copy(receivedMessage = "${_uiState.value.receivedMessage } \n ${dataState.message}")
            }
            is TillCalls.Disconnect -> {
                println("Device got a disconnected")
                _uiState.value.copy(isConnected = false, connectedDevice = "")
            }
            is TillCalls.CommandReceived -> {
                println("Device got a command")
                _uiState.value.copy(receivedMessage = "${_uiState.value.receivedMessage } \n ${dataState.command}")
            }
            else -> {
                _uiState.value.copy()
            }
        }
    }



}