package com.mobiledev.randomuserapp.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val salt: String,
    val phoneNumber: String?,
    val address: String?,
    val birthday: String,
    val profilePicture: String?,
    val qrCode: String?
)