package io.cptpackage.bluetoothchat.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.cptpackage.bluetoothchat.R;
import io.cptpackage.bluetoothchat.broadcast.callbacks.DeviceConnectionChangeRequester;
import io.cptpackage.bluetoothchat.broadcast.callbacks.DevicesScanRequester;
import io.cptpackage.bluetoothchat.broadcast.receivers.DeviceConnectionChangeReceiver;
import io.cptpackage.bluetoothchat.broadcast.receivers.DevicesScanReceiver;
import io.cptpackage.bluetoothchat.connection.BluetoothUtils;
import io.cptpackage.bluetoothchat.connection.PairedDevicesSort;
import io.cptpackage.bluetoothchat.db.entities.Device;
import io.cptpackage.bluetoothchat.db.repositories.implementation.DevicesRepositoryImpl;
import io.cptpackage.bluetoothchat.db.repositories.interfaces.DevicesRepository;
import io.cptpackage.bluetoothchat.lists.adapters.DetailedDevicesAdapter;

import static androidx.recyclerview.widget.RecyclerView.LayoutManager;

public class ConnectionActivity extends LobbyChildActivity implements View.OnClickListener, DevicesScanRequester, DeviceConnectionChangeRequester {
    public static final String INTENT_CONNECTED_DEVICE_NAME_KEY = "connectedDeviceName";
    private DevicesScanReceiver scanReceiver;
    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> pairedDevices = new ArrayList<>();
    private List<BluetoothDevice> foundDevices = new ArrayList<>();
    private DetailedDevicesAdapter foundDevicesAdapter;
    private DeviceConnectionChangeReceiver connectionChangeReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        initRecyclerViews();
        Button scanButton = findViewById(R.id.scan_btn);
        scanButton.setOnClickListener(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        scanReceiver = new DevicesScanReceiver(this);
        IntentFilter[] scanFilters = scanReceiver.getIntentFilters();
        registerReceiver(scanReceiver, scanFilters[0]);
        registerReceiver(scanReceiver, scanFilters[1]);

        connectionChangeReceiver = new DeviceConnectionChangeReceiver(this);
        registerReceiver(connectionChangeReceiver, connectionChangeReceiver.getIntentFilters()[0]);

        if (!bluetoothAdapter.isEnabled()) {
            BluetoothUtils.enableBluetoothIfDisabled();
            Toast.makeText(this, R.string.toast_enabling_bluetooth, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initAndSortPairedDevices();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(scanReceiver);
        unregisterReceiver(connectionChangeReceiver);
    }

    private void initAndSortPairedDevices(){
        List<BluetoothDevice> unsortedPairedDevicesList = new ArrayList<>();
        for (Object device : bluetoothAdapter.getBondedDevices().toArray()) {
            BluetoothDevice tmpDevice = (BluetoothDevice) device;
            unsortedPairedDevicesList.add(tmpDevice);
        }
        DevicesRepository<Device> devicesRepository = DevicesRepositoryImpl.getInstance(this);
        List<Device> contactsList = devicesRepository.getAllDevices();
        PairedDevicesSort devicesSort = new PairedDevicesSort(unsortedPairedDevicesList,contactsList);
        pairedDevices.addAll(devicesSort.getSortedDevicesList());
    }

    private void initRecyclerViews() {
        RecyclerView pairedDevicesList = findViewById(R.id.paired_devices_list);
        RecyclerView foundDevicesList = findViewById(R.id.new_devices_list);
        LayoutManager pairedDevicesLayoutManager = new LinearLayoutManager(this);
        LayoutManager foundDevicesLayoutManager = new LinearLayoutManager(this);
        pairedDevicesList.setLayoutManager(pairedDevicesLayoutManager);
        foundDevicesList.setLayoutManager(foundDevicesLayoutManager);
        DetailedDevicesAdapter pairedDevicesAdapter = new DetailedDevicesAdapter(this, pairedDevices);
        foundDevicesAdapter = new DetailedDevicesAdapter(this, foundDevices);
        pairedDevicesList.setAdapter(pairedDevicesAdapter);
        foundDevicesList.setAdapter(foundDevicesAdapter);
    }

    @Override
    public void onBackPressed() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        BluetoothUtils.enableBluetoothIfDisabled();
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
        foundDevices.clear();
        foundDevicesAdapter.notifyDataSetChanged();
    }


    @Override
    public void notifyNewDeviceFound(BluetoothDevice foundDevice) {
        if (!foundDevices.contains(foundDevice)) {
            foundDevices.add(foundDevice);
            foundDevicesAdapter.notifyItemInserted(foundDevices.size());
        }
    }

    @Override
    public void notifyNoDevicesFound() {
        Toast.makeText(this, R.string.toast_no_device_found, Toast.LENGTH_LONG).show();
    }

    @Override
    public void notifyDeviceDisconnected() {
    }

    @Override
    public void notifyDeviceConnecting() {
    }

    @Override
    public void notifyDeviceConnected(String deviceName, String deviceAddress) {
        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
        Intent data = new Intent();
        data.putExtra(INTENT_CONNECTED_DEVICE_NAME_KEY,deviceName);
        setResult(RESULT_OK,data);
        finish();
    }
}
