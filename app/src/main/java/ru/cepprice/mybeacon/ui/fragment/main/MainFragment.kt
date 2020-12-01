package ru.cepprice.mybeacon.ui.fragment.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.cepprice.mybeacon.R
import ru.cepprice.mybeacon.databinding.FragmentMainBinding
import ru.cepprice.mybeacon.ui.fragment.beacons.BeaconListFragment
import ru.cepprice.mybeacon.ui.fragment.devices.DeviceListFragment
import ru.cepprice.mybeacon.utils.autoCleared
import ru.cepprice.mybeacon.utils.extension.hasGpsPermission

class MainFragment : Fragment() {

    private var binding: FragmentMainBinding by autoCleared()

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var KEY_IS_FIRST: Preferences.Key<Boolean>

    private var snackbar: Snackbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (hasGpsPermission()) {
            snackbar?.dismiss()
        }
        else {
            dataStore = requireContext().createDataStore(name = "launch")
            KEY_IS_FIRST = preferencesKey<Boolean>("is_first_launch")

            lifecycleScope.launch {
                val isFirstLaunch = readDataStore()
                if (isFirstLaunch) {
                    writeDataStore()
                    showAlertGpsRequired()
                } else requestGpsPermission()
            }
        }

        showTabLayout()

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
            showTabLayout()
        }
        else showErrorGpsPermissionNeeded()
    }

    private suspend fun readDataStore(): Boolean {
        val preference = dataStore.data.first()
        return preference[KEY_IS_FIRST] ?: true
    }

    private suspend fun writeDataStore() {
        dataStore.edit { isFirst -> isFirst[KEY_IS_FIRST] = false }
    }

    private fun showAlertGpsRequired() {
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.main_alert_title_gps_required))
                .setMessage(resources.getString(R.string.main_alert_message_gps_required))
                .setPositiveButton(resources.getString(R.string.btn_ok)) { _, _ ->
                    requestGpsPermission()
                }
                .show()
    }

    private fun hasGpsPermission(): Boolean = requireContext().hasGpsPermission()

    private fun requestGpsPermission() {
        val PERMISSION_REQUEST_FINE_LOCATION = 1
        requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_FINE_LOCATION)
    }

    private fun showTabLayout() {
        val adapter = ViewPagerAdapter(parentFragmentManager)
        adapter.addFragment(DeviceListFragment(), resources.getString(R.string.main_label_ble))
        adapter.addFragment(BeaconListFragment(), resources.getString(R.string.main_label_beacons))
        binding.viewPager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewPager)
    }

    private fun showErrorGpsPermissionNeeded() {
        snackbar = Snackbar.make(
            binding.viewPager,
            getString(R.string.main_snackbar_gps_required),
            Snackbar.LENGTH_INDEFINITE)
        snackbar?.show()
    }
}
