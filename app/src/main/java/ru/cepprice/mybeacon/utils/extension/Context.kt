package ru.cepprice.mybeacon.utils.extension

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat

fun Context.isGpsEnabled(): Boolean {
    val lm = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var isLocationEnabled = false
    try {
        isLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    } catch (ex: Exception) {}
    return isLocationEnabled
}

fun Context.hasGpsPermission(): Boolean =
    (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED)
