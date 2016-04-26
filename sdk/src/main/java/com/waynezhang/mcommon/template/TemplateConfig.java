package com.waynezhang.mcommon.template;

import java.util.Random;

/**
 * Created by sunxinxin on 11/5/15.
 */
public class TemplateConfig {

    public static String RANDOM_KEY = "";//一次会话的RANDOMKEY 不保存

    public static final String RANDOM_RULES = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890!@#$%^&*()";
    public static final int RANDOM_SIZE = 32;

    public static String resetRandomKey(){
        boolean rpoint = false;
        StringBuffer generateRandStr = new StringBuffer();
        Random rand = new Random();
        int var5 = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890!@#$%^&*()".length();

        for(int i = 0; i < 8; ++i) {
            int randNum = rand.nextInt(var5);
            generateRandStr.append("QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890!@#$%^&*()".substring(randNum, randNum + 1));
        }

        RANDOM_KEY = generateRandStr + "";

        return RANDOM_KEY;
    }
}
