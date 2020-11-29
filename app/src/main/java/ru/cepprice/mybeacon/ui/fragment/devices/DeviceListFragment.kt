package ru.cepprice.mybeacon.ui.fragment.devices

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ru.cepprice.mybeacon.R
import ru.cepprice.mybeacon.data.receiver.BluetoothStateChangeListener
import ru.cepprice.mybeacon.data.receiver.BluetoothStateChangeNotifier
import ru.cepprice.mybeacon.databinding.FragmentDeviceListBinding
import ru.cepprice.mybeacon.utils.autoCleared
import ru.cepprice.mybeacon.utils.extension.mapToDeviceView

class DeviceListFragment() : Fragment(), BluetoothStateChangeNotifier {

    private var binding: FragmentDeviceListBinding by autoCleared()

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var leScanCallback: ScanCallback
    private var bluetoothLeScanner: BluetoothLeScanner? = null

    private lateinit var receiver: BroadcastReceiver


    lateinit var deviceListAdapter: DeviceListAdapter
    private var snackbar: Snackbar? = null

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

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        setupScanCallback()
        registerReceiver()

        if (bluetoothAdapter.isEnabled) {
            bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        } else {
            onBluetoothDisabled()
        }

        setupRecyclerView()
    }

    override fun onBluetoothDisabled() {
        if (snackbar == null) {
            snackbar = Snackbar.make(
                binding.rvBleDevices,
                getString(R.string.message_main_turn_on_bluetooth),
                Snackbar.LENGTH_INDEFINITE)
        }

        if (!snackbar!!.isShown) snackbar!!.show()
    }

    override fun onBluetoothEnabled() {
        snackbar?.dismiss()
        if (bluetoothLeScanner == null) bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        if (bluetoothAdapter.isEnabled) startScanningForLe()
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
        if (bluetoothAdapter.isEnabled) startScanningForLe()
        else onBluetoothDisabled()
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(receiver)
        stopScanningForLe()
    }

    private fun registerReceiver() {
        receiver = BluetoothStateChangeListener(this)
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        requireActivity().registerReceiver(receiver, filter)
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

    private fun startScanningForLe() {
        bluetoothLeScanner?.startScan(leScanCallback)
        Log.d("M_DeviceListFragment", "Started scanning for BLE devices")
    }

    private fun stopScanningForLe() {
        bluetoothLeScanner?.stopScan(leScanCallback)
        Log.d("M_DeviceListFragment", "Stopped scanning for BLE devices")
    }

}