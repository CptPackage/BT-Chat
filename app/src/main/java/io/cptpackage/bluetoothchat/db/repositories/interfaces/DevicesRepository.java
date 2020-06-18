package io.cptpackage.bluetoothchat.db.repositories.interfaces;

import java.util.List;

import io.cptpackage.bluetoothchat.db.entities.Device;
import io.cptpackage.bluetoothchat.db.entities.Entity;

public interface DevicesRepository<T extends Entity> {
    boolean addDevice(T device);

    boolean exist(T device);

    boolean addDeviceIfNotExist(T device);

    Device getDeviceById(String id);

    boolean updateDevice(T device);

    Device getDeviceByName(String name);

    Device getPersonalDevice();

    List<Device> getAllDevices();
}
