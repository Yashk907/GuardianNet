package com.example.guardiannetapp.Viewmodels.PatientViewModel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
class PatientHomeScreenVM @Inject constructor(private val repo : Repo,
    private val sharedPref : SharedPreferences): ViewModel() {

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

                val safeZone = result.data.safeZoneCenter
                sharedPref.edit().apply {
                    putDouble("center_lat", safeZone.coordinates[1]) // [lng, lat]
                    putDouble("center_lng", safeZone.coordinates[0])
                    putInt("radius", result.data.safeZoneRadius)
                    putString("userId",userId)
                    apply()
                }
                onSuccess(result.data)   // ✅ Pass patient back
            }
            response.onFailure {
                onError(it.message.toString())
                isLoading.value = false
            }
        }
    }

}

fun SharedPreferences.Editor.putDouble(key: String, value: Double): SharedPreferences.Editor {
    return putLong(key, java.lang.Double.doubleToRawLongBits(value))
}

fun SharedPreferences.getDouble(key: String, defaultValue: Double): Double {
    return java.lang.Double.longBitsToDouble(getLong(key, java.lang.Double.doubleToRawLongBits(defaultValue)))
}

