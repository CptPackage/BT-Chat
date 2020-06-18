package io.cptpackage.bluetoothchat.connection.threads;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import io.cptpackage.bluetoothchat.connection.BluetoothConnectionsManager;

import static io.cptpackage.bluetoothchat.connection.ConnectionConstants.SERVER_CREATED_CONNECTION;
import static io.cptpackage.bluetoothchat.connection.ConnectionConstants.SERVICE_RECORD_NAME;
import static io.cptpackage.bluetoothchat.connection.ConnectionConstants.STATE_ATTEMPTING_CONNECTION;
import static io.cptpackage.bluetoothchat.connection.ConnectionConstants.STATE_CONNECTION_ESTABLISHED;
import static io.cptpackage.bluetoothchat.connection.ConnectionConstants.STATE_DISCONNECTED;
import static io.cptpackage.bluetoothchat.connection.ConnectionConstants.STATE_LISTENING;
import static io.cptpackage.bluetoothchat.connection.ConnectionConstants.STATE_STOPPED;

public class ServerConnectionThread extends Thread {
    private static final String TAG = "ServerConnectionThread";
    private BluetoothServerSocket serverSocket;
    private BluetoothConnectionsManager connectionsManager;
    private boolean isAlive = false;

    public ServerConnectionThread(BluetoothConnectionsManager connectionsManager, BluetoothAdapter adapter, UUID deviceUUID) {
        BluetoothServerSocket tmpSocket = null;
        this.connectionsManager = connectionsManager;
        // Create a new listening server socket
        try {
            tmpSocket = adapter.listenUsingInsecureRfcommWithServiceRecord(
                    SERVICE_RECORD_NAME, deviceUUID);
        } catch (IOException e) {
            Log.e(TAG, "Server socket failed to start listening!", e);
        }

        if (tmpSocket != null) {
            serverSocket = tmpSocket;
            connectionsManager.setState(STATE_LISTENING);
        }
        isAlive = true;
    }

    public void run() {
        Log.d(TAG, "Server connection thread started running!");

        BluetoothSocket socket = null;
        // Listen to the server socket if we're not connected
        while (connectionsManager.getState() != STATE_CONNECTION_ESTABLISHED && isAlive) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                Log.d(TAG, "Server stopped listening!");
            }

            // If a connection was accepted
            if (socket != null) {
                synchronized (ServerConnectionThread.this) {
                    switch (connectionsManager.getState()) {
                        case STATE_LISTENING:
                        case STATE_ATTEMPTING_CONNECTION:
                        case STATE_DISCONNECTED:
                            // Situation normal. Start the connected thread.
                            connectionsManager.startCommunicationStreams(socket, socket.getRemoteDevice(), SERVER_CREATED_CONNECTION);
                            break;
                        case STATE_STOPPED:
                        case STATE_CONNECTION_ESTABLISHED:
                            // Either not ready or already connected. Terminate new socket.
                            try {
                                socket.close();
                            } catch (IOException e) {
                                Log.e(TAG, "Failed to close deprecated socket!", e);
                            }
                            break;
                    }
                }
            }
        }
    }

    public void close() {
        Log.d(TAG, "Attempting to close server connection!");
        try {
            serverSocket.close();
            isAlive = false;
        } catch (IOException e) {
            Log.e(TAG, "Failed to close server socket connection!", e);
        }
    }

    public void killServer(){
        isAlive = false;
    }

}
