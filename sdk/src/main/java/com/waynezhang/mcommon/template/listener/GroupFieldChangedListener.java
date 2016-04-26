package com.waynezhang.mcommon.template.listener;

import com.waynezhang.mcommon.template.response.TemplateResponse;

/**
 * Created by sunxinxin on 10/28/15.
 */
public interface GroupFieldChangedListener {

     void onGroupFieldValueChanged(String group, TemplateResponse.TemplateField templateField, String value);
}
