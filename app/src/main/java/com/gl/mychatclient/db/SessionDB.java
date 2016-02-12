package com.gl.mychatclient.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.gl.mychatclient.apis.models.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gl on 2016/2/3.
 */
public class SessionDB {
    private static SessionDB sessionDBDB;
    private SQLiteDatabase database;

    public synchronized static SessionDB getInstance(Context context) {
        if (sessionDBDB == null) {
            sessionDBDB = new SessionDB(context);
        }
        return sessionDBDB;
    }

    private SessionDB(Context context) {
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context, "mychat", 1);
        database = myDatabaseHelper.getWritableDatabase();
    }

    public int saveSession(Session session) {
        int rowId = findSessionId(session.getUsername());
        if (rowId == -1) {
            SQLiteStatement statement = database.compileStatement("INSERT INTO TBL_SESSION(SENDER_USERNAME,LASTWORD,UPDATE_DATE) VALUES (?,?,?)");
            statement.bindString(1, session.getUsername());
            statement.bindString(2, session.getLastWord());
            statement.bindLong(3, session.getTime());
            rowId = (int) statement.executeInsert();
            statement.close();
        } else {
            updateSession(session);
        }
        return rowId;
    }

    public void deleteSession(String senderUsername) {
        database.execSQL("DELETE FROM TBL_SESSION WHERE SENDER_USERNAME=?", new String[]{senderUsername});
    }

    public void deleteSessions() {
        database.execSQL("DELETE FROM TBL_SESSION");
    }

    public int findSessionId(String senderUsername) {
        Cursor cursor = database.rawQuery("SELECT ROWID FROM TBL_SESSION WHERE SENDER_USERNAME=?", new String[]{senderUsername});
        int rowId = -1;
        while (cursor.moveToNext()) {
            rowId = cursor.getInt(0);
        }
        return rowId;
    }

    private void updateSession(Session session) {
        database.execSQL("UPDATE TBL_SESSION SET LASTWORD=?,UPDATE_DATE=? WHERE SENDER_USERNAME=?",
                new String[]{session.getLastWord(), session.getTime() + "", session.getUsername()});

    }

    public List<Session> readSession() {
        List<Session> sessions = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT SENDER_USERNAME,LASTWORD,UPDATE_DATE FROM TBL_SESSION", null);
        while (cursor.moveToNext()) {
            Session session = new Session();
            session.setUsername(cursor.getString(0));
            session.setLastWord(cursor.getString(1));
            session.setTime(cursor.getLong(2));
            sessions.add(session);
        }
        if (sessions.size() == 0)
            return null;
        return sessions;
    }
}
