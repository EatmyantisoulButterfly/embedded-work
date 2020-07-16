package com.example.embedded.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;

import com.zcgroup.e_management.entity.Data;
import com.zcgroup.e_management.entity.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class MyDAO {

    //private SQLiteDatabase mDB;
    private String SERVER_IP = "192.168.0.106";
    private static final String[] USER_COLUMN_NAME = {ContentContract.TableUser.COLUMN_USER_ID,
            ContentContract.TableUser.COLUMN_PASSWORD,
            ContentContract.TableUser.COLUMN_NAME};
    private int REGISTER_PORT = 9003;
    private int LOGIN_PORT = 9002;
    private int SUBMIT_PORT = 9001;

    long insertUser(ContentValues values) {
        User user = new User(values.getAsString(ContentContract.TableUser.COLUMN_USER_ID),
                values.getAsString(ContentContract.TableUser.COLUMN_PASSWORD),
                values.getAsString(ContentContract.TableUser.COLUMN_NAME),
                values.getAsString(ContentContract.TableUser.COLUMN_AGE),
                values.getAsString(ContentContract.TableUser.COLUMN_PERSON_NUMBER),
                values.getAsString(ContentContract.TableUser.COLUMN_SEX),
                values.getAsString(ContentContract.TableUser.COLUMN_ADDRESS));
        Socket socket = null;
        try {
            socket = new Socket(SERVER_IP, REGISTER_PORT);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(user);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            byte[] buffer = new byte[1024];
            int length = dataInputStream.read(buffer);
            String ID = new String(buffer, 0, length);
            return Long.parseLong(ID);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return -1L;
    }

    Cursor queryUser(String userID) {
        MatrixCursor cursor = new MatrixCursor(USER_COLUMN_NAME);
        Socket socket = null;
        try {
            socket = new Socket(SERVER_IP, LOGIN_PORT);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.write(userID.getBytes());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            User user = (User) ois.readObject();
            if (user != null)
                cursor.addRow(new Object[]{user.getAccount(), user.getPassWord(), user.getName()});
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cursor;
    }

    long insertData(ContentValues values) {
        Data data = new Data(null,
                values.getAsString(ContentContract.TableData.COLUMN_USER_ID),
                values.getAsString(ContentContract.TableData.COLUMN_LOCATION),
                values.getAsString(ContentContract.TableData.COLUMN_TEMPERATURE),
                values.getAsString(ContentContract.TableData.COLUMN_AROUND_INJECTION),
                null);
        Socket socket = null;
        try {
            socket = new Socket(SERVER_IP, SUBMIT_PORT);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(data);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            byte[] buffer = new byte[1024];
            int length = dataInputStream.read(buffer);
            String ID = new String(buffer, 0, length);
            return Long.parseLong(ID);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return -1L;
    }
/*
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
*/

}
