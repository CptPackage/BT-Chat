package io.cptpackage.bluetoothchat.db.repositories.implementation;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.cptpackage.bluetoothchat.db.entities.Device;
import io.cptpackage.bluetoothchat.db.entities.Message;
import io.cptpackage.bluetoothchat.db.manager.DBManager;
import io.cptpackage.bluetoothchat.db.repositories.interfaces.DevicesRepository;
import io.cptpackage.bluetoothchat.db.repositories.interfaces.MessagesRepository;

import static io.cptpackage.bluetoothchat.db.ddl.MessagesDDL.CONTENT_COL;
import static io.cptpackage.bluetoothchat.db.ddl.MessagesDDL.DATE_COL;
import static io.cptpackage.bluetoothchat.db.ddl.MessagesDDL.ID_COL;
import static io.cptpackage.bluetoothchat.db.ddl.MessagesDDL.RECEIVER_ID_COL;
import static io.cptpackage.bluetoothchat.db.ddl.MessagesDDL.SENDER_ID_COL;
import static io.cptpackage.bluetoothchat.db.ddl.MessagesDDL.TABLE_NAME;
import static io.cptpackage.bluetoothchat.db.ddl.MessagesDDL.TIME_COL;


public class MessagesRepositoryImpl implements MessagesRepository<Message> {
    private static DBManager dbManager;
    private static MessagesRepository<Message> instance;
    private static DevicesRepository<Device> devicesRepository;

    private MessagesRepositoryImpl(Context context) {
        dbManager = DBManager.getInstance(context);
        devicesRepository = DevicesRepositoryImpl.getInstance(context);
    }

    public static MessagesRepository<Message> getInstance(Context context) {
        if (instance == null) {
            synchronized (MessagesRepositoryImpl.class) {
                if (instance == null) {
                    instance = new MessagesRepositoryImpl(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    @Override
    public boolean addMessage(Message message) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        try {
            db.beginTransaction();
            db.insertOrThrow(TABLE_NAME, null, message.getInsertableValues());
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(this.getClass().getCanonicalName(), "Failed to add new message! " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public List<Message> getMessagesByDevice(Device device) {
        SQLiteDatabase db = dbManager.getReadableDatabase();
        String query = String.format("SELECT * FROM %s WHERE %s = ? OR %s = ?", TABLE_NAME, SENDER_ID_COL, RECEIVER_ID_COL);
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(device.getId()), String.valueOf(device.getId())});
        List<Message> messages = new ArrayList<>();
        while (cursor.moveToNext()) {
            messages.add(new Message().compile(cursor));
        }
        cursor.close();
        return messages;
    }

    @Override
    public List<Message> getMessagesContaining(String keyword) {
        SQLiteDatabase db = dbManager.getReadableDatabase();
        String forgedKeyword = "'%" + keyword + "%'";
        String query = String.format("SELECT * FROM %s WHERE %s LIKE %s", TABLE_NAME, CONTENT_COL, forgedKeyword);
        Log.e("QUERY", "getMessagesContaining: " + query);
        Cursor cursor = db.rawQuery(query, null);
        List<Message> messages = new ArrayList<>();
        while (cursor.moveToNext()) {
            messages.add(new Message().compile(cursor));
        }
        cursor.close();
        return messages;
    }

    @Override
    public boolean deleteMessage(Message message) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        db.beginTransaction();
        try {
            String clause = String.format(" %s=?", ID_COL);
            db.delete(TABLE_NAME, clause, new String[]{String.valueOf(message.getId())});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.d(MessagesRepositoryImpl.class.getName(), "Error while deleting single message! " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public boolean exist(Message message) {
        SQLiteDatabase db = dbManager.getReadableDatabase();
        if (message.getSender() == null || message.getReceiver() == null) {
            return false;
        }

        String query = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ?",
                TABLE_NAME, SENDER_ID_COL, RECEIVER_ID_COL, DATE_COL, TIME_COL, CONTENT_COL);
        String senderId = String.valueOf(message.getSender().getId());
        String receiverId = String.valueOf(message.getReceiver().getId());
        Cursor cursor = db.rawQuery(query, new String[]{senderId, receiverId,
                message.getDateString(), message.getTimeString(), message.getContent()});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    @Override
    public boolean deleteMessages(List<Message> messages) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        db.beginTransaction();
        try {
            List<String> ids = new ArrayList<>();
            for (Message message : messages) {
                ids.add(String.valueOf(message.getId()));
            }
            String args = TextUtils.join(", ", ids);
            String clause = String.format(" %s IN (%s)", ID_COL, args);
            db.delete(TABLE_NAME, clause, null);
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.d(MessagesRepositoryImpl.class.getName(), "Error while deleting list of messages! " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public boolean deleteMessagesByDevice(Device device) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        db.beginTransaction();
        try {
            String clause = String.format("%s = ? OR %s = ?", SENDER_ID_COL, RECEIVER_ID_COL);
            String id = String.valueOf(device.getId());
            db.delete(TABLE_NAME, clause, new String[]{id, id});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.d(MessagesRepositoryImpl.class.getName(), "Error while deleting device related messages! " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public boolean deleteAllMessages() {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_NAME, null, null);
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.d(MessagesRepositoryImpl.class.getName(), "Error while deleting all messages! " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public List<Message> getLatestMessagesByDevices(List<Device> devices) {
        SQLiteDatabase db = dbManager.getReadableDatabase();
        String query = String.format("SELECT * FROM %s WHERE %s = ? OR %s = ? ORDER BY %s DESC LIMIT 1", TABLE_NAME, SENDER_ID_COL, RECEIVER_ID_COL, ID_COL);
        List<Message> messages = new ArrayList<>();
        for (Device device : devices) {
            String id = String.valueOf(device.getId());
            Cursor cursor = db.rawQuery(query, new String[]{id, id});
            if (cursor.moveToFirst()) {
                messages.add(new Message().compile(cursor));
            }
            cursor.close();
        }
        return messages;
    }
}
