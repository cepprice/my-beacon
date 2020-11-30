package ru.cepprice.mybeacon.data.receiver

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BluetoothStateChangeListener(
    private val stateChangeNotifier: BluetoothStateChangeNotifier
    ) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            val state = intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)

            when (state) {
                BluetoothAdapter.STATE_OFF, BluetoothAdapter.STATE_TURNING_OFF ->
                    stateChangeNotifier.onBluetoothDisabled()
                BluetoothAdapter.STATE_ON, BluetoothAdapter.STATE_TURNING_ON ->
                    stateChangeNotifier.onBluetoothEnabled()
            }
        }
    }
}

interface BluetoothStateChangeNotifier {
    fun onBluetoothDisabled()
    fun onBluetoothEnabled()
}
