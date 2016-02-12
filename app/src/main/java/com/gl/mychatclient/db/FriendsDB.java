package com.gl.mychatclient.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.gl.mychatclient.apis.models.FriendProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gl on 2016/2/3.
 */
public class FriendsDB {
    private static FriendsDB friendsDB;
    private SQLiteDatabase database;

    public synchronized static FriendsDB getInstance(Context context) {
        if (friendsDB == null) {
            friendsDB = new FriendsDB(context);
        }
        return friendsDB;
    }

    private FriendsDB(Context context) {
        MyDatabaseHelper databaseHelper = new MyDatabaseHelper(context, "mychat", 1);
        database = databaseHelper.getWritableDatabase();
    }

    public void saveFriend(FriendProfile friendProfile) {
        if (findFriend(friendProfile.getUsername()) == null) {
            String picId = null;
            //查询是否有头像
            if (friendProfile.getPortrait() != null) {
                SQLiteStatement statement = database.compileStatement("INSERT INTO TBL_PICTURE(PICTURE) VALUES (?)");
                statement.bindBlob(1, friendProfile.getPortrait());
                long rowId = statement.executeInsert();
                statement.close();
                Cursor cursor = database.rawQuery("SELECT PIC_ID FROM TBL_PICTURE WHERE ROWID=?", new String[]{rowId + ""});
                while (cursor.moveToNext()) {
                    picId = cursor.getInt(0) + "";
                }
            }
            database.execSQL("INSERT INTO TBL_USER(USERNAME,NICKNAME,GENDER,REGION,PORTRAIT,DESCRIPTION,FRIENDNOTE) " +
                    "VALUES (?,?,?,?,?,?,?)", new String[]{friendProfile.getUsername(), friendProfile.getNickname(), friendProfile.getGender(),
                    friendProfile.getRegion(), picId, friendProfile.getDescription(), friendProfile.getFriendNote()});
        }
        else
            updateFriend(friendProfile);
    }

    private void updateFriend(FriendProfile friendProfile) {
        if (friendProfile.getPortrait() != null) {
            SQLiteStatement statement = database.compileStatement("UPDATE TBL_PICTURE SET PICTURE=? WHERE PIC_ID=(SELECT PORTRAIT FROM TBL_USER WHERE USERNAME=?)");
            statement.bindBlob(1, friendProfile.getPortrait());
            statement.bindString(2, friendProfile.getUsername());
            statement.executeUpdateDelete();
        }
        database.execSQL("UPDATE TBL_USER SET NICKNAME=?,GENDER=?,REGION=?,DESCRIPTION=?,FRIENDNOTE=?" +
                " WHERE USERNAME=?", new String[]{
                friendProfile.getNickname(), friendProfile.getGender(), friendProfile.getRegion(), friendProfile.getDescription(), friendProfile.getFriendNote(), friendProfile.getUsername()});
    }


    public List<FriendProfile> findAllFriend() {
        List<FriendProfile> friendProfileList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT USERNAME,NICKNAME,GENDER,REGION,PORTRAIT,DESCRIPTION,FRIENDNOTE " +
                "FROM TBL_USER", null);
        while (cursor.moveToNext()) {
            byte[] bytes = null;
            if (cursor.getString(4) != null) {
                Cursor picCursor = database.rawQuery("SELECT PICTURE FROM TBL_PICTURE WHERE PIC_ID=?", new String[]{cursor.getString(4)});
                while (picCursor.moveToNext())
                    bytes = picCursor.getBlob(0);
            }
            FriendProfile friendProfile = new FriendProfile();
            friendProfile.setUsername(cursor.getString(0));
            friendProfile.setNickname(cursor.getString(1));
            friendProfile.setGender(cursor.getString(2));
            friendProfile.setRegion(cursor.getString(3));
            friendProfile.setPortrait(bytes);
            friendProfile.setDescription(cursor.getString(5));
            friendProfile.setFriendNote(cursor.getString(6));
            friendProfileList.add(friendProfile);
        }
        return friendProfileList;
    }

    public FriendProfile findFriend(String username) {
        Cursor cursor = database.rawQuery("SELECT USERNAME,NICKNAME,GENDER,REGION,PORTRAIT,DESCRIPTION,FRIENDNOTE " +
                "FROM TBL_USER WHERE USERNAME=?", new String[]{username});
        FriendProfile friendProfile = null;
        while (cursor.moveToNext()) {
            byte[] bytes = null;
            if (cursor.getString(4) != null) {
                Cursor picCursor = database.rawQuery("SELECT PICTURE FROM TBL_PICTURE WHERE PIC_ID=?", new String[]{cursor.getString(4)});
                picCursor.moveToNext();
                bytes = picCursor.getBlob(0);
            }
            friendProfile = new FriendProfile();
            friendProfile.setUsername(cursor.getString(0));
            friendProfile.setNickname(cursor.getString(1));
            friendProfile.setGender(cursor.getString(2));
            friendProfile.setRegion(cursor.getString(3));
            friendProfile.setPortrait(bytes);
            friendProfile.setDescription(cursor.getString(5));
            friendProfile.setFriendNote(cursor.getString(6));
        }
        return friendProfile;
    }

    public void deleteFriend(String username) {
        database.execSQL("PRAGMA foreign_keys=ON");
        database.execSQL("DELETE FROM TBL_USER WHERE USERNAME=?", new String[]{username});
    }
}
