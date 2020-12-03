package ru.cepprice.mybeacon.ui.fragment.beacons

import android.content.*
import android.os.Bundle
import android.os.RemoteException
import android.view.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.altbeacon.beacon.*
import ru.cepprice.mybeacon.R
import ru.cepprice.mybeacon.databinding.FragmentBeaconListBinding
import ru.cepprice.mybeacon.ui.fragment.base.ScanningFragment
import ru.cepprice.mybeacon.ui.fragment.main.MainFragmentDirections
import ru.cepprice.mybeacon.utils.TimedBeaconSimulator
import ru.cepprice.mybeacon.utils.autoCleared
import ru.cepprice.mybeacon.utils.extension.component
import ru.cepprice.mybeacon.utils.extension.quickSort
import ru.cepprice.mybeacon.utils.extension.toBeaconView
import javax.inject.Inject

class BeaconListFragment : ScanningFragment(), BeaconConsumer, RangeNotifier {

    companion object {
        val region1 = Region("Region1", null, null, null)
    }

    @Inject lateinit var beaconManager: BeaconManager

    private var binding: FragmentBeaconListBinding by autoCleared()

    private lateinit var beaconListAdapter: BeaconListAdapter

    private var firstStart = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = FragmentBeaconListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        component().inject(this)
        setupRecyclerView()
        beaconManager.bind(this)
    }

    override fun onStop() {
        super.onStop()
        beaconManager.unbind(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_beacon_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun startScanning() {
        if (firstStart) {
            BeaconManager.setBeaconSimulator(TimedBeaconSimulator())
            firstStart = false
        }

        try {
            beaconManager.startMonitoringBeaconsInRegion(region1)
            beaconManager.startRangingBeaconsInRegion(region1)
        } catch (e: RemoteException) {}
    }

    override fun stopScanning() {
        beaconManager.stopMonitoringBeaconsInRegion(region1)
        beaconManager.stopRangingBeaconsInRegion(region1)
    }

    private fun setupRecyclerView() {
        beaconListAdapter = BeaconListAdapter{
            val action = MainFragmentDirections.actionMainFragmentToBeaconInfoDialog(arrayOf(
                it.uuid, it.major, it.minor, it.rssi, it.distance
            ))
            findNavController().navigate(action)
        }

        binding.rvBeacons.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBeacons.adapter = beaconListAdapter
    }

    override fun onBeaconServiceConnect() {
        beaconManager.addMonitorNotifier(object : MonitorNotifier {

            override fun didEnterRegion(p0: Region?) {
                beaconManager.removeRangeNotifier(this@BeaconListFragment)
                beaconManager.addRangeNotifier(this@BeaconListFragment)
            }

            override fun didExitRegion(p0: Region?) {
            }

            override fun didDetermineStateForRegion(p0: Int, p1: Region?) {
                beaconManager.removeRangeNotifier(this@BeaconListFragment)
                beaconManager.addRangeNotifier(this@BeaconListFragment)
            }
        })
    }

    override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>?, region: Region?) {
        if (beacons == null) return

        beaconListAdapter.updateBeacons(beacons.toList().quickSort().map { it.toBeaconView() })
    }

    override fun getApplicationContext(): Context = requireContext()

    override fun unbindService(p0: ServiceConnection) {
        requireContext().unbindService(p0)
    }

    override fun bindService(p0: Intent, p1: ServiceConnection, p2: Int): Boolean =
            requireContext().bindService(p0, p1, p2)

}