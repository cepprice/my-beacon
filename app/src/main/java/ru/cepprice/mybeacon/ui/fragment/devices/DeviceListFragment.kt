package ru.cepprice.mybeacon.ui.fragment.devices

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ru.cepprice.mybeacon.databinding.FragmentDeviceListBinding
import ru.cepprice.mybeacon.utils.autoCleared
import ru.cepprice.mybeacon.utils.extension.mapToDeviceView

class DeviceListFragment : Fragment() {

    private var binding: FragmentDeviceListBinding by autoCleared()

    private val bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
    private var mScanning = false

    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000

    private lateinit var deviceListAdapter: DeviceListAdapter


    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            deviceListAdapter.addDevice(result.mapToDeviceView())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeviceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deviceListAdapter = DeviceListAdapter()
        with(binding.rvBleDevices) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = deviceListAdapter
        }
        scanLeDevice()

    }

    private fun scanLeDevice() {
        if (!mScanning) {
            Handler(Looper.getMainLooper()).postDelayed({
                mScanning = false
                bluetoothLeScanner.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            mScanning = true
            Log.d("M_DeviceListFragment", "Started scanning for BLE Devices")
            bluetoothLeScanner.startScan(leScanCallback)
        } else {
            mScanning = false
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }

}