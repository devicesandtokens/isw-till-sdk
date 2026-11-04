package com.interswitch.iswtillsdksample.data.repository

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.interswitch.smart_till.IswTillManagerSDK
import com.interswitch.smart_till.enums.TillConnectionModes
import com.interswitch.smart_till.enums.TillReturnCodes
import com.interswitch.smart_till.interfaces.TillCallBackListener
import com.interswitch.smart_till.interfaces.TillCommand
import com.interswitch.smart_till.models.UsbConfig
import com.interswitch.smart_till.models.UsbDeviceInfo
import com.interswitch.smart_till.models.requests.HeartBeatRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class AppRepositoryImpl(
    private val tillManagerSDK: IswTillManagerSDK,
    context: Context
) : IAppRepository {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val _tillLiveData = MutableLiveData<TillCalls>()

    private val _currentMode = MutableStateFlow(loadMode())
    override val currentMode: StateFlow<TillConnectionModes> = _currentMode.asStateFlow()

    override fun initializeTill(mode: TillConnectionModes) {
        _currentMode.value = mode
        tillManagerSDK.setListener(object : TillCallBackListener {
            override fun onMessageReceived(message: String) {
                _tillLiveData.postValue(TillCalls.MessageReceived(message))
            }

            override fun onCommandReceived(command: TillCommand) {
                _tillLiveData.postValue(TillCalls.CommandReceived(command))
            }

            override fun onConnected(device: String) {
                _tillLiveData.postValue(TillCalls.Connected(device))
            }

            override fun onDisConnected() {
                _tillLiveData.postValue(TillCalls.Disconnect)
            }

            override fun onHeartBeat(message: HeartBeatRequest) {
                // Heartbeat is handled silently - just signals connection is alive
                _tillLiveData.postValue(TillCalls.HeartBeatReceived)
            }

            override fun onStateChanged() {
                TODO("Not yet implemented")
            }

            override fun onError(error: TillReturnCodes, message: String?) {
                _tillLiveData.postValue(TillCalls.ErrorReceived(message ?: "", error.ordinal))
            }
        })

        try {
            // Manual selection: the USB host waits for a device to be picked from the list in
            // Settings, instead of connecting to the first AOA capable device it finds.
            tillManagerSDK.setUsbConfig(UsbConfig(autoConnect = false))
            tillManagerSDK.setMode(mode)
            tillManagerSDK.initialize()
            tillManagerSDK.getTillManager().startService()
        } catch (e: Exception) {
            _tillLiveData.postValue(TillCalls.ErrorReceived("There was an error starting ${e.message}", 0))
        }
    }

    override fun sendMessage(message: TillCommand) {
        tillManagerSDK.getTillManager().sendCommand(message)
    }

    override fun changeMode(mode: TillConnectionModes) {
        if (mode == _currentMode.value) return
        try {
            // Stop whatever the old mode was doing before the new one starts. Without stopping
            // its service, for example the Wi-Fi one, it would keep answering broadcasts.
            val old = tillManagerSDK.getTillManager()
            old.disconnect()
            old.stopService()
        } catch (e: Exception) {
            // Nothing was started yet.
        }
        preferences.edit().putString(KEY_MODE, mode.name).apply()
        initializeTill(mode)
    }


    override fun disconnect() {
        tillManagerSDK.getTillManager().disconnect()
    }

    override fun connect(ip: String) {
        tillManagerSDK.getTillManager().connect(ip)
    }

    override fun connect(device: BluetoothDevice) {
        TODO("Not yet implemented")
    }

    override fun getTillLiveState(): LiveData<TillCalls> {
        return _tillLiveData
    }

    override fun requestConnectionState() {
        tillManagerSDK.getTillManager().requestConnectionState()
    }

    override fun usesAndroidUsb(): Boolean = try {
        tillManagerSDK.getTillManager().usesAndroidUsb()
    } catch (e: Exception) {
        false
    }

    override fun isUsbHostSupported(): Boolean = try {
        tillManagerSDK.getTillManager().isUsbHostSupported()
    } catch (e: Exception) {
        false
    }

    override fun getUsbDevices(): List<UsbDeviceInfo> = try {
        tillManagerSDK.getTillManager().getUsbDevices()
    } catch (e: Exception) {
        emptyList()
    }

    override fun connectUsbDevice(id: String) {
        tillManagerSDK.getTillManager().connect(id)
    }

    private fun loadMode(): TillConnectionModes {
        val saved = preferences.getString(KEY_MODE, null)
        return TillConnectionModes.values().firstOrNull { it.name == saved } ?: TillConnectionModes.WIFI
    }

    companion object {
        private const val PREFERENCES_NAME = "till_settings"
        private const val KEY_MODE = "connection_mode"
    }
}
