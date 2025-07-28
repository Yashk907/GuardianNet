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
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.guardiannetapp.R
import com.google.android.gms.location.*

class LocationService : Service() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

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

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000 // 5 seconds
        ).build()

        // Start location updates if permission granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, mainLooper
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundServiceNotification()
        return START_STICKY
    }

    /** Show foreground notification so service is not killed **/
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
            .setSmallIcon(R.mipmap.ic_launcher) // ✅ use valid icon
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
    }

    /** Check if location is inside safe zone **/
    private fun checkSafeZone(location: Location) {
        // Replace these with actual values from server/db
        val safeZoneLat = 18.462760
        val safeZoneLng = 73.879198
        val radius = 1000 // meters

        val distance = FloatArray(1)
        Location.distanceBetween(
            location.latitude,
            location.longitude,
            safeZoneLat,
            safeZoneLng,
            distance
        )

        if (distance[0] > radius) {
            // Outside safe zone - send alert notification
            sendOutsideSafeZoneNotification()
        }
    }

    /** Send notification when user is outside safe zone **/
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
        // Stop location updates when service is destroyed
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}
