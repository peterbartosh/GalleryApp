package com.example.galleryapp.presentation.features.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.galleryapp.data.utils.UiState
import com.example.galleryapp.data.utils.showToast
import com.example.galleryapp.presentation.components.Loading

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    onAuthorisationDone: () -> Unit
) {

    val context = LocalContext.current

    var selectedOptionInd by remember {
        mutableStateOf(0)
    }

    var login by remember { // add all limitations and requirements
        mutableStateOf("")
    }

    val password = remember {
        mutableStateOf("")
    }

    val passwordConfirm = remember {
        mutableStateOf("")
    }

    val passwordVisibility = remember {
        mutableStateOf(false)
    }

    val passwordVisibilityConfirm = remember {
        mutableStateOf(false)
    }

    var processing by remember {
        mutableStateOf(false)
    }


    val valid by remember(login, password.value, passwordConfirm.value, selectedOptionInd) {
        mutableStateOf(
            login.trim().length in (4..32) &&
                    password.value.trim().length in (8..500) &&
                    login.trim().matches(Regex("[a-z0-9_\\-.@]+")) &&
            if (selectedOptionInd == 1) password.value.trim() == passwordConfirm.value.trim() else true
        )
    }

    val uiState = authViewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        Switcher(selectedInd = selectedOptionInd, onOptionClick = { ind -> selectedOptionInd = ind })

        if (uiState.value is UiState.Loading)
            Loading()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                label = { Text(text = "Login") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    disabledContainerColor = MaterialTheme.colorScheme.background,
                ),
                value = login,
                onValueChange = { newInput -> login = newInput },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(30.dp))


            PasswordInput(
                label = "Password",
                password = password,
                passwordVisibility = passwordVisibility,
                imeAction = if (selectedOptionInd == 0)
                    ImeAction.Done
                else
                    ImeAction.Next
            )

            Spacer(modifier = Modifier.height(30.dp))

            if (selectedOptionInd == 1){
                PasswordInput(
                    label = "Confirm password",
                    password = passwordConfirm,
                    passwordVisibility = passwordVisibilityConfirm,
                    imeAction = ImeAction.Done
                )

                Spacer(modifier = Modifier.height(30.dp))
            }

            Button(
                modifier = Modifier.fillMaxWidth(0.8f),
                enabled = valid && !processing,
                onClick = {
                    processing = true
                    if (selectedOptionInd == 0)
                        authViewModel.signIn(
                            login = login.trim(),
                            password = password.value.trim(),
                            onError = { message ->
                                context.showToast(message)
                            },
                            onSuccess = onAuthorisationDone
                        ).invokeOnCompletion { processing = false }
                    else
                        authViewModel.signUp(
                            login = login.trim(),
                            password = password.value.trim(),
                            onError = { message ->
                                context.showToast(message)
                            },
                            onSuccess = onAuthorisationDone
                        ).invokeOnCompletion { processing = false }
                }
            ) {
                Text(text = if (selectedOptionInd == 0) "LOG IN" else "SIGN UP")
            }



        }
    }


}