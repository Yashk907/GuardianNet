package com.example.guardiannetapp.services

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
import com.example.guardiannetapp.R
import com.example.guardiannetapp.Viewmodels.PatientViewModel.getDouble
import com.google.android.gms.location.*
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class LocationService : Service() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var socket: Socket

    private var safeZoneLat: Double = 0.0
    private var safeZoneLng: Double = 0.0
    private var radius: Int = 1000
    private var userId: String = ""

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize Socket.IO
        try {
            socket = IO.socket("http://10.54.88.9:8000")
        } catch (e: Exception) {
            Log.e("SocketIO", "Error: ${e.message}")
        }

        // Connect
        socket.connect()

        // Optional: Listen for responses from server
        socket.on("patientLocation") { args ->
            Log.d("SocketIO", "Server: ${args[0]}")
        }

        // Location callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    checkSafeZone(location)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sharedPrefs = getSharedPreferences("SafeZonePrefs", MODE_PRIVATE)
        safeZoneLat = sharedPrefs.getDouble("center_lat", 0.0)
        safeZoneLng = sharedPrefs.getDouble("center_lng", 0.0)
        radius = sharedPrefs.getInt("radius", 1000)
        userId = sharedPrefs.getString("userId", "") ?: ""

        // Register patient
        val registerData = JSONObject()
        registerData.put("role", "Patient")
        registerData.put("userId", userId)
        socket.emit("register", registerData)

        Log.d("LocationService", "Registered Patient: $userId")

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

            // Send location update to server
            val json = JSONObject()
            json.put("userId", userId)
            json.put("lat", location.latitude)
            json.put("lng", location.longitude)
            json.put("outsideSafeZone", true)

            socket.emit("locationUpdate", json)
            Log.d("SocketIO", "Location sent: $json")
        }
    }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundServiceNotification() {
        val channelId = "location_service_channel"
                              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Location Service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("GuardianNet is tracking location")
            .setContentText("Your location is being monitored for safety")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
    }

    private fun sendOutsideSafeZoneNotification() {
        val channelId = "safezone_alert_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Safe Zone Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("⚠️ Outside Safe Zone")
            .setContentText("Patient has moved outside the safe zone")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        getSystemService(NotificationManager::class.java).notify(2, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        socket.disconnect()
    }
}
