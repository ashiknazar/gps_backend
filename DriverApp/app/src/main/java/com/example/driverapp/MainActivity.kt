package com.example.driverapp
import androidx.compose.material3.ExperimentalMaterial3Api

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.driverapp.ui.theme.DriverAppTheme
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private lateinit var fusedClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null

    private val httpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            DriverAppTheme {
                LocationToggleUI(
                    onStart = { startLocationUpdates() },
                    onStop = { stopLocationUpdates() }
                )
            }
        }
    }

    // -------- LOCATION START --------
    private fun startLocationUpdates() {
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10_000L // 10 seconds
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                sendToBackend(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            }
        }

        fusedClient.requestLocationUpdates(
            request,
            locationCallback!!,
            mainLooper
        )
    }

    private fun stopLocationUpdates() {
        locationCallback?.let {
            fusedClient.removeLocationUpdates(it)
        }
        locationCallback = null
    }

    // -------- NETWORK --------
    private fun sendToBackend(latitude: Double, longitude: Double) {
        val json = JSONObject().apply {
            put("driver_id", "driver_001")
            put("latitude", latitude)
            put("longitude", longitude)
        }

        val body = json.toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://gps-backend....onrender.com/location")
            .post(body)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                httpClient.newCall(request).execute().close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // -------- PERMISSIONS --------
    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) startLocationUpdates()
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationToggleUI(
    onStart: () -> Unit,
    onStop: () -> Unit
){
    var isOnline by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Driver Status") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = if (isOnline)
                    "ONLINE – streaming GPS"
                else
                    "OFFLINE",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            Switch(
                checked = isOnline,
                onCheckedChange = {
                    isOnline = it
                    if (it) onStart() else onStop()
                }
            )
        }
    }
}
