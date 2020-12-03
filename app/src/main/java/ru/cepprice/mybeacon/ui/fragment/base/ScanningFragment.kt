package ru.cepprice.mybeacon.ui.fragment.base

import android.bluetooth.le.BluetoothLeScanner
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.cepprice.mybeacon.R
import ru.cepprice.mybeacon.utils.BluetoothManager
import ru.cepprice.mybeacon.utils.GpsManager
import ru.cepprice.mybeacon.utils.extension.component
import ru.cepprice.mybeacon.utils.extension.hasGpsPermission
import javax.inject.Inject

abstract class ScanningFragment : Fragment()  {

    @Inject lateinit var bluetoothManager: BluetoothManager
    @Inject lateinit var gpsManager: GpsManager

    protected var bluetoothLeScanner: BluetoothLeScanner? = null

    private lateinit var bluetoothSnackbar: Snackbar
    private lateinit var gpsSnackbar: Snackbar

    private var gpsStateDisposable: Disposable? = null
    private var bluetoothStateDisposable: Disposable? = null

    private var menu: Menu? = null


    protected abstract fun startScanning()
    protected abstract fun stopScanning()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        component().inject(this)
        initializeSnackbars()
    }

    override fun onResume() {
        super.onResume()
        observeBluetoothState()
        observeGpsState()
    }

    override fun onStop() {
        super.onStop()
        bluetoothManager.unregisterReceiver()
        gpsManager.unregisterReceiver()
        stopScanning()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_toggle_bluetooth) bluetoothManager.toggle()

        return super.onOptionsItemSelected(item)
    }

    private fun observeGpsState() {
        gpsStateDisposable?.dispose()
        gpsStateDisposable = gpsManager.asFlowable()
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe { isEnabled ->
                if (isEnabled) {
                    gpsSnackbar.dismiss()
                    if (bluetoothManager.isEnabled()) startScanning()
                    else showErrorBtRequired()
                } else {
                    stopScanning()
                    if (!bluetoothSnackbar.isShown) showErrorGpsRequired()
                }
            }
    }

    private fun observeBluetoothState() {
        bluetoothStateDisposable = bluetoothManager.asFlowable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isEnabled ->

                updateBluetoothIcon(bluetoothManager.isEnabled())

                if (isEnabled) {
                    bluetoothSnackbar.dismiss()

                    if (bluetoothLeScanner == null) {
                        bluetoothLeScanner = component().provideBluetoothLeScanner()
                    }

                    if (gpsManager.isEnabled()) startScanning()
                    else showErrorGpsRequired()
                } else {
                    stopScanning()
                    if (!gpsSnackbar.isShown) showErrorBtRequired()
                }
            }
    }

    private fun updateBluetoothIcon(enabled: Boolean) {
        val iconId =
            if (enabled) R.drawable.ic_bluetooth_off
            else R.drawable.ic_bluetooth_on
        val icon = AppCompatResources.getDrawable(requireContext(), iconId)?.mutate()

        menu?.findItem(R.id.action_toggle_bluetooth)?.icon = icon
    }

    private fun initializeSnackbars() {
        bluetoothSnackbar = Snackbar.make(
            requireView(), getString(R.string.message_turn_on_bluetooth),
            Snackbar.LENGTH_INDEFINITE
        ).setAction("OK") {
                bluetoothSnackbar.dismiss()
            }

        gpsSnackbar = Snackbar.make(
            requireView(), getString(R.string.message_turn_on_gps),
            Snackbar.LENGTH_INDEFINITE
        ).setAction("OK") {
            gpsSnackbar.dismiss()
        }
    }

    private fun showErrorBtRequired() {
        if (requireContext().hasGpsPermission()) bluetoothSnackbar.show()
    }

    private fun showErrorGpsRequired() {
        if (requireContext().hasGpsPermission()) gpsSnackbar.show()
    }

}