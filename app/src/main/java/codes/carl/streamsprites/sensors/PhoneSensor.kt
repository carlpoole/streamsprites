package codes.carl.streamsprites.sensors

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.BatteryManager
import androidx.core.app.ActivityCompat
import codes.carl.streamsprites.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PhoneSensor(private val context: Context, private val permissionRequester: PermissionRequester): LocationListener {
    private var locationManager: LocationManager? = null
    private var batteryStatus: Intent? = null
    private val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        batteryStatus = context.registerReceiver(null, intentFilter)
        startSpeedUpdates()
        startPollingTask()
    }

    private fun startSpeedUpdates() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Permissions are not granted, request permissions from the user.
            permissionRequester.requestLocationPermissions()
            return
        }
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1f, this)
    }

    override fun onLocationChanged(location: Location) {
        ApiClient.putData("speed", "{ value: ${location.speed} }", "123456")
    }

    private fun startPollingTask() {
        coroutineScope.launch {
            while (isActive) {
                val batteryLevel = getBatteryLevel()
                val batteryTemperature = getBatteryTemperature()
                ApiClient.putData("battery", "{ level: $batteryLevel, temperature: $batteryTemperature }", "123456")
                delay(2000L)
            }
        }
    }

    private fun getBatteryTemperature(): Float {
        val temperature = batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0

        // Convert to Celsius
        return temperature / 10.0f
    }

    private fun getBatteryLevel(): Float {
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

        return if (level >= 0 && scale > 0) (level.toFloat() / scale.toFloat()) * 100.0f else 0.0f
    }

    interface PermissionRequester {
        fun requestLocationPermissions()
    }
}