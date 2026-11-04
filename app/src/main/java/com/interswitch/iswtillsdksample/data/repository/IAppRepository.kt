package com.interswitch.iswtillsdksample.data.repository

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import com.interswitch.smart_till.enums.TillConnectionModes
import com.interswitch.smart_till.interfaces.TillCommand
import com.interswitch.smart_till.models.UsbDeviceInfo
import kotlinx.coroutines.flow.StateFlow

interface IAppRepository {

    /** The selected connection mode. It survives app restarts. */
    val currentMode: StateFlow<TillConnectionModes>

    fun initializeTill(mode: TillConnectionModes)

    fun sendMessage(message: TillCommand)

    /** Disconnects the current mode, remembers the new one and starts it. */
    fun changeMode(mode: TillConnectionModes)

    fun disconnect()

    fun connect(ip: String)

    fun connect(device: BluetoothDevice)

    fun getTillLiveState(): LiveData<TillCalls>

    fun requestConnectionState()

    /** True if the current mode uses Android USB (AOA), so a USB device list applies. */
    fun usesAndroidUsb(): Boolean

    /** True if this device can be the USB host (OTG). */
    fun isUsbHostSupported(): Boolean

    /** The devices attached while this device is the USB host. */
    fun getUsbDevices(): List<UsbDeviceInfo>

    /** Connects to a device from [getUsbDevices]. */
    fun connectUsbDevice(id: String)
}
