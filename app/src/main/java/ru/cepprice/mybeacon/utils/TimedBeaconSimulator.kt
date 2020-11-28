package ru.cepprice.mybeacon.utils

import org.altbeacon.beacon.AltBeacon
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.simulator.BeaconSimulator

class TimedBeaconSimulator : BeaconSimulator {

    private val beacons = ArrayList<Beacon>()

    init {
        repeat(5) {
            beacons.add(getRandomBeacon())
        }
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