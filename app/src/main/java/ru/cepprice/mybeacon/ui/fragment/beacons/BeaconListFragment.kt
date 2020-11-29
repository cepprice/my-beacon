package ru.cepprice.mybeacon.ui.fragment.beacons

import android.bluetooth.BluetoothAdapter
import android.content.*
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import org.altbeacon.beacon.*
import ru.cepprice.mybeacon.R
import ru.cepprice.mybeacon.data.receiver.BluetoothStateChangeListener
import ru.cepprice.mybeacon.data.receiver.BluetoothStateChangeNotifier
import ru.cepprice.mybeacon.databinding.FragmentBeaconListBinding
import ru.cepprice.mybeacon.ui.fragment.main.MainFragmentDirections
import ru.cepprice.mybeacon.utils.TimedBeaconSimulator
import ru.cepprice.mybeacon.utils.autoCleared
import ru.cepprice.mybeacon.utils.extension.quickSort
import ru.cepprice.mybeacon.utils.extension.toBeaconView

class BeaconListFragment : Fragment(), BeaconConsumer, RangeNotifier, BluetoothStateChangeNotifier {

    companion object {
        val region1 = Region("Region1", null, null, null)
    }

    private var binding: FragmentBeaconListBinding by autoCleared()

    private lateinit var beaconListAdapter: BeaconListAdapter
    private var snackbar: Snackbar? = null

    private lateinit var bluetoothAdapter: BluetoothAdapter

    private lateinit var beaconManager: BeaconManager
    private lateinit var receiver: BroadcastReceiver

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

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        registerReceiver()
        setupRecyclerView()
        setupBeaconManager()

        if (bluetoothAdapter.isEnabled) {
            startScanningForBeacons()
        } else {
            onBluetoothDisabled()
        }
    }

    override fun onStop() {
        super.onStop()
        stopScanningForBeacons()
        requireActivity().unregisterReceiver(receiver)
        beaconManager.unbind(this)
    }

    override fun onBluetoothDisabled() {
        stopScanningForBeacons()

        if (snackbar == null) {
            snackbar = Snackbar.make(
                binding.rvBeacons,
                getString(R.string.message_main_turn_on_bluetooth),
                Snackbar.LENGTH_INDEFINITE)
        }

        if (!snackbar!!.isShown) snackbar!!.show()
    }

    override fun onBluetoothEnabled() {
        snackbar?.dismiss()
        startScanningForBeacons()
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

    private fun setupBeaconManager() {
        BeaconManager.setBeaconSimulator(TimedBeaconSimulator())
        beaconManager = BeaconManager.getInstanceForApplication(requireContext())

        beaconManager.beaconParsers
            .add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))
        beaconManager.beaconParsers
            .add(BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT))
        beaconManager.beaconParsers
            .add(BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT))
        beaconManager.beaconParsers
            .add(BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT))
        beaconManager.beaconParsers
            .add(BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT))

        beaconManager.bind(this)
    }

    private fun registerReceiver() {
        receiver = BluetoothStateChangeListener(this)
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        requireActivity().registerReceiver(receiver, filter)
    }

    private fun startScanningForBeacons() {
        try {
            beaconManager.startMonitoringBeaconsInRegion(region1)
            beaconManager.startRangingBeaconsInRegion(region1)
        } catch (e: RemoteException) {}
    }

    private fun stopScanningForBeacons() {
        beaconManager.stopMonitoringBeaconsInRegion(region1)
        beaconManager.stopRangingBeaconsInRegion(region1)
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
            }

            override fun didDetermineStateForRegion(p0: Int, p1: Region?) {
                Log.d("M_BeaconListFragment", "Determined: ${p1?.uniqueId}")
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