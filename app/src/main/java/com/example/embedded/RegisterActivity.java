package com.example.embedded;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.embedded.data.ContentContract;

import java.lang.ref.WeakReference;

public class RegisterActivity extends AppCompatActivity {

    private EditText mAccount;
    private EditText mPassWord;
    private EditText mName;
    private EditText mAge;
    private EditText mPersonNumber;
    private Spinner mSex;
    private EditText mAddress;
    private ProgressBar progressBar;

    private static final int UPLOAD_FACE_REQUEST_CODE = 967;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAccount = findViewById(R.id.et_account);
        mPassWord = findViewById(R.id.et_passWord);
        mName = findViewById(R.id.et_name);
        mAge = findViewById(R.id.et_age);
        mPersonNumber = findViewById(R.id.et_personNumber);
        mSex = findViewById(R.id.sp_sex);
        mAddress = findViewById(R.id.et_address);
        progressBar=findViewById(R.id.progressBar);
        findViewById(R.id.bt_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = mAccount.getText().toString();
                String passWord = mPassWord.getText().toString();
                String name = mName.getText().toString();
                String age = mAge.getText().toString();
                String personNumber = mPersonNumber.getText().toString();
                String sex = mSex.getSelectedItem().toString();
                String address = mAddress.getText().toString();
                if (account.isEmpty() || passWord.isEmpty() || name.isEmpty() || age.isEmpty() ||
                        personNumber.isEmpty() || sex.isEmpty() || address.isEmpty())
                    Toast.makeText(RegisterActivity.this, "请填写完整", Toast.LENGTH_SHORT).show();
                else{
                    ContentValues values = new ContentValues();
                    values.put(ContentContract.TableUser.COLUMN_USER_ID, account);
                    values.put(ContentContract.TableUser.COLUMN_PASSWORD, passWord);
                    values.put(ContentContract.TableUser.COLUMN_NAME, name);
//                    values.put(ContentContract.TableUser.COLUMN_AGE, age);
//                    values.put(ContentContract.TableUser.COLUMN_PERSON_NUMBER, personNumber);
//                    values.put(ContentContract.TableUser.COLUMN_SEX, sex);
//                    values.put(ContentContract.TableUser.COLUMN_ADDRESS, address);
                    new RegisterTask(RegisterActivity.this,getContentResolver()).execute(values);
                }
            }
        });
        //startActivity(new Intent(this,UploadPicActivity.class));
    }

    static class RegisterTask extends AsyncTask<ContentValues, Void, Uri> {
        private WeakReference<RegisterActivity> activityReference;
        private ContentResolver mResolver;

        RegisterTask(RegisterActivity context, ContentResolver resolver) {
            activityReference = new WeakReference<RegisterActivity>(context);
            mResolver = resolver;
        }

        @Override
        protected void onPreExecute() {
            RegisterActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            activity.progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Uri doInBackground(ContentValues... contentValues) {
            return mResolver.insert(ContentContract.USER_CONTENT_URI, contentValues[0]);
        }

        @Override
        protected void onPostExecute(Uri uri) {
            RegisterActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            if (ContentUris.parseId(uri) != -1) {
                Toast.makeText(activity, "注册成功", Toast.LENGTH_SHORT).show();
                Intent uploadFace=new Intent(activity,UploadPicActivity.class);
                uploadFace.putExtra("userId",activity.mAccount.getText().toString());
                activity.startActivityForResult(uploadFace,UPLOAD_FACE_REQUEST_CODE);
            } else
                Toast.makeText(activity, "注册失败", Toast.LENGTH_SHORT).show();
            activity.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==UPLOAD_FACE_REQUEST_CODE&&resultCode==RESULT_OK){
            finish();
        }
    }
}
