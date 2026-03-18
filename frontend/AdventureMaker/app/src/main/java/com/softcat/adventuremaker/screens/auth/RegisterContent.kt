package com.softcat.adventuremaker.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterContent(
    state: AuthState.Register,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRepeatPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onSwitchToEnter: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Создать аккаунт",
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        AuthTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = "Введите имя"
        )

        Spacer(modifier = Modifier.height(12.dp))

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

        Spacer(modifier = Modifier.height(12.dp))

        AuthTextField(
            value = state.repeatedPassword,
            onValueChange = onRepeatPasswordChange,
            label = "Повторите пароль",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        AuthButton(text = "Зарегистрироваться", onClick = onRegisterClick)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Уже есть аккаунт? Войти",
            modifier = Modifier.clickable { onSwitchToEnter() }
        )
    }
}