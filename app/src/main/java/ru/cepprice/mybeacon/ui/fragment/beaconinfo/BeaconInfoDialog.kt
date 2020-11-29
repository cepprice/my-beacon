package ru.cepprice.mybeacon.ui.fragment.beaconinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.cepprice.mybeacon.data.local.BeaconView
import ru.cepprice.mybeacon.databinding.DialogBeaconInfoBinding
import ru.cepprice.mybeacon.utils.autoCleared

class BeaconInfoDialog : BottomSheetDialogFragment() {

    private val args: BeaconInfoDialogArgs by navArgs()
    private var binding: DialogBeaconInfoBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBeaconInfoBinding.inflate(inflater, container, false)
        with(args) {
            binding.beacon = BeaconView(
                uuid = stringBeacon[0],
                major = stringBeacon[1],
                minor = stringBeacon[2],
                rssi = stringBeacon[3],
                distance = stringBeacon[4]
            )
        }
        return binding.root
    }


}