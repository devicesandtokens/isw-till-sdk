package com.interswitch.iswtillsdksample.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.interswitch.iswtillsdksample.data.models.MessageModalType


@Composable
fun MessageModalSheet(
    type: MessageModalType,
    showSheet: MutableState<Boolean>,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
) {

    when(type) {
        MessageModalType.TRANSACTION -> {
            NewTransactionModalSheet(onSubmit = {
                onSubmit.invoke()
                showSheet.value = false

            }) {
                onDismiss.invoke()
                showSheet.value = false
            }
        }
        MessageModalType.RESPONSE -> {
            NewResponseModalSheet(onSubmit = { /*TODO*/ }) {
                showSheet.value = false
                onDismiss.invoke()
            }
        }
        MessageModalType.STATUS -> {
            NewStatusModalSheet(onSubmit = { /*TODO*/ }) {
                showSheet.value = false
                onDismiss.invoke()
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTransactionModalSheet(
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = { onDismiss.invoke() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {

        var amount by remember{mutableStateOf("1000")}
        var staffId by remember { mutableStateOf("Hello") }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)

            )

            TextField(
                value = staffId,
                onValueChange = { staffId = it },
                label = { Text("Staff ID") },
                singleLine = true,
            )

            Button(
                onClick = {
                    // Process form data

                    onSubmit.invoke()
                }
            ) {
                Text("Submit")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewStatusModalSheet(
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = { onDismiss.invoke() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {

    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewResponseModalSheet(
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = { onDismiss.invoke() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {

    }
}