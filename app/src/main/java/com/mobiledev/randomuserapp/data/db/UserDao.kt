package com.mobiledev.randomuserapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update

@Dao
interface UserDao {
    @Insert
    suspend fun insertAll(users: List<User>)

    @androidx.room.Query("SELECT * FROM users")
    suspend fun getAll(): List<User>

    @androidx.room.Query("DELETE FROM users")
    suspend fun clearAll()

    @androidx.room.Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUSerById(id: Int): User?

    @Update
    suspend fun update(user: User)
}