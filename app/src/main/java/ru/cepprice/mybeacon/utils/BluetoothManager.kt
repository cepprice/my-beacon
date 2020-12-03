package ru.cepprice.mybeacon.utils

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import javax.inject.Inject

class BluetoothManager @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter?,
    private val context: Context
) {

    private val subject: BehaviorProcessor<Boolean> =
        BehaviorProcessor.createDefault(isEnabled())

    private val receiver: BroadcastReceiver

    init {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (BluetoothAdapter.ACTION_STATE_CHANGED == intent?.action) {
                    val state = isEnabled()
                    subject.onNext(state)
                }
            }
        }

        context.registerReceiver(
            receiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
    }

    fun unregisterReceiver() {
        context.unregisterReceiver(receiver)
    }

    fun asFlowable(): Flowable<Boolean> {
        return subject
    }

    fun isEnabled() = bluetoothAdapter?.isEnabled == true

    fun enable() = bluetoothAdapter?.enable()

    fun disable() = bluetoothAdapter?.disable()

    fun toggle() {
        if (isEnabled()) disable()
        else enable()
    }

}
