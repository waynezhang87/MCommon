package com.waynezhang.mcommon.template.interfaces;

import android.widget.EditText;

/**
 * Created by sunxinxin on 10/28/15.
 */
public interface ITemplateInputView extends ITemplateView{

     void setInputType(String type);

     void setHint(String hint);

     void setMaxLength(int length);

     void setminiValue(int miniValue);

     void setmaxValue(int maxValue);

     void setUnitName(String unitName);

     void setIntegrity(int integrity);

     EditText getEditTextView();

}
