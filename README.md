# SDK README

Welcome to the TILL for POS Terminals Connectivity! This SDK provides developers with the tools necessary to seamlessly integrate POS terminals with various till systems using Bluetooth, USB, and TCP/IP connections. Below you will find essential information to get started with the SDK.

## Overview

The POS Terminals Connectivity SDK enables developers to establish communication between POS terminals and till systems. It offers a flexible and robust solution to connect devices using Bluetooth, USB, and TCP/IP protocols, ensuring reliable data transmission in various environments.

## Features

- **Multi-Protocol Support:** This SDK supports multiple communication protocols including Bluetooth, Wi-Fi (TCP/IP), and USB. USB now supports two separate modes: PAX UART (for PAX terminals) and generic Android USB (AOA) for Android-to-Android connections.

- **USB Mode Split:**
    - `USB` is now **auto-detecting**: PAX terminals keep using the original PAX UART path, while other devices use the new AOA transport.
    - `PAXUART` always uses the PAX UART path.
    - `ANDROID_USB` always uses Android Open Accessory (AOA) for Android-to-Android connections.

- **Seamless Integration:** Integrate POS terminals with till systems effortlessly, enabling smooth data exchange for transaction processing and other operations.

- **Seamless Integration:** Integrate POS terminals with till systems effortlessly, enabling smooth data exchange for transaction processing and other operations.

- **Robust Connectivity:** Ensure reliable and stable connections between POS terminals and till systems, minimizing downtime and enhancing overall system performance.

- **Flexible Configuration:** Customize connection settings and parameters to accommodate diverse hardware configurations and network environments.

- **Comprehensive Documentation:** Access detailed documentation covering API references, integration guides, and troubleshooting tips to streamline the development process.

## How to Integrate

Integrating the `smart-pos-till` SDK into your Android project is straightforward. Follow the steps below to get started.

### 1. Add Permissions and Features to your AndroidManifest.xml

Your app will need the following permissions to use the SDK. Add them to your `AndroidManifest.xml` file:

```xml
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />

<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />

<!-- Required for the foreground service used by the AOA transport. -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE" />

<!-- Optional: declare so your app can still be installed on devices without these features. -->
<uses-feature android:name="android.hardware.usb.host" android:required="false" />
<uses-feature android:name="android.hardware.usb.accessory" android:required="false" />
```

### 2. Initialize the SDK

The `IswTillManagerSDK` is the main entry point to the SDK. To get an instance, use the `createInstance` method, which follows the singleton pattern.

```kotlin
val tillManagerSDK = IswTillManagerSDK.createInstance(
    context = applicationContext,
    listener = myTillCallBackListener,
    initialMode = TillConnectionModes.BLUETOOTH // Or .WIFI, .USB, .ANDROID_USB, .PAXUART
)
```

You'll need to provide three parameters:
- `context`: The application context.
- `listener`: An implementation of the `TillCallBackListener` interface.
- `initialMode`: The desired connection mode. See the mode table below.

### Connection modes

| Mode | What it does |
| --- | --- |
| `TillConnectionModes.BLUETOOTH` | Bluetooth. |
| `TillConnectionModes.WIFI` | TCP over the local network. |
| `TillConnectionModes.USB` | **Auto.** On a PAX terminal this uses the PAX UART. On any other device it uses Android USB (AOA). This keeps older apps working on PAX terminals without changes. |
| `TillConnectionModes.ANDROID_USB` | Always Android USB (AOA), to another Android device over a cable. |
| `TillConnectionModes.PAXUART` | Always the PAX UART. PAX terminals only. |

## Android USB (AOA)

AOA connects two Android devices with a USB cable. Both devices run the SDK, and each works out
its own role from the hardware. There is no role to set.

- **Host.** The device on the USB-A or OTG end, usually the POS. It lists the attached devices,
  probes each one with AOA request 51, and switches the real peer into accessory mode.
- **Accessory.** The other device, usually the till. It waits until the host switches it, and then
  connects.

```kotlin
// Host side: list the attached devices and connect to the one the user picks.
val manager = tillManagerSDK.getTillManager()
if (manager.usesAndroidUsb()) {
    manager.getUsbDevices().forEach { device ->
        // device.name, device.likelyCandidate, device.inAccessoryMode
    }
    manager.connect(deviceId)
}
```

