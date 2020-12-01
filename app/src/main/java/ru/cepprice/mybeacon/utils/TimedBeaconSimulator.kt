package ru.cepprice.mybeacon.utils

import org.altbeacon.beacon.AltBeacon
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.simulator.BeaconSimulator
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class TimedBeaconSimulator(beaconCount: Int? = null, maxPeriod: Int = 3) : BeaconSimulator {

    private val beacons = ArrayList<Beacon>()
    private var scheduledTaskExecutor: ScheduledExecutorService

    private var mBeaconCount: Int = 5

    init {
        if (beaconCount != null && beaconCount > 0) mBeaconCount = beaconCount
        var counter = 0

        scheduledTaskExecutor = Executors.newScheduledThreadPool(mBeaconCount)
        scheduledTaskExecutor.scheduleAtFixedRate({
            if (counter++ < mBeaconCount) beacons.add(getRandomBeacon())
            else scheduledTaskExecutor.shutdown()
        }, 0, (2..maxPeriod).random().toLong(), TimeUnit.SECONDS)
    }

    private fun getRandomBeacon(): Beacon =
        AltBeacon.Builder()
            .setId1(Utils.generateRandomUuid())
            .setId2("1")
            .setId3("1")
            .setRssi((-100..-26).random())
            .setTxPower((-80..-69).random())
            .build()

    override fun getBeacons(): MutableList<Beacon> = beacons
}