package kevin.android.texts;

import android.util.Log;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

public class Converters {
    @TypeConverter
    public String arrayToString(String[] array) {
        if (array.length == 0) return "";
        String string = array[0];
        for (int i = 1; i < array.length; ++i) {
            string = string + "|" + array[i];
        }
        return string;
        //return new Gson().toJson(array);
    }

    @TypeConverter
    public String[] stringToArray(String string) {
        String[] array = string.split("\\|");
//        for (String s : array) {
//            Log.e("CONVERTER", s);
//        }
        return array;
    }

    @TypeConverter
    public String listToString(List<Integer> list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public List<Integer> stringToList(String string) {
        Type type = new TypeToken<List<Integer>>() {}.getType();
        return new Gson().fromJson(string, type);
    }
}
