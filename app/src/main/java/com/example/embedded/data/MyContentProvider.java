package com.example.embedded.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class MyContentProvider extends ContentProvider {
    public MyContentProvider() {
    }

    public static final String authority = ContentContract.CONTENT_AUTHORITY;
    public static final String user_path = ContentContract.TABLE_USER;
    private static final int user = 1;
    public static final String data_path = ContentContract.TABLE_DATA;
    private static final int data = 2;

    private static UriMatcher mMatcher;

    static {
            mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
            mMatcher.addURI(authority, user_path, user);
            mMatcher.addURI(authority, data_path, data);
    }

    //    private LocalDataDBHelper mDBHHelper;
    private MyDAO mDao;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowid = -1;
        switch (mMatcher.match(uri)) {
            case user:
                rowid = mDao.insertUser(values);
                break;
            case data:
                rowid = mDao.insertData(values);
                break;
        }
            return ContentUris.withAppendedId(uri, rowid);
    }

    @Override
    public boolean onCreate() {
//        mDBHHelper=new LocalDataDBHelper(getContext());
        mDao = new MyDAO();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String userID = null;
        switch (mMatcher.match(uri)) {
            case user:
                userID = uri.getQueryParameter(ContentContract.TableUser.COLUMN_USER_ID);
                if (userID != null)
                    return mDao.queryUser(userID);
                break;
            /*case data:
                userID = uri.getQueryParameter(ContentContract.TableUser.COLUMN_USER_ID);
                String timeStr = uri.getQueryParameter(ContentContract.TableData.COLUMN_TIME);
                if (timeStr != null) {
                    long time = Long.parseLong(timeStr);
                    if (userID != null)
                        return mDao.queryDataForSomeday(userID,time);
                    else
                        return mDao.queryDataForSomeday(time);
                }
                break;*/
        }
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
