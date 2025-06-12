package com.mobiledev.randomuserapp.utils.globalflags

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AdminState {
    var isAdmin by mutableStateOf(false)
}