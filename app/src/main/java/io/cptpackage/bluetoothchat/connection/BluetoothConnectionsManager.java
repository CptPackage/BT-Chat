package io.cptpackage.bluetoothchat.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.UUID;

import io.cptpackage.bluetoothchat.connection.threads.ClientConnectionThread;
import io.cptpackage.bluetoothchat.connection.threads.ConnectionEstablishedThread;
import io.cptpackage.bluetoothchat.connection.threads.ServerConnectionThread;

import static io.cptpackage.bluetoothchat.broadcast.BroadcastConstants.*;
import static io.cptpackage.bluetoothchat.connection.ConnectionConstants.CLIENT_CREATED_CONNECTION;
import static io.cptpackage.bluetoothchat.connection.ConnectionConstants.SERVER_CREATED_CONNECTION;
import static io.cptpackage.bluetoothchat.connection.ConnectionConstants.STATE_ATTEMPTING_CONNECTION;
import static io.cptpackage.bluetoothchat.connection.ConnectionConstants.STATE_CONNECTION_ESTABLISHED;
import static io.cptpackage.bluetoothchat.connection.ConnectionConstants.STATE_CONNECTION_FAILED;
import static io.cptpackage.bluetoothchat.connection.ConnectionConstants.STATE_DISCONNECTED;
import static io.cptpackage.bluetoothchat.connection.ConnectionConstants.STATE_STOPPED;

public class BluetoothConnectionsManager {
    private static final String TAG = "BluetoothConnectionsMan";
    private static BluetoothConnectionsManager instance;
    private final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Context context;
    private BluetoothAdapter adapter;
    private ServerConnectionThread serverConnectionThread;
    private ClientConnectionThread clientConnectionThread;
    private ConnectionEstablishedThread connectionEstablishedThread;
    private BluetoothDevice connectedDevice;
    private int state = STATE_STOPPED;

    private BluetoothConnectionsManager(Context context) {
        adapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
    }

