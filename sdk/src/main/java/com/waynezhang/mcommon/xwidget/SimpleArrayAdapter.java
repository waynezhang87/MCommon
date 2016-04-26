package com.waynezhang.mcommon.xwidget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.waynezhang.mcommon.compt.ArrayAdapterCompat;

import java.util.List;

/**
 * Created by don on 12/12/14.
 */
public abstract class SimpleArrayAdapter<I, V extends Bindable<I>> extends ArrayAdapterCompat<I>{
    public SimpleArrayAdapter(Context context) {
        super(context);
    }

    public void bind(List<I> list) {
        bind(list, true);
    }

    public void bind(List<I> list, boolean doClear) {
        if (doClear) {
            clear();
        }
        addAll(list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        V itemView;
        if (convertView == null) {
            itemView = build(getContext());
        } else {
            itemView = (V) convertView;
        }

        itemView.bind(getItem(position));
        return (View) itemView;
    }

    protected abstract V build(Context context);
}
