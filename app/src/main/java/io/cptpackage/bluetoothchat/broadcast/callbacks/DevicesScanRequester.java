package io.cptpackage.bluetoothchat.broadcast.callbacks;

import android.bluetooth.BluetoothDevice;

public interface DevicesScanRequester {
    void notifyNewDeviceFound(BluetoothDevice foundDevice);
    void notifyNoDevicesFound();
}
