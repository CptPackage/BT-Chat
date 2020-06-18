package io.cptpackage.bluetoothchat.lists.viewholders;

import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.cptpackage.bluetoothchat.R;
import io.cptpackage.bluetoothchat.connection.BluetoothConnectionsManager;

public class DetailedDeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final TextView deviceName;
    private final TextView deviceAddress;
    private BluetoothDevice device;
    private BluetoothConnectionsManager connectionsManager;

    public DetailedDeviceViewHolder(@NonNull View itemView, BluetoothConnectionsManager connectionsManager) {
        super(itemView);
        deviceName = itemView.findViewById(R.id.device_name);
        deviceAddress = itemView.findViewById(R.id.device_address);
        itemView.setOnClickListener(this);
        this.connectionsManager = connectionsManager;
    }

    public void setDeviceName(String name) {
        deviceName.setText(name);
    }

    public void setDeviceAddress(String address) {
        deviceAddress.setText(address);
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    @Override
    public void onClick(View view) {
        connectionsManager.attemptToConnect(device);
    }
}
