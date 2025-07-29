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
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class GuardianLocationListenerService : Service() {

    private lateinit var webSocket: WebSocket
    private var guardianUserId: String = "" // get from SharedPreferences

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        guardianUserId = getSharedPreferences("SafeZonePrefs", MODE_PRIVATE)
            .getString("guardianUserId", "") ?: ""

        Log.d("GuardianWS", "GuardianUserId: $guardianUserId")

        startForegroundNotification()
        initWebSocket()

        return START_STICKY
    }

    /** Connect to WebSocket **/
    private fun initWebSocket() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("wss://guardiannet-production.up.railway.app") // your backend url
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: okhttp3.Response) {
                Log.d("GuardianWS", "Connected to server")
                // Register as Guardian
                val registerJson = """{"role":"Guardian","userId":"$guardianUserId"}"""
                ws.send(registerJson)
            }

            override fun onMessage(ws: WebSocket, text: String) {
                Log.d("GuardianWS", "Message from server: $text")
                try {
                    val json = JSONObject(text)
                    if (json.has("userId") && json.has("lat") && json.has("lng")) {
                        val patientId = json.getString("userId")
                        val lat = json.getDouble("lat")
                        val lng = json.getDouble("lng")
                        showPatientLocationNotification(patientId, lat, lng)
                    }
                } catch (e: Exception) {
                    Log.e("GuardianWS", "Invalid message format: $text")
                }
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: okhttp3.Response?) {
                Log.e("GuardianWS", "WebSocket Error: ${t.message}")
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                Log.d("GuardianWS", "Closed: $reason")
            }
        })
    }

    /** Foreground Notification **/
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

    /** Show Patient Location Alert **/
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

        getSystemService(NotificationManager::class.java).notify(patientId.hashCode(), notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        if (::webSocket.isInitialized) {
            webSocket.close(1000, "Service stopped")
        }
    }
}