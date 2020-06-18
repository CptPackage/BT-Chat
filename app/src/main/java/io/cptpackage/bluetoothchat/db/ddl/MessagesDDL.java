package io.cptpackage.bluetoothchat.db.ddl;

import io.cptpackage.bluetoothchat.db.entities.Device;

public class MessagesDDL implements DDL {
    public static final String TABLE_NAME = "messages";
    public static final String ID_COL = "ID";
    public static final String SENDER_ID_COL = "SENDER_DEVICE_ID";
    public static final String RECEIVER_ID_COL = "RECEIVER_DEVICE_ID";
    public static final String DATE_COL = "DATE";
    public static final String TIME_COL = "TIME";
    public static final String CONTENT_COL = "CONTENT";


    public String getTableCreationSchema(){
        String foreignTable = DevicesDDL.TABLE_NAME;
        return "create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, SENDER_DEVICE_ID TEXT REFERENCES "+ foreignTable +", RECEIVER_DEVICE_ID TEXT REFERENCES "+ foreignTable +
                ", DATE TEXT, TIME TEXT, CONTENT TEXT)";
    }
}
