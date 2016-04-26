package com.waynezhang.mcommon.compt;

import android.os.Build;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Created by liuyagang on 16/3/2.
 */
public class TextViewCompt {
    public static int getHighlightColor(TextView textView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return textView.getHighlightColor();
        }
        try {
            Field field = textView.getClass().getDeclaredField("mHighlightColor");
            field.setAccessible(true);
            return field.getInt(textView);
        } catch (NoSuchFieldException e) {
            return 0;
        } catch (IllegalAccessException e) {
            return 0;
        }
    }
}
