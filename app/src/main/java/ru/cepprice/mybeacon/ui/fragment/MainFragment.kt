package ru.cepprice.mybeacon.ui.fragment

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import ru.cepprice.mybeacon.R
import ru.cepprice.mybeacon.databinding.FragmentMainBinding
import ru.cepprice.mybeacon.ui.fragment.adapter.ViewPagerAdapter
import ru.cepprice.mybeacon.utils.autoCleared

private const val REQUEST_ENABLE_BT = 1

class MainFragment : Fragment() {

    private var binding: FragmentMainBinding by autoCleared()

    private lateinit var bluetoothAdapter: BluetoothAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeBluetoothAdapter()
        enableBluetoothIfNeeded()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != REQUEST_ENABLE_BT) {
            Log.d("M_MainFragment", "Got unknown request code")
            return
        }

        if (resultCode != Activity.RESULT_OK) {
            Log.d("M_MainFragment", "Bluetooth wan't turned on")
            // TODO Replace with snackbar
            Toast.makeText(requireContext(), "App won't work without bluetooth", Toast.LENGTH_SHORT).show()
        }

        Log.d("M_MainFragment", "Enabled bluetooth from intent")
        handlePermission()
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
        else showErrorMessage()
    }

    private fun initializeBluetoothAdapter() {
        val blManager =
                requireActivity().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = blManager.adapter
    }

    private fun enableBluetoothIfNeeded() {
        if (bluetoothAdapter.isEnabled) {
            Log.d("M_MainFragment", "Bluetooth already turned on")
            handlePermission()
            return
        }

        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT).also {
            Log.d("M_MainFragment", "Requesting bluetooth connection")
        }
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

    private fun showErrorMessage() {
        Log.d("M_MainFragment", "Showing error message")
        // TODO Replace with snackbar like in vk
        Toast.makeText(requireContext(), "No permission", Toast.LENGTH_SHORT).show()
    }
}

