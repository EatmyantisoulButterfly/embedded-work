package com.example.embedded.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class LocalDataDBHelper extends SQLiteOpenHelper {


    public LocalDataDBHelper(@Nullable Context context) {
        super(context, ContentContract.DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TB_USER = "create table " + ContentContract.TABLE_USER + " (" +
                ContentContract.TableUser.COLUMN_USER_ID + " varchar primary key," +
                ContentContract.TableUser.COLUMN_PASSWORD + " integer not null," +
                ContentContract.TableUser.COLUMN_NAME + " varchar not null"+
                ")";
        db.execSQL(CREATE_TB_USER);
        final String CREATE_TB_DATA="create table "+ ContentContract.TABLE_DATA+"("+
                ContentContract.TableData.COLUMN_ID+" integer primary key autoincrement,"+
                ContentContract.TableData.COLUMN_USER_ID+" varchar not null,"+
                ContentContract.TableData.COLUMN_NAME + " varchar not null,"+
                ContentContract.TableData.COLUMN_LOCATION+" varchar not null,"+
                ContentContract.TableData.COLUMN_TEMPERATURE+" real not null,"+
                ContentContract.TableData.COLUMN_AROUND_INJECTION+" integer not null,"+
                ContentContract.TableData.COLUMN_TIME+" TIMESTAMP default (datetime('now', 'localtime'))"+
                ")";
        db.execSQL(CREATE_TB_DATA);
        ContentValues values=new ContentValues();
        values.put(ContentContract.TableUser.COLUMN_USER_ID,"123");
        values.put(ContentContract.TableUser.COLUMN_PASSWORD,"123");
        values.put(ContentContract.TableUser.COLUMN_NAME,"test1");
        db.insert(ContentContract.TABLE_USER,null,values);
        values.clear();
        values.put(ContentContract.TableUser.COLUMN_USER_ID,"456");
        values.put(ContentContract.TableUser.COLUMN_PASSWORD,"456");
        values.put(ContentContract.TableUser.COLUMN_NAME,"test2");
        db.insert(ContentContract.TABLE_USER,null,values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
