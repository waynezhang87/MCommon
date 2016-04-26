package com.waynezhang.mcommon.support.image.selector;

import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 将其他类型的model类转换成ItemSelectPic模型
 *
 * Created by panhongchao.payne on 2015/3/24.
 */
public class Convertor {

    /**
     * @param sparseArray 原始的数据
     * @param smallUrlName 原始泛型T中小图field的名称
     * @param bigUrlName 原始泛型T中大图field的名称
     * @param <T> 泛型
     * @return
     */
    public static <T> ArrayList<ItemSelectPic> convert(Class cla, ArrayList<T> sparseArray, String smallUrlName, String bigUrlName) {
        ArrayList<ItemSelectPic> itemSelectPicArrayList = new ArrayList<>();
        try {
            for (int i=0;i<sparseArray.size();i++) {
                ItemSelectPic itemSelectPic = new ItemSelectPic();
                T t = sparseArray.get(i);
                Field big = cla.getDeclaredField(bigUrlName);
                big.setAccessible(true);
                itemSelectPic.url = big.get(t)==null ? "" : String.valueOf(big.get(t));
                Field small = cla.getDeclaredField(smallUrlName);
                small.setAccessible(true);
                itemSelectPic.small_url = small.get(t)==null ? "" : String.valueOf(small.get(t));
                // 对于没有小图的模型，直接将大图赋值给小图
                if(TextUtils.isEmpty(itemSelectPic.small_url)) {
                    itemSelectPic.small_url = itemSelectPic.url;
                }
                itemSelectPicArrayList.add(itemSelectPic);
            }
        } catch (Exception e) {
            Log.d("", "error in reflect", e);
        }
        return itemSelectPicArrayList;
    }

    /**
     * 对于非结构体
     *
     * @param sparseArray
     * @return
     */
    public static ArrayList<ItemSelectPic> convert(List<String> sparseArray) {
        ArrayList<ItemSelectPic> itemSelectPicArrayList = new ArrayList<>();
        try {
            for (int i=0;i<sparseArray.size();i++) {
                ItemSelectPic itemSelectPic = new ItemSelectPic();
                itemSelectPic.url = sparseArray.get(i);
                itemSelectPicArrayList.add(itemSelectPic);
            }
        } catch (Exception e) {
            Log.d("", "error in reflect", e);
        }
        return itemSelectPicArrayList;
    }

}
