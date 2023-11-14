package com.example.galleryapp.presentation.features.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.galleryapp.R
import com.example.galleryapp.presentation.theme.LightGreen


@Composable
fun PasswordInput(
    label: String,
    password: MutableState<String>,
    passwordVisibility: MutableState<Boolean>,
    imeAction: ImeAction
) {
    val visualTransformation =
        if (passwordVisibility.value)
            VisualTransformation.None
        else
            PasswordVisualTransformation()

    TextField(
        label = { Text(text = label) },
        trailingIcon = { PasswordVisibilityIcon(passwordVisibility = passwordVisibility) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            disabledContainerColor = MaterialTheme.colorScheme.background,
        ),
        value = password.value,
        onValueChange = { newInput -> password.value = newInput },
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        )
    )
}

@Composable
fun PasswordVisibilityIcon(passwordVisibility: MutableState<Boolean>) {
    IconButton(
        onClick = { passwordVisibility.value = !passwordVisibility.value}
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id =
                                      if (passwordVisibility.value)
                                          R.drawable.hide_password
                                      else
                                          R.drawable.show_password
            ),
            contentDescription = null
        )

    }

}

@Composable
fun Switcher(selectedInd: Int, onOptionClick: (Int) -> Unit) {

    val texts = listOf("Login", "Register")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        repeat(2){ ind ->
            SwitchOption(text = texts[ind], ind = ind, selectedInd = selectedInd, onClick = onOptionClick)
        }
    }
}

@Composable
fun RowScope.SwitchOption(
    text: String,
    ind: Int,
    selectedInd: Int,
    onClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .weight(0.5f)
            .clipToBounds()
            .background(LightGreen)
            .clickable { onClick(ind) }
    ){
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(5.dp))
            Divider(
                modifier = Modifier,
                thickness = 5.dp,
                color = if (selectedInd == ind) Color.LightGray else LightGreen
            )
        }
    }
}