package io.cptpackage.bluetoothchat.db.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.cptpackage.bluetoothchat.db.ddl.DDL;
import io.cptpackage.bluetoothchat.db.ddl.DevicesDDL;
import io.cptpackage.bluetoothchat.db.ddl.MessagesDDL;

public class DBManager extends SQLiteOpenHelper {
    public final static String DB_NAME = "BTchat.db";
    public final static Integer DB_VERSION = 1;
    private static DBManager instance;

    public static DBManager getInstance(Context context) {
        if(instance == null){
            synchronized(DBManager.class){
                if (instance == null) {
                    instance = new DBManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private DBManager(Context context){
        super(context,DB_NAME, null, DB_VERSION);
        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DDL devicesDDL = new DevicesDDL();
        DDL messagesDDL = new MessagesDDL();
        db.execSQL(devicesDDL.getTableCreationSchema());
        db.execSQL(messagesDDL.getTableCreationSchema());
        db.insert(DevicesDDL.TABLE_NAME,null,((DevicesDDL) devicesDDL).getPersonalDeviceContent());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
