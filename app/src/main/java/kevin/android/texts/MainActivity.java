package kevin.android.texts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.emoji2.bundled.BundledEmojiCompatConfig;
import androidx.emoji2.text.EmojiCompat;
import androidx.preference.PreferenceManager;

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
    private Thread.UncaughtExceptionHandler defaultUEH;

    private Thread.UncaughtExceptionHandler myUEH = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
            Log.e(TAG, "uncaughtException");
            saveSharedPreferences();
            // rethrow the exception again (important)
            defaultUEH.uncaughtException(thread, throwable);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        // setup custom exception handler
        Thread.setDefaultUncaughtExceptionHandler(myUEH);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Gson gson = new Gson();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // load existing timeline/endings
        String jsonTimeline = sharedPref.getString("Timeline", null);
        if (jsonTimeline != null) {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            GameManager.timeline = gson.fromJson(jsonTimeline, type);
            Log.e(TAG, "loaded timeline as " + GameManager.timeline);
        } else {
            loadTimeline();
        }

        // load the GameManager key decisions from shared preferences
        String json = sharedPref.getString("MyHashMap", null);
        GameManager.gameCompleted = sharedPref.getBoolean("gameCompleted", false);
        GameManager.npc1FirstName = sharedPref.getString("npc1FirstName", "Default NPC1 First Name");
        GameManager.npc1LastName = sharedPref.getString("npc1LastName", "Default NPC1 Last Name");
        GameManager.npc1Nickname = sharedPref.getString("npc1Nickname", "Default NPC1 Nickname");
        GameManager.friendFirstName = sharedPref.getString("friendFirstName", "Default Friend First Name");
        GameManager.friendLastName = sharedPref.getString("friendLastName", "Default Friend Last Name");
        GameManager.friendNickname = sharedPref.getString("friendNickname", "Default Friend Nickname");
        GameManager.npc2FirstName = sharedPref.getString("npc2FirstName", "Default NPC2 First Name");
        GameManager.npc2LastName = sharedPref.getString("npc2LastName", "Default NPC2 Last Name");
        GameManager.npc2Nickname = sharedPref.getString("npc2Nickname", "Default NPC2 Nickname");
        GameManager.nextInsertNum = sharedPref.getInt("nextInsertNum", 0);
        if (json == null) return;
        Type myType = new TypeToken<Map<String, Integer>>() {}.getType();
        Map<String, Integer> keyChoices = gson.fromJson(json, myType);
        Log.e(TAG, "loading key decisions from sharedPrefs. size: " + keyChoices.size());
        GameManager.setKeyChoices(keyChoices);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSharedPreferences();
    }

    private void saveSharedPreferences() {
        Log.e(TAG, "saving shared preferences");
        // save the GameManager key decisions to shared preferences
        // SharedPreferences sharedPref = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        editor.putString("MyHashMap", gson.toJson(GameManager.getKeyChoices()));
//        editor.putBoolean("firstRun", GameManager.firstRun);
        editor.putBoolean("gameCompleted", GameManager.gameCompleted);
        editor.putString("npc1FirstName", GameManager.npc1FirstName);
        editor.putString("npc1LastName", GameManager.npc1LastName);
        editor.putString("npc1Nickname", GameManager.npc1Nickname);
        editor.putString("friendFirstName", GameManager.friendFirstName);
        editor.putString("friendLastName", GameManager.friendLastName);
        editor.putString("friendNickname", GameManager.friendNickname);
        editor.putString("npc2FirstName", GameManager.npc2FirstName);
        editor.putString("npc2LastName", GameManager.npc2LastName);
        editor.putString("npc2Nickname", GameManager.npc2Nickname);
        editor.putInt("nextInsertNum", GameManager.nextInsertNum);
        String timeline = gson.toJson(GameManager.timeline);
        Log.e(TAG, "saving timeline to sharedPrefs: " + timeline);
        editor.putString("Timeline", timeline);
        editor.commit();
        Log.e(TAG, "Saved " + GameManager.getKeyChoices().size() + " key decisions to shared preferences");
    }

    private void loadTimeline() {
//        String json = Utils.loadFileFromAssets(getApplicationContext(), "timeline (full)");
        String json = Utils.loadFileFromAssets(getApplicationContext(), "timeline");
        Utils.setupTimeline(json);
    }
}