Notes:
- Both devices need a data-capable cable. The host needs a host-capable port or an OTG adapter.
  Check with `manager.isUsbHostSupported()` or `PackageManager.FEATURE_USB_HOST`.
- `UsbConfig(autoConnect = false)` makes the host wait for `connect(deviceId)` instead of
  connecting to the first AOA-capable device it finds. It has no effect on the accessory side.
- **Another app on the host can take over USB accessories.** Samsung phones, for example, may give
  the accessory to Smart Switch or SmartThings, and then this app never receives it. The SDK now
  includes an `AccessoryAttachActivity` to claim the accessory attach broadcast automatically, which
  prevents this in most cases. If the connection still stays on "connecting", clear that app's USB
  default in Settings, Apps.

### 3. Implement the TillCallBackListener

Create a class in your project that implements the `TillCallBackListener` interface to handle events from the SDK.

```kotlin
class MyTillCallBackListener : TillCallBackListener {
    override fun onMessageReceived(message: String) {
        // Handle incoming messages from the till
    }

    override fun onCommandReceived(command: TillCommand) {
        // Handle commands from the till
    }

    override fun onConnected(device: String) {
        // Called when a connection is established
    }

    override fun onDisConnected() {
        // Called when the connection is lost
    }

    override fun onHeartBeat(message: HeartBeatRequest) {
        // Handle heartbeat messages
    }

    override fun onStateChanged() {
        // Handle state changes
    }

    override fun onError(error: TillReturnCodes, message: String?) {
        // Handle errors - see TCP Error Codes section below for details
    }
}
```

### 4. Get the TillManager

Once the SDK is initialized, you can get the `TillManager` instance to interact with the till.

```kotlin
val tillManager = tillManagerSDK.getTillManager()

// Now you can use the tillManager to send commands, etc.
// For example, to initiate a connection (depending on the implementation of GenericTillManager)
// tillManager.connect() 
```
The `tillManager` object will allow you to send commands, disconnect, and manage the connection with the till.

### 5. Switching modes at runtime

You can switch modes after initialization. The SDK stops the previous mode's service before starting the new one.

```kotlin
tillManagerSDK.switchMode(TillConnectionModes.ANDROID_USB)
// or
tillManagerSDK.getTillManager().disconnect()
```

> [!NOTE]
> `USB` and `PAXUART` use a legacy `USBService` that does not fully stop on a mode switch. Switching away from PAX UART on a PAX terminal may leave that service running. If you need to switch frequently between PAX UART and AOA, use `PAXUART` and `ANDROID_USB` explicitly rather than `USB` auto-detect.

### 6. AOA configuration

For Android USB you can change the default configuration. By default, the host connects to the first AOA-capable device it finds (`autoConnect = true`). To let the user pick a device from a list, use:

```kotlin
import com.interswitch.smart_till.models.UsbConfig

tillManagerSDK.setUsbConfig(UsbConfig(autoConnect = false))
tillManagerSDK.initialize()
```

You can also pin `USB` to the PAX UART path if auto-detection misidentifies a terminal model:

```kotlin
tillManagerSDK.setUsbConfig(UsbConfig(forcePaxUart = true))
```

## Getting Started

To begin using the POS Terminals Connectivity SDK, follow these steps:

1. **Download the SDK:** Obtain the SDK package from the designated source, ensuring compatibility with your development environment and target platforms.

2. **Explore Documentation:** Review the provided documentation to familiarize yourself with the SDK's functionality, including API usage and integration guidelines.

3. **Set Up Development Environment:** Configure your development environment according to the SDK requirements, ensuring all dependencies are installed and configured correctly.

4. **Integrate SDK:** Integrate the SDK into your project following the integration guide provided in the documentation. Ensure proper initialization and configuration of connection parameters.

5. **Test Connectivity:** Test the connectivity between POS terminals and till systems using various connection methods (Bluetooth, USB, TCP/IP), verifying data exchange and system interoperability.

6. **Handle Errors and Exceptions:** Implement error handling mechanisms to address connection issues, timeouts, and other potential errors encountered during runtime. Refer to the **TCP Error Codes** section below for a comprehensive list of error codes and their meanings.

