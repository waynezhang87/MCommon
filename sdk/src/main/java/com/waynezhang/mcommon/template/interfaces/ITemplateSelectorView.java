package com.waynezhang.mcommon.template.interfaces;

import com.waynezhang.mcommon.template.response.TemplateResponse;

import java.util.List;

/**
 * Created by sunxinxin on 10/28/15.
 */
public interface ITemplateSelectorView extends ITemplateView {

    String getValueText();

    void setValues(List<TemplateResponse.TemplateField> templateFields);

    void setIntegrity(int integrity);
}
