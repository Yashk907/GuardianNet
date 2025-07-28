package com.example.guardiannetapp.Viewmodels.GurdianViewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardiannetapp.Models.Patient
import com.example.guardiannetapp.Repo.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GuardianSidePatientDetailsVM @Inject constructor(private val repo: Repo) : ViewModel() {

    private val _patient = MutableStateFlow<Patient>(Patient())
    val patient = _patient

    val isLoading = MutableStateFlow(false)

    fun fetchPatient(userId : String , onError: (String)->Unit){
        isLoading.value=true
        viewModelScope.launch {
            val response = repo.fetchPatient(userId)
            response.onSuccess {
                result->
                _patient.value=result.data
                isLoading.value=false
            }
            response.onFailure {
                error->
                onError(error.message.toString())
                isLoading.value=false
            }
        }
    }
}