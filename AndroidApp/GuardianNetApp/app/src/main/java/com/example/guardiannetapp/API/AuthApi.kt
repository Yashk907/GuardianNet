package com.example.guardiannetapp.API

import com.example.guardiannetapp.Models.User
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("/api/v1/users/register")
    suspend fun SignUp(@Body user : User) : Response
}