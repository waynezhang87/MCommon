package com.waynezhang.mcommon.template.listener;

import com.waynezhang.mcommon.template.interfaces.TemplateImageCallback;
import com.waynezhang.mcommon.template.model.TemplateImageItem;
import com.waynezhang.mcommon.template.response.TemplateResponse;
import com.waynezhang.mcommon.template.widget.TemplateImageView;
import com.waynezhang.mcommon.xwidget.SimpleArrayAdapter;

/**
 * Created by don on 1/21/16.
 */
public interface GroupImgFieldChangedListener {
    
    void onGroupImgFieldChangedListener(String group, TemplateResponse.TemplateField templateField, String value);
    //添加图片的回调
    public void addImageCallback(String group, TemplateResponse.TemplateField templateField , TemplateImageItem item, TemplateImageCallback callback);

    //删除图片的回调
    public void deleteImageCallback(TemplateImageItem item, TemplateImageCallback callback);

    //长按
    void OnItemLongClickListener(TemplateImageItem item , SimpleArrayAdapter<TemplateImageItem, TemplateImageView.TemplateImageItemView> PicAdapter);
}
