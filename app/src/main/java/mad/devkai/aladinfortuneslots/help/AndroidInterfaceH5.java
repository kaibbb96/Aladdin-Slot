package mad.devkai.aladinfortuneslots.help;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
import com.appsflyer.AppsFlyerLib;
import com.just.agentweb.AgentWeb;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import mad.devkai.aladinfortuneslots.AppLauncher;

public class AndroidInterfaceH5 {

    private final Handler deliver = new Handler(Looper.getMainLooper());
    private final AgentWeb agent;
    private final Context context;

    public AndroidInterfaceH5(AgentWeb agent, Context context) {
        this.agent = agent;
        this.context = context;
    }

    @JavascriptInterface
    public void openWebView(String url) {
        Log.d(AppLauncher.TAG, "call: window.Android.openWebView : " + url);
        AppsFlyerLib.getInstance().logEvent(context, "openWebView", null);
    }

    @JavascriptInterface
    public void eventTracker(String eventType, String eventValues){
        Log.d(AppLauncher.TAG, "call: window.Android.eventTracker");
        Log.i(AppLauncher.TAG, "Event: " + eventType + " / Values: " + bundleFromJson(eventValues));

        AppsFlyerLib.getInstance().logEvent(context, eventType, bundleFromJson(eventValues));
    }

    @JavascriptInterface
    public void callAndroid(final String msg) {

        deliver.post(() -> {
            Log.i("Info", "main Thread:" + Thread.currentThread());
            Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        });

        Log.i("Info", "Thread:" + Thread.currentThread());
    }

    private Map<String, Object> bundleFromJson(String json) {
        Map<String, Object> result = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                Object value = jsonObject.get(key);
                if (value instanceof String) {
                    result.put(key, value);
                } else if (value instanceof Integer) {
                    result.put(key, value);
                } else if (value instanceof Double) {
                    result.put(key, value);
                } else {
                    Log.w(AppLauncher.TAG, "Value for key " + key + " not one of [String, Integer, Double]");
                }
            }
        } catch (JSONException e) {
            Log.w(AppLauncher.TAG, "Failed to parse JSON, returning empty Bundle.", e);
            return new HashMap<>();
        }
        return result;
    }
}