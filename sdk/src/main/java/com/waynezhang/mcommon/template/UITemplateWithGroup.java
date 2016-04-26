package com.waynezhang.mcommon.template;

import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.waynezhang.mcommon.template.interfaces.FieldTemplateImageCallback;
import com.waynezhang.mcommon.template.interfaces.GroupTemplateImageCallback;
import com.waynezhang.mcommon.template.interfaces.IViewFactory;
import com.waynezhang.mcommon.template.interfaces.TemplateImageCallback;
import com.waynezhang.mcommon.template.listener.FieldChangedListener;
import com.waynezhang.mcommon.template.listener.GroupFieldChangedListener;
import com.waynezhang.mcommon.template.model.TemplateImageItem;
import com.waynezhang.mcommon.template.response.TemplateResponse;
import com.waynezhang.mcommon.template.widget.TemplateImageView;
import com.waynezhang.mcommon.xwidget.ProgressWithTextBar;
import com.waynezhang.mcommon.xwidget.SimpleArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxinxin on 10/28/15.
 */
public class UITemplateWithGroup {

    TemplateResponse response;
    IViewFactory factory;
    GroupFieldChangedListener groupFieldChangedListener;



    GroupTemplateImageCallback groupTemplateImageCallback;

    ProgressWithTextBar progressWithTextBar;

    Map<String, UITemplate> uiTemplateMaps = new HashMap<>();

    public UITemplateWithGroup(IViewFactory factory, String template) {
        this.factory = factory;
        Gson gson = new Gson();
        response = gson.fromJson(template, TemplateResponse.class);
    }

    public UITemplateWithGroup(IViewFactory factory, TemplateResponse result) {
        this.factory = factory;
        this.response = result;
    }

    public TemplateResponse.TemplateField getFieldWithID(String mid) {
        for (final TemplateResponse.GroupItem groupItem : response.content.groups) {
                List<TemplateResponse.TemplateField> fields = groupItem.fields;
                if (fields != null){
                    for (TemplateResponse.TemplateField templateField : fields) {
                        if (templateField.id.equals(mid)) {
                            return templateField;
                        }
                    }
                }
        }
        return null;
    }

    public void createView(ViewGroup container, String group) {
        for (final TemplateResponse.GroupItem groupItem : response.content.groups) {
            if (groupItem.id.equals(group) && groupItem.fields != null) {
                UITemplate uiTemplate = new UITemplate(factory, groupItem.fields);
                uiTemplate.setProgressWithTextBar(progressWithTextBar);
                uiTemplate.createView(container);
                uiTemplate.setFieldChangedListener(new FieldChangedListener() {
                    @Override
                    public void onFieldValueChanged(TemplateResponse.TemplateField sender, String value) {
                        if (groupFieldChangedListener != null) {
                            groupFieldChangedListener.onGroupFieldValueChanged(groupItem.text, sender, value);
                        }
                    }
                });
                uiTemplate.setTemplateImageCallback(new FieldTemplateImageCallback() {
                    @Override
                    public void addImgCallback(TemplateResponse.TemplateField templateField, TemplateImageItem item, TemplateImageCallback.AfterCallback afterCallback) {
                        if (groupTemplateImageCallback != null) {
                            groupTemplateImageCallback.addImgCallback(groupItem.text, templateField, item, afterCallback);
                        }
                    }

                    @Override
                    public void deleteImgCallback(TemplateResponse.TemplateField templateField, TemplateImageItem item, TemplateImageCallback.AfterCallback afterCallback) {
                        if (groupTemplateImageCallback != null) {
                            groupTemplateImageCallback.deleteImgCallback(groupItem.text, templateField, item, afterCallback);
                        }
                    }

                    @Override
                    public void OnItemLongClickListener(TemplateResponse.TemplateField templateField, TemplateImageItem item, SimpleArrayAdapter<TemplateImageItem, TemplateImageView.TemplateImageItemView> PicAdapter) {
                        if (groupTemplateImageCallback != null){
                            groupTemplateImageCallback.OnItemLongClickListener(groupItem.text, templateField, item, PicAdapter);
                        }
                    }
                });
                uiTemplateMaps.put(groupItem.id, uiTemplate);
            }
        }
    }

