package com.waynezhang.mcommon.util;

import android.content.Context;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListPopupWindow;
import android.widget.PopupMenu;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by don on 12/29/14.
 */
public final class PopupMenuHelper {
    public static void showIcon(PopupMenu popup){
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static void setMenuWidth(PopupMenu popup, int width) {
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Field[] fields1 = menuPopupHelper.getClass().getDeclaredFields();
                    for (Field field1 : fields1) {
                        if ("mPopup".equals(field1.getName())) {
                            field1.setAccessible(true);
                            ListPopupWindow popupWindow = (ListPopupWindow) field1.get(menuPopupHelper);
                            popupWindow.setWidth(width);
                            popupWindow.setPromptPosition(ListPopupWindow.POSITION_PROMPT_BELOW);
                            popupWindow.postShow();
                            break;
                        }
                    }
                    break;
                }
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public static PopupMenu createWithIcon(int menuResId, Context context, View anchor){
        PopupMenu popup = new PopupMenu(context, anchor);
        PopupMenuHelper.showIcon(popup);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(menuResId, popup.getMenu());
        return popup;
    }

    public static PopupMenu createWithoutIcon(int menuResId, Context context, View anchor){
        PopupMenu popup = new PopupMenu(context, anchor);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(menuResId, popup.getMenu());
        return popup;
    }
}
