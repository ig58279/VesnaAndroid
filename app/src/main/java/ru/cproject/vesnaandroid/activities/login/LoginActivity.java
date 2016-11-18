package ru.cproject.vesnaandroid.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.activities.MainActivity;

/**
 * Created by Bitizen on 03.11.16.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText phoneField;
    private EditText passwordField;

    private TextView forgotPassword;

    private ImageView vk;
    private ImageView facebook;
    private Button loginButton;
    private TextView skip;
    private Button register;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneField = (EditText) findViewById(R.id.phone_field);
        passwordField = (EditText) findViewById(R.id.password_field);
        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        vk = (ImageView) findViewById(R.id.vk);
        facebook = (ImageView) findViewById(R.id.facebook);
        loginButton = (Button) findViewById(R.id.login_button);
        skip = (TextView) findViewById(R.id.skip);
        register = (Button) findViewById(R.id.register);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 03.11.16 востановление пароля
            }
        });

        vk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 03.11.16 вход по вк
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 03.11.16 вход по фейсбуку
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });


    }

    private void login() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "phone");
        params.put("phone", phoneField.getText());
        params.put("pass", passwordField.getText());

        client.get(ServerApi.AUTH, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (responseString != null)
                    Log.e(TAG, responseString);
                // TODO ошибка
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);
                // TODO логин
            }
        });
    }
}
