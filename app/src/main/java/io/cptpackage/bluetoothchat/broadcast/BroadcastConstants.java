package io.cptpackage.bluetoothchat.broadcast;

public class BroadcastConstants {
    private static final String LOCAL_BROADCAST_PREFIX = "io.cptpackage.bluetoothchat";

    public static final String MESSAGE_DELIVERY_BROADCAST = LOCAL_BROADCAST_PREFIX + ".MessageDelivery";
    public static final String KEY_MESSAGE_TYPE = "DataTypeKey";
    public static final String KEY_OUTGOING_MESSAGE = "OutgoingMessageKey";
    public static final String KEY_INCOMING_MESSAGE = "IncomingMessageKey";

    public static final int INCOMING_MESSAGE = 88;
    public static final int OUTGOING_MESSAGE = 99;

    public static final String CONNECTION_CHANGE_BROADCAST = LOCAL_BROADCAST_PREFIX + ".ConnectionChange";
    public static final String KEY_CONNECTION_STATE = "NewConnectionState";
    public static final String KEY_CONNECTED_DEVICE_NAME = "NewConnectedDeviceName";
    public static final String KEY_CONNECTED_DEVICE_ADDRESS = "NewConnectedDeviceAddress";
    public static final int DISCONNECTED = 100;
    public static final int CONNECTING = 101;
    public static final int CONNECTED = 102;
}
