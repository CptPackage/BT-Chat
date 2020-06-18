package io.cptpackage.bluetoothchat.broadcast.receivers;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;


import io.cptpackage.bluetoothchat.R;
import io.cptpackage.bluetoothchat.broadcast.callbacks.DeviceConnectionChangeRequester;
import io.cptpackage.bluetoothchat.connection.BluetoothConnectionsManager;
import io.cptpackage.bluetoothchat.db.entities.Device;
import io.cptpackage.bluetoothchat.db.repositories.implementation.DevicesRepositoryImpl;
import io.cptpackage.bluetoothchat.db.repositories.interfaces.DevicesRepository;

import static io.cptpackage.bluetoothchat.broadcast.BroadcastConstants.CONNECTED;
import static io.cptpackage.bluetoothchat.broadcast.BroadcastConstants.CONNECTING;
import static io.cptpackage.bluetoothchat.broadcast.BroadcastConstants.CONNECTION_CHANGE_BROADCAST;
import static io.cptpackage.bluetoothchat.broadcast.BroadcastConstants.DISCONNECTED;
import static io.cptpackage.bluetoothchat.broadcast.BroadcastConstants.KEY_CONNECTED_DEVICE_ADDRESS;
import static io.cptpackage.bluetoothchat.broadcast.BroadcastConstants.KEY_CONNECTED_DEVICE_NAME;
import static io.cptpackage.bluetoothchat.broadcast.BroadcastConstants.KEY_CONNECTION_STATE;

public class DeviceConnectionChangeReceiver extends BroadcastReceiver implements FiltersContainingReceiver {
    private DeviceConnectionChangeRequester requester;
    private BluetoothDevice connectedDevice;
    private DevicesRepository<Device> devicesRepository;

    public DeviceConnectionChangeReceiver() {}

    public DeviceConnectionChangeReceiver(DeviceConnectionChangeRequester requester) {
        this.requester = requester;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (CONNECTION_CHANGE_BROADCAST.equals(action)) {
            int connectionState = intent.getIntExtra(KEY_CONNECTION_STATE, -1);
            switch (connectionState) {
                case CONNECTING:
                    Toast.makeText(context, R.string.toast_attempt_to_connect, Toast.LENGTH_SHORT).show();
                    if (requester != null) {
                        requester.notifyDeviceConnecting();
                    }
                    break;
                case CONNECTED:
                    initRepo(context);
                    connectedDevice = BluetoothConnectionsManager.getInstance(context).getConnectedDevice();
                    regulateDeviceInDB();
                    String deviceName = intent.getStringExtra(KEY_CONNECTED_DEVICE_NAME);
                    String deviceAddress = intent.getStringExtra(KEY_CONNECTED_DEVICE_ADDRESS);
                    String connectionToast = context.getString(R.string.toast_connected,deviceName);
                    Toast.makeText(context, connectionToast, Toast.LENGTH_SHORT).show();
                    if (requester != null && deviceName != null && deviceAddress != null) {
                        requester.notifyDeviceConnected(deviceName, deviceAddress);
                    }
                    break;
                case DISCONNECTED:
                    Toast.makeText(context, R.string.toast_disconnected, Toast.LENGTH_SHORT).show();
                    if (requester != null) {
                        requester.notifyDeviceDisconnected();
                    }
                    break;
            }
        }
    }

    private void initRepo(Context context){
        if(devicesRepository == null){
            devicesRepository = DevicesRepositoryImpl.getInstance(context);
        }
    }

    private void regulateDeviceInDB(){
        if(connectedDevice != null){
            Device device = new Device(connectedDevice);
            boolean exists = devicesRepository.exist(device);
            if (!exists) {
                devicesRepository.addDevice(device);
            }
        }
    }

    @Override
    public IntentFilter[] getIntentFilters() {
        return new IntentFilter[]{new IntentFilter(CONNECTION_CHANGE_BROADCAST)};
    }
}
