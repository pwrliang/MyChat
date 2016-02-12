package com.gl.mychatclient.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.gl.mychatclient.apis.models.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gl on 2016/2/3.
 */
public class MessageDB {
    private static MessageDB messageDB;
    private SQLiteDatabase database;

    public synchronized static MessageDB getInstance(Context context) {
        if (messageDB == null) {
            messageDB = new MessageDB(context);
        }
        return messageDB;
    }

    private MessageDB(Context context) {
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context, "mychat", 1);
        database = myDatabaseHelper.getWritableDatabase();
    }

    public void saveMessage(Message message, String status) {
        if (message.getType() == Message.VALUE_LEFT_TEXT || message.getType() == Message.VALUE_RIGHT_TEXT) {
            database.execSQL("INSERT INTO TBL_MESSAGE_QUEUE(USERNAME,CONTENT,TYPE,STATUS) VALUES (?,?,?,?)",
                    new String[]{message.getUsername(), message.getText(), message.getType() + "", status});
        } else if (message.getType() == Message.VALUE_LEFT_IMAGE || message.getType() == Message.VALUE_RIGHT_IMAGE) {
            SQLiteStatement statement = database.compileStatement("INSERT INTO TBL_PICTURE(PICTURE) VALUES (?)");
            statement.bindBlob(1, message.getImage());
            long rowid = statement.executeInsert();
            statement.close();
            Cursor cursor = database.rawQuery("SELECT PIC_ID FROM TBL_PICTURE WHERE ROWID=?", new String[]{rowid + ""});
            cursor.moveToNext();
            int picId = cursor.getInt(0);
            database.execSQL("INSERT INTO TBL_MESSAGE_QUEUE(USERNAME,CONTENT,TYPE,STATUS) VALUES (?,?,?,?)",
                    new String[]{message.getUsername(), picId + "", message.getType() + "", status});
        }
    }
    public void deleteMessages() {
        database.execSQL("DELETE FROM TBL_MESSAGE_QUEUE");
    }
    public void deleteMessages(String username) {
        database.execSQL("DELETE FROM TBL_MESSAGE_QUEUE WHERE USERNAME=?", new String[]{username});
    }

    public List<Message> readMessages(String username) {
        List<Message> messages = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT USERNAME,CONTENT,TYPE,STATUS FROM TBL_MESSAGE_QUEUE WHERE USERNAME=?", new String[]{username});
        while (cursor.moveToNext()) {
            Message message = new Message();
            message.setUsername(cursor.getString(0));
            if (cursor.getInt(2) == Message.VALUE_LEFT_TEXT || cursor.getInt(2) == Message.VALUE_RIGHT_TEXT) {//文字
                message.setText(cursor.getString(1));
            } else if (cursor.getInt(2) == Message.VALUE_LEFT_IMAGE || cursor.getInt(2) == Message.VALUE_RIGHT_IMAGE) {//图片
                Cursor picCursor = database.rawQuery("SELECT PICTURE FROM TBL_PICTURE WHERE PIC_ID=?", new String[]{cursor.getString(1)});
                picCursor.moveToNext();
                byte[] bytes = picCursor.getBlob(0);
                message.setImage(bytes);
            }
            message.setType(cursor.getInt(2));
            message.setStatus(cursor.getString(3));
            messages.add(message);
        }
        database.execSQL("UPDATE TBL_MESSAGE_QUEUE SET STATUS='READ' WHERE STATUS='UNREAD' AND USERNAME=?", new String[]{username});
        if (messages.size() == 0) return null;
        return messages;
    }

    public List<Message> readMessages(String username, String status) {
        List<Message> messages = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT USERNAME,CONTENT,TYPE,STATUS FROM TBL_MESSAGE_QUEUE WHERE USERNAME=? AND STATUS=?", new String[]{username, status});
        while (cursor.moveToNext()) {
            Message message = new Message();
            message.setUsername(cursor.getString(0));
            if (cursor.getInt(2) == Message.VALUE_LEFT_TEXT || cursor.getInt(2) == Message.VALUE_RIGHT_TEXT) {//文字
                message.setText(cursor.getString(1));
            } else if (cursor.getInt(2) == Message.VALUE_LEFT_IMAGE || cursor.getInt(2) == Message.VALUE_RIGHT_IMAGE) {//图片
                Cursor picCursor = database.rawQuery("SELECT PICTURE FROM TBL_PICTURE WHERE PIC_ID=?", new String[]{cursor.getString(1)});
                picCursor.moveToNext();
                byte[] bytes = picCursor.getBlob(0);
                message.setImage(bytes);
            }
            message.setType(cursor.getInt(2));
            message.setStatus(cursor.getString(3));
            messages.add(message);
        }
        database.execSQL("UPDATE TBL_MESSAGE_QUEUE SET STATUS='READ' WHERE STATUS='UNREAD' AND USERNAME=?", new String[]{username});
        if (messages.size() == 0) return null;
        return messages;
    }
}
