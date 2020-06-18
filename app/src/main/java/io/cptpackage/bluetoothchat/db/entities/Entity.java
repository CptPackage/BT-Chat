package io.cptpackage.bluetoothchat.db.entities;

import android.content.ContentValues;
import android.database.Cursor;

public interface Entity<T extends Entity> {
    public T compile(Cursor cursor);
    public boolean isValid();
    public ContentValues getInsertableValues();
}
