package com.interswitch.iswtillsdksample.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ButtonPill(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp), // Adjust the corner radius as needed
        colors = ButtonDefaults.buttonColors(),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(text = text)
    }
}