package com.waynezhang.mcommon.template.interfaces;

import android.view.View;

import com.waynezhang.mcommon.template.listener.ValueChangedListener;
import com.waynezhang.mcommon.xwidget.ProgressWithTextBar;

/**
 * Created by sunxinxin on 10/28/15.
 */
public interface ITemplateView {

     View getView();

     CharSequence getText();

     void setText(String text);

     void setEnable(int allowModify);

     void setFocus(View v);

     void setTip(String text);

     String getValue();

     void setValue(String value);

     void setValueChangedListener(ValueChangedListener changedListener);

     void setProgressWithTextBar(ProgressWithTextBar progressWithTextBar);

}
