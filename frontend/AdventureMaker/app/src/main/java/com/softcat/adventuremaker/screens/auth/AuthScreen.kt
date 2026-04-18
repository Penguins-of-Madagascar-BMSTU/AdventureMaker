package com.softcat.adventuremaker.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.softcat.adventuremaker.navigation.BottomNavigationBar
import org.koin.androidx.compose.koinViewModel
import com.softcat.adventuremaker.navigation.NavigationItem

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AuthViewModel = koinViewModel()
) {
    val state by viewModel.state.observeAsState(AuthState.Loading)

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.logInEvent.collect { event ->
            when (event) {
                is AuthEvent.LoggedIn -> {
                    navController.navigate(NavigationItem.Networking.Profile) {
                        popUpTo(NavigationItem.Auth) { inclusive = true }
                    }
                }
                is AuthEvent.Error -> {
                    snackbarHostState.showSnackbar(event.msg)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomNavigationBar(
                configuration = NavigationItem.BottomBarConfiguration.None,
                navController = navController
            )
        }
    ) { padding ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            when (val s = state) {

                AuthState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                AuthState.NoUser -> {
                    NoUserContent(
                        onEnterClick = viewModel::switchToEnter,
                        onRegisterClick = viewModel::switchToRegister,
                        modifier = modifier
                    )
                }

                is AuthState.Enter -> {
                    EnterContent(
                        state = s,
                        onEmailChange = viewModel::changeEmail,
                        onPasswordChange = viewModel::changePassword,
                        onLoginClick = viewModel::onLogInClicked,
                        onSwitchToRegister = viewModel::switchToRegister,
                        modifier = modifier
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
                        modifier = modifier
                    )
                }
            }
        }
    }
}