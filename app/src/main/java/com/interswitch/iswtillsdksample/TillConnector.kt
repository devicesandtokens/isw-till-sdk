package com.interswitch.iswtillsdksample

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.isw_smart_till.IswTillManagerSDK
import com.example.isw_smart_till.enums.TillConnectionModes
import com.example.isw_smart_till.enums.TillReturnCodes
import com.example.isw_smart_till.interfaces.TillCallBackListener
import com.example.isw_smart_till.interfaces.TillCommand
import com.example.isw_smart_till.models.requests.HeartBeatRequest
import com.interswitch.iswtillsdksample.data.repository.TillCalls

object TillConnector {
    private lateinit var sdk: IswTillManagerSDK
    private val _tillLiveData = MutableLiveData<TillCalls>()
    val tillLiveData: LiveData<TillCalls> get() = _tillLiveData

    private var isInitialized = false

    fun init(context: Context) {
        sdk = IswTillManagerSDK.createInstance(context, listener, TillConnectionModes.WIFI)
        sdk.getTillManager().startService()
        if (!isInitialized) {
            isInitialized = true
        }
    }


    val listener = object : TillCallBackListener {
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
            _tillLiveData.postValue(TillCalls.MessageReceived(message.message))
        }

        override fun onStateChanged() {
            // optional: handle state change
        }

        override fun onError(error: TillReturnCodes, message: String?) {
            _tillLiveData.postValue(TillCalls.ErrorReceived(message ?: "", error.ordinal))
        }
    }

    /**
     * Must be called from Activity / UI thread
     */
    fun startTillService() {
        // Initialize SDK mode
//        try {
//            sdk.getTillManager().startService()
//        } catch (e: Exception) {
//            _tillLiveData.postValue(TillCalls.ErrorReceived("Error starting SDK: ${e.message}", 0))
//        }
    }

    fun sendMessage(message: TillCommand) {
        sdk.getTillManager().sendCommand(message)
    }

    fun disconnect() {
        sdk.getTillManager().disconnect()
    }

    fun connect(ip: String) {
        sdk.getTillManager().connect(ip)
    }

    fun connect(device: BluetoothDevice) {
        sdk.getTillManager().connect(device)
    }
}