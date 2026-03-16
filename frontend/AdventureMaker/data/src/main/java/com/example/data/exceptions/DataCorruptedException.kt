package com.example.data.exceptions

class DataCorruptedException(
    msg: String
): Exception("Data is corrupted: $msg")