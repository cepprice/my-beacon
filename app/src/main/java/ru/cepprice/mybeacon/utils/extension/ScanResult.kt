package ru.cepprice.mybeacon.utils.extension

import android.bluetooth.le.ScanResult
import ru.cepprice.mybeacon.data.DeviceView

fun ScanResult.mapToDeviceView(): DeviceView {
    return DeviceView(this.device.name, this.device.address, "${this.rssi} dBm")
}