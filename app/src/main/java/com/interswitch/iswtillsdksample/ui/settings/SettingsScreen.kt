package com.interswitch.iswtillsdksample.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.interswitch.smart_till.enums.TillConnectionModes
import com.interswitch.smart_till.models.UsbDeviceInfo
import kotlinx.coroutines.delay
import java.text.DateFormat
import java.util.Date

private data class ModeOption(
    val mode: TillConnectionModes,
    val title: String,
    val description: String
)

private val modeOptions = listOf(
    ModeOption(
        TillConnectionModes.USB,
        "USB (auto)",
        "On a PAX terminal this uses the PAX UART. On any other device it uses Android USB."
    ),
    ModeOption(
        TillConnectionModes.ANDROID_USB,
        "Android USB (AOA)",
        "Always connect to another Android device over a USB cable."
    ),
    ModeOption(
        TillConnectionModes.PAXUART,
        "PAX UART",
        "Always use the USB UART port of a PAX terminal."
    ),
    ModeOption(
        TillConnectionModes.WIFI,
        "Wi-Fi (TCP)",
        "Connect over the local network."
    ),
    ModeOption(
        TillConnectionModes.BLUETOOTH,
        "Bluetooth",
        "Connect to a paired Bluetooth device."
    )
)

@Composable
fun SettingsScreen(
    modifier: Modifier,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val state by settingsViewModel.uiState.collectAsState()

    // Android does not tell apps when a USB device is attached, so look again while this screen
    // is showing and the mode uses USB. The loop stops when the screen goes away.
    LaunchedEffect(state.mode) {
        if (state.isUsbMode) {
            while (true) {
                settingsViewModel.refreshUsbDevices()
                delay(USB_REFRESH_INTERVAL_MS)
            }
        }
    }

    Column(
        modifier = modifier
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Connection mode",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        modeOptions.forEach { option ->
            ModeRow(
                option = option,
                selected = option.mode == state.mode,
                onSelected = { settingsViewModel.selectMode(option.mode) }
            )
        }

        if (state.isConnected) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Connected to: ${state.connectedDevice}",
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(onClick = { settingsViewModel.disconnect() }) {
                        Text("Disconnect")
                    }
                }
            }
        }

        if (state.isUsbMode) {
            Spacer(modifier = Modifier.height(24.dp))
            UsbSection(
                state = state,
                onRefresh = { settingsViewModel.refreshUsbDevices() },
                onDeviceSelected = { settingsViewModel.connectUsbDevice(it) }
            )
        }

        state.statusMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ModeRow(
    option: ModeOption,
    selected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelected, role = Role.RadioButton)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // The row handles the click, so the button itself does not.
        RadioButton(selected = selected, onClick = null)
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(text = option.title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = option.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun UsbSection(
    state: SettingsUiState,
    onRefresh: () -> Unit,
    onDeviceSelected: (UsbDeviceInfo) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "USB devices",
            style = MaterialTheme.typography.titleMedium
        )
        if (state.usesAndroidUsb) {
            OutlinedButton(onClick = onRefresh) {
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                Text(text = "Refresh", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
    if (state.usesAndroidUsb && state.usbLastCheckedAt > 0L) {
        // Shows that the check ran, and what it found, even when the list is empty.
        val time = DateFormat.getTimeInstance().format(Date(state.usbLastCheckedAt))
        Text(
            text = "Last checked $time  -  ${state.usbDevices.size} device(s) attached, " +
                    "USB host ${if (state.usbHostSupported) "supported" else "not available"}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Spacer(modifier = Modifier.height(8.dp))

    if (!state.usesAndroidUsb) {
        // USB (auto) on a PAX terminal, or PAX UART.
        Text(
            text = "This device uses the PAX UART for USB, so there is nothing to select. " +
                    "It connects on its own.",
            style = MaterialTheme.typography.bodyMedium
        )
        return
    }

    if (!state.usbHostSupported) {
        Text(
            text = "This device cannot act as a USB host, so it is the accessory. " +
                    "Connect it to the POS with the cable and wait for the POS to connect to it.",
            style = MaterialTheme.typography.bodyMedium
        )
        return
    }

    val candidates = state.usbDevices.filter { it.likelyCandidate }
    val hidden = state.usbDevices.size - candidates.size

    if (candidates.isEmpty()) {
        Text(
            text = "No devices found. Connect the cable to the other device. " +
                    "If this device is the till, nothing needs to be selected here: " +
                    "the POS connects to it.",
            style = MaterialTheme.typography.bodyMedium
        )
    } else {
        Text(
            text = "Tap a device to connect to it.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        candidates.forEach { device ->
            UsbDeviceRow(device = device, onClick = { onDeviceSelected(device) })
        }
    }

    if (hidden > 0) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$hidden other USB device(s) hidden (hubs, storage, keyboards and similar).",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun UsbDeviceRow(
    device: UsbDeviceInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = device.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "%04X:%04X".format(device.vendorId, device.productId) +
                        if (device.inAccessoryMode) "  -  ready" else "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private const val USB_REFRESH_INTERVAL_MS = 2000L
