package com.gl.mychatclient.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gl on 2016/2/3.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_TABLE_PICTURE = "CREATE TABLE TBL_PICTURE(PIC_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "PICTURE BLOB NOT NULL," +
            "POST_DATE DATE DEFAULT SYSDATE)";
    private static final String CREATE_TABLE_FRIENDS = "CREATE TABLE TBL_USER(USERNAME NVARCHAR2(20) PRIMARY KEY," +
            "NICKNAME NVARCHAR2(10) NOT NULL," +
            "GENDER NVARCHAR2(10) CHECK(GENDER IN ('MALE','FEMALE'))," +
            "REGION NVARCHAR2(20)," +
            "PORTRAIT INTEGER," +
            "DESCRIPTION NVARCHAR2(30)," +
            "FRIENDNOTE NVARCHAR2(30)," +
            "FOREIGN KEY (PORTRAIT) REFERENCES TBL_PICTURE(PIC_ID))";
    private static final String CREATE_TABLE_MESSAGE = "CREATE TABLE TBL_MESSAGE_QUEUE(MESSAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "USERNAME NVARCHAR2(20) NOT NULL," +
            "CONTENT NVARCHAR2(1000) NOT NULL," +
            "TYPE INTEGER," +
            "STATUS NVARCHAR2(10) CHECK(STATUS IN ('READ','UNREAD')),"+
            "POST_DATE INTEGER DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY (USERNAME) REFERENCES TBL_USER(USERNAME) ON DELETE CASCADE)";
    private static final String CREATE_TABLE_SESSION = "CREATE TABLE TBL_SESSION(SENDER_USERNAME NVARCHAR2(20) PRIMARY KEY," +
            "LASTWORD NVARCHAR2(1000) NOT NULL," +
            "UPDATE_DATE DATE DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY (SENDER_USERNAME) REFERENCES TBL_USER(USERNAME) ON DELETE CASCADE)";

    public MyDatabaseHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PICTURE);
        db.execSQL(CREATE_TABLE_FRIENDS);
        db.execSQL(CREATE_TABLE_MESSAGE);
        db.execSQL(CREATE_TABLE_SESSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
