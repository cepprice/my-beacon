package ru.cepprice.mybeacon.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import ru.cepprice.mybeacon.utils.extension.isGpsEnabled
import javax.inject.Inject

class GpsManager @Inject constructor(private val context: Context) {

    private val subject: BehaviorProcessor<Boolean> = BehaviorProcessor.createDefault(
        context.isGpsEnabled()
    )

    private val receiver: BroadcastReceiver

    init {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action.equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                    if (context == null) return
                    val state = context.isGpsEnabled()
                    subject.onNext(state)
                }
            }
        }

        context.registerReceiver(receiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
    }

    fun unregisterReceiver() {
        context.unregisterReceiver(receiver)
    }

    fun isEnabled() = context.isGpsEnabled()

    fun asFlowable(): Flowable<Boolean> {
        return subject
    }

}
