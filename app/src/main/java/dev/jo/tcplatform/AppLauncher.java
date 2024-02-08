package dev.jo.tcplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.flyingpigeon.library.ServiceManager;
import com.flyingpigeon.library.annotations.thread.MainThread;

import dev.jo.tcplatform.base.Api;

public class AppLauncher extends AppCompatActivity {

    public static final String TAG = "TCPlatform";
    public static final String AF_ID = "AppsFlyerToken";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_launcher);

        ServiceManager.getInstance().publish(mApi);
        startActivity(new Intent(this, WebActivity.class));

    }

    private Api mApi = new Api() {

        @MainThread
        // default callback on the bind Thread , if you wanna it callback on mainThread , may be you should add MainThread annotation
        @Override
        public void onReady() {
            Log.e(TAG, "web process onReady, i am runing on main process , received web procecss onready signal.");
        }
    };
}