package com.waynezhang.mcommon.template.interfaces;

import com.waynezhang.mcommon.template.model.TemplateImageItem;
import com.waynezhang.mcommon.template.widget.TemplateImageView;
import com.waynezhang.mcommon.xwidget.SimpleArrayAdapter;

/**
 * Created by don on 1/21/16.
 */
public interface TemplateImageCallback {

    public static interface AfterCallback {
        public void callback(TemplateImageItem item);
    }

    public void addImgCallback(TemplateImageItem item,AfterCallback afterCallback);

    public void deleteImgCallback(TemplateImageItem item,AfterCallback afterCallback);

    public void OnItemLongClickListener(TemplateImageItem item , SimpleArrayAdapter<TemplateImageItem, TemplateImageView.TemplateImageItemView> PicAdapter);


}
