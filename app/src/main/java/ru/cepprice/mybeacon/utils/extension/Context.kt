package ru.cepprice.mybeacon.utils.extension

import android.content.Context
import android.location.LocationManager

fun Context.isGpsEnabled(): Boolean {
    val lm = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var isLocationEnabled = false
    try {
        isLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    } catch (ex: Exception) {}
    return isLocationEnabled
}