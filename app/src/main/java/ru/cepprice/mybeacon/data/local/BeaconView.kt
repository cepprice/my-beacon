package ru.cepprice.mybeacon.data.local

data class BeaconView(
    val uuid: String,
    val major: String,
    val minor: String,
    val rssi: String,
    val distance: String
)
