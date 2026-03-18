package com.softcat.adventuremaker.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = koinViewModel()
) {
    val state by viewModel.state.observeAsState(AuthState.Loading)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        when (val s = state) {

            AuthState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            AuthState.NoUser -> {
                // можно сразу переключить в Enter
                LaunchedEffect(Unit) {
                    viewModel.switchToEnter()
                }
            }

            is AuthState.Enter -> {
                EnterContent(
                    state = s,
                    onEmailChange = viewModel::changeEmail,
                    onPasswordChange = viewModel::changePassword,
                    onLoginClick = viewModel::onLogInClicked,
                    onSwitchToRegister = viewModel::switchToRegister
                )
            }

            is AuthState.Register -> {
                RegisterContent(
                    state = s,
                    onNameChange = viewModel::changeName,
                    onEmailChange = viewModel::changeEmail,
                    onPasswordChange = viewModel::changePassword,
                    onRepeatPasswordChange = viewModel::changeRepeatedPassword,
                    onRegisterClick = viewModel::onLogInClicked,
                    onSwitchToEnter = viewModel::switchToEnter
                )
            }
        }
    }
}