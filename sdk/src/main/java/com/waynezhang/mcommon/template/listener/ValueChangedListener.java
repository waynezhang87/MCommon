package com.waynezhang.mcommon.template.listener;

import com.waynezhang.mcommon.template.interfaces.ITemplateView;

/**
 * Created by sunxinxin on 10/28/15.
 */
public interface ValueChangedListener {

     void onValueChanged(ITemplateView sender, String value, String showText);

}
