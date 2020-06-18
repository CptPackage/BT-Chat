package io.cptpackage.bluetoothchat.broadcast.callbacks;

public interface DeviceConnectionChangeRequester {

    void notifyDeviceDisconnected();
    void notifyDeviceConnecting();
    void notifyDeviceConnected(String deviceName, String deviceAddress);

}
