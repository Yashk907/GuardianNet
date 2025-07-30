package com.example.guardiannetapp.Viewmodels.GurdianViewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardiannetapp.Models.Guardian
import com.example.guardiannetapp.Models.Patient
import com.example.guardiannetapp.Repo.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GuardianHomeScreenVM @Inject constructor(private val authRepo: Repo,
    private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _guardian : MutableStateFlow<Guardian> = MutableStateFlow(Guardian())
    val guardian = _guardian

    private val _patientList  : MutableStateFlow<Patient> = MutableStateFlow(Patient())
    val patientList = _patientList


    val isLoading = MutableStateFlow(false)
    fun fetchGuardian(
        userId: String,
        onError: (String) -> Unit,
        onSuccess: (String) -> Unit
    ) {
        isLoading.value = true
        viewModelScope.launch {
            val result = authRepo.fetchGuardian(userId)
            result.onSuccess { response ->
                _guardian.value = response.data
                isLoading.value = false
                Log.d("guardian", response.data.toString())

                // Save in SharedPreferences
                sharedPreferences.edit().apply {
                    putString("guardianUserId", userId)
                    apply()
                }

                // Notify UI with guardianId
                onSuccess(userId)
            }
            result.onFailure {
                Log.d("error", it.message.toString())
                onError(it.message.toString())
                isLoading.value = false
            }
        }
    }

    fun fetchPatient(patientId : String){
        viewModelScope.launch {
            val result = authRepo.fetchPatient(patientId)
            result.onSuccess {

            }
        }
    }

}