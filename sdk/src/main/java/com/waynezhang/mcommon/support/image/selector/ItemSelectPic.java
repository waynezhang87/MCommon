package com.waynezhang.mcommon.support.image.selector;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by panhongchao.payne on 2014/12/24.
 */
public class ItemSelectPic implements Parcelable {
    public String url;  // 正式展示的图片
    public String small_url;  // loading用的小图

    public boolean __isPicked; // 针对大图预览，标记是否被选中

    public boolean checkable = true;   //可以被选中

    public ItemSelectPic(){}

    public ItemSelectPic(String url, String small_url) {
        this.small_url = small_url;
        this.url = url;
    }
    private ItemSelectPic(Parcel in) {
        url = in.readString();
        small_url = in.readString();
        __isPicked = in.readByte() != 0;
        checkable = in.readByte() != 0;
    }

    public ItemSelectPic(String url) {
        this.small_url = url;
        this.url = url;
    }

    public static final Parcelable.Creator<ItemSelectPic> CREATOR = new Parcelable.Creator<ItemSelectPic>() {
        public ItemSelectPic createFromParcel(Parcel in) {
            return new ItemSelectPic(in);
        }

        public ItemSelectPic[] newArray(int size) {
            return new ItemSelectPic[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(url);
        out.writeString(small_url);
        out.writeByte((byte) (__isPicked ? 1 : 0));
        out.writeByte((byte) (checkable? 1 : 0));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==null || getClass()!=obj.getClass())
            return false;

        ItemSelectPic pic = (ItemSelectPic) obj;

        // 防止null异常
        if(small_url==null) { small_url=""; }
        if(pic.small_url==null) { pic.small_url=""; }
        if(url==null) { url=""; }
        if(pic.url==null) { pic.url=""; }

        if(small_url.equalsIgnoreCase(pic.small_url) && url.equalsIgnoreCase(pic.url)){
            return true;
        }

        return false;
    }
}
