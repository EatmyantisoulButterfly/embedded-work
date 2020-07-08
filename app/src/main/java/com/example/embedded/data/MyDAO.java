package com.example.embedded.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class MyDAO {
    private SQLiteDatabase mDB;

    MyDAO(Context context) {
        LocalDataDBHelper mDBHHelper = new LocalDataDBHelper(context);
        mDB = mDBHHelper.getWritableDatabase();
    }

    long insertUser(ContentValues values) {
        return mDB.insert(ContentContract.TABLE_USER, null, values);
    }

    long insertData(ContentValues values) {
        if (checkToday(values.getAsString(ContentContract.TableData.COLUMN_USER_ID)))
            return -1;
        return mDB.insert(ContentContract.TABLE_DATA, null, values);
    }

    private boolean checkToday(String userId) {
        boolean isHas = false;
        Cursor cursor = mDB.rawQuery("select * from " + ContentContract.TABLE_DATA +
                        " where " + ContentContract.TableData.COLUMN_USER_ID + "=" + userId +
                        " and strftime('%Y-%m-%d'," + ContentContract.TableData.COLUMN_TIME + ")=date('now','localtime')",
                null);
        if (cursor.getCount() >= 1)
            isHas = true;
        cursor.close();
        return isHas;
    }

    Cursor queryUser(String userID) {
        return mDB.rawQuery("select * from " + ContentContract.TABLE_USER +
                " where " + ContentContract.TableUser.COLUMN_USER_ID + "=?", new String[]{userID});
    }

    Cursor queryDataForSomeday(String userID, long time) {
        return mDB.rawQuery("select * from " + ContentContract.TABLE_DATA +
                        " where " + ContentContract.TableData.COLUMN_USER_ID + "=" + userID +
                        " and strftime('%Y-%m-%d'," + ContentContract.TableData.COLUMN_TIME + ")=date(" + time/1000 + ", 'unixepoch', 'localtime')",
                null);

    }

    Cursor queryDataForSomeday(long time) {
//        String sql="select * from " + ContentContract.TABLE_DATA +
//                " where strftime('%Y-%m-%d'," + ContentContract.TableData.COLUMN_TIME + ")=date(" + time + ", 'unixepoch', 'localtime')";
        return mDB.rawQuery("select * from " + ContentContract.TABLE_DATA +
            " where strftime('%Y-%m-%d'," + ContentContract.TableData.COLUMN_TIME + ")=date(" + time/1000 + ", 'unixepoch', 'localtime')",
            null);
}

}
