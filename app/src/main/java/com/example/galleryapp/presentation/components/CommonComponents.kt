package com.example.galleryapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Error(text: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = text, fontSize = 25.sp)
    }
}

@Composable
fun Loading() {
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp)
            .height(4.dp),
    )
}

@Composable
fun MyAlertDialog(
    text: String,
    expandDeleteDialog: MutableState<Boolean>,
    onConfirmCLick: () -> Unit
) {
    if (expandDeleteDialog.value)
        AlertDialog(
            modifier = Modifier.wrapContentSize(),
            text = { Text(text = text) },
            title = { Text(text = "Alert") },
            dismissButton = { Button(onClick = { expandDeleteDialog.value = false }) {
                Text(text = "No")
            }
            },
            confirmButton = { Button(onClick = {
                onConfirmCLick()
                expandDeleteDialog.value = false
            }) {
                Text(text = "Yes")
            }
            },
            onDismissRequest = { expandDeleteDialog.value = false }
        )
}
