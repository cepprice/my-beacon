package ru.cepprice.mybeacon.ui.fragment

import android.Manifest
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

class MainFragment : Fragment() {

    private var binding: FragmentMainBinding by autoCleared()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (hasPermission()) showTabLayout().also { Log.d("M_MainActivity", "Already has permission") }
        else requestPermission()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty()) return

        val permission = grantResults[0]
        if (permission == PackageManager.PERMISSION_GRANTED) showTabLayout()
                .also { Log.d("M_MainActivity", "Got permission") }
        else showErrorMessage()
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
        Log.d("M_MainActivity", "Showing tab layout")
        val adapter = ViewPagerAdapter(parentFragmentManager)
        adapter.addFragment(DeviceListFragment(), resources.getString(R.string.main_label_ble))
        adapter.addFragment(BeaconListFragment(), resources.getString(R.string.main_label_beacons))
        binding.viewPager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewPager)
    }

    private fun showErrorMessage() {
        Log.d("M_MainActivity", "Showing error message")
        Toast.makeText(requireContext(), "No permission", Toast.LENGTH_SHORT).show()
    }
}

