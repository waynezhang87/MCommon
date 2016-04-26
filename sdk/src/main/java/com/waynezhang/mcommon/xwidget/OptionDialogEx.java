package com.waynezhang.mcommon.xwidget;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.List;

/**
 * Created by liuyagang on 15-4-14.
 */
public class OptionDialogEx<D, IV extends Bindable<D>, B extends Buildable<D, IV>> extends McDialog implements View.OnClickListener {
    private int layoutId;
    private int containerId;
    private B itemBuilder;
    private List<D> data;
    private AdapterView.OnItemClickListener onClickListener;


    public static <D, IV extends Bindable<D>, B extends Buildable<D, IV>> OptionDialogEx build(int layoutId, int containerId, B itemBuilder) {
        OptionDialogEx dialog = new OptionDialogEx<D, IV, B>();
        dialog.layoutId = layoutId;
        dialog.containerId = containerId;
        dialog.itemBuilder = itemBuilder;
        return dialog;
    }

    public void show(FragmentActivity activity, List<D> data, AdapterView.OnItemClickListener listener) {
        this.data = data;
        this.onClickListener = listener;
        this.fixedShow(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layoutId, container, false);
        //view.setBackground();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ViewGroup viewGroup = (ViewGroup) view.findViewById(containerId);
        int id = 0;
        for (D d : data) {

            IV itemView = itemBuilder.build(d);
            viewGroup.addView((View) itemView);
            itemView.bind(d);
            ((View) itemView).setId(id);
            ((View) itemView).setOnClickListener(this);
            id++;
        }
//        if (view instanceof ViewGroup) {
//            setOnClickListener((ViewGroup) view);
//        }

        return view;
    }

    private void setOnClickListener(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childView = viewGroup.getChildAt(i);
            if (childView instanceof ViewGroup) {
                setOnClickListener((ViewGroup) childView);
            }
            if (childView.getId() != -1) {
                childView.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();

        if (onClickListener != null) {
            onClickListener.onItemClick(null, v, v.getId(), v.getId());
        }
    }
}
