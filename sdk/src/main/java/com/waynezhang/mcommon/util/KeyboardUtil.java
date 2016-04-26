package com.waynezhang.mcommon.util;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by panhongchao.payne on 2015/1/21.
 */
public class KeyboardUtil {
    InputMethodManager imm;

    public KeyboardUtil(Context context){
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void showAndHideKeyboard() {
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showKeyboard(EditText view) {
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideKeyboard(EditText view) {
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean isShow() {
        return imm.isActive();
    }

    public void hideAlways(Activity activity) {
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
