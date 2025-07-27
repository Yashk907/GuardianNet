package com.example.guardiannetapp.Viewmodels.GurdianViewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardiannetapp.Models.Response.ApiResponse
import com.example.guardiannetapp.Repo.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class connectToPatientVM @Inject constructor(private val repo : Repo) : ViewModel() {
    val isLoading = MutableStateFlow(false)

    fun connectToPatient(userId : String, linkCode : String, onResult : (String)-> Unit) {
        isLoading.value =true
        viewModelScope.launch {
            val result = repo.connectToPatient(userId,linkCode)
            result.onSuccess {
                res->
                onResult(res.message.toString())
                isLoading.value=false
            }
            result.onFailure {
                e->
                onResult(e.message.toString())
                isLoading.value=false
            }
        }
    }

}