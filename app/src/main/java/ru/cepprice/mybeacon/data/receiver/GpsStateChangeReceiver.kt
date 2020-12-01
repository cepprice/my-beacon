package ru.cepprice.mybeacon.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import ru.cepprice.mybeacon.utils.extension.isGpsEnabled

class GpsStateChangeReceiver(
    private val stateChangeNotifier: GpsStateChangeNotifier
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
            if (context == null) return

            if (context.isGpsEnabled())  stateChangeNotifier.onGpsEnabled()
            else stateChangeNotifier.onGpsDisabled()
        }
    }
}

interface GpsStateChangeNotifier {
    fun onGpsEnabled()
    fun onGpsDisabled()
}