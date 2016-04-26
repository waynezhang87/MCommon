package com.waynezhang.mcommon.support.image.selector;

import android.text.TextUtils;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则计算器
 *
 * Created by panhongchao.payne on 2015/4/21.
 */
public class ExpCalculate {
    public static String EXP_1 = "\\((.*?)/.*"; //匹配：(?/10)
    public static String EXP_2 = "(.*?)/.*";  //匹配：?/10
    public static String EXP_3 = "\\((.*?)\\).*";  //匹配：(?)

    public static String calculate(String str, String exp, boolean isAdd) {
        String newStr = str;
        try {
            Pattern pattern = Pattern.compile(exp);
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                String finds = matcher.group(1);
                if(!TextUtils.isEmpty(finds)){
                    int curCount = Integer.valueOf(finds);
                    curCount = isAdd ? ++curCount : --curCount;
                    newStr = str.replaceFirst(finds, curCount+"");
                }
            }
        } catch(Exception e) {
            Log.e("error", "calculate error", e);
        }

        return newStr;
    }

    public static String calculate(String str, String exp, String newValue) {
        String newStr = str;
        try {
            Pattern pattern = Pattern.compile(exp);
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                String finds = matcher.group(1);
                if(!TextUtils.isEmpty(finds)){
                    newStr = str.replaceFirst(finds, newValue);
                }
            }
        } catch(Exception e) {
            Log.e("error", "calculate error", e);
        }

        return newStr;
    }
}
