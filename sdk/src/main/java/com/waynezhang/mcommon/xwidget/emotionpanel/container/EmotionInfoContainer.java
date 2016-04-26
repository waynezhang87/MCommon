package com.waynezhang.mcommon.xwidget.emotionpanel.container;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.util.ResourceHelper;
import com.waynezhang.mcommon.util.XmlPullParserUtil;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Created by waynezhang on 7/7/15.
 */
public class EmotionInfoContainer {
    private final static String TAG="EmotionInfoContainer";

    public static Map<String, String> mapPngEmotionTxtInfoGlobal = new HashMap<String, String>();
    public static Map<String, Integer> mapPngEmotionInfoGlobal = new HashMap<String, Integer>();
    public static Map<String, Integer> mapGifEmotionInfoGlobal = new HashMap<String, Integer>();
    public static Map<String, EmotionInfo> vecPageEmotionInfoVecGlobal = new HashMap<String, EmotionInfo>();
    public static int totalPageSizeGlobal = 0;
    public static boolean hasInitGlobal = false;

    public static Map<String, String> mapPngEmotionTxtInfoLocal = new HashMap<String, String>();
    public static Map<String, Integer> mapPngEmotionInfoLocal = new HashMap<String, Integer>();
    public static Map<String, Integer> mapGifEmotionInfoLocal = new HashMap<String, Integer>();
    public static Vector<Vector<EmotionPageInfo>> vecPageEmotionInfoVecLocal = new Vector<Vector<EmotionPageInfo>>();
    public static int totalPageSizeLocal = 0;
    public static int[] tabIconResIdArray;
    public static String[] tabTxtArray;

    public static void initGlobalEmotionInfo(Context context, int[] _rawResIdArray) {
        if (hasInitGlobal) {
            return;
        }
        //若初始化时未传入相应表情配置xml, 则使用默认表情配置
        if (_rawResIdArray == null) {
            String[] rawResIdStringArray = context.getResources().getStringArray(R.array.emotion_container_array_default);
            _rawResIdArray = new int[rawResIdStringArray.length];
            for (int i = 0 ; i < rawResIdStringArray.length; i++) {
                _rawResIdArray[i] = ResourceHelper.getId(context, "R.raw." + rawResIdStringArray[i]);
            }
        }

        totalPageSizeGlobal = 0;
        mapPngEmotionInfoGlobal.clear();
        mapGifEmotionInfoGlobal.clear();
        mapPngEmotionTxtInfoGlobal.clear();
        vecPageEmotionInfoVecGlobal.clear();

        for (int m = 0; m < _rawResIdArray.length; m++) {
            if (_rawResIdArray[m] == 0) {
                continue;
            }
            InputStream in = context.getResources().openRawResource(_rawResIdArray[m]);

            if (in == null) {
                return;
            }

            Vector<EmotionPageInfo> vecPageEmotionInfo = new Vector<EmotionPageInfo>();

            //解析xml
            XmlPullParserUtil.parsePageEmotion(in, "UTF-8", vecPageEmotionInfo, mapPngEmotionTxtInfoGlobal);

            for (int i = 0; i < vecPageEmotionInfo.size(); ++i) {
                Vector<EmotionInfo> vec = vecPageEmotionInfo.get(i).getVecEmotionInfo();
                for (int j = 0; j < vec.size(); j++) {
                    int resId = ResourceHelper.getId(context, vec.get(j).getIcon());
                    vec.get(j).setResId(resId);
                    if (resId == 0) {
                        continue;
                    }

                    if (vecPageEmotionInfo.get(i).getType() == EmotionPageInfo.PNG_BIG
                            || vecPageEmotionInfo.get(i).getType() == EmotionPageInfo.PNG_SMALL) {

                        mapPngEmotionInfoGlobal.put(vec.get(j).getName(), resId);


                    }
                    if (vecPageEmotionInfo.get(i).getType() == EmotionPageInfo.GIF) {
                        mapGifEmotionInfoGlobal.put(vec.get(j).getName(), resId);
                    }

                    vecPageEmotionInfoVecGlobal.put(vec.get(j).getName(), vec.get(j));
                }
            }
            totalPageSizeGlobal += vecPageEmotionInfo.size();
            // vecPageEmotionInfoVecGlobal.add(vecPageEmotionInfo);
        }
        hasInitGlobal = true;
    }

