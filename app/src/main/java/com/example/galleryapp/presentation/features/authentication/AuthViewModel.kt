package com.example.galleryapp.presentation.features.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galleryapp.data.model.network.SignUserDtoIn
import com.example.galleryapp.data.repository.RestApiRepository
import com.example.galleryapp.data.user.UserData
import com.example.galleryapp.data.utils.UiState
import com.example.galleryapp.data.utils.checkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val restApiRepository: RestApiRepository): ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.NotStarted())
    val uiState : StateFlow<UiState> = _uiState

    // add limitations
    fun signIn(
        login: String,
        password: String,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) = viewModelScope.launch {

        _uiState.value = UiState.Loading()

        val dataOrException = restApiRepository.signIn(userDtoIn = SignUserDtoIn(login, password))

        _uiState.value = checkResponse(
            dataOrException = dataOrException,
            onError = onError,
            onSuccess = { data ->
                UserData.init(data.token, data.userId, data.login)
//                UserToken.init(data.token)

                onSuccess()
            }
        )
    }


    fun signUp(
        login: String,
        password: String,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) = viewModelScope.launch {

        _uiState.value = UiState.Loading()

        val dataOrException = restApiRepository.signUp(userDtoIn = SignUserDtoIn(login, password))

        _uiState.value = checkResponse(
            dataOrException = dataOrException,
            onError = onError,
            onSuccess = { data ->
                UserData.init(data.token, data.userId, data.login)
//                UserToken.init(data.token)
                onSuccess()
            }
        )

//        when (dataOrException.data?.status) {
//            200 -> {
//                try {
//                    dataOrException.data?.data?.first()?.let { signUserOutDto ->
//                        UserToken.init(signUserOutDto.token)
//                        onSuccess()
//                    }
//                    _uiState.value = UiState.Success()
//                } catch (e: Exception){
//                    val message = "Error.\nNot found."
//                    onError(message)
//                    UiState.Failure(Exception(message))
//                }
//            }
//            else -> {
//                val message = try {
//                    throw _uiState.value.exception ?: Exception("Error.\nNull Exception.")
//                } catch (ioE: IOException) {
//                    "Error.\nYour connection lost."
//                } catch (hE: HttpException){
//                    hE.response()?.errorBody()?.string().toString()
//                } catch (e : Exception){
//                    e.message.toString()
//                }
//                onError(message)
//                UiState.Failure(dataOrException.exception)
//            }
//        }
    }
}