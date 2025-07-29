package com.example.guardiannetapp.Viewmodels.PatientViewModel

import android.content.Context
import android.content.Intent
import androidx.compose.material3.darkColorScheme
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardiannetapp.Models.Patient
import com.example.guardiannetapp.Repo.Repo
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.jvm.java

@HiltViewModel
class PatientHomeScreenVM @Inject constructor(private val repo : Repo): ViewModel() {

    private val _patient = MutableStateFlow(Patient())
    val patient = _patient

    val isLoading = MutableStateFlow(false)



    fun fetchPatient(
        userId: String,
        onError: (String) -> Unit,
        onSuccess: (Patient) -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            val response = repo.fetchPatient(userId)
            response.onSuccess { result ->
                _patient.value = result.data
                isLoading.value = false
                onSuccess(result.data)   // ✅ Pass patient back
            }
            response.onFailure {
                onError(it.message.toString())
                isLoading.value = false
            }
        }
    }

}