package com.waynezhang.mcommon.support.image;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by don on 12/31/14.
 */
public class Image implements Serializable{
    private static final long serialVersionUID = 1L;

    public String url;
    public String smallUrl;
    public String link;

    public int smallImageResId;
    public int imageResId;

    public int smallWidth;
    public int smallHeight;
    public Date timestamp;

    public Image(){
    }

    public Image(String url , String smallUrl){
       this.url = url;
        this.smallUrl = smallUrl;
    }

    public Image(int smallImageResId, int imageResId){
        this.smallImageResId = smallImageResId;
        this.imageResId = imageResId;
    }
}
