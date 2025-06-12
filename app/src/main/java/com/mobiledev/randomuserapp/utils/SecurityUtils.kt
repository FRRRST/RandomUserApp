package com.mobiledev.randomuserapp.utils

import java.security.MessageDigest
import java.security.SecureRandom
import android.util.Base64

fun hashPassword(password: String, salt: String) :String {
    val combined = password + salt
    val digest = MessageDigest.getInstance("SHA-256").digest(combined.toByteArray())
    return digest.joinToString("") {"%02x".format(it)}
}

fun generateSalt(): String {
    val salt = ByteArray(16)
    SecureRandom().nextBytes(salt)
    return Base64.encodeToString(salt, Base64.NO_WRAP)
}