    public void createView(ViewGroup container, String group, String id) {
        for (final TemplateResponse.GroupItem groupItem : response.content.groups) {
            if (groupItem.id.equals(group) && groupItem.fields != null) {
                for (TemplateResponse.TemplateField templateFields : groupItem.fields) {
                    if (templateFields.id.equals(id)) {
                        List<TemplateResponse.TemplateField> list = new ArrayList<>();
                        list.add(templateFields);
                        UITemplate uiTemplate = new UITemplate(factory, list);
                        /*这个地方构造函数传入的list，只能传入该id对应的数据，因为在validate的时候会根据 该list去for循环查看数据是否完整
                        * */
                        uiTemplate.setProgressWithTextBar(progressWithTextBar);
                        uiTemplate.createViewWithId(container, id);
                        uiTemplate.setFieldChangedListener(new FieldChangedListener() {
                            @Override
                            public void onFieldValueChanged(TemplateResponse.TemplateField sender, String value) {
                                if (groupFieldChangedListener != null) {
                                    groupFieldChangedListener.onGroupFieldValueChanged(groupItem.text, sender, value);
                                }
                            }
                        });
                        uiTemplateMaps.put(id, uiTemplate);
                    }
                }
            }
        }
    }

    public GroupTemplateImageCallback getGroupTemplateImageCallback() {
        return groupTemplateImageCallback;
    }

    public void setGroupTemplateImageCallback(GroupTemplateImageCallback groupTemplateImageCallback) {
        this.groupTemplateImageCallback = groupTemplateImageCallback;
    }

    public void setProgressWithTextBar(ProgressWithTextBar progressWithTextBar){
        this.progressWithTextBar = progressWithTextBar;
    }

    public void setGroupFieldChangedListener(GroupFieldChangedListener listener) {
        this.groupFieldChangedListener = listener;
    }

    public TemplateResponse.TemplateField getField(String group, String id) {
        return null;
    }

    public View getView(String id) {
        for (String groupId : uiTemplateMaps.keySet()) {
            if(uiTemplateMaps.get(groupId).getView(id) != null){
                return uiTemplateMaps.get(groupId).getView(id);
            }
        }
        return null;
    }

    public String validate() {
        for (String groupId : uiTemplateMaps.keySet()) {
            if (!uiTemplateMaps.get(groupId).validate().equals(UITemplate.VALIDATE_SUCCESS)) {
                return uiTemplateMaps.get(groupId).validate();
            }
        }
        return UITemplate.VALIDATE_SUCCESS;
    }

    public Map<String, String> getAllUploadData() {
        Map<String, String> returnMap = new HashMap<>();
        for (String groupId : uiTemplateMaps.keySet()) {
            if (uiTemplateMaps.containsKey(groupId)) {
                returnMap.putAll(uiTemplateMaps.get(groupId).uploadData);
            }
        }
        return returnMap;
    }

    public Map<String, String> getUploadDataWithID(String groupID){
        for (String groupId : uiTemplateMaps.keySet()) {
            if (uiTemplateMaps.containsKey(groupId) && groupId.equals(groupID)) {
                return uiTemplateMaps.get(groupId).uploadData;
            }
        }
        return null;
    }

    public Map<String, String> getAllShowData() {
        Map<String, String> returnMap = new HashMap<>();
        for (String groupId : uiTemplateMaps.keySet()) {
            if (uiTemplateMaps.containsKey(groupId)) {
                returnMap.putAll(uiTemplateMaps.get(groupId).showData);
            }
        }
        return returnMap;
    }

    public Map<String, String> getShowDataWithID(String groupID){
        for (String groupId : uiTemplateMaps.keySet()) {
            if (uiTemplateMaps.containsKey(groupId) && groupId.equals(groupID)) {
                return uiTemplateMaps.get(groupId).showData;
            }
        }
        return null;
    }

    public void setValues(Map<String, String> values) {
    /*
    * 在调用setValues之前必须要先调用 createView或者createViewWithID ,否则会引起异常
    * */
        if(values == null)
            return;
        for (String groupId : uiTemplateMaps.keySet()) {
            if (uiTemplateMaps.containsKey(groupId)) {
                uiTemplateMaps.get(groupId).setValues(values);
            }
        }
    }

}
