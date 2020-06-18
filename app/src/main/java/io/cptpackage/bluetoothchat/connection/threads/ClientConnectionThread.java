package io.cptpackage.bluetoothchat.connection.threads;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import io.cptpackage.bluetoothchat.connection.BluetoothConnectionsManager;

import static io.cptpackage.bluetoothchat.connection.ConnectionConstants.CLIENT_CREATED_CONNECTION;
import static io.cptpackage.bluetoothchat.connection.ConnectionConstants.STATE_ATTEMPTING_CONNECTION;

public class ClientConnectionThread extends Thread {
    private static final String TAG = "ClientConnectionThread";
    private BluetoothConnectionsManager connectionsManager;
    private BluetoothAdapter adapter;
    private BluetoothDevice device;
    private BluetoothSocket clientSocket;

    public ClientConnectionThread(BluetoothConnectionsManager connectionsManager, BluetoothAdapter adapter, BluetoothDevice device, UUID deviceUUID) {
        BluetoothSocket tmpSocket = null;
        this.device = device;
        this.connectionsManager = connectionsManager;
        this.adapter = adapter;

        // Get a BluetoothSocket for a connection with the given BluetoothDevice
        try {
            tmpSocket = device.createInsecureRfcommSocketToServiceRecord(
                    deviceUUID);
        } catch (IOException e) {
            Log.e(TAG, "Failed to create client connection socket!", e);
        }
        if (tmpSocket != null) {
            clientSocket = tmpSocket;
            connectionsManager.setState(STATE_ATTEMPTING_CONNECTION);
        }
    }

    public void run() {
        Log.i(TAG, "Client connection thread started running!");

        // Always cancel discovery because it will slow down a connection
        if (adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }

        // Make a connection to the BluetoothSocket
        try {
            // This is a blocking call and will only return on a
            // successful connection or an exception
            clientSocket.connect();
            Log.d(TAG, "[1ST ATTEMPT] Client socket connected successfully!");
        } catch (IOException e) {
            try {
                Log.d(TAG, "[1ST ATTEMPT] Client socket connection failed!");
                setupSocketPortUsingReflection();
                clientSocket.connect();
                Log.d(TAG, "[2ND ATTEMPT] Client socket connected successfully!");
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | IOException ex) {
                Log.e(TAG, "Failed to connect, attempting to close socket!", ex);
                // Close the socket
                try {
                    clientSocket.close();
                } catch (IOException ex2) {
                    Log.e(TAG, "Failed to close client socket on connection attempt failure!", ex2);
                }
                connectionsManager.connectionFailed();
                return;
            }
        }

        // Reset any old established connection to focus on the new connection
        connectionsManager.resetEstablishedConnection();

        // Start the connected thread
        connectionsManager.startCommunicationStreams(clientSocket, device, CLIENT_CREATED_CONNECTION);
    }


    public void setupSocketPortUsingReflection() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final String METHOD_NAME = "createRfcommSocket";
        clientSocket = (BluetoothSocket) device.getClass().getMethod(METHOD_NAME,
                new Class[]{int.class}).invoke(device, 2);
    }

    public void close() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to close client connection socket!", e);
        }
    }
}
