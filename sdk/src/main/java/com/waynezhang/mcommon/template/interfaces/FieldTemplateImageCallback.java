package com.waynezhang.mcommon.template.interfaces;

import com.waynezhang.mcommon.template.model.TemplateImageItem;
import com.waynezhang.mcommon.template.response.TemplateResponse;
import com.waynezhang.mcommon.template.widget.TemplateImageView;
import com.waynezhang.mcommon.xwidget.SimpleArrayAdapter;

/**
 * Created by don on 1/27/16.
 */
public interface FieldTemplateImageCallback {

    public void addImgCallback(TemplateResponse.TemplateField templateField,TemplateImageItem item,TemplateImageCallback.AfterCallback afterCallback);

    public void deleteImgCallback(TemplateResponse.TemplateField templateField,TemplateImageItem item,TemplateImageCallback.AfterCallback afterCallback);

    public void OnItemLongClickListener(TemplateResponse.TemplateField templateField,TemplateImageItem item , SimpleArrayAdapter<TemplateImageItem, TemplateImageView.TemplateImageItemView> PicAdapter);


}
