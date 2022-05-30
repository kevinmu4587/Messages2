package kevin.android.texts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.emoji2.bundled.BundledEmojiCompatConfig;
import androidx.emoji2.text.EmojiCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Gson gson = new Gson();
        SharedPreferences sharedPref = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
        // load timeline
        String jsonTimeline = sharedPref.getString("Timeline", null);
        if (jsonTimeline != null) {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            GameManager.timeline = gson.fromJson(jsonTimeline, type);
        } else {
            loadTimeline();
        }

        // load the GameManager key decisions from shared preferences
        String json = sharedPref.getString("MyHashMap", null);
        GameManager.firstRun = sharedPref.getBoolean("firstRun", true);
        if (json == null) return;
        Type myType = new TypeToken<Map<String, Integer>>() {}.getType();
        Map<String, Integer> keyChoices = gson.fromJson(json, myType);
        Log.e(TAG, "loading key decisions from sharedPrefs. size: " + keyChoices.size());
        GameManager.setKeyChoices(keyChoices);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // save the GameManager key decisions to shared preferences
        SharedPreferences sharedPref = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("MyHashMap", new Gson().toJson(GameManager.getKeyChoices()));
        editor.putBoolean("firstRun", GameManager.firstRun);
        Gson gson = new Gson();
        String timeline = gson.toJson(GameManager.timeline);
        editor.putString("Timeline", timeline);
        editor.apply();
        // Log.e(TAG, "Saved " + GameManager.getKeyChoices().size() + " key decisions to shared preferences");
    }

    private void loadTimeline() {
        String json = Utils.loadJSONFromAssets(getApplicationContext(), "timeline.txt");
        Utils.setupTimeline(json);
    }
}