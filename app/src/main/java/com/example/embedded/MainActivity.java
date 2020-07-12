package com.example.embedded;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.embedded.data.ContentContract;
import com.example.embedded.utilities.AlarmUtils;
import com.example.embedded.utilities.FaceUtils;
import com.example.embedded.utilities.UserUtils;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private static final int LOCATION_REQUEST_CODE = 1;
    private static final int LOCATION_SETTING_REQUEST_CODE = 2;
    private static final int TAKE_PHOTO_REQUEST_CODE = 3;
    public static final int DEFAULT_VIEW = 0x22;
    private static final int REQUEST_CODE_SCAN = 0X01;

    private TextView tvName, tvAccount, tvLocation, tvTemperatureUnit;
    private EditText etTemperature;
    private CheckedTextView ctvAroundInjection;
    private Button submit;
    private ProgressBar progressBar;
    LocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: ");

        /*Cursor data = getContentResolver().query(ContentContract.DATA_CONTENT_URI.buildUpon()
                        .appendQueryParameter(ContentContract.TableData.COLUMN_TIME,
                                String.valueOf(System.currentTimeMillis())).build(),
                null, null, null, null);
        System.out.println(data.getCount());*/
        getLocationPermission();
        getStorageAndCameraPermission();

        tvName = findViewById(R.id.tvName);
        tvAccount = findViewById(R.id.tvAccount);
        tvLocation = findViewById(R.id.tvLocation);
        etTemperature = findViewById(R.id.etTemperature);
        tvTemperatureUnit = findViewById(R.id.tvTemperatureUnit);
        progressBar = findViewById(R.id.progressBar);
        ctvAroundInjection = findViewById(R.id.ctvAroundInjection);
        ctvAroundInjection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
            }
        });
        submit = findViewById(R.id.btn_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!etTemperature.getText().toString().isEmpty()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            if (!FaceUtils.isUpload(UserUtils.getUserId(MainActivity.this))) {
                                Intent uploadFace = new Intent(MainActivity.this, UploadPicActivity.class);
                                uploadFace.putExtra("userId", UserUtils.getUserId(MainActivity.this));
                                startActivity(uploadFace);
                            } else
                                takePhoto();
                        }
                    }).start();
                } else
                    Toast.makeText(MainActivity.this, getString(R.string.not_completion), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            requestLocation();
        }
    }

    private void getStorageAndCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, TAKE_PHOTO_REQUEST_CODE);
        }
    }

    static class SubmitTask extends AsyncTask<ContentValues, Void, Uri> {
        private WeakReference<MainActivity> activityReference;
        private ContentResolver mResolver;

        SubmitTask(MainActivity context, ContentResolver resolver) {
            activityReference = new WeakReference<>(context);
            mResolver = resolver;
        }

        @Override
        protected void onPreExecute() {
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            activity.progressBar.setVisibility(View.VISIBLE);

            super.onPreExecute();
        }

        @Override
        protected Uri doInBackground(ContentValues... contentValues) {
            MainActivity activity = activityReference.get();
            if (FaceUtils.searchFace(activity))
                return mResolver.insert(ContentContract.DATA_CONTENT_URI, contentValues[0]);
            else return null;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            if(uri==null) {
                Toast.makeText(activity, activity.getString(R.string.face_fail), Toast.LENGTH_SHORT).show();
            } else if (ContentUris.parseId(uri) != -1) {
                Toast.makeText(activity, activity.getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(activity, activity.getString(R.string.submit_fail), Toast.LENGTH_SHORT).show();
            activity.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        String userName = UserUtils.getUserName(getApplicationContext());
        String userId = UserUtils.getUserId(getApplicationContext());
        if (userName.isEmpty() || userId.isEmpty())
            startLogIn();
        tvName.setText(userName);
        tvAccount.setText(userId);
        AlarmUtils.setAlarm(getApplicationContext());
        Log.i(TAG, "onStart: ");
    }

    private void startLogIn() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                return true;
            case R.id.code_scan:
                ScanUtil.startScan(this, REQUEST_CODE_SCAN, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(1000);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (bdLocation.hasAddr()) {
                    StringBuffer locationStr = new StringBuffer();
                    locationStr.append(bdLocation.getProvince())
                            .append(bdLocation.getCity())
                            .append(bdLocation.getDistrict())
                            .append(bdLocation.getStreet());
                    tvLocation.setText(locationStr);
                    submit.setEnabled(true);
                } else {
                    tvLocation.setText(getString(R.string.location));
                    submit.setEnabled(false);
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocation();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.location_permission_error),
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            case TAKE_PHOTO_REQUEST_CODE:
                if (grantResults.length < 3 || grantResults[0] != PackageManager.PERMISSION_GRANTED
                        || grantResults[1] != PackageManager.PERMISSION_GRANTED
                        || grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.storage_permission_error),
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
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
        if (requestCode == TAKE_PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                progressBar.setVisibility(View.VISIBLE);
                if (!etTemperature.getText().toString().isEmpty()) {
                    ContentValues values = new ContentValues();
                    values.put(ContentContract.TableData.COLUMN_NAME, tvName.getText().toString());
                    values.put(ContentContract.TableData.COLUMN_USER_ID,
                            tvAccount.getText().toString());
                    values.put(ContentContract.TableData.COLUMN_LOCATION,
                            tvLocation.getText().toString());
                    values.put(ContentContract.TableData.COLUMN_TEMPERATURE,
                            etTemperature.getText().toString() +
                                    tvTemperatureUnit.getText().toString());
                    values.put(ContentContract.TableData.COLUMN_AROUND_INJECTION,
                            ctvAroundInjection.isChecked() ? 1 : 0);
                    new SubmitTask(MainActivity.this, getContentResolver()).execute(values);
                } else
                    Toast.makeText(MainActivity.this, getString(R.string.not_completion), Toast.LENGTH_SHORT).show();
            }
        }

        //从onActivityResult返回data中，用 ScanUtil.RESULT作为key值取到HmsScan返回值
        if (requestCode == REQUEST_CODE_SCAN) {
            if (resultCode != RESULT_OK || data == null) {
                return;
            }
            Object obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj instanceof HmsScan) {
                if (!TextUtils.isEmpty(((HmsScan) obj).getOriginalValue())) {
                    //Toast.makeText(this, ((HmsScan) obj).getOriginalValue(), Toast.LENGTH_SHORT).show();
                    if (((HmsScan) obj).getOriginalValue().equals("out")) {
                        UserUtils.setUserStatus(this, true);
                        Toast.makeText(this, "疫情期间，请在两小时内返回小区！", Toast.LENGTH_LONG).show();
                    } else if (((HmsScan) obj).getOriginalValue().equals("in")) {
                        boolean status = UserUtils.getUserStatus(this);
                        if (status) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            LayoutInflater layoutInflater = getLayoutInflater();
                            View v1 = layoutInflater.inflate(R.layout.activity_scan_result, null);
                            builder.create();
                            builder.setView(v1);
                            builder.setTitle("权限验证");
                            builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    UserUtils.setUserStatus(MainActivity.this, false);
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        } else {
                            Toast.makeText(this, "您不是本小区居民，疫情期间请勿走动，如有必要，请联系小区负责人！", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }
    }

}
