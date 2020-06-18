package io.cptpackage.bluetoothchat.broadcast.receivers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.cptpackage.bluetoothchat.connection.BluetoothConnectionsManager;

public class BluetoothStateReceiver extends BroadcastReceiver {
    private static final String TAG = "InitConnectionReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            BluetoothConnectionsManager connectionsManager = BluetoothConnectionsManager.getInstance(context);
            switch (state) {
                case BluetoothAdapter.STATE_TURNING_ON:
                    connectionsManager.stop();
                    break;
                case BluetoothAdapter.STATE_ON:
                    connectionsManager.start();
                    Log.d(TAG, "Turned on bluetooth!");
                    break;
                case BluetoothAdapter.STATE_OFF:
                    connectionsManager.stop();
                    Log.d(TAG, "Turning off bluetooth!");
                    break;
            }
        }
    }
}
