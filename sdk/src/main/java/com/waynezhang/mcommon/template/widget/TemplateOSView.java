package com.waynezhang.mcommon.template.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.template.util.TemplateOSButtonHelper;

import java.io.Serializable;
import java.util.List;


/**
 * Created by sunxinxin on 11/4/15.
 */
public class TemplateOSView extends LinearLayout {

    RadioGroup group;

    OsChangedListener changedListener;

    public TemplateOSView(Context context) {
        super(context);
        initView(context,null);
    }

    public TemplateOSView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs);
    }

    private void initView(Context context, AttributeSet attrs){
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.mc_template_radiogroup_view, this);
        group = (RadioGroup) findViewById(R.id.osGroup);
    }

    public void setOSList(List<OSItem> oslist){
        group.removeAllViews();
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (changedListener != null) {
                    changedListener.OsChanged(checkedId);
                }
            }
        });
        for (OSItem item : oslist){
            addItem(group,item.game_app_os_name,item.game_app_os);
        }
    }

    public void setValue(int game_app_os){
        RadioButton button;
        button = ((RadioButton)group.findViewById(game_app_os));
        if (button != null){
            button.setChecked(true);
        }
    }

    public void setOsChangedListener(OsChangedListener changedListener){
        this.changedListener = changedListener;
    }

    public interface OsChangedListener{
        void OsChanged(int game_app_os);
    }


    private void addItem(RadioGroup radioGroup,String text, int id) {
        final RadioButton item = TemplateOSButtonHelper.createView(getContext());
        item.setTag(id);
        item.setId(id);
        item.setText(text);
        radioGroup.addView(item, TemplateOSButtonHelper.createLayoutParams(getContext()));
    }

    public static class OSItem implements Serializable {
        public int game_app_os;             //平台id
        public String game_app_os_name;

        public OSItem(int game_app_os,String game_app_os_name){
            this.game_app_os = game_app_os;
            this.game_app_os_name = game_app_os_name;
        }
    }


}


