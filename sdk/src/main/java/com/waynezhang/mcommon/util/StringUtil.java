package com.waynezhang.mcommon.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    static String[] units = {"", "十", "百", "千"};
    static char[] numArray = {'零', '一', '二', '三', '四', '五', '六', '七', '八', '九'};


    public static boolean isSpace(String str) {
        for (int i = 0; i < str.length(); i++) {
            int chr = (int) str.charAt(i);
            if (chr != 32)
                return false;
        }
        return true;
    }

    public static boolean isNull(String str, boolean bValidNullString) {
        boolean b = false;
        if (str == null || str.trim().length() == 0)
            b = true;
        if (!b && bValidNullString) {
            if (str != null && str.equalsIgnoreCase("null"))
                b = true;
        }
        return b;
    }

    public static boolean isUrl(String str) {
        if (isEmpty(str))
            return false;
        return str.matches("^http://(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*(\\?\\S*)?$");
    }

    public static boolean str2Boolean(String s, boolean defaultV) {
        if (isEmpty(s))
            return defaultV;
        if (s != null && s.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }

    public static int str2Int(String s, int defaultV) {
        if (s != null && !s.equals("")) {
            int num = defaultV;
            try {
                num = Integer.parseInt(s);
            } catch (Exception ignored) {
            }
            return num;
        } else {
            return defaultV;
        }
    }

    public static long str2Long(String s, long defaultV) {
        if (s != null && !s.equals("")) {
            long num = defaultV;
            try {
                num = Long.parseLong(s);
            } catch (Exception ignored) {
            }
            return num;
        } else {
            return defaultV;
        }
    }

    public static double str2Double(String s, double defaultV) {
        if (s != null && !s.equals("")) {
            double num = defaultV;
            try {
                num = Double.parseDouble(s);
            } catch (Exception ignored) {
            }
            return num;
        } else {
            return defaultV;
        }
    }

    public static float str2Float(String s, float defaultV) {
        if (s != null && !s.equals("")) {
            float num = defaultV;
            try {
                num = Float.parseFloat(s);
            } catch (Exception ignored) {
            }
            return num;
        } else {
            return defaultV;
        }
    }

    public static boolean IsChinese(char c) {
        return (int) c >= 0x4E00 && (int) c <= 0x9FA5;
    }

    /**
     * 判断是否全为数字和字母
     *
     * @param s
     * @return
     */
    public static boolean IsNumberOrCharacter(String s) {
        boolean isValid = false;
        String expression = "[a-zA-Z0-9]+";
        CharSequence inputStr = s;
        /* 创建Pattern */
        Pattern pattern = Pattern.compile(expression);
		/* 将Pattern 以参数传入Matcher作Regular expression */
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isNumber(String number) {
        boolean isValid = false;
        // String regExp = "^[1][0-9]{10}$";
        String regExp = "[0-9]+";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(number);
        if (m.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * 判断是否全为数字和字母，并且字母开头
     *
     * @param s
     * @return
     */
    public static boolean IsNumberOrCharacterAndFC(String s) {
        boolean isValid = false;
        String expression = "[a-zA-Z]{1}[a-zA-Z0-9]+";
        CharSequence inputStr = s;
		/* 创建Pattern */
        Pattern pattern = Pattern.compile(expression);
		/* 将Pattern 以参数传入Matcher作Regular expression */
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /*
     * Version format like x.x.x.x if oldVersion > newVersion return false else
     * return true;(need update)
     */
    public static boolean compareVersion(String oldVersion, String newVersion) {
        boolean tempState = false;
        if (isEmpty(oldVersion) == true || oldVersion.indexOf(".") < 0)
            tempState = true;
        if (isEmpty(newVersion) == true || newVersion.indexOf(".") < 0)
            tempState = false;
        oldVersion = getRealVersion(oldVersion);
        newVersion = getRealVersion(newVersion);
        String[] oldVersions = oldVersion.split("\\.");
        String[] newVersions = newVersion.split("\\.");
        if (oldVersions.length == newVersions.length) {
            for (int i = 0; i < oldVersions.length; i++) {
                int oldNum = str2Int(oldVersions[i], -1);
                int newNum = str2Int(newVersions[i], -1);
                if (oldNum > newNum) {
                    tempState = false;
                    break;
                } else if (oldNum < newNum) {
                    tempState = true;
                    break;
                }
            }
        } else {
            if (oldVersions.length < newVersions.length) {
                tempState = true;
            } else {
                tempState = false;
            }
        }
        return tempState;
    }

    public static String getRealVersion(String version) {
        String rVersion = "";
        if (isEmpty(version) == true)
            return rVersion;
        rVersion = version;
        int indexChar = version.indexOf("_");
        if (indexChar > -1) {
            rVersion = version.substring(0, indexChar);
        }
        return rVersion;
    }

    private static boolean isExist(int iId, List<Integer> iList) {
        boolean result = false;
        for (int i : iList) {
            if (i == iId) {
                result = true;
            }
        }
        return result;
    }

    public static List<Integer> getList(int iTotalNumber, int iMaxNumber) {
        List<Integer> mList = new ArrayList<Integer>();
        Random rnd = new Random();
        while (mList.size() < iTotalNumber) {
            int id = rnd.nextInt(iMaxNumber);
            if (!isExist(id, mList)) {
                mList.add(id);
            }
        }
        return mList;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !StringUtil.isEmpty(str);
    }

    public static boolean isBlank(CharSequence str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(String str) {
        return !StringUtil.isBlank(str);
    }

    public static boolean isNumeric(CharSequence cs) {
        if (cs == null || cs.length() == 0) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static String getValueByKey(String result, String key) {
        String value = null;
        if (!isEmpty(result)) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String resultCode = jsonObject.getString("Result");
                if (resultCode != null && "0".equals(resultCode)) {
                    value = jsonObject.getString(key);
                } else {
                    System.out.println("result may be have some problem .");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    public static boolean checkHpsTimeOut(String result) {
        boolean flag = false;
        if (!isEmpty(result)) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String resultCode = jsonObject.getString("Result");
                if (resultCode != null && "-10242504".equals(resultCode)) {
                    flag = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static boolean checkCallBackStat(int code, String sessionId) {
        boolean stat = false;
        if (code == 0 && !isEmpty(sessionId)) {
            stat = true;
        }
        return stat;
    }

    //判断是否为手机号
    public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    public static final String toHexString(byte[] in) {
        int len = in.length;
        StringBuilder sb = new StringBuilder(len * 2);
        String tmp;
        for (int i = 0; i < len; i++) {
            tmp = Integer.toHexString(in[i] & 0xFF);
            if (tmp.length() < 2) {
                sb.append(0);
            }
            sb.append(tmp);
        }
        return sb.toString();
    }

    public static String jsStringEncode(String js) {
        if (js == null) {
            return null;
        }
        return js.replace("\\", "\\\\").replace("\'", "\\\'").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
    }

    public static boolean isEmail(String email) {
        try {
            String str = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern p = Pattern.compile(str);
            Matcher m = p.matcher(email);
            return m.matches();
        } catch (Exception e) {
            return false;
        }
    }

    public static Vector<Integer> findStringIndex(String source, String sub) {
        Vector<Integer> vec = new Vector<Integer>();

        int from = 0;
        while (true) {
            int i = source.indexOf(sub, from);
            if (i == -1) {
                break;
            }
            vec.add(i);
            from = i + sub.length();
        }

        return vec;
    }

//	private static boolean isDrawbale(String source, int pos){
//		if (pos == 0) return true;
//		int count = 0;
//		while(source.charAt(--pos) == '#'){
//			count++;
//		}
//
//		return  (count % 2) == 0 ;
//	}

    // "##" to "#"
    public static String removeEscapeEnable(String raw) {
        String strRet = raw.replace("##", "#");
        return strRet;
    }

    public static Vector<Integer> findStringIndexForEscape(String source, String sub) {
        Vector<Integer> vec = new Vector<Integer>();
        source = source.replace("##", ",");
        int from = 0;
        while (true) {
            int i = source.indexOf(sub, from);
            if (i == -1) {
                break;
            }

//			if (!isDrawbale(source, i)){
//				continue;
//			}

            vec.add(i);
            from = i + sub.length();
        }

        return vec;
    }

    public static boolean isInRange(float min, float max, String value) {
        boolean inRange = false;
        try {
            Float newValue = Float.parseFloat(value);
            if (min <= newValue && max >= newValue) {
                inRange = true;
            }
        } catch (Exception e) {
            return false;
        }
        return inRange;
    }


    public static String formatInteger(int num) {
        char[] val = String.valueOf(num).toCharArray();
        int len = val.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            String m = val[i] + "";
            int n = Integer.valueOf(m);
            boolean isZero = n == 0;
            String unit = units[(len - 1) - i];
            if (isZero) {
                if ('0' == val[i - 1]) {
                    continue;
                } else {
                    sb.append(numArray[n]);
                }
            } else {
                sb.append(numArray[n]);
                sb.append(unit);
            }
        }
        return sb.toString();
    }
}
