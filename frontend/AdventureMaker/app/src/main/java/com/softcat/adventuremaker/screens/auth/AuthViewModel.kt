package com.softcat.adventuremaker.screens.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecases.UserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(
    private val userUseCase: UserUseCase
): ViewModel() {

    // Состояние экрана, данные которого отображаются для пользователя.
    private val _state = MutableLiveData<AuthState>(AuthState.Initial)
    val state: LiveData<AuthState> // Это неизменяемый объект LiveData для подписки на состояние.
        get() = _state

    // События для отправки новостей в компонент навигации.
    private val _logInEvent = MutableSharedFlow<AuthEvent>()
    val logInEvent: SharedFlow<AuthEvent> = _logInEvent.asSharedFlow()

    init {
        _state.value = AuthState.Initial
        tryEnter()
    }

    /*-----------Функции для изменения вводимых данных.-----------*/

    fun changeName(newValue: String) {
        // Пытаемся привести к типу AuthState.Register и, если не получилось, прерываем функцию.
        val currentState = (state.value as? AuthState.Register) ?: return
        _state.value = currentState.copy(name = newValue)
    }

    fun changeEmail(newValue: String) {
        when (val currentState = state.value) {
            is AuthState.Enter -> _state.value = currentState.copy(email = newValue)
            is AuthState.Register -> _state.value = currentState.copy(email = newValue)
            else -> {}
        }
    }

    fun changePassword(newValue: String) {
        when (val currentState = state.value) {
            is AuthState.Enter -> _state.value = currentState.copy(password = newValue)
            is AuthState.Register -> _state.value = currentState.copy(password = newValue)
            else -> {}
        }
    }

    fun changeRepeatedPassword(newValue: String) {
        // Пытаемся привести к типу AuthState.Register и, если не получилось, прерываем функцию.
        val currentState = (state.value as? AuthState.Register) ?: return
        _state.value = currentState.copy(repeatedPassword = newValue)
    }

    /*-----------Функции для переключения между экранами входа и регистрации.-----------*/

    fun switchToRegister() {
        val currentState = state.value
        Log.d("AuthVM", "switchToRegister called with $currentState")
        when (currentState) {
            AuthState.Initial -> {
                _state.value = AuthState.Register("", "", "", "")
            }
            is AuthState.Enter -> {
                _state.value = AuthState.Register("", currentState.email, currentState.password, "")
            }
            else -> {}
        }
    }

    fun switchToEnter() {
        val currentState = state.value
        Log.d("AuthVM", "switchToEnter called with $currentState")
        when (currentState) {
            AuthState.Initial -> {
                _state.value = AuthState.Enter("", "")
            }
            is AuthState.Register -> {
                _state.value = AuthState.Enter(currentState.email, currentState.password)
            }
            else -> {}
        }
    }

    fun switchToInitial() {
        val currentState = state.value
        Log.d("AuthVM", "switchToInitial called with $currentState")

        val newState = when (currentState) {
            is AuthState.Register -> AuthState.Initial
            is AuthState.Enter -> AuthState.Initial
            else -> null
        } ?: return

        _state.value = newState
    }


    /*-----------Обработка запроса входа или регистрации.-----------*/

    fun onLogInClicked() = when (val currentState = _state.value) {
        is AuthState.Register -> processRegisterRequest(currentState)
        is AuthState.Enter -> processEnterRequest(currentState)
        else -> {}
    }

    private fun tryEnter() {
        viewModelScope.launch {
            userUseCase.getLastEnteredUser()?.let { user ->
                _logInEvent.emit(AuthEvent.LoggedIn(user))
            }
        }
    }

    private fun processRegisterRequest(currentState: AuthState.Register) = with (currentState) {
        if (name.isBlank() || email.isBlank() || password.isEmpty() || repeatedPassword.isEmpty()) {
            viewModelScope.launch {
                _logInEvent.emit(
                    AuthEvent.Error(EmptyAuthFieldsException())
                )
            }
            return@with
        }

        // Запуск регистрации на отдельном потоке
        viewModelScope.launch(Dispatchers.IO) {
            val result = userUseCase.createUser(name, email, password, repeatedPassword)
            // Переключаемся на главный поток для публикации результата.
            withContext(Dispatchers.Main) {
                result.onSuccess {
                    _logInEvent.emit(AuthEvent.LoggedIn(it))
                }.onFailure {
                    _state.value = currentState
                    _logInEvent.emit(AuthEvent.Error(it))
                }
            }
        }
    }

    private fun processEnterRequest(currentState: AuthState.Enter) = with (currentState) {
        if (email.isBlank() || password.isEmpty()) {
            viewModelScope.launch {
                _logInEvent.emit(
                    AuthEvent.Error(EmptyAuthFieldsException())
                )
            }
            return@with
        }
        _state.value = AuthState.Loading

        // Запуск входа на отдельном потоке
        viewModelScope.launch(Dispatchers.IO) {
            val result = userUseCase.enter(email, password)
            // Переключаемся на главный поток для публикации результата.
            withContext(Dispatchers.Main) {
                result.onSuccess {
                    _logInEvent.emit(AuthEvent.LoggedIn(it))
                }.onFailure {
                    _state.value = currentState
                    _logInEvent.emit(AuthEvent.Error(it))
                }
            }
        }
    }
}