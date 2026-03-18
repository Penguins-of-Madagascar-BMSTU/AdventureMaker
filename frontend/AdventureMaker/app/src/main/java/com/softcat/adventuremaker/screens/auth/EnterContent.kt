package com.softcat.adventuremaker.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EnterContent(
    state: AuthState.Enter,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSwitchToRegister: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Outlined.Lock,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Войти в профиль",
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        AuthTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = "Введите email"
        )

        Spacer(modifier = Modifier.height(12.dp))

        AuthTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = "Введите пароль",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        AuthButton(text = "Войти", onClick = onLoginClick)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Нет аккаунта? Зарегистрироваться",
            modifier = Modifier.clickable { onSwitchToRegister() }
        )
    }
}