package com.example.guardiannetapp.Viewmodels.GurdianViewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardiannetapp.Models.Guardian
import com.example.guardiannetapp.Repo.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GuardianHomeScreenVM @Inject constructor(private val authRepo: Repo) : ViewModel() {
    private val _guardian : MutableStateFlow<Guardian> = MutableStateFlow(Guardian())
    val guardian = _guardian

    val isLoading = MutableStateFlow(false)

    fun fetchGuardian(userId : String, onError : (String)-> Unit){
        isLoading.value = true
        viewModelScope.launch {
            val result= authRepo.fetchGuardian(userId)
            result.onSuccess {
                response->
                _guardian.value = response.data
                isLoading.value = false
            }
            result.onFailure {
                onError(it.message.toString())
                isLoading.value = false
            }
        }
    }
}