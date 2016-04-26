package com.waynezhang.mcommon.template;

import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.waynezhang.mcommon.template.interfaces.FieldTemplateImageCallback;
import com.waynezhang.mcommon.template.interfaces.ITemplateImageView;
import com.waynezhang.mcommon.template.interfaces.ITemplateInputView;
import com.waynezhang.mcommon.template.interfaces.ITemplateMultiInputView;
import com.waynezhang.mcommon.template.interfaces.ITemplateSelectorView;
import com.waynezhang.mcommon.template.interfaces.ITemplateTextView;
import com.waynezhang.mcommon.template.interfaces.ITemplateView;
import com.waynezhang.mcommon.template.interfaces.IViewFactory;
import com.waynezhang.mcommon.template.interfaces.TemplateImageCallback;
import com.waynezhang.mcommon.template.listener.FieldChangedListener;
import com.waynezhang.mcommon.template.listener.ValueChangedListener;
import com.waynezhang.mcommon.template.model.TemplateImageItem;
import com.waynezhang.mcommon.template.response.TemplateResponse;
import com.waynezhang.mcommon.template.widget.TemplateImageView;
import com.waynezhang.mcommon.util.StringUtil;
import com.waynezhang.mcommon.xwidget.ProgressWithTextBar;
import com.waynezhang.mcommon.xwidget.SimpleArrayAdapter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxinxin on 10/28/15.
 */
public class UITemplate {

    Map<String, ITemplateView> viewData = new HashMap<>();//用来保存页面生成的所有控件的

    Map<String, String> uploadData = new HashMap<>();//用来保存用户已经选择和填写的数据

    Map<String, String> showData = new HashMap<>();//用来保存用户已经选择和填写的数据

    List<TemplateResponse.TemplateField> templateFields;

    FieldChangedListener fieldChangedListener;

    public FieldTemplateImageCallback getTemplateImageCallback() {
        return templateImageCallback;
    }

    public void setTemplateImageCallback(FieldTemplateImageCallback templateImageCallback) {
        this.templateImageCallback = templateImageCallback;
    }

    FieldTemplateImageCallback templateImageCallback;

    IViewFactory factory;

    ProgressWithTextBar progressWithTextBar;

    public static final String VALIDATE_SUCCESS = "校验通过";

    public UITemplate(IViewFactory factory, String template) {
        this.factory = factory;
        Gson gson = new Gson();
        Type type = new TypeToken<List<TemplateResponse.TemplateField>>() {
        }.getType();
        templateFields = gson.fromJson(template, type);
    }

    public UITemplate(IViewFactory factory, List<TemplateResponse.TemplateField> templateFields) {
        this.factory = factory;
        this.templateFields = templateFields;
    }

    public void createView(ViewGroup container) {

        for (final TemplateResponse.TemplateField templateField : templateFields) {
            ITemplateView mtemplateView = CreateTemplateView(templateField);
            if (mtemplateView != null){
                container.addView((View) mtemplateView);
                viewData.put(templateField.id, mtemplateView);
            }

        }
    }

    public void createViewWithId(ViewGroup container, String id) {
        for (final TemplateResponse.TemplateField templateField : templateFields) {
            if (templateField.id.equals(id)) {
                ITemplateView mtemplateView = CreateTemplateView(templateField);
                if (mtemplateView != null){
                    container.addView((View) mtemplateView);
                    viewData.put(templateField.id, mtemplateView);
                }
            }
        }

    }

