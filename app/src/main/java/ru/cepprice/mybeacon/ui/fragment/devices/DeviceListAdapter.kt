package ru.cepprice.mybeacon.ui.fragment.devices

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.cepprice.mybeacon.data.local.DeviceView
import ru.cepprice.mybeacon.databinding.ListItemDeviceBinding

class DeviceListAdapter : RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder>() {

    private val devices = ArrayList<DeviceView>()

    fun addDevice(device: DeviceView) {
        if (!devices.map { it.mac }.contains(device.mac)) {
            devices.add(device)
            notifyDataSetChanged()
        }
    }

    fun clear() {
        devices.clear()
        notifyDataSetChanged()
    }

    class DeviceViewHolder(val binding: ListItemDeviceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val binding = ListItemDeviceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        return DeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val currentDevice = devices[position]
        holder.binding.device = currentDevice
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = devices.size
}