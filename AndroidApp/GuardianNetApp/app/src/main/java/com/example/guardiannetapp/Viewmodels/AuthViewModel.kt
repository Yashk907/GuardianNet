package com.example.guardiannetapp.Viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardiannetapp.Models.Response.ApiResponse
import com.example.guardiannetapp.Models.Response.AuthData
import com.example.guardiannetapp.Models.SignInRequest
import com.example.guardiannetapp.Models.SignUpRequest
import com.example.guardiannetapp.Repo.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private  val repo : Repo):
    ViewModel() {

        private val _signUpState = MutableStateFlow<Result<ApiResponse<AuthData>>?>(null)
        val signupState = _signUpState

        private val _signInState = MutableStateFlow<Result<ApiResponse<AuthData>>?>(null)
        val signinState = _signInState

    fun signUp(name: String, email: String, phone: String, password: String, role: String,address : String) {
        viewModelScope.launch {
            val request = SignUpRequest(name, email, phone, password, role,address)
            Log.d("AuthLog",request.toString())
            val state = repo.signUp(request)
            _signUpState.value = state
            Log.d("AuthRes",state.toString())
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            val request = SignInRequest(email, password)
            val state = repo.signIn(request)
            _signInState.value = state
        }
    }

}