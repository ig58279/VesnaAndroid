package ru.cproject.vesnaandroid.activities.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.Settings;
import ru.cproject.vesnaandroid.activities.MainActivity;

import static android.view.View.GONE;
import static ru.cproject.vesnaandroid.R.id.register;

/**
 * Created by andro on 12.12.2016.
 */

public class RegisterCodeActivity extends AppCompatActivity {
    private static final String TAG = "RegisterCodeActivity";

    private EditText code;
    private Button apply;

    private String phone;
    private String name;
    private String surname;
    private ProgressBar loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_code);

        code = (EditText) findViewById(R.id.code_field);
        apply = (Button) findViewById(R.id.apply);
        loading = (ProgressBar) findViewById(R.id.progress);

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        name = intent.getStringExtra("name");
        surname = intent.getStringExtra("surname");

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apply.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                confirmCode();
            }
        });
    }

    private void confirmCode() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            boolean isConnected = activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("type", "phone");
                params.put("token", phone);
                params.put("fname", name);
                params.put("lname", surname);
                params.put("code", code.getText());
                params.setUseJsonStreamer(true);

                client.post(ServerApi.AUTH, params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        RegisterCodeActivity.this.onFailure(responseString);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, responseString);
                        SharedPreferences.Editor editor = getSharedPreferences(Settings.REGISTRATION_INFO, MODE_PRIVATE).edit();
                        JsonParser parser = new JsonParser();
                        JsonObject userInfo = parser.parse(responseString).getAsJsonObject();

                        String ID = "id";
                        if (userInfo.has(ID) && !userInfo.get(ID).isJsonNull()) {
                            editor.putString(Settings.RegistrationInfo.ID, userInfo.get(ID).getAsString());
                        }
                        String ROLE = "role";
                        if (userInfo.has(ROLE) && !userInfo.get(ROLE).isJsonNull()) {
                            editor.putString(Settings.RegistrationInfo.ROLE, userInfo.get(ROLE).getAsString());
                        }
                        String PBS = "pbs";
                        if (userInfo.has(PBS) && !userInfo.get(PBS).isJsonNull()) {
                            editor.putString(Settings.RegistrationInfo.PBS, userInfo.get(PBS).getAsString());
                        }
                        String PSS = "pss";
                        if (userInfo.has(PSS) && !userInfo.get(PSS).isJsonNull()) {
                            editor.putString(Settings.RegistrationInfo.PSS, userInfo.get(PSS).getAsString());
                        }
                        editor.putString(Settings.RegistrationInfo.PHONE, phone);
                        editor.putString(Settings.RegistrationInfo.NAME, name);
                        editor.putString(Settings.RegistrationInfo.SURNAME, surname);
                        editor.apply();
                        Intent intent = new Intent(RegisterCodeActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        apply.setVisibility(View.VISIBLE);
        Toast.makeText(RegisterCodeActivity.this, "Произошла непредвиденная ошибка!", Toast.LENGTH_LONG).show();
    }
}
