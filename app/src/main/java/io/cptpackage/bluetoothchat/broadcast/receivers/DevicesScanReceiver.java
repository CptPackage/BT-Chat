package io.cptpackage.bluetoothchat.broadcast.receivers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.HashSet;
import java.util.Set;

import io.cptpackage.bluetoothchat.broadcast.callbacks.DevicesScanRequester;

public class DevicesScanReceiver extends BroadcastReceiver implements FiltersContainingReceiver {
    private DevicesScanRequester devicesScanRequester;
    private Set<BluetoothDevice> devicesFound;


    public DevicesScanReceiver(DevicesScanRequester devicesScanRequester) {
        this.devicesScanRequester = devicesScanRequester;
        this.devicesFound = new HashSet<>();
    }

    @Override
    public IntentFilter[] getIntentFilters() {
        IntentFilter newDeviceFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter devicesDiscoveryFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        return new IntentFilter[]{newDeviceFoundFilter, devicesDiscoveryFinishedFilter};
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        // When discovery finds a device
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Get the BluetoothDevice object from the Intent
            BluetoothDevice foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // If it's already paired, skip it, because it's been listed already
            if (foundDevice != null && foundDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                if (!devicesFound.contains(foundDevice)) {
                    devicesFound.add(foundDevice);
                    devicesScanRequester.notifyNewDeviceFound(foundDevice);
                }
            }
            // When discovery is finished, update the UI to the new devices or none found!
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            if (devicesFound.size() == 0) {
                devicesScanRequester.notifyNoDevicesFound();
            }
        }
    }
}
