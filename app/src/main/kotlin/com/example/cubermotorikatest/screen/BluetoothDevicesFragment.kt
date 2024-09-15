package com.example.cubermotorikatest.screen

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.cubermotorikatest.R
import com.example.cubermotorikatest.databinding.FragmentBluetoothDevicesBinding

class BluetoothDevicesFragment : Fragment(R.layout.fragment_bluetooth_devices) {

    private var _binding: FragmentBluetoothDevicesBinding? = null
    private val binding get() = _binding!!
    private val adapter = BluetoothDevicesAdapter()
    private val listDevices = mutableSetOf<String>()

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        // Handle if rejected by user
    }
    private val requestEnableBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // Handle if rejected by user
    }

    private var callback: ScanCallback? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBluetoothDevicesBinding.inflate(inflater, container, false)
        bindUI()
        requestPermissions()
        return binding.root
    }

    private fun bindUI() {
        binding.recycler.adapter = adapter
        binding.scan.setOnClickListener {
            listDevices.clear()
            adapter.submitList(listDevices.toList())
            turnBluetoothAndStartScan()
        }
        binding.stopScan.setOnClickListener {
            stopScan()
        }
    }

    private fun requestPermissions() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun turnBluetoothAndStartScan() {
        if (bluetoothAdapter.isEnabled) {
            startScan()
        } else {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestEnableBluetooth.launch(enableBtIntent)
        }
    }

    private fun startScan() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            val scanSettings = ScanSettings.Builder().setScanMode(SCAN_MODE_LOW_LATENCY).build()
            callback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    super.onScanResult(callbackType, result)
                    val bluetoothDevice = result?.device
                    if (bluetoothDevice != null) {
                        try {
                            val name = if (bluetoothDevice.name == null) {
                                bluetoothDevice.address
                            } else {
                                bluetoothDevice.name
                            }
                            listDevices.add(name)
                            adapter.submitList(listDevices.toList())
                        } catch (e: SecurityException) {
                            Log.d("TAG", "Exception scanResult $e")
                        }
                    }
                }
            }
            bluetoothAdapter.bluetoothLeScanner?.startScan(null, scanSettings, callback)
        }
    }

    private fun stopScan() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
            bluetoothAdapter.bluetoothLeScanner.stopScan(callback)
        }
    }
}
