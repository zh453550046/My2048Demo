package lm.wh.com.my2048.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2015/11/20.
 */
public class SPUtil {

    private static SharedPreferences sp;

    private static final String SP_PLAYER_SCORE = "playerScore", SP_NAME = "data";

    private static SharedPreferences getInstance(Context context) {
        if (sp == null) {
            synchronized (SPUtil.class) {
                if (sp == null) {
                    sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                }
            }
        }
        return sp;
    }

    public static void savePlayScore(Context context, int score) {
        SharedPreferences.Editor editor = getInstance(context).edit();
        editor.putInt(SP_PLAYER_SCORE, score);
        editor.commit();
    }

    public static int getPlayScore(Context context) {
        return getInstance(context).getInt(SP_PLAYER_SCORE, 0);
    }
}
