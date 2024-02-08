package dev.jo.tcplatform.base;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebResourceRequest;
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
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import dev.jo.tcplatform.AppLauncher;
import dev.jo.tcplatform.R;

public class GlobalWebView extends AppCompatActivity {

    private static final String METHOD = "AES/CBC/PKCS5Padding";
    private static final String IV = "fedcba9876543210";
    protected AgentWeb mAgentWeb;
    private LinearLayout mLinearLayout;
    protected String APP_URL = "file:///android_asset/index.html";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_global_web_view);

        mLinearLayout = this.findViewById(R.id.container);

        long p = System.currentTimeMillis();

        AppsFlyerLib.getInstance().init(AppLauncher.AF_ID, null, this);
        AppsFlyerLib.getInstance().start(this);

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .setMainFrameErrorView(com.just.agentweb.R.layout.agentweb_error_page, -1)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setWebLayout(new GlobalWebLayout(this))
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                .interceptUnkownUrl()
                .createAgentWeb()
                .ready()
                .go(getUrl());

        if(mAgentWeb!=null){
            mAgentWeb.getJsInterfaceHolder().addJavaObject("Android", new AndroidInterfaceH5(mAgentWeb, this));
        }

        mAgentWeb.getUrlLoader().loadUrl(getUrl());

        long n = System.currentTimeMillis();
        Log.i("Info", "init used time:" + (n - p));

    }

    private com.just.agentweb.WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.w(AppLauncher.TAG, "URL: " + request.getUrl());

            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.i("Info", "BaseWebActivity onPageStarted");
        }
    };
    private com.just.agentweb.WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);

        }
    };

    public String getUrl() {

        RequestQueue connectAPI = Volley.newRequestQueue(this);
        JSONObject requestBody = new JSONObject();

        String endPoint = "https://platformtc.one/api/gameid?appid=TC&package=" + getPackageName();

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, endPoint, requestBody,
                response -> {
                    try {
                        JSONObject jsonData = new JSONObject(response.toString());
                        String decryptedData = decrypt(jsonData.getString("data"), "21913618CE86B5D53C7B84A75B3774CD");
                        JSONObject gameData = new JSONObject(decryptedData);

                        APP_URL = gameData.getString("gameURL");

                    } catch (JSONException e) {
                        Log.e(AppLauncher.TAG, "Error parsing JSON: " + e.getMessage());
                    } catch (Exception e) {
                        Log.e(AppLauncher.TAG, "Error processing API response: " + e.getMessage());
                    }
                },
                error -> Log.e(AppLauncher.TAG, "API request failed: " + error.toString()));

        connectAPI.add(jsonRequest);

        return APP_URL;
    }

    private static String decrypt(String message, String key) throws Exception {
        if (Objects.equals(message, "")) {
            throw new EmptyMessageException("Message cannot be empty");
        }
        else {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance(METHOD);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(IV.getBytes()));
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(message));

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

    /**
     * @deprecated
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     */
    @Override
    @Deprecated
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