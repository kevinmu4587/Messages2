package kevin.android.texts;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Utils {
    private static final String TAG = "Utils";

    public static int[] listToIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        for (int i = 0; i < list.size(); i++)
            ret[i] = list.get(i);
        return ret;
    }

    public static String loadJSONFromAssets(Context context, String file) {
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
            return old.replace("%playername", GameManager.playerFirstName);
        }
        return "";
//        } else if (old.contains("%npc1name")) {
//            return old.replace("%npc1name", )
//        }
    }
}
