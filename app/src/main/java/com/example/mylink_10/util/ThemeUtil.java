package com.example.mylink_10.util;

import android.content.Context;

import com.example.mylink_10.R;

public class ThemeUtil {
    public static void setTheme(Context context) {
        //获取之前选择的选项
        int themeType = getValuesUtil.getIntValue(context,"themeType"); //第一次默认亮色主题
        if (themeType == 0) {
            context.setTheme(R.style.AppTheme);
        } else {
            context.setTheme(R.style.AppTheme2);
        }
    }
}
