package kevin.android.texts;

import java.util.List;

public class Utils {
    public static int[] listToIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        for(int i = 0; i < list.size(); i++)
            ret[i] = list.get(i);
        return ret;
    }
}
