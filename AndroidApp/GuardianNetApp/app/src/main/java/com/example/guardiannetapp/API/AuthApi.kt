package com.example.guardiannetapp.API

import com.example.guardiannetapp.Models.Guardian
import com.example.guardiannetapp.Models.Response.ApiResponse
import com.example.guardiannetapp.Models.Response.AuthData
import com.example.guardiannetapp.Models.SignInRequest
import com.example.guardiannetapp.Models.SignUpRequest
import com.example.guardiannetapp.Models.User
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("/api/v1/users/register")
    suspend fun SignUp(@Body user : SignUpRequest) : ApiResponse<AuthData>

    @POST("/api/v1/users/login")
    suspend fun SignIn(@Body user : SignInRequest) : ApiResponse<AuthData>

    @POST("/api/v1/guardians/getGuardian")
    suspend fun fetchGuardian(@Body guardianRequest: GuardianRequest) : ApiResponse<Guardian>

}

data class GuardianRequest(
    val userId: String
)
