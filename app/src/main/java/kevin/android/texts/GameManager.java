package kevin.android.texts;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// this class is for saving game-wide decisions, like which blocks were picked, and what ending were reached
public class GameManager {
    private static final String TAG = "GameManager";

    private static Map<String, Integer> keyChoices = new HashMap<String, Integer>();
    public static boolean firstRun = true;
    public static String playerFirstName;
    public static String playerLastName;
    public static String playerNickname;
    public static String npc1FirstName;
    public static String npc1LastName;
    public static String npc1Nickname;
    public static String friendFirstName;
    public static String friendLastName;
    public static String friendNickname;
    public static String npc2FirstName;
    public static String npc2LastName;
    public static String npc2Nickname;

    public static ArrayList<String> timeline = new ArrayList<>();

    public static Map<String, Integer> getKeyChoices() {
        return keyChoices;
    }

    public static void setKeyChoices(Map<String, Integer> keyChoices) {
        GameManager.keyChoices = keyChoices;
    }

    public static int getKeyDecision(String block) {
        Integer choice = keyChoices.get(block);
        if (choice == null) {
            return -1;
        } else {
            return choice;
        }
    }

    public static void addKeyDecision(String block, int choice) {
        keyChoices.put(block, Integer.valueOf(choice));
    }

//    public static boolean isFirstRun() {
//        return firstRun;
//    }

//    public static void setFirstRun(boolean firstRun) {
//        GameManager.firstRun = firstRun;
//    }

    public static int getBackgroundIDByName(String name) {
        if (name.equals("market")) {
            return R.drawable.market;
        } else {
            return R.drawable.market;
        }
    }

    public static void setNPCNames(int id, String first, String last, String nickname) {
        switch (id) {
            case 1:
                npc1FirstName = first;
                npc1LastName = last;
                npc1Nickname = nickname;
                break;
            case 2:
                friendFirstName = first;
                friendLastName = last;
                friendNickname = nickname;
                break;
            case 3:
                npc2FirstName = first;
                npc2LastName = last;
                npc2Nickname = nickname;
                break;
            default:
                Log.e(TAG, "Error: ID " + id + " is not a valid conversation id");
        }
    }
}
