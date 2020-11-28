package ru.cepprice.mybeacon.ui.fragment.beacons

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.cepprice.mybeacon.data.BeaconView
import ru.cepprice.mybeacon.databinding.ListItemBeaconBinding

class BeaconListAdapter(
    private val lister: (BeaconView) -> Unit
) : RecyclerView.Adapter<BeaconListAdapter.BeaconViewHolder>() {

    private val beacons = ArrayList<BeaconView>()

    class BeaconViewHolder(val binding: ListItemBeaconBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeaconViewHolder {
        val binding = ListItemBeaconBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return BeaconViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BeaconViewHolder, position: Int) {
        val currentBeacon = beacons[position]
        with(holder.binding) {
            beacon = currentBeacon
            cardContainer.setOnClickListener { lister(currentBeacon) }
            executePendingBindings()
        }
    }

    override fun getItemCount(): Int = beacons.size
}