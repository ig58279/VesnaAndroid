package ru.cproject.vesnaandroid.activities.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.Settings;
import ru.cproject.vesnaandroid.activities.MainActivity;

public class VkAuthActivity extends AppCompatActivity {

    WebView webView;
    WebClient client;
    private ProgressDialog progressDialog;
    private ImageView back;

    private final static String VK_AUTH_URL = "https://oauth.vk.com/authorize?client_id=5699849&response_type=token&scope=offline,status,wall,groups";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vk_auth);

        back = (ImageView) findViewById(R.id.background);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        webView = (WebView) findViewById(R.id.web_view);
        progressDialog = new ProgressDialog(VkAuthActivity.this);
        progressDialog.setTitle("");
        progressDialog.setMessage("Загрузка...");
        progressDialog.setIndeterminate(true);
        client = new WebClient();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(client);
        webView.loadUrl(VK_AUTH_URL);

    }

    class WebClient extends WebViewClient {
        private boolean mLoadingFinished = false;
        //Helps to know what page is loading in the moment
        // Allows check url to prevent onReceivedError/onPageFinished calling for wrong url
        // Helps to prevent double call of onPageStarted
        // These problems cached on many devices
        private String mUrl = "";



        // We need startsWith because some extra characters like ? or # are added to the url occasionally
        // However it could cause a problem if your server load similar links, so fix it if necessary
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favIcon) {
            if(url != null)Log.e("onPageStarted url",url);
            if(!progressDialog.isShowing()){
                progressDialog.show();
            }
            if(url.contains("access_token")){
                webView.stopLoading();
                progressDialog.dismiss();
                int positionOfToken = url.indexOf("access_token",10) + 13;
                int positionOfUserId = url.indexOf("user_id",10) + 8;
                String accessToken = url.substring(positionOfToken, url.indexOf("&",positionOfToken) == -1 ? url.length() : url.indexOf("&",positionOfToken));
                String userId = url.substring(positionOfUserId, url.indexOf("&",positionOfUserId) == -1 ? url.length() : url.indexOf("&",positionOfUserId));

                getInfoAboutProfileAndTryAuth(userId,accessToken);

            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {

        }


        @Override
        public void onPageFinished(WebView view, String url) {
            progressDialog.dismiss();
            url = removeLastSlash(url);
            if (startsWith(url, mUrl) && !mLoadingFinished) {
                Log.e("WebActivity", "Page ");
                mLoadingFinished = true;

                mUrl = null;
            } else if (mUrl == null) {
                // On some devices (e.g. Lg Nexus 5) onPageStarted sometimes not called at all
                // The only way I found to fix it is to reset WebViewClient
                view.setWebViewClient(new WebClient());
                mLoadingFinished = true;
            }
        }

        private String removeLastSlash(String url) {
            while (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            return url;
        }

        // We need startsWith because some extra characters like ? or # are added to the url occasionally
        // However it could cause a problem if your server load similar links, so fix it if necessary
        private boolean startsWith(String str, String prefix) {
            return str != null && prefix != null && str.startsWith(prefix);
        }


    }

    void showAlertDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(VkAuthActivity.this);
        builder
                .setMessage(msg)
                .setPositiveButton("Ok", null);
        AlertDialog error = builder.create();
        error.show();
    }

    /**
     * Получает инфу о пользователе и отправляет её на сервер весны
     * @param userId получаем из redirectedUrl
     * @param token получаем из redirectedUrl
     */
    private void getInfoAboutProfileAndTryAuth(final String userId,final String token){
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("user_ids",userId);
        requestParams.put("access_token",token);
        requestParams.put("fields","photo_200");
        asyncHttpClient.get("https://api.vk.com/method/users.get", requestParams, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                VkAuthActivity.this.onFailure(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                JsonObject jsonObj = new JsonParser().parse(responseString).getAsJsonObject();
                if(jsonObj.has("response") && !jsonObj.get("response").isJsonNull()){
                    JsonObject responseJsonObj = jsonObj.get("response").getAsJsonArray().get(0).getAsJsonObject();
                    String firstName = "";
                    String lastName = "";
                    String photo200 = "";
                    if(responseJsonObj.has("first_name") && !responseJsonObj.get("first_name").isJsonNull())firstName = responseJsonObj.get("first_name").getAsString();
                    if(responseJsonObj.has("last_name") && !responseJsonObj.get("last_name").isJsonNull())lastName = responseJsonObj.get("last_name").getAsString();
                    if(responseJsonObj.has("photo_200") && !responseJsonObj.get("photo_200").isJsonNull())photo200 = responseJsonObj.get("photo_200").getAsString();
                    final String fname = firstName;
                    final String lname = lastName;
                    final String photo = photo200;
                    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("type","vk");
                    jsonObject.addProperty("token",token);
                    if(!firstName.isEmpty())jsonObject.addProperty("fname",firstName);
                    if(!lastName.isEmpty())jsonObject.addProperty("lname",lastName);
                    if(!photo200.isEmpty())jsonObject.addProperty("photo",photo200);
                    StringEntity entity = new StringEntity(jsonObject.toString(),"UTF-8");
                    asyncHttpClient.post(VkAuthActivity.this,ServerApi.AUTH, entity, "application/json", new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            VkAuthActivity.this.onFailure(responseString);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            if(responseString!=null)Log.e("AuthSuc",responseString);
                            progressDialog.dismiss();
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
                            editor.putString(Settings.RegistrationInfo.NAME, fname);
                            editor.putString(Settings.RegistrationInfo.SURNAME, lname);
                            editor.putString(Settings.RegistrationInfo.PHOTO,photo);
                            editor.apply();
                            Intent intent = new Intent(VkAuthActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }
    private void onFailure (@Nullable String responseString) {
        if (responseString != null)
            Log.e("Fail:", responseString);
        progressDialog.dismiss();
        Toast.makeText(VkAuthActivity.this, "Произошла непредвиденная ошибка!", Toast.LENGTH_LONG).show();
    }
}
