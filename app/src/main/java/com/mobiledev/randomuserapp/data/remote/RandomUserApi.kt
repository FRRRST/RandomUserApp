package com.mobiledev.randomuserapp.data.remote

import com.mobiledev.randomuserapp.data.db.User
import com.mobiledev.randomuserapp.data.db.UserDao
import com.mobiledev.randomuserapp.utils.generateSalt
import com.mobiledev.randomuserapp.utils.hashPassword
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Locale
import java.text.SimpleDateFormat

interface RandomUserApi {
    @GET("api")
    suspend fun getUsers(@Query("results") count: Int): RandomUserResponse
}

data class RandomUserResponse(
    val results: List<ApiUser>
)

data class ApiUser(
    val name: Name,
    val email: String,
    val login: Login,
    val phone: String?,
    val location: Location,
    val dob: Dob,
    val picture: Picture
)

data class Name(val first: String, val last: String)
data class Login(val password: String)
data class Location(val street: Street)
data class Street(val name: String)
data class Dob(val date: String)
data class Picture(val large: String)

fun ApiUser.toUser(): User {
    val salt = generateSalt()
    return User(
        name = "${name.first} ${name.last}",
        email = email,
        password = hashPassword(login.password, salt),
        salt = salt,
        phoneNumber = phone,
        address = location.street.name,
        birthday = formatDate(dob.date),
        profilePicture = picture.large,
        qrCode = null
    )
}

object RetrofitClient {
    val api: RandomUserApi by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl("https://randomuser.me")
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(RandomUserApi::class.java)
    }
}

suspend fun fillDatabase(count: Int, dao: UserDao) {
    try {
        val response = RetrofitClient.api.getUsers(count)
        val users = response.results.map {it.toUser()}
        dao.insertAll(users)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

suspend fun clearDatabase(dao: UserDao) {
    try {
        dao.clearAll()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun formatDate(input: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        formatter.format(parser.parse(input)!!)
    } catch (e: Exception) {
        input
    }
}