    public static void initLocalEmotionInfo(Context context, int[] _rawResIdArray) {
        //若初始化时未传入相应表情配置xml, 则使用默认表情配置
        if (_rawResIdArray == null) {
            String[] rawResIdStringArray = context.getResources().getStringArray(R.array.emotion_container_array_default);
            _rawResIdArray = new int[rawResIdStringArray.length];
            for (int i = 0 ; i < rawResIdStringArray.length; i++) {
                _rawResIdArray[i] = ResourceHelper.getId(context, "R.raw." + rawResIdStringArray[i]);
            }
        }

        tabIconResIdArray = new int[_rawResIdArray.length];
        tabTxtArray = new String[_rawResIdArray.length];

        totalPageSizeLocal = 0;
        mapPngEmotionInfoLocal.clear();
        mapGifEmotionInfoLocal.clear();
        mapPngEmotionTxtInfoLocal.clear();
        vecPageEmotionInfoVecLocal.clear();

        for (int m = 0; m < _rawResIdArray.length; m++) {
            if (_rawResIdArray[m] == 0) {
                continue;
            }
            InputStream in = context.getResources().openRawResource(_rawResIdArray[m]);

            if (in == null) {
                return;
            }

            Vector<EmotionPageInfo> vecPageEmotionInfo = new Vector<EmotionPageInfo>();

            //解析xml, 并获得表情底栏tab的资源
            String[] tabResArray = XmlPullParserUtil.parsePageEmotion(in, "UTF-8", vecPageEmotionInfo, mapPngEmotionTxtInfoLocal);
            tabIconResIdArray[m] = ResourceHelper.getId(context, "R.drawable." + tabResArray[0]);
            tabTxtArray[m] = tabResArray[1];

            for (int i = 0; i < vecPageEmotionInfo.size(); ++i) {
                Vector<EmotionInfo> vec = vecPageEmotionInfo.get(i).getVecEmotionInfo();
                for (int j = 0; j < vec.size(); j++) {
                    int resId = ResourceHelper.getId(context, vec.get(j).getIcon());
                    vec.get(j).setResId(resId);
                    if (resId == 0) {
                        continue;
                    }
                    if (vecPageEmotionInfo.get(i).getType() == EmotionPageInfo.PNG_BIG
                            || vecPageEmotionInfo.get(i).getType() == EmotionPageInfo.PNG_SMALL) {

                        mapPngEmotionInfoLocal.put(vec.get(j).getName(), resId);


                    }
                    if (vecPageEmotionInfo.get(i).getType() == EmotionPageInfo.GIF) {
                        mapGifEmotionInfoLocal.put(vec.get(j).getName(), resId);

                    }
                }
            }
            totalPageSizeLocal += vecPageEmotionInfo.size();
            vecPageEmotionInfoVecLocal.add(vecPageEmotionInfo);
        }
    }

    public static boolean IsBigEmotion(String name)
    {
        boolean flag = false;
        Log.d(TAG, "IsBigEmotion name=" + name);

        EmotionInfo einfo = vecPageEmotionInfoVecGlobal.get(name);
        if (einfo != null && einfo.getType() == EmotionInfo.EMOTION_BIG)
        {
            Log.d(TAG, "IsBigEmotion 2 name=" + name);

            return  true;
        }

//        for (int i = 0; i < vecPageEmotionInfoVecLocal.size(); i++)
//        {
//            Vector<EmotionPageInfo> infos = vecPageEmotionInfoVecLocal.get(i);
//            if (infos == null) break;
//            for (int j = 0; j < infos.size(); j++)
//            {
//                EmotionPageInfo infosh = infos.get(j);
//                if (infosh == null) break;
//
//                Vector<EmotionInfo> eis = infosh.getVecEmotionInfo();
//                for (int h = 0; h < eis.size(); h++ )
//                {
//                    EmotionInfo einfo = eis.get(h);
//
//                    if (einfo == null) break;
//                    if (einfo.getType() == EmotionInfo.EMOTION_BIG && einfo.getName().equals(name))
//                    {
//                        Log.d(TAG, "IsBigEmotion big image is exist name=" + name);
//                        return  true;
//                    }
//                }
//            }
//        }
        return flag;
    }

    /**
     * 将表情id转换为可读文字，比如xml文件中的name
     * @param text
     * @return
     */
    public static String convertEmotionToText(final String text){
        String result = text;
        if (!TextUtils.isEmpty(result) && EmotionInfoContainer.mapPngEmotionTxtInfoGlobal != null) {
            Set<Map.Entry<String, String>> set = EmotionInfoContainer.mapPngEmotionTxtInfoGlobal.entrySet();
            for (Iterator<Map.Entry<String, String>> it = set.iterator(); it.hasNext();) {
                Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
                result = result.replace(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
}
