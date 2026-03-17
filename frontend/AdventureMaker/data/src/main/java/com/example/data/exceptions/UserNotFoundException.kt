package com.example.data.exceptions

class UserNotFoundException(
    email: String
): Exception("User with email = '$email' is not found")