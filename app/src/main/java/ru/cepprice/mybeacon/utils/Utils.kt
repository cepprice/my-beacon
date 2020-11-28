package ru.cepprice.mybeacon.utils

import org.altbeacon.beacon.Beacon

object Utils {

    fun getNewBeacons(prev: MutableCollection<Beacon>, curr: MutableCollection<Beacon>)
    : List<Beacon> = curr.filter { currBeacon -> !prev
                .map { it.id1 to it.id2 to it.id3 }
                .contains(currBeacon.id1 to currBeacon.id2 to currBeacon.id3)
    }

    fun addSavingOrder(
        srcBeacons: ArrayList<Beacon>,
        newBeacons: List<Beacon>
    ): ArrayList<Beacon> {
        if (srcBeacons.isEmpty()) {
            srcBeacons.addAll(newBeacons)
            return srcBeacons
        }

        newBeacons.forEach { newBeacon ->
            var counter = 0
            val newDist = newBeacon.distance
            while (counter < srcBeacons.size && newDist > srcBeacons[counter].distance) {
                counter++
            }
            srcBeacons.add(counter, newBeacon)
        }
        return srcBeacons
    }

}