package kevin.android.texts;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import kevin.android.texts.Ending.Ending;

public class Utils {
    private static final String TAG = "Utils";

    public static int[] listToIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        for (int i = 0; i < list.size(); i++)
            ret[i] = list.get(i);
        return ret;
    }

    public static String loadFileFromAssets(Context context, String file) {
        String json;
        try {
            InputStream is = context.getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static void setupTimeline(String json) {
        // Log.e(TAG, json);
        String[] lines = json.split("\n");
        for (String line : lines) {
            if (line.charAt(0) != '%') {
                GameManager.timeline.add(line);
            }
        }
    }

    public static void setupEndings(String json) {
        try {
            JSONArray endings = new JSONArray(json);
            int len = endings.length();
            GameManager.endingsFound = new int[len];
            for (int i = 0; i < len; ++i) {
                JSONObject jsonObject = endings.getJSONObject(i);
                String title = jsonObject.getString("endingName");
                String description = jsonObject.getString("description");
                String guide = jsonObject.getString("unlockBy");
                GameManager.allEndingsList.add(new Ending(title, description, guide));
                GameManager.endingsFound[i] = 0;
            }
        } catch (JSONException x) {
            Log.e(TAG, "JSONException when parsing endings," + x);
        }

    }

    public static boolean contains(String needle, String[] haystack) {
        for (String h : haystack) {
            if (needle.equals(h)) {
                return true;
            }
        }
        return false;
    }

    public static String replaceName(String old) {
        if (old.contains("%playername")) {
            old = old.replace("%playername", GameManager.playerFirstName);
        }
        if (old.contains("%playerlastname")) {
            old = old.replace("%playerlastname", GameManager.playerLastName);
        }
        if (old.contains("%playernickname")) {
            old = old.replace("%playernickname", GameManager.playerNickname);
        }
        if (old.contains("%npc1name")) {
            old = old.replace("%npc1name", GameManager.npc1FirstName);
        }
        if (old.contains("%npc1lastname")) {
            old = old.replace("%npc1lastname", GameManager.npc1LastName);
        }
        if (old.contains("%npc2name")) {
            old = old.replace("%npc2name", GameManager.npc2FirstName);
        }
        if (old.contains("%npc2lastname")) {
            old = old.replace("%npc2lastname", GameManager.npc2LastName);
        }
        if (old.contains("%friendname")) {
            old = old.replace("%friendname", GameManager.friendFirstName);
        }
        return old;
    }
}