package io.cptpackage.bluetoothchat.db.entities;

import androidx.annotation.NonNull;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.Objects;

import io.cptpackage.bluetoothchat.security.SecurityConstants;
import static io.cptpackage.bluetoothchat.db.ddl.DevicesDDL.*;

public class Device implements Entity<Device> {
    private int id;
    private String name;
    private String key = SecurityConstants.ENCRYPTION_SECRET_KEY;
    private String address = ":::::";
    private Boolean encrypted = true;

    public Device(){}

    public Device(BluetoothDevice btDevice){
        name = btDevice.getName();
        address = btDevice.getAddress();
    }

    public Device(String name){
        this.name = name;
    }

    public Device(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(Boolean encrypted) {
        this.encrypted = encrypted;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean isValid(){
        return name != null && !name.isEmpty() && address != null && !address.isEmpty();
    }

    @Override
    public ContentValues getInsertableValues() {
        ContentValues values = new ContentValues();
        values.put(DEVICE_NAME_COL, name);
        values.put(ENCRYPTED_COL, encrypted ? 1 : 0);
        values.put(ADDRESS_COL, address);
        return values;
    }

    @Override
    public Device compile(Cursor cursor) {
        setId(cursor.getInt(0));
        setName(cursor.getString(1));
        setEncrypted(cursor.getInt(2) == 1);
        setAddress(cursor.getString(3));
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address);
    }

    @NonNull
    @Override
    public String toString() {
        return "Device{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", key='" + key + '\'' +
                ", encrypted='" + encrypted + '\'' +
                '}';
    }
}
