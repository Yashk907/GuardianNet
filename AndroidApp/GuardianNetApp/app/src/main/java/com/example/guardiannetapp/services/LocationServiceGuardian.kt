package com.example.guardiannetapp.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.guardiannetapp.R
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
class GuardianLocationListenerService : Service() {

    private lateinit var socket: Socket
    private var guardianUserId: String = ""

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        guardianUserId = getSharedPreferences("SafeZonePrefs", MODE_PRIVATE)
            .getString("guardianUserId", "") ?: ""

        Log.d("GuardianWS", "GuardianUserId: $guardianUserId")

        startForegroundNotification()
        initSocket()

        return START_STICKY
    }

    private fun initSocket() {
        try {
            socket = IO.socket("http://10.136.192.9:8000/")
        } catch (e: Exception) {
            Log.e("GuardianWS", "Error: ${e.message}")
            return
        }

        socket.connect()

        // When connected, register guardian
        socket.on(Socket.EVENT_CONNECT) {
            Log.d("GuardianWS", "Connected to server")
            val json = JSONObject()
            json.put("role", "Guardian")
            json.put("userId", guardianUserId)
            socket.emit("register", json)
        }

        // Listen for patient location updates
        socket.on("patientLocation") { args ->
            val data = args[0] as JSONObject
            val patientId = data.getString("userId")
            val lat = data.getDouble("lat")
            val lng = data.getDouble("lng")
            Log.d("GuardianWS", "Patient location: $lat,$lng")
            if(data.getBoolean("outsideSafeZone")){
                showPatientLocationNotification(patientId, lat, lng)
            }else{
                showSafeNotification(patientId)
            }
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            Log.d("GuardianWS", "Disconnected from server")
        }
    }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundNotification() {
        val channelId = "guardian_listener_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Guardian Listener", NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("GuardianNet")
            .setContentText("Listening for patient alerts...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        startForeground(3, notification)
    }

    private fun showPatientLocationNotification(patientId: String, lat: Double, lng: Double) {
        val channelId = "patient_location_alert"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Patient Location Alerts", NotificationManager.IMPORTANCE_HIGH
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Patient Outside Safe Zone ⚠️")
            .setContentText("Patient $patientId is at $lat,$lng")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        getSystemService(NotificationManager::class.java)
            .notify(patientId.hashCode(), notification)
    }
    private fun showSafeNotification(patientId: String) {
        val channelId = "patient_safe_zone_alert"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Patient Safe Zone Alerts",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("✅ Patient Back in Safe Zone")
            .setContentText("Patient $patientId is now inside the safe zone")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        // Use same notification ID as the outside alert so it REPLACES it
        getSystemService(NotificationManager::class.java)
            .notify(patientId.hashCode(), notification)
    }


    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
