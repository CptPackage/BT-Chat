package io.cptpackage.bluetoothchat.connection.threads;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.cptpackage.bluetoothchat.connection.BluetoothConnectionsManager;

import static io.cptpackage.bluetoothchat.connection.ConnectionConstants.STATE_CONNECTION_ESTABLISHED;


public class ConnectionEstablishedThread extends Thread {
    private static final String TAG = "ConnectionEstablishedTh";
    private final BluetoothConnectionsManager connectionsManager;
    private final BluetoothSocket connectionSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public ConnectionEstablishedThread(BluetoothConnectionsManager connectionsManager, BluetoothSocket connectionSocket) {
        Log.d(TAG, "Created established connection thread!");
        InputStream tmpInputStream = null;
        OutputStream tmpOutputStream = null;
        this.connectionSocket = connectionSocket;
        this.connectionsManager = connectionsManager;
        // Get the BluetoothSocket input and output streams
        try {
            tmpInputStream = connectionSocket.getInputStream();
            tmpOutputStream = connectionSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "temp sockets not created", e);
        }

        if (tmpInputStream != null && tmpOutputStream != null) {
            inputStream = tmpInputStream;
            outputStream = tmpOutputStream;
            connectionsManager.setState(STATE_CONNECTION_ESTABLISHED);
        }
    }

    public void run() {
        Log.i(TAG, "Connection Established Thread started!");
        byte[] dataBuffer = new byte[1024];
        int dataSize;

        // Keep listening to the InputStream while connected
        while (connectionsManager.getState() == STATE_CONNECTION_ESTABLISHED) {
            try {
                // Read from the InputStream
                dataSize = inputStream.read(dataBuffer);

                // Send the obtained bytes to the UI Activity
                connectionsManager.onDataReceived(dataBuffer, dataSize);
            } catch (IOException e) {
                Log.d(TAG, "Connection was dropped!");
                connectionsManager.connectionDropped();
                break;
            }
        }
    }


    /**
     * Write to the connected OutStream.
     *
     * @param buffer The bytes to write
     */
    public void write(byte[] buffer) {
        try {
            outputStream.write(buffer);

            // Share the sent message back to the UI Activity
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
        }
    }

    public void close() {
        try {
            connectionSocket.close();
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "close() of connect socket failed", e);
        }
    }
}

