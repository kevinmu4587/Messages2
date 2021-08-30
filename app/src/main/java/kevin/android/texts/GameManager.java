package kevin.android.texts;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

// this class is for saving game-wide decisions, like which blocks were picked, and what ending were reached
public class GameManager {
    private Map<String, Integer> keyChoices = new HashMap<String, Integer>();

    public GameManager() {
    }

    public Map<String, Integer> getKeyChoices() {
        return keyChoices;
    }

    public int getKeyDecision(String block) {
        Integer choice = keyChoices.get(block);
        if (choice == null) {
            return -1;
        } else {
            return choice;
        }
    }

    public void addKeyDecision(String block, int choice) {
        keyChoices.put(block, Integer.valueOf(choice));
    }
}
