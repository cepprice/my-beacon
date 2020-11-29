package ru.cepprice.mybeacon.utils.extension

import android.bluetooth.le.ScanResult
import ru.cepprice.mybeacon.data.local.DeviceView

fun ScanResult.mapToDeviceView(): DeviceView {
    val name = this.device.name.let {
        if (it != null) {
            return@let it
        }

        val manufacturerData = this.scanRecord?.manufacturerSpecificData
        val bytes = manufacturerData?.get(6) ?: return@let "None"
        val reg = Regex("[a-zA-z0-9-]+")
        val lastInvalid = bytes.indexOfLast { byte ->  !reg.containsMatchIn(byte.toChar().toString()) }
        return@let bytes.decodeToString(lastInvalid + 1)
    }

    return DeviceView(name, this.device.address, "${this.rssi} dBm")
}