7. **Optimize Performance:** Fine-tune connection settings and optimize network configurations to enhance performance and reliability, ensuring smooth operation in production environments.

## TCP Error Codes

When using TCP/IP connections, the SDK provides standardized error codes to help you handle various error scenarios. These errors are delivered through the `onError` callback in your `TillCallBackListener` implementation.

### Error Code Ranges

| Range | Category | Description |
|-------|----------|-------------|
| 10-19 | Connection Errors | Issues related to establishing or maintaining connections |
| 20-29 | Communication Errors | Issues during data transmission |
| 90-99 | Unexpected Errors | Runtime exceptions and crashes |

### Connection Errors (10-19)

| Code | Error | Description |
|------|-------|-------------|
| 10 | `NoDeviceSelected` | No device IP address was provided for connection |
| 11 | `AlreadyConnected` | A connection already exists. Message includes the connected IP address (e.g., "Already connected to 192.168.1.100") |
| 12 | `CannotConnectToSelf` | Attempted to connect to the device's own IP address |
| 13 | `ConnectionFailed` | General connection failure. Message includes IP, port, and reason |
| 14 | `ConnectionRefused` | The target device actively refused the connection |
| 15 | `ConnectionTimeout` | Connection attempt timed out |
| 16 | `ConnectionLost` | An established connection was lost |

### Communication Errors (20-29)

| Code | Error | Description |
|------|-------|-------------|
| 20 | `NotConnected` | Attempted to send a message without an active connection |
| 21 | `SendFailed` | Failed to send a message. Check the error message for details |
| 22 | `ReadFailed` | Failed to read incoming data. Check the error message for details |

### Unexpected Errors (90-99)

| Code | Error | Description |
|------|-------|-------------|
| 90 | `SocketError` | Low-level socket error (e.g., connection reset) |
| 91 | `IOError` | I/O error during read or write operations |
| 99 | `UnexpectedError` | An unexpected exception occurred. Contains the original exception details |

### Example: Handling TCP Errors

```kotlin
class MyTillCallBackListener : TillCallBackListener {
    override fun onError(error: TillReturnCodes, message: String?) {
        // The message contains the error description
        // You can parse the error code from the broadcast if needed

        when {
            message?.contains("Already connected") == true -> {
                // Handle already connected scenario
                Log.d("Till", "Device is already connected: $message")
            }
            message?.contains("Connection refused") == true -> {
                // Handle connection refused
                Log.e("Till", "Connection refused - check if target device is listening")
            }
            message?.contains("Connection timed out") == true -> {
                // Handle timeout
                Log.e("Till", "Connection timed out - check network connectivity")
            }
            message?.contains("not connected") == true -> {
                // Handle not connected
                Log.e("Till", "Cannot send message - establish connection first")
            }
            else -> {
                // Handle other errors
                Log.e("Till", "Error: $message")
            }
        }
    }
}
```

## Support and Feedback

For any inquiries, assistance, or feedback regarding the POS Terminals Connectivity SDK, please contact our support team at [support@example.com](mailto:support@example.com). We welcome your suggestions and comments to improve the SDK and enhance the developer experience.

## License

The POS Terminals Connectivity SDK is licensed under [License Name]. Please review the license agreement included in the SDK package for usage terms and conditions.

## Version History

- **[NEXT]:**
    - Added Android Open Accessory (AOA) transport for Android-to-Android USB connections.
    - Split `USB` mode into auto-detecting `USB`, explicit `PAXUART`, and explicit `ANDROID_USB`.
    - Added `UsbConfig` for `autoConnect` and `forcePaxUart`.
    - Added `getUsbDevices()`, `isUsbHostSupported()`, and `usesAndroidUsb()` to the manager API.
    - Added `AccessoryAttachActivity` to claim the USB accessory attach broadcast automatically.
    - Added `stopService()` to `GenericTillManager` so switching modes stops the previous transport.

- **[Version Number]:** [Brief Description of Changes]
- **[Version Number]:** [Brief Description of Changes]

## Resources

- [SDK Documentation](link-to-documentation): Access comprehensive documentation covering API references, integration guides, and troubleshooting tips.
- [Download SDK](link-to-download): Obtain the latest version of the SDK package for your development needs.

Thank you for choosing the POS Terminals Connectivity SDK. We look forward to your successful integration and feedback.