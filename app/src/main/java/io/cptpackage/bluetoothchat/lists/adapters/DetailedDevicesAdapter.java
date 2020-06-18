package io.cptpackage.bluetoothchat.lists.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.cptpackage.bluetoothchat.R;
import io.cptpackage.bluetoothchat.connection.BluetoothConnectionsManager;
import io.cptpackage.bluetoothchat.lists.viewholders.DetailedDeviceViewHolder;

public class DetailedDevicesAdapter extends RecyclerView.Adapter<DetailedDeviceViewHolder> {
    private List<BluetoothDevice> devices;
    private BluetoothConnectionsManager connectionsManager;

    public DetailedDevicesAdapter(Context context, List<BluetoothDevice> devices) {
        this.devices = devices;
        connectionsManager = BluetoothConnectionsManager.getInstance(context);
    }

    @NonNull
    @Override
    public DetailedDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_device, parent, false);
        return new DetailedDeviceViewHolder(view, connectionsManager);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailedDeviceViewHolder holder, int position) {
        BluetoothDevice device = devices.get(position);
        String deviceName = device.getName() != null ? device.getName() : device.getAddress();
        holder.setDeviceName(deviceName);
        holder.setDeviceAddress(device.getAddress());
        holder.setDevice(device);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }
}
