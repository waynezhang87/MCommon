package com.waynezhang.mcommon.template.interfaces;

import com.waynezhang.mcommon.template.model.TemplateImageItem;

import java.util.List;

/**
 * Created by sunxinxin on 10/28/15.
 */
public interface ITemplateImageView extends ITemplateView{
     void setMiniCount(int count);
     void setMaxCount(int count);

     //图片加载过程中的占位图
     int getPlaceholderResId();


     //获取提示
     String getTip();

     //获取添加图片的资源id
     int getAddImageResId();

     void setDescription(String description);

     String getDescription();

     List<TemplateImageItem> getImageList();

     void setTemplateImageCallback(TemplateImageCallback callback);
}
