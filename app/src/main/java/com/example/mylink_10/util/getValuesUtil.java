package com.example.mylink_10.util;

import android.content.Context;

public class getValuesUtil {
    /**
     * 查询SharedPreference中内容
     * @param context   Activity上下文
     * @param valueName 所需要获取的值
     * @return          所取出的值，默认为空字符串
     */
    public static String getStrValue(Context context,String valueName) {
       return context.getSharedPreferences("option-config",Context.MODE_PRIVATE).getString(valueName,"");
    }
    public static int getIntValue(Context context,String valueName) {
       return context.getSharedPreferences("option-config",Context.MODE_PRIVATE).getInt(valueName,0);
    }

    public static boolean getBooValue(Context context,String valueName) {
        return context.getSharedPreferences("option-config",Context.MODE_PRIVATE).getBoolean(valueName,false);
    }
}
