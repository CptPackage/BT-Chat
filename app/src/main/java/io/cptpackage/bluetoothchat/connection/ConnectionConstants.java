package io.cptpackage.bluetoothchat.connection;

public final class  ConnectionConstants {
    public static final String SERVICE_RECORD_NAME = "BTChat";


    public static final int STATE_STOPPED = 0;       // we're doing nothing
    public static final int STATE_LISTENING = 1;     // now listening for incoming connections
    public static final int STATE_ATTEMPTING_CONNECTION = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTION_ESTABLISHED = 3;  // now connected to a remote device
    public static final int STATE_CONNECTION_FAILED = 4;  // connection to a remote device failed
    public static final int STATE_DISCONNECTED = 5;  // connection dropped

    public static final int CLIENT_CREATED_CONNECTION = 101;
    public static final int SERVER_CREATED_CONNECTION = 102;


}
