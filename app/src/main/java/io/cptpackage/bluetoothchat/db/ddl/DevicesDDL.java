package io.cptpackage.bluetoothchat.db.ddl;

import android.content.ContentValues;
import java.util.UUID;
import io.cptpackage.bluetoothchat.db.entities.Device;
import io.cptpackage.bluetoothchat.db.entities.Entity;

public class DevicesDDL implements DDL {
    public static final String TABLE_NAME = "devices";
    public static final String PERSONAL_DEVICE_NAME = "Personal-DVC-RESERVED";
    public static final String ID_COL = "ID";
    public static final String DEVICE_NAME_COL = "DEVICE_NAME";
    public static final String ENCRYPTED_COL = "ENCRYPTED";
    public static final String ADDRESS_COL = "ADDRESS";

    @Override
    public String getTableCreationSchema() {
        return "create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, DEVICE_NAME TEXT, ENCRYPTED INTEGER, ADDRESS TEXT)";
    }

    public ContentValues getPersonalDeviceContent(){
        Entity<Device> device = new Device(PERSONAL_DEVICE_NAME);
        return device.getInsertableValues();
    }

}
