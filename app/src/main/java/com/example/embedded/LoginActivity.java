package com.example.embedded;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.embedded.data.ContentContract;
import com.example.embedded.utilities.UserUtils;

public class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static String KEY_USER_ID = "userId";
    private final static String KEY_USER_NAME = "userName";
    private String passWord;
    private boolean isLogIn = false;

    private EditText userIdEditText;
    private EditText passwordEditText;
    private Button loginButton,registerButton;
    private ProgressBar loadingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userIdEditText = findViewById(R.id.userId);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        registerButton=findViewById(R.id.bt_register);
        loadingProgressBar = findViewById(R.id.loading);


        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login(userIdEditText.getText().toString().trim(),
                            passwordEditText.getText().toString().trim());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(userIdEditText.getText().toString().trim(),
                        passwordEditText.getText().toString().trim());
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent=new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }

    private void login(String userId, String passWord) {
        if(userId.isEmpty()||passWord.isEmpty())
            return;
        if (isLogIn)
            return;
        isLogIn = true;
        loginButton.setEnabled(false);
        loadingProgressBar.setVisibility(View.VISIBLE);
        this.passWord = passWord;//暂存password
        Bundle args = new Bundle();
        args.putString(KEY_USER_ID, userId);
        LoaderManager.getInstance(this).initLoader(0, args, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri uri = ContentContract.USER_CONTENT_URI.buildUpon().
                appendQueryParameter(ContentContract.TableUser.COLUMN_USER_ID, args.getString(KEY_USER_ID)).
                build();
        return new CursorLoader(getApplicationContext(),
                uri,
                null,
                null,
                null,
                null
        );

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        isLogIn = false;
        data.moveToFirst();
        if (data.getCount() == 1 && this.passWord.equals(data.getString(data.getColumnIndex(ContentContract.TableUser.COLUMN_PASSWORD)))) {

            String userName = data.getString(data.getColumnIndex(ContentContract.TableUser.COLUMN_NAME));
            String userId = data.getString(data.getColumnIndex(ContentContract.TableUser.COLUMN_USER_ID));
            UserUtils.saveUserNameAndUserId(getApplicationContext(), userName, userId);
            loginButton.setEnabled(true);
            loadingProgressBar.setVisibility(View.GONE);
            finish();

        }else {
            loginButton.setEnabled(true);
            loadingProgressBar.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show();
        }
        LoaderManager.getInstance(this).destroyLoader(0);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
