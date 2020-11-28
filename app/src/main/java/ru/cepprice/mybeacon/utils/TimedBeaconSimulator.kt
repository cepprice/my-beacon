package ru.cepprice.mybeacon.utils

import android.util.Log
import org.altbeacon.beacon.AltBeacon
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.simulator.BeaconSimulator
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class TimedBeaconSimulator(beaconCount: Int? = null) : BeaconSimulator {

    private val beacons = ArrayList<Beacon>()
    private var scheduledTaskExecutor: ScheduledExecutorService

    private var mBeaconCount: Int = 5

    init {
        if (beaconCount != null && beaconCount > 0) mBeaconCount = beaconCount
        var counter = 0

        scheduledTaskExecutor = Executors.newScheduledThreadPool(mBeaconCount)
        scheduledTaskExecutor.scheduleAtFixedRate({
            if (counter++ < mBeaconCount) beacons.add(getRandomBeacon()
                .also { Log.d("M_TimedBeaconSimulator", "Randomly created beacon with dist: ${it.distance}") })
            else scheduledTaskExecutor.shutdown()
        }, 0, 1, TimeUnit.SECONDS)
    }

    private fun getRandomBeacon(): Beacon =
        AltBeacon.Builder()
            .setId1("DF7E1C79-43E9-44FF-886F-1D1F7DA699${(10..99).random()}")
            .setId2("1")
            .setId3("1")
            .setRssi((-100..-26).random())
            .setTxPower((-115..-80).random())
//            .setBluetoothAddress()
            .build()

    override fun getBeacons(): MutableList<Beacon> = beacons
}