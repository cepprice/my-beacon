package ru.cepprice.mybeacon.ui.fragment.beacons

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.altbeacon.beacon.*
import ru.cepprice.mybeacon.databinding.FragmentBeaconListBinding
import ru.cepprice.mybeacon.utils.TimedBeaconSimulator
import ru.cepprice.mybeacon.utils.Utils
import ru.cepprice.mybeacon.utils.autoCleared

class BeaconListFragment : Fragment(), BeaconConsumer, RangeNotifier {

    companion object {
        val region1 = Region("Region1", null, null, null)
    }


    private var binding: FragmentBeaconListBinding by autoCleared()

    private lateinit var beaconManager: BeaconManager

    private var prevBeacons: MutableCollection<Beacon> = mutableListOf()
    private var beaconsForAdapter = ArrayList<Beacon>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBeaconListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BeaconManager.setBeaconSimulator(TimedBeaconSimulator())
        beaconManager = BeaconManager.getInstanceForApplication(requireContext())
//        beaconManager.beaconParsers
//            .add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))
        beaconManager.bind(this)
    }

    override fun onStop() {
        super.onStop()
        beaconManager.unbind(this)
    }

    override fun onBeaconServiceConnect() {
        beaconManager.addMonitorNotifier(object : MonitorNotifier {

            override fun didEnterRegion(p0: Region?) {
                Log.d("M_BeaconListFragment", "Entered: ${p0?.uniqueId}")
                beaconManager.removeRangeNotifier(this@BeaconListFragment)
                beaconManager.addRangeNotifier(this@BeaconListFragment)
                p0.let { beaconManager.startRangingBeaconsInRegion(region1) }
            }

            override fun didExitRegion(p0: Region?) {
                Log.d("M_BeaconListFragment", "Exited: ${p0?.id1}")
                // TODO Remove item from adapter
            }

            override fun didDetermineStateForRegion(p0: Int, p1: Region?) {
                Log.d("M_BeaconListFragment", "Determined: ${p1?.uniqueId}")
                beaconManager.removeRangeNotifier(this@BeaconListFragment)
                beaconManager.addRangeNotifier(this@BeaconListFragment)
            }
        })

        try {
            beaconManager.startMonitoringBeaconsInRegion(region1)
            beaconManager.startRangingBeaconsInRegion(region1)
        } catch (e: RemoteException) {}
    }

    override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>?, region: Region?) {
        if (beacons == null) return
        Log.d("M_BeaconListFragment",
            "Beacons: ${beacons.map { beacons.indexOf(it).toString().plus(": ${it.distance}") }}")

        val newBeacons = Utils.getNewBeacons(prevBeacons, beacons)
        if (newBeacons.isNotEmpty()) {
            beaconsForAdapter = Utils.addSavingOrder(beaconsForAdapter, newBeacons)
        }
        Log.d("M_BeaconListFragment", "\nSorted:")
        beaconsForAdapter.forEach{
            Log.d("M_BeaconListFragment", "%.2f".format(it.distance))
        }
        Log.d("M_BeaconListFragment", "\n")

        prevBeacons.clear()
        prevBeacons.addAll(beacons)
    }

    override fun getApplicationContext(): Context = requireContext()

    override fun unbindService(p0: ServiceConnection) {
        requireContext().unbindService(p0)
    }

    override fun bindService(p0: Intent, p1: ServiceConnection, p2: Int): Boolean =
            requireContext().bindService(p0, p1, p2)

}