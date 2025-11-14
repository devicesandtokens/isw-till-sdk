# ISW Till SDK Sample

This is a sample Android application demonstrating the integration of the Interswitch Till SDK.

## Getting Started

### Manifest

The following permissions and services must be added to your `AndroidManifest.xml` file:

```xml
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />

<application>
    <service
        android:name="com.example.isw_smart_till.service.tcp.TCPService"
        android:exported="false"
        tools:replace="android:exported"
        android:foregroundServiceType="dataSync|connectedDevice" />
</application>
```

### Gradle Setup

To include the Till SDK in your project, add the following to your app-level `build.gradle.kts` file:

```kotlin
dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
}
```

### Initialization

The Till SDK is initialized using the `IswTillManagerSDK.createInstance()` method. This should be done once, preferably in your Application class or a singleton object.

```kotlin
fun init(context: Context) {
    sdk = IswTillManagerSDK.createInstance(context, listener, TillConnectionModes.WIFI)
    sdk.getTillManager().startService()
    if (!isInitialized) {
        isInitialized = true
    }
}
```

### Starting the Service

After initialization, the Till service is started by calling `sdk.getTillManager().startService()`.

### The Listener

The `TillCallBackListener` is used to receive messages and events from the Till.

```kotlin
val listener = object : TillCallBackListener {
    override fun onMessageReceived(message: String) {
        // Handle message from the Till
    }

    override fun onCommandReceived(command: TillCommand) {
        // Handle command from the Till
    }

    override fun onConnected(device: String) {
        // Handle successful connection
    }

    override fun onDisConnected() {
        // Handle disconnection
    }

    override fun onHeartBeat(message: HeartBeatRequest) {
        // Handle heartbeat from the Till
    }

    override fun onStateChanged() {
        // Optional: handle state change
    }

    override fun onError(error: TillReturnCodes, message: String?) {
        // Handle errors
    }
}
```

### Available Methods

The following methods are available in the Till SDK:

*   `sendMessage(message: TillCommand)`: Sends a command to the Till.

    ```kotlin
    fun sendMessage(message: TillCommand) {
        sdk.getTillManager().sendCommand(message)
    }
    ```

*   `disconnect()`: Disconnects from the Till.

    ```kotlin
    fun disconnect() {
        sdk.getTillManager().disconnect()
    }
    ```

*   `connect(ip: String)`: Connects to the Till using its IP address.

    ```kotlin
    fun connect(ip: String) {
        sdk.getTillManager().connect(ip)
    }
    ```

*   `connect(device: BluetoothDevice)`: Connects to the Till using a Bluetooth device.

    ```kotlin
    fun connect(device: BluetoothDevice) {
        sdk.getTillManager().connect(device)
    }
    ```
