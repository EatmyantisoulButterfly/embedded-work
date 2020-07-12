package com.example.embedded;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.embedded.data.ContentContract;
import com.example.embedded.utilities.FaceUtils;

import java.lang.ref.WeakReference;

public class UploadPicActivity extends AppCompatActivity {

    private ImageView mIV;
    private ImageView upload;
    private Bitmap bitmap;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pic);
        mIV = findViewById(R.id.iv_face);
        upload = findViewById(R.id.upload);
        progressBar = findViewById(R.id.progressBar);
        findViewById(R.id.bt_choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoto();
            }
        });
        findViewById(R.id.bt_take).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UploadTask(UploadPicActivity.this).execute(getIntent().getStringExtra("userId"));
            }
        });
    }

    public static final int CHOOSE_PHOTO_REQUEST_CODE = 2;
    private static final int TAKE_PHOTO_REQUEST_CODE = 3;

    private void choosePhoto() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, CHOOSE_PHOTO_REQUEST_CODE);
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = FaceUtils.getFaceImageUri(getApplicationContext());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, TAKE_PHOTO_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CHOOSE_PHOTO_REQUEST_CODE:
                if (data != null && data.getData() != null) {
                    Uri uri = data.getData();
                    bitmap = FaceUtils.getBitmapFromUri(this, uri);// BitmapFactory.decodeStream(fis);
                    mIV.setImageBitmap(bitmap);
                    upload.setVisibility(View.VISIBLE);
                }
                break;
            case TAKE_PHOTO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    bitmap = FaceUtils.getPhoto(this);
                    mIV.setImageBitmap(bitmap);
                    upload.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    static class UploadTask extends AsyncTask<String, Void, Boolean> {
        private WeakReference<UploadPicActivity> activityReference;

        UploadTask(UploadPicActivity context) {
            activityReference = new WeakReference<UploadPicActivity>(context);
        }

        @Override
        protected void onPreExecute() {
            UploadPicActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            activity.progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... userId) {
            UploadPicActivity activity = activityReference.get();
            return FaceUtils.uploadFace(activity.getApplicationContext(), activity.bitmap, userId[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            UploadPicActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            if (aBoolean) {
                Toast.makeText(activity, "注册成功", Toast.LENGTH_SHORT).show();
                activity.setResult(RESULT_OK);
                activity.finish();
            } else {
                Toast.makeText(activity, "注册失败", Toast.LENGTH_SHORT).show();
                activity.setResult(RESULT_CANCELED);
            }
            activity.progressBar.setVisibility(View.GONE);
        }
    }
}
