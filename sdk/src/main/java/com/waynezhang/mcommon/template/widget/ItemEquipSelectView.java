package com.waynezhang.mcommon.template.widget;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.template.response.TemplateResponse;
import com.waynezhang.mcommon.xwidget.Bindable;

/**
 * Created by sunxinxin on 2014/12/26.
 */
public class ItemEquipSelectView extends FrameLayout implements Bindable<TemplateResponse.TemplateField> {

    TextView item_text;

    public ItemEquipSelectView(Context context) {
        super(context);
        inflate(context, R.layout.mc_item_textview,this);
        item_text = (TextView)findViewById(R.id.item_text);
    }

    @Override
    public void bind(TemplateResponse.TemplateField item) {
        item_text.setText(item.text);
    }
}
