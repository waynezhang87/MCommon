package com.waynezhang.mcommon.template.util;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.util.ScreenUtil;

/**
 * Created by sunxinxin on 15/12/15.
 */
public class TemplateOSButtonHelper {

    public static final RadioButton createView(Context context) {
        RadioButton radioButton = new RadioButton(context);
        radioButton.setPadding(ScreenUtil.convertDipToPixel(context, 16), 0, ScreenUtil.convertDipToPixel(context, 16), 0);
        radioButton.setButtonDrawable(new StateListDrawable());
        radioButton.setBackgroundResource(R.drawable.mc_os_item_bg);
        radioButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        radioButton.setTextColor(context.getResources().getColorStateList(R.color.mc_radio_text_bg));
        return radioButton;
    }

    public static final View addDivider(RadioGroup radioGroup, Context context) {
        View view = new View(context);
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(context.getResources().getDimensionPixelSize(R.dimen.radiobutton_margin), 1);
        radioGroup.addView(view, params);
        return view;
    }

    public static final RadioGroup.LayoutParams createLayoutParams(Context context) {
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, context.getResources().getDimensionPixelSize(R.dimen.radiobutton_height));
        params.setMargins(0,0,context.getResources().getDimensionPixelSize(R.dimen.radiobutton_margin),0);
        return params;
    }

}
