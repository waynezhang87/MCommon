package com.waynezhang.mcommon.template.model;

import com.waynezhang.mcommon.support.image.Image;

import java.io.Serializable;

/**
 * Created by don on 1/21/16.
 */
public class TemplateImageItem extends Image implements Serializable {
    //添加图片按钮的图片资源id
    public int res_id;
    // 是否显示loading
    public boolean isLoading;

    //本地图片地址
    public String localPath;

    public String coverPath;

    public void setUrl(String small_url, String url) {
        this.url = url;
        this.smallUrl = small_url;
    }

    public TemplateImageItem() {

    }
}
