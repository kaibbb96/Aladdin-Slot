package mad.devkai.aladinfortuneslots;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;


import com.flyingpigeon.library.ServiceManager;

import mad.devkai.aladinfortuneslots.help.Api;
public class AppLauncher extends AppCompatActivity {

    public static final String TAG = "Aladdin's Fortune";

    public static final String AF_ID = "2jAVWqgmQoeQmHCJyVUsRh";

    public static final String APP_ID = "TC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.applauncher);

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