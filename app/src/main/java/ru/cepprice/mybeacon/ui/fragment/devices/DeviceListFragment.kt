package ru.cepprice.mybeacon.ui.fragment.devices

import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import ru.cepprice.mybeacon.R
import ru.cepprice.mybeacon.databinding.FragmentDeviceListBinding
import ru.cepprice.mybeacon.ui.fragment.base.ScanningFragment
import ru.cepprice.mybeacon.utils.autoCleared
import ru.cepprice.mybeacon.utils.extension.mapToDeviceView

class DeviceListFragment : ScanningFragment() {

    private var binding: FragmentDeviceListBinding by autoCleared()

    private lateinit var leScanCallback: ScanCallback
    private var bluetoothLeScanner: BluetoothLeScanner? = null

    private lateinit var deviceListAdapter: DeviceListAdapter
    private var menu: Menu? = null

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

        if (isBluetoothEnabled()) bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        setupRecyclerView()
        setupScanCallback()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_device_list, menu)
        this.menu = menu
        if (isBluetoothEnabled()) updateBluetoothIcon(true)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_clear -> deviceListAdapter.clear()
            R.id.action_toggle_bluetooth -> toggleBluetooth()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun startScanning() {
        bluetoothLeScanner?.startScan(leScanCallback)
    }

    override fun stopScanning() {}

    override fun onBluetoothEnabled() {
        if (bluetoothLeScanner == null) bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        updateBluetoothIcon(true)
        super.onBluetoothEnabled()
    }

    override fun onBluetoothDisabled() {
        updateBluetoothIcon(false)
        super.onBluetoothDisabled()
    }

    private fun updateBluetoothIcon(enabled: Boolean) {
        val iconId =
                if (enabled) R.drawable.ic_bluetooth_off
                else R.drawable.ic_bluetooth_on
        val icon = AppCompatResources.getDrawable(requireContext(), iconId)?.mutate()

        menu?.findItem(R.id.action_toggle_bluetooth)?.icon = icon
    }

    private fun setupRecyclerView() {
        deviceListAdapter = DeviceListAdapter()
        with(binding.rvBleDevices) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = deviceListAdapter
        }
    }

    private fun setupScanCallback() {
        leScanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                deviceListAdapter.addDevice(result.mapToDeviceView())
            }
        }
    }

}