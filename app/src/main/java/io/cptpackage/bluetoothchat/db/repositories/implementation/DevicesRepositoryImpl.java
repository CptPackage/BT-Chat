package io.cptpackage.bluetoothchat.db.repositories.implementation;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.cptpackage.bluetoothchat.db.entities.Device;
import io.cptpackage.bluetoothchat.db.manager.DBManager;
import io.cptpackage.bluetoothchat.db.repositories.interfaces.DevicesRepository;

import static io.cptpackage.bluetoothchat.db.ddl.DevicesDDL.ADDRESS_COL;
import static io.cptpackage.bluetoothchat.db.ddl.DevicesDDL.DEVICE_NAME_COL;
import static io.cptpackage.bluetoothchat.db.ddl.DevicesDDL.ID_COL;
import static io.cptpackage.bluetoothchat.db.ddl.DevicesDDL.PERSONAL_DEVICE_NAME;
import static io.cptpackage.bluetoothchat.db.ddl.DevicesDDL.TABLE_NAME;

public class DevicesRepositoryImpl implements DevicesRepository<Device> {
    private static DevicesRepository<Device> instance;
    private static DBManager dbManager;
    private static Device personalDevice;

    private DevicesRepositoryImpl(Context context) {
        dbManager = DBManager.getInstance(context);
        personalDevice = getDeviceByName(PERSONAL_DEVICE_NAME);
    }

    public static DevicesRepository<Device> getInstance(Context context) {
        if (instance == null) {
            synchronized (DevicesRepositoryImpl.class) {
                if (instance == null) {
                    instance = new DevicesRepositoryImpl(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    @Override
    public boolean addDevice(Device device) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        try {
            db.beginTransaction();
            db.insertOrThrow(TABLE_NAME, null, device.getInsertableValues());
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(this.getClass().getCanonicalName(), "Failed to add new device! " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public boolean exist(Device device) {
        SQLiteDatabase db = dbManager.getReadableDatabase();
        String query = String.format("SELECT * FROM %s WHERE %s = ? OR %s = ?", TABLE_NAME, DEVICE_NAME_COL, ADDRESS_COL);
        Cursor cursor = db.rawQuery(query, new String[]{device.getName(), device.getAddress()});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    @Override
    public boolean addDeviceIfNotExist(Device device) {
        if (!exist(device)) {
            addDevice(device);
            return true;
        }
        return false;
    }

    @Override
    public Device getDeviceById(String id) {
        SQLiteDatabase db = dbManager.getReadableDatabase();
        String query = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_NAME, ID_COL);
        Cursor cursor = db.rawQuery(query, new String[]{id});
        Device device = null;
        if (cursor.moveToFirst()) {
            device = new Device().compile(cursor);
        }
        cursor.close();
        return device;
    }

    @Override
    public boolean updateDevice(Device device) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        try {
            db.beginTransaction();
            db.update(TABLE_NAME, device.getInsertableValues(),
                    String.format("%s = ?", ADDRESS_COL), new String[]{device.getAddress()});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(this.getClass().getCanonicalName(), "Failed to update device! " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public Device getDeviceByName(String name) {
        SQLiteDatabase db = dbManager.getReadableDatabase();
        String query = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_NAME, DEVICE_NAME_COL);
        Cursor cursor = db.rawQuery(query, new String[]{name});
        Device device = null;
        if (cursor.moveToFirst()) {
            device = new Device().compile(cursor);
        }
        cursor.close();
        return device;
    }

    @Override
    public Device getPersonalDevice() {
        return personalDevice;
    }


    @Override
    public List<Device> getAllDevices() {
        SQLiteDatabase db = dbManager.getReadableDatabase();
        String query = String.format("SELECT * FROM %s WHERE %s != ?", TABLE_NAME, DEVICE_NAME_COL);
        Cursor cursor = db.rawQuery(query, new String[]{PERSONAL_DEVICE_NAME});
        List<Device> devices = new ArrayList<>();
        while (cursor.moveToNext()) {
            devices.add(new Device().compile(cursor));
        }
        cursor.close();
        return devices;
    }
}
