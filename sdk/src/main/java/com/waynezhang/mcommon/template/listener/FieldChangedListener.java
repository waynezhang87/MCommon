package com.waynezhang.mcommon.template.listener;

import com.waynezhang.mcommon.template.response.TemplateResponse;

/**
 * Created by sunxinxin on 10/28/15.
 */
public interface FieldChangedListener {

     void onFieldValueChanged(TemplateResponse.TemplateField sender, String value);
}
