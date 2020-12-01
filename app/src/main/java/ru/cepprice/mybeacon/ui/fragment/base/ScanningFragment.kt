package ru.cepprice.mybeacon.ui.fragment.base

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import ru.cepprice.mybeacon.R
import ru.cepprice.mybeacon.data.receiver.BluetoothStateChangeListener
import ru.cepprice.mybeacon.data.receiver.BluetoothStateChangeNotifier
import ru.cepprice.mybeacon.data.receiver.GpsStateChangeNotifier
import ru.cepprice.mybeacon.data.receiver.GpsStateChangeReceiver
import ru.cepprice.mybeacon.utils.extension.hasGpsPermission
import ru.cepprice.mybeacon.utils.extension.isGpsEnabled

abstract class ScanningFragment : Fragment(),
    BluetoothStateChangeNotifier, GpsStateChangeNotifier  {

    private lateinit var bluetoothReceiver: BroadcastReceiver
    private lateinit var gpsReceiver: BroadcastReceiver
    private lateinit var bluetoothSnackbar: Snackbar
    private lateinit var gpsSnackbar: Snackbar

    protected lateinit var bluetoothAdapter: BluetoothAdapter

    protected abstract fun startScanning()
    protected abstract fun stopScanning()

    protected fun isBluetoothEnabled(): Boolean = bluetoothAdapter.isEnabled

    protected fun toggleBluetooth() {
        if (isBluetoothEnabled()) bluetoothAdapter.disable()
        else bluetoothAdapter.enable()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        initSnackbars()
    }

    override fun onStart() {
        super.onStart()

        registerReceivers()

        if (!isBluetoothEnabled()) {
            showErrorBtRequired()
            return
        }

        if (!isGpsEnabled()) {
            showErrorGpsRequired()
            return
        }

        startScanning()
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(bluetoothReceiver)
        requireActivity().unregisterReceiver(gpsReceiver)
        stopScanning()
    }

    override fun onBluetoothDisabled() {
        stopScanning()
        if (!gpsSnackbar.isShown) {
            showErrorBtRequired()
        }
    }

    override fun onBluetoothEnabled() {
        bluetoothSnackbar.dismiss()
        if (isGpsEnabled()) startScanning()
        else showErrorGpsRequired()
    }

    override fun onGpsDisabled() {
        stopScanning()
        if (!bluetoothSnackbar.isShown) {
            showErrorGpsRequired()
        }
    }

    override fun onGpsEnabled() {
        gpsSnackbar.dismiss()
        if (isBluetoothEnabled()) startScanning()
        else showErrorBtRequired()
    }

    private fun registerReceivers() {
        bluetoothReceiver = BluetoothStateChangeListener(this)
        val bluetoothFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        requireActivity().registerReceiver(bluetoothReceiver, bluetoothFilter)

        gpsReceiver = GpsStateChangeReceiver(this)
        val gpsFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        requireActivity().registerReceiver(gpsReceiver, gpsFilter)
    }

    private fun initSnackbars() {
        bluetoothSnackbar = Snackbar.make(
            requireView(), getString(R.string.message_turn_on_bluetooth),
            Snackbar.LENGTH_INDEFINITE
        )

        gpsSnackbar = Snackbar.make(
            requireView(), getString(R.string.message_turn_on_gps),
            Snackbar.LENGTH_INDEFINITE
        )
    }

    private fun isGpsEnabled(): Boolean = requireContext().isGpsEnabled()

    private fun showErrorBtRequired() {
        if (requireContext().hasGpsPermission()) bluetoothSnackbar.show()
    }

    private fun showErrorGpsRequired() {
        if (requireContext().hasGpsPermission()) gpsSnackbar.show()
    }

}