package com.interswitch.iswtillsdksample.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.interswitch.smart_till.enums.TillConnectionModes
import com.interswitch.smart_till.models.UsbDeviceInfo
import com.interswitch.iswtillsdksample.data.repository.IAppRepository
import com.interswitch.iswtillsdksample.data.repository.TillCalls
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val mode: TillConnectionModes,
    /** True if the current mode uses Android USB, so the device list applies. */
    val usesAndroidUsb: Boolean = false,
    val usbHostSupported: Boolean = false,
    val usbDevices: List<UsbDeviceInfo> = emptyList(),
    /** When the USB devices were last read, in epoch milliseconds. 0 if never. */
    val usbLastCheckedAt: Long = 0L,
    val isConnected: Boolean = false,
    val connectedDevice: String = "",
    val statusMessage: String? = null
) {
    val isUsbMode: Boolean
        get() = mode == TillConnectionModes.USB || mode == TillConnectionModes.ANDROID_USB
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appRepository: IAppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState(mode = appRepository.currentMode.value))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            var lastMode = appRepository.currentMode.value
            appRepository.currentMode.collect { mode ->
                if (mode != lastMode) {
                    // A new mode starts without a connection.
                    lastMode = mode
                    _uiState.value = _uiState.value.copy(
                        isConnected = false,
                        connectedDevice = "",
                        statusMessage = null
                    )
                }
                _uiState.value = _uiState.value.copy(mode = mode)
                refreshUsbDevices()
            }
        }
        viewModelScope.launch {
            appRepository.getTillLiveState().asFlow().collect { call ->
                when (call) {
                    is TillCalls.Connected -> _uiState.value = _uiState.value.copy(
                        isConnected = true,
                        connectedDevice = call.device,
                        statusMessage = null
                    )
                    is TillCalls.Disconnect -> _uiState.value = _uiState.value.copy(
                        isConnected = false,
                        connectedDevice = ""
                    )
                    is TillCalls.ErrorReceived -> _uiState.value = _uiState.value.copy(
                        statusMessage = call.errorMessage
                    )
                    else -> Unit
                }
            }
        }
    }

    fun selectMode(mode: TillConnectionModes) {
        appRepository.changeMode(mode)
    }

    /**
     * Reads the USB devices again. Android does not tell apps when a USB device is attached, so
     * the screen calls this regularly while it is showing.
     */
    fun refreshUsbDevices() {
        val usesUsb = appRepository.usesAndroidUsb()
        _uiState.value = _uiState.value.copy(
            usesAndroidUsb = usesUsb,
            usbHostSupported = usesUsb && appRepository.isUsbHostSupported(),
            usbDevices = if (usesUsb) appRepository.getUsbDevices() else emptyList(),
            usbLastCheckedAt = System.currentTimeMillis()
        )
    }

    fun connectUsbDevice(device: UsbDeviceInfo) {
        _uiState.value = _uiState.value.copy(statusMessage = "Connecting to ${device.name}...")
        appRepository.connectUsbDevice(device.id)
    }

    fun disconnect() {
        appRepository.disconnect()
    }
}