    private ITemplateView CreateTemplateView(final TemplateResponse.TemplateField templateField) {
        if (templateField.id.equals("pic_url"))//上架商品不处理
            return null;
        ITemplateView templateView = factory.createView(templateField.type);
        if (templateView == null)
            return null;
        templateView.setText(templateField.text);
        templateView.setTip(templateField.tip);
        templateView.setEnable(templateField.allowModify);
        templateView.setValueChangedListener(new ValueChangedListener() {
            @Override
            public void onValueChanged(ITemplateView sender, String value, String showText) {
                if (value != null){
                    uploadData.put(templateField.id, value);
                }
                if (showText != null){
                    showData.put(templateField.text, showText);
                }
                if (fieldChangedListener != null) {
                    fieldChangedListener.onFieldValueChanged(templateField, value);
                }
            }
        });
        templateView.setProgressWithTextBar(progressWithTextBar);

        if (templateView instanceof ITemplateSelectorView) {
            ((ITemplateSelectorView) templateView).setValues(templateField.values);
            ((ITemplateSelectorView) templateView).setIntegrity(templateField.integrity);
        } else if (templateView instanceof ITemplateTextView) {
            templateView.setValue(templateField.text);
        } else if (templateView instanceof ITemplateInputView) {
            ((ITemplateInputView) templateView).setInputType(templateField.inputType);
            ((ITemplateInputView) templateView).setHint(templateField.hint);
            ((ITemplateInputView) templateView).setMaxLength(templateField.maxLength);
            ((ITemplateInputView) templateView).setminiValue(templateField.minValue);
            ((ITemplateInputView) templateView).setmaxValue(templateField.maxValue);
            ((ITemplateInputView) templateView).setUnitName(templateField.unitName);
            ((ITemplateInputView) templateView).setIntegrity(templateField.integrity);
        } else if (templateView instanceof ITemplateMultiInputView) {
            ((ITemplateMultiInputView) templateView).setHint(templateField.hint);
            ((ITemplateMultiInputView) templateView).setMaxLength(templateField.maxLength);
            ((ITemplateMultiInputView) templateView).setminiValue(templateField.minValue);
            ((ITemplateMultiInputView) templateView).setmaxValue(templateField.maxValue);
            ((ITemplateMultiInputView) templateView).setIntegrity(templateField.integrity);
        } else if (templateView instanceof ITemplateImageView) {
            ((ITemplateImageView) templateView).setDescription(templateField.desc);
            ((ITemplateImageView) templateView).setMaxCount(templateField.maxCount);
            ((ITemplateImageView) templateView).setMiniCount(templateField.miniCount);
            ((ITemplateImageView) templateView).setTemplateImageCallback(new TemplateImageCallback() {
                @Override
                public void addImgCallback(TemplateImageItem item, AfterCallback afterCallback) {
                    if (templateImageCallback != null){
                        templateImageCallback.addImgCallback(templateField,item,afterCallback);
                    }
                }

                @Override
                public void deleteImgCallback(TemplateImageItem item, AfterCallback afterCallback) {
                    if (templateImageCallback != null){
                        templateImageCallback.deleteImgCallback(templateField, item, afterCallback);
                    }
                }

                @Override
                public void OnItemLongClickListener(TemplateImageItem item, SimpleArrayAdapter<TemplateImageItem, TemplateImageView.TemplateImageItemView> PicAdapter) {
                    if (templateImageCallback != null){
                        templateImageCallback.OnItemLongClickListener(templateField, item, PicAdapter);
                    }
                }
            });
        }
        return templateView;
    }

    public void setFieldChangedListener(FieldChangedListener fieldChangedListener) {
        this.fieldChangedListener = fieldChangedListener;
    }

    public void setProgressWithTextBar(ProgressWithTextBar progressWithTextBar){
        this.progressWithTextBar = progressWithTextBar;
    }

    public TemplateResponse.TemplateField getField(String id) {
        return null;
    }

    public View getView(String mid) {
        for (String id : viewData.keySet()) {
            if (id.equals(mid))
                return viewData.get(id).getView();
        }
        return null;
    }

    public String validate() {
        //校验模版中所填写的数据是否符合规则
        for (TemplateResponse.TemplateField templateField : templateFields) {
            if (templateField.type.equals(TemplateResponse.TYPE_IMAGE)){

            }
            else if (templateField.allowEmpty != 1) {
                String value = getValueFromKey(uploadData, templateField.id);
                if (StringUtil.isEmpty(value))
                    return templateField.text+"不能为空";
                if (templateField.validate != null && !value.matches(templateField.validate))
                    return templateField.validateFailedMsg;
                try{
                    if (templateField.minValue != -1 && Long.valueOf(value.trim()) < templateField.minValue)
                        return templateField.text+"过低";
                    if  (templateField.maxValue != -1 && Long.valueOf(value.trim()) > templateField.maxValue)
                        return templateField.text+"过高";
                    if (value.length() > templateField.maxLength)
                        return templateField.text+"文本过长";
                }catch (Exception e){
                    return templateField.text+"不符合上架规则";
                }
            }
        }
        return VALIDATE_SUCCESS;
    }

    public Map<String, String> getValues() {
        return uploadData;
    }

    public Map<String, String> getShowValues() {
        return showData;
    }

    public void setValues(Map<String, String> data) {
    /*
    * 在调用setValues之前必须要先调用 createView或者createViewWithID ,否则会引起异常
    * */
        for (String key : data.keySet()) {
            ITemplateView iTemplateView = viewData.get(key);
                if(iTemplateView != null){
                    iTemplateView.setValue(data.get(key));
                }
        }
    }

    private String getValueFromKey(Map<String, String> map, String key) {
        if (map.containsKey(key) && map.get(key) != null) {
            return map.get(key).toString();
        }
        return null;
    }

}
