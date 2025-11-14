package com.interswitch.iswtillsdksample.data.repository

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import com.example.isw_smart_till.enums.TillConnectionModes
import com.example.isw_smart_till.interfaces.TillCommand

interface IAppRepository {
    fun initializeTill()

    fun sendMessage(message: TillCommand)

    fun changeMode(mode: TillConnectionModes)

    fun disconnect()

    fun connect(ip: String)

    fun connect(device: BluetoothDevice)

    fun getTillLiveState(): LiveData<TillCalls>
}