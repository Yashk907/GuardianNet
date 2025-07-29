package com.example.guardiannetapp.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.guardiannetapp.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationService : Service() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    // Store safe zone values here
    private var safeZoneLat: Double = 0.0
    private var safeZoneLng: Double = 0.0
    private var radius: Float = 100f

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    checkSafeZone(location)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Extract values from Intent
        safeZoneLat = intent?.getDoubleExtra("center_lat", 0.0) ?: 0.0
        safeZoneLng = intent?.getDoubleExtra("center_lng", 0.0) ?: 0.0
        radius = intent?.getFloatExtra("radius", 100f) ?: 100f
        Log.d("Location",safeZoneLat.toString())

        startForegroundServiceNotification()

        startLocationUpdates()
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000 // 5 seconds
        ).build()

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, mainLooper
        )
    }

    /** Show foreground notification **/
    @SuppressLint("ForegroundServiceType")
    private fun startForegroundServiceNotification() {
        val channelId = "location_service_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Location Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("GuardianNet is tracking location")
            .setContentText("Your location is being monitored for safety")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
    }

    /** Check if location is inside safe zone **/
    private fun checkSafeZone(location: Location) {
        val distance = FloatArray(1)
        Location.distanceBetween(
            location.latitude,
            location.longitude,
            safeZoneLat,
            safeZoneLng,
            distance
        )

        if (distance[0] > radius) {
            sendOutsideSafeZoneNotification()
        }
    }

    /** Send alert notification **/
    private fun sendOutsideSafeZoneNotification() {
        val channelId = "safezone_alert_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Safe Zone Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("⚠️ Outside Safe Zone")
            .setContentText("Patient has moved outside the safe zone")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(2, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}
