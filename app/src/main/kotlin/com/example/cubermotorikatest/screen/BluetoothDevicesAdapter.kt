package com.example.cubermotorikatest.screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cubermotorikatest.databinding.ItemBluetoothDeviceBinding
import com.example.cubermotorikatest.screen.BluetoothDevicesAdapter.VH

class BluetoothDevicesAdapter : ListAdapter<String, VH>(Differ) {

    class VH(private val binding: ItemBluetoothDeviceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bluetoothDevice: String) {
            binding.name.text = bluetoothDevice
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflate = LayoutInflater.from(parent.context)
        val binding = ItemBluetoothDeviceBinding.inflate(inflate, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    object Differ : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}
