package com.waynezhang.mcommon.template.model;

import java.io.Serializable;

/**
 * Created by don on 2/1/16.
 */
public class SimplateTemplateImage implements Serializable{
    public String url;
    public String smallUrl;

    public SimplateTemplateImage(String url , String smallUrl){
        this.url = url;
        this.smallUrl = smallUrl;
    }

}
