package com.softcat.adventuremaker.screens.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import com.softcat.adventuremaker.navigation.NavigationItem

@Composable
fun AuthScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = koinViewModel()
) {
    val state by viewModel.state.observeAsState(AuthState.Initial)
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.logInEvent.collect { event ->
            when (event) {
                is AuthEvent.LoggedIn -> {
                    navController.navigate(NavigationItem.Networking.Profile(event.user)) {
                        popUpTo(NavigationItem.Networking.Auth) { inclusive = true }
                    }
                }
                is AuthEvent.Error -> {
                    snackBarHostState.showSnackbar(event.throwable.toErrorMessage(context))
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            when (val s = state) {

                AuthState.Loading -> LoadingContent()

                AuthState.Initial -> {
                    InitialContent(
                        onEnterClick = viewModel::switchToEnter,
                        onRegisterClick = viewModel::switchToRegister,
                        onBackClick = { navController.navigate(NavigationItem.Networking.Posts) },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is AuthState.Enter -> {
                    EnterContent(
                        state = s,
                        onEmailChange = viewModel::changeEmail,
                        onPasswordChange = viewModel::changePassword,
                        onLoginClick = viewModel::onLogInClicked,
                        onSwitchToRegister = viewModel::switchToRegister,
                        onBackClick = viewModel::switchToInitial,
                        modifier = Modifier.fillMaxSize()
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
                        onSwitchToEnter = viewModel::switchToEnter,
                        onBackClick = viewModel::switchToInitial,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}