package ru.cepprice.mybeacon.ui.fragment.main

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import ru.cepprice.mybeacon.R
import ru.cepprice.mybeacon.databinding.FragmentMainBinding
import ru.cepprice.mybeacon.ui.fragment.beacons.BeaconListFragment
import ru.cepprice.mybeacon.ui.fragment.devices.DeviceListFragment
import ru.cepprice.mybeacon.utils.autoCleared

class MainFragment : Fragment() {

    private var binding: FragmentMainBinding by autoCleared()

    private lateinit var bluetoothAdapter: BluetoothAdapter

    private val REQUEST_ENABLE_BT = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showTabLayout()
        initializeBluetoothAdapter()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty()) return

        val permission = grantResults[0]
        if (permission == PackageManager.PERMISSION_GRANTED) {
            Log.d("M_MainFragment", "Got permission")
            showTabLayout()
        }
        else showErrorPermissionNeeded()
    }

    private fun initializeBluetoothAdapter() {
        val blManager =
                requireActivity().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = blManager.adapter
    }

    private fun handlePermission() {
        if (hasPermission()) {
            Log.d("M_MainFragment", "Already has permission")
            showTabLayout()
        }
        else requestPermission()
    }

    private fun hasPermission(): Boolean =
            (checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)

    private fun requestPermission() {
        val PERMISSION_REQUEST_FINE_LOCATION = 1
        requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_FINE_LOCATION)
    }

    private fun showTabLayout() {
        Log.d("M_MainFragment", "Showing tab layout")
        val adapter = ViewPagerAdapter(parentFragmentManager)
        adapter.addFragment(DeviceListFragment(), resources.getString(R.string.main_label_ble))
        adapter.addFragment(BeaconListFragment(), resources.getString(R.string.main_label_beacons))
        binding.viewPager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewPager)
    }

    private fun showErrorPermissionNeeded() {
        Log.d("M_MainFragment", "Showing error message")
        // TODO Replace with snackbar like in vk
        Toast.makeText(requireContext(), "No permission", Toast.LENGTH_SHORT).show()
    }
}
