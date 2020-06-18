package io.cptpackage.bluetoothchat.connection;

import android.bluetooth.BluetoothAdapter;

public class BluetoothUtils {
    public final static void enableBluetoothIfDisabled(){
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!btAdapter.isEnabled()){
            btAdapter.enable();
        }
    }
}
