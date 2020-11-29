package ru.cepprice.mybeacon.utils.extension

import org.altbeacon.beacon.Beacon
import ru.cepprice.mybeacon.data.local.BeaconView

fun Beacon.toBeaconView(): BeaconView = with(this) {
    BeaconView(
        uuid = id1.toString(),
        major = id2.toString(),
        minor = id3.toString(),
        rssi = "$rssi dBm",
        distance = "%.2f m".format(distance)
    )
}