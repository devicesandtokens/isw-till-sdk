package com.interswitch.iswtillsdksample.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun DisabledLongTextField(text: String) {

    BasicTextField(
        value = TextFieldValue(text),
        onValueChange = { },
        modifier = Modifier
            .fillMaxSize()
            .border(BorderStroke(1.dp, Color.LightGray), RoundedCornerShape(8.dp))
            .shadow(2.dp, shape = RectangleShape) // Inner shadow
            .padding(8.dp)
            .verticalScroll(rememberScrollState()),
        enabled = false,
        decorationBox = { innerTextField ->
            Text(
                text = text,
                color = Color.Gray,
                modifier = Modifier
            )
        }
    )
}

@Composable
fun singleLineTextField(
    modifier: Modifier = Modifier,
    onButtonClicked: (String) -> Unit
){
    var text = remember { mutableStateOf("192.168.100.133") }

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        // TextField for input value
        TextField(
            value = text.value,
            onValueChange = { text.value = it },
            modifier = Modifier.weight(1f)
        )

        // Button as suffix
        Button(
            onClick = {
                println("testing")
                onButtonClicked.invoke(text.value)
                      },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(text = "Button")
        }
    }
}