    public static BluetoothConnectionsManager getInstance(Context context) {
        if (instance == null) {
            synchronized (BluetoothConnectionsManager.class) {
                if (instance == null) {
                    instance = new BluetoothConnectionsManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public void start() {
        Log.d(TAG, "Starting Bluetooth Connections Manager!");
        if(!adapter.isEnabled()){
            BluetoothUtils.enableBluetoothIfDisabled();
            return;
        }

        if(connectedDevice != null){
            connectedDevice = null;
        }

        // Cancel any thread attempting to make a connection
        if (clientConnectionThread != null) {
            clientConnectionThread.close();
            clientConnectionThread = null;
        }

        // Cancel any thread currently running a connection
        if (connectionEstablishedThread != null) {
            connectionEstablishedThread.close();
            connectionEstablishedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (serverConnectionThread != null) {
            serverConnectionThread.close();
            serverConnectionThread = null;
        }

        serverConnectionThread = new ServerConnectionThread(this, adapter, uuid);
        serverConnectionThread.start();
    }

    public synchronized void stop() {
        if (clientConnectionThread != null) {
            clientConnectionThread.close();
            clientConnectionThread = null;
        }

        if (connectionEstablishedThread != null) {
            connectionEstablishedThread.close();
            connectionEstablishedThread = null;
        }

        if (serverConnectionThread != null) {
            serverConnectionThread.killServer();
            serverConnectionThread.close();
            serverConnectionThread = null;
        }

        connectedDevice = null;

        state = STATE_STOPPED;
    }

    public void attemptToConnect(BluetoothDevice device) {
        switch (state) {
            case STATE_ATTEMPTING_CONNECTION:
                if (serverConnectionThread != null) {
                    serverConnectionThread.close();
                    serverConnectionThread = null;
                }
                break;
            case STATE_CONNECTION_ESTABLISHED:
                connectionDropped();
                return;
        }

        // Cancel any thread currently running a connection
        if (connectionEstablishedThread != null) {
            connectionEstablishedThread.close();
            connectionEstablishedThread = null;
        }

        // Start the thread to connect with the given device
        clientConnectionThread = new ClientConnectionThread(this, adapter, device, uuid);
        clientConnectionThread.start();
        setState(STATE_ATTEMPTING_CONNECTION);
        Intent broadcastIntent = new Intent(CONNECTION_CHANGE_BROADCAST);
        broadcastIntent.putExtra(KEY_CONNECTION_STATE,CONNECTING);
        context.sendBroadcast(broadcastIntent);
    }

    public synchronized void startCommunicationStreams(BluetoothSocket socket, BluetoothDevice
            device, int ConnectionCreator) {


        switch (ConnectionCreator) {
            case SERVER_CREATED_CONNECTION:
                if (clientConnectionThread != null) {
                    clientConnectionThread.close();
                    clientConnectionThread = null;
                }
                break;
            case CLIENT_CREATED_CONNECTION:
                if (serverConnectionThread != null) {
                    serverConnectionThread.close();
                    serverConnectionThread = null;
                }
                break;
        }

        // Cancel any thread currently running a connection
        if (connectionEstablishedThread != null) {
            connectionEstablishedThread.close();
            connectionEstablishedThread = null;
        }


        // Start the thread to manage the connection and perform transmissions
        connectionEstablishedThread = new ConnectionEstablishedThread(this, socket);
        connectionEstablishedThread.start();
        setState(STATE_CONNECTION_ESTABLISHED);
        connectedDevice = socket.getRemoteDevice();
        Intent broadcastIntent = new Intent(CONNECTION_CHANGE_BROADCAST);
        broadcastIntent.putExtra(KEY_CONNECTION_STATE,CONNECTED);
        broadcastIntent.putExtra(KEY_CONNECTED_DEVICE_NAME,device.getName());
        broadcastIntent.putExtra(KEY_CONNECTED_DEVICE_ADDRESS,device.getAddress());
        context.sendBroadcast(broadcastIntent);
        Log.d(TAG, String.format("Connected to device: [%s] %s", device.getAddress(), device.getName()));
    }

    public synchronized void connectionFailed() {
        state = STATE_CONNECTION_FAILED;
        if (clientConnectionThread != null) {
            clientConnectionThread.close();
            clientConnectionThread = null;
        }
        this.start();
    }

    public synchronized void connectionDropped() {
        this.stop();
        state = STATE_DISCONNECTED;
        connectedDevice = null;
        Intent broadcastIntent = new Intent(CONNECTION_CHANGE_BROADCAST);
        broadcastIntent.putExtra(KEY_CONNECTION_STATE,DISCONNECTED);
        context.sendBroadcast(broadcastIntent);
        this.start();
    }

    public void onDataReceived(byte[] dataBuffer, int dataSize) {
        String parsedMessage = new String(dataBuffer, 0, dataSize);
        Intent dataIntent = new Intent(MESSAGE_DELIVERY_BROADCAST);
        dataIntent.putExtra(KEY_MESSAGE_TYPE, INCOMING_MESSAGE);
        dataIntent.putExtra(KEY_INCOMING_MESSAGE, parsedMessage);
        context.sendBroadcast(dataIntent);
    }

    public void sendData(String message) {
        if (isConnected()) {
            Log.e(TAG, "Sending outgoing message!");
            byte[] dataBuffer = message.getBytes();
            connectionEstablishedThread.write(dataBuffer);
            Intent dataIntent = new Intent(MESSAGE_DELIVERY_BROADCAST);
            dataIntent.putExtra(KEY_MESSAGE_TYPE, OUTGOING_MESSAGE);
            dataIntent.putExtra(KEY_OUTGOING_MESSAGE, message);
            context.sendBroadcast(dataIntent);
        }
    }

    public synchronized void resetEstablishedConnection() {
        connectionEstablishedThread = null;
    }

    public boolean isConnected() {
        return adapter.isEnabled() && state == STATE_CONNECTION_ESTABLISHED && connectedDevice != null;
    }

    public int getState() {
        return state;
    }

    public synchronized void setState(int newState) {
        this.state = newState;
    }

    public BluetoothDevice getConnectedDevice(){
        return connectedDevice;
    }
}
