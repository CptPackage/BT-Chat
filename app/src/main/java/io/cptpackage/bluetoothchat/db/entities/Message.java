package io.cptpackage.bluetoothchat.db.entities;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;

import androidx.annotation.NonNull;
import io.cptpackage.bluetoothchat.db.utils.DateAndTimeUtils;

import static io.cptpackage.bluetoothchat.db.ddl.MessagesDDL.CONTENT_COL;
import static io.cptpackage.bluetoothchat.db.ddl.MessagesDDL.DATE_COL;
import static io.cptpackage.bluetoothchat.db.ddl.MessagesDDL.RECEIVER_ID_COL;
import static io.cptpackage.bluetoothchat.db.ddl.MessagesDDL.SENDER_ID_COL;
import static io.cptpackage.bluetoothchat.db.ddl.MessagesDDL.TIME_COL;

public class Message implements Entity<Message> {
    DateAndTimeUtils dateAndTimeUtils = DateAndTimeUtils.getInstance();
    private int id;
    private Device sender;
    private Device receiver;
    private Date date;
    private Date time;
    private String content;

    public Message() {
    }

    public Message(Message srcMessage){
        this.sender = srcMessage.getSender();
        this.receiver = srcMessage.getReceiver();
        this.date = srcMessage.getDate();
        this.time = srcMessage.getTime();
        this.content = srcMessage.getContent();
    }

    public Message(Device sender, Device receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.date = DateAndTimeUtils.getInstance().now();
        this.time = DateAndTimeUtils.getInstance().now();
        this.content = content;
    }

    public Message(Device sender, Device receiver, Date date, Date time, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.time = time;
        this.content = content;
    }

    public Message(int id, Device sender, Device receiver, Date date, Date time, String content) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.time = time;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Device getSender() {
        return sender;
    }

    public void setSender(Device sender) {
        this.sender = sender;
    }

    public Device getReceiver() {
        return receiver;
    }

    public void setReceiver(Device receiver) {
        this.receiver = receiver;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getShortDateString() {
        return DateAndTimeUtils.getInstance().parseDateToStringCompacted(date);
    }

    public String getFullDateString() {
        return DateAndTimeUtils.getInstance().parseDateToString(date);
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getShortTimeString() {
        return DateAndTimeUtils.getInstance().parseTimeToStringCompacted(time);
    }

    public String getFullTimeString() {
        return DateAndTimeUtils.getInstance().parseTimeToString(time);
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean isValid() {
        return sender != null && receiver != null && date != null && time != null && content != null
                && sender.isValid() && receiver.isValid();
    }

    @Override
    public ContentValues getInsertableValues() {
        ContentValues values = new ContentValues();
        values.put(SENDER_ID_COL, sender.getId());
        values.put(RECEIVER_ID_COL, receiver.getId());
        values.put(DATE_COL, dateAndTimeUtils.parseDateToString(date));
        values.put(TIME_COL, dateAndTimeUtils.parseTimeToString(time));
        values.put(CONTENT_COL, content);
        return values;
    }

    @Override
    public Message compile(Cursor cursor) {
        DateAndTimeUtils dateAndTimeUtils = DateAndTimeUtils.getInstance();
        setId(cursor.getInt(0));
        setSender(new Device(cursor.getInt(1)));
        setReceiver(new Device(cursor.getInt(2)));
        setDate(dateAndTimeUtils.parseStringToDate(cursor.getString(3)));
        setTime(dateAndTimeUtils.parseStringToTime(cursor.getString(4)));
        setContent(cursor.getString(5));
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", date=" + date +
                ", time=" + time +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;

        return sender.getId() == message.sender.getId() &&
                receiver.getId() == message.receiver.getId() &&
                getFullDateString().equals(message.getFullDateString()) &&
                getFullTimeString().equals(message.getFullTimeString()) &&
                content.equals(message.content);
    }

}
