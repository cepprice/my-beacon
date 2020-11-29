package ru.cepprice.mybeacon.utils

import org.altbeacon.beacon.Beacon

object Utils {

    fun generateRandomUuid(): String =
        "${randomHexFourDigitNumber()}${randomHexFourDigitNumber()}-" +
                "${randomHexFourDigitNumber()}-" +
                "${randomHexFourDigitNumber()}-" +
                "${randomHexFourDigitNumber()}-" +
                "${randomHexFourDigitNumber()}${randomHexFourDigitNumber()}${randomHexFourDigitNumber()}"

    fun getLostBeacons(prev: MutableCollection<Beacon>, curr: MutableCollection<Beacon>)
    : List<Beacon> = prev.filterNot { prevBeacon -> curr
        .map { it.id1 to it.id2 to it.id3 }
        .contains(prevBeacon.id1 to prevBeacon.id2 to prevBeacon.id3)
    }

    fun getNewBeacons(prev: MutableCollection<Beacon>, curr: MutableCollection<Beacon>)
    : List<Beacon> = curr.filterNot { currBeacon -> prev
        .map { it.id1 to it.id2 to it.id3 }
        .contains(currBeacon.id1 to currBeacon.id2 to currBeacon.id3)
    }

    fun addBeaconSavingOrder(
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

    private fun randomHexDigit(): String {
        val chars = "1234567890ABCDEF"
        return chars[(0..15).random()].toString()
    }

    private fun randomHexFourDigitNumber(): String =
        randomHexDigit()
            .plus(randomHexDigit())
            .plus(randomHexDigit())
            .plus(randomHexDigit())

}