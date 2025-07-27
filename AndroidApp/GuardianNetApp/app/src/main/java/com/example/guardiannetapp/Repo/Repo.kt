package com.example.guardiannetapp.Repo

import com.example.guardiannetapp.API.AuthApi
import com.example.guardiannetapp.API.GuardianRequest
import com.example.guardiannetapp.Models.Guardian
import com.example.guardiannetapp.Models.Response.ApiResponse
import com.example.guardiannetapp.Models.Response.AuthData
import com.example.guardiannetapp.Models.SignInRequest
import com.example.guardiannetapp.Models.SignUpRequest
import javax.inject.Inject

class Repo @Inject constructor(private val authApi : AuthApi) {
    suspend fun signUp(user: SignUpRequest): Result<ApiResponse<AuthData>> {
        return try {
            val response = authApi.SignUp(user)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(user: SignInRequest) : Result<ApiResponse<AuthData>>{
        return try {
            val response =authApi.SignIn(user)
            Result.success((response))
        }catch (e : Exception){
            Result.failure(e)
        }
    }

    suspend fun fetchGuardian(userId : String) : Result<ApiResponse<Guardian>>{
        return try {
            val response = authApi.fetchGuardian(GuardianRequest(userId))
            Result.success(response)
        }catch (e : Exception){
            Result.failure(e)
        }
    }


}