package ru.cproject.vesnaandroid.activities.login;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;

import static android.view.View.GONE;

/**
 * Created by Bitizen on 03.11.16.
 */

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private EditText phone;
    private EditText name;
    private EditText surname;
    private Button register;
    private ProgressBar loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtity_register);

        phone = (EditText) findViewById(R.id.phone_field);
        name = (EditText) findViewById(R.id.name_field);
        surname = (EditText) findViewById(R.id.surname_field);
        register = (Button) findViewById(R.id.register);
        loading = (ProgressBar) findViewById(R.id.progress);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register.setVisibility(GONE);
                loading.setVisibility(View.VISIBLE);
                sendSMS();
            }
        });
    }

    private void sendSMS() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            boolean isConnected = activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("phone", phone.getText());
                params.setUseJsonStreamer(true);

                client.post(ServerApi.SMS, params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        RegisterActivity.this.onFailure(responseString);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, responseString);
                        Intent intent = new Intent(RegisterActivity.this, RegisterCodeActivity.class);
                        intent.putExtra("phone", phone.getText().toString());
                        intent.putExtra("name", name.getText().toString());
                        intent.putExtra("surname", surname.getText().toString());
                        startActivity(intent);
                    }
                });
            } else
                onFailure(null);
        } else
            onFailure(null);
    }
    private void onFailure (@Nullable String responseString) {
        if (responseString != null)
            Log.e(TAG, responseString);
        loading.setVisibility(GONE);
        register.setVisibility(View.VISIBLE);
        Toast.makeText(RegisterActivity.this, "Произошла непредвиденная ошибка!", Toast.LENGTH_LONG).show();
    }
}
