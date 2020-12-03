package ru.cepprice.mybeacon.ui.fragment.devices

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import ru.cepprice.mybeacon.R
import ru.cepprice.mybeacon.databinding.FragmentDeviceListBinding
import ru.cepprice.mybeacon.ui.fragment.base.ScanningFragment
import ru.cepprice.mybeacon.utils.autoCleared
import ru.cepprice.mybeacon.utils.extension.mapToDeviceView

class DeviceListFragment : ScanningFragment() {

    private var binding: FragmentDeviceListBinding by autoCleared()

    private lateinit var leScanCallback: ScanCallback

    private lateinit var deviceListAdapter: DeviceListAdapter

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

        setupRecyclerView()
        setupScanCallback()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_device_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_clear) deviceListAdapter.clear()

        return super.onOptionsItemSelected(item)
    }

    override fun startScanning() {
        bluetoothLeScanner?.startScan(leScanCallback)
    }

    override fun stopScanning() {}

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