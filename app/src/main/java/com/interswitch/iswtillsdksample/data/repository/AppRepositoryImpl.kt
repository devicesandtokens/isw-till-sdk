package com.interswitch.iswtillsdksample.data.repository

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.isw_smart_till.IswTillManagerSDK
import com.example.isw_smart_till.enums.TillConnectionModes
import com.example.isw_smart_till.enums.TillReturnCodes
import com.example.isw_smart_till.interfaces.TillCallBackListener
import com.example.isw_smart_till.interfaces.TillCommand
import com.example.isw_smart_till.models.requests.HeartBeatRequest
import com.interswitch.iswtillsdksample.TillConnector


class AppRepositoryImpl: IAppRepository {

    private val _tillLiveData = MutableLiveData<TillCalls>()

    override fun initializeTill() {
        TillConnector.startTillService()
    }

    override fun sendMessage(message: TillCommand) {
        TillConnector.sendMessage(message)
    }

    override fun changeMode(mode: TillConnectionModes) {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        TillConnector.disconnect()
    }

    override fun connect(ip: String) {
        TillConnector.connect(ip)
    }

    override fun connect(device: BluetoothDevice) {
        TODO("Not yet implemented")
    }

    override fun getTillLiveState(): LiveData<TillCalls> {
        return _tillLiveData
    }


}