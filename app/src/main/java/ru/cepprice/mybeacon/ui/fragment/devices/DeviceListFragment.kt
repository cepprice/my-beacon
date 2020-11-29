package ru.cepprice.mybeacon.ui.fragment.devices

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ru.cepprice.mybeacon.R
import ru.cepprice.mybeacon.databinding.FragmentDeviceListBinding
import ru.cepprice.mybeacon.utils.autoCleared
import ru.cepprice.mybeacon.utils.extension.mapToDeviceView

class DeviceListFragment() : Fragment() {

    private var binding: FragmentDeviceListBinding by autoCleared()

    private val bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner

    lateinit var deviceListAdapter: DeviceListAdapter

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
        setHasOptionsMenu(true)
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_device_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_clear -> deviceListAdapter.clear()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        startScanningForLe()
    }

    override fun onStop() {
        super.onStop()
        stopScanningForLe()
    }

    private fun startScanningForLe() {
        bluetoothLeScanner.startScan(leScanCallback)
        Log.d("M_DeviceListFragment", "Started scanning for BLE devices")
    }

    private fun stopScanningForLe() {
        bluetoothLeScanner.stopScan(leScanCallback)
        Log.d("M_DeviceListFragment", "Stopped scanning for BLE devices")
    }

}