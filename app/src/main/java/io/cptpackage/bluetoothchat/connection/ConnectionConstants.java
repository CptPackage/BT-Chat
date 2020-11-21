package io.cptpackage.bluetoothchat.connection;

public final class  ConnectionConstants {
    public static final String SERVICE_RECORD_NAME = "BTChat";
    public static final int STATE_STOPPED = 0;       // Connections manager is stopped
    public static final int STATE_LISTENING = 1;     // Server listening to any incoming connections
    public static final int STATE_ATTEMPTING_CONNECTION = 2; // Client attempting to establish a remote connection
    public static final int STATE_CONNECTION_ESTABLISHED = 3;  // Connection established with another device
    public static final int STATE_CONNECTION_FAILED = 4;  // Connection attempt failed
    public static final int STATE_DISCONNECTED = 5;  // Connection was dropped

    public static final int CLIENT_CREATED_CONNECTION = 101; // Connection was created by a client-connection thread
    public static final int SERVER_CREATED_CONNECTION = 102; // Connection was created by a server-connection thread
}
