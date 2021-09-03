package kevin.android.texts;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// this class is for saving game-wide decisions, like which blocks were picked, and what ending were reached
public class GameManager {
    private static Map<String, Integer> keyChoices = new HashMap<String, Integer>();
//    private static int returnToBlock = -1;
//    private static List<Integer> precedingBlocks = new ArrayList<>();

    public GameManager() {
    }

//    public static int getReturnToBlock() {
//        return returnToBlock;
//    }
//
//    public static void setReturnToBlock(int returnToBlock) {
//        GameManager.returnToBlock = returnToBlock;
//    }

//    public static void addPrecedingBlock(int block) {
//        if (precedingBlocks.contains(Integer.valueOf(block))) {
//            return;
//        }
//        precedingBlocks.add(Integer.valueOf(block));
//    }
//
//    public static int[] getPrecedingBlocks() {
//        int[] ret = new int[precedingBlocks.size()];
//        for(int i = 0; i < precedingBlocks.size();i++)
//            ret[i] = precedingBlocks.get(i);
//        return ret;
//    }

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
}
