package com.waynezhang.mcommon.xwidget.Groups;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liuyagang on 16/3/8.
 */
public class RadioGroupHelper {
    final static String TAG = RadioGroupHelper.class.getSimpleName();

    private ViewGroup radioGroup;
    private OnSelectChangedListener onSelectChangedListener;

    public RadioGroupHelper(ViewGroup viewGroup) {
        radioGroup = viewGroup;
        radioGroup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onTouchEvent(event);
                return true;
            }
        });
    }

    public void onTouchEvent(MotionEvent event) {
//        Log.d(TAG, "onTouchEvent action=" + event.getAction() + ",ax=" + (int) event.getX() + ",ay=" + (int) event.getY());

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getViewAt((int) event.getX(), (int) event.getY());
            if (view != null) {
                setSelected(view);
            }
        }
    }

    View getViewAt(int x, int y) {
        int count = radioGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = radioGroup.getChildAt(i);
            // 忽略没有设置id的控件
            if (view.getId() == -1) {
                continue;
            }
            Rect rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            if (rect.contains(x, y)) {
                return view;
            }
        }
        return null;
    }

    void setSelected(View newView) {
        if (newView.isSelected()) {
            return;
        }
        View oldView = null;
        int count = radioGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            oldView = radioGroup.getChildAt(i);
            // 忽略没有设置id的控件
            if (oldView.getId() == -1) {
                continue;
            }
            if (oldView.isSelected()) {
                oldView.setSelected(false);
                break;
            }
        }
        newView.setSelected(true);
        onSelectChanged(oldView, newView);
    }

    void onSelectChanged(View oldView, View newView) {
        if (onSelectChangedListener != null) {
            onSelectChangedListener.onSelectChanged(oldView, newView);
        }
    }

    public void setOnSelectChangedListener(OnSelectChangedListener listener) {
        onSelectChangedListener = listener;
    }

    public void setSelected(int id) {
        View view = radioGroup.findViewById(id);
        if (view != null) {
            setSelected(view);
        }
    }

    public int getSelected() {
        int count = radioGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = radioGroup.getChildAt(i);
            // 忽略没有设置id的控件
            if (view.getId() == -1) {
                continue;
            }
            if (view.isSelected()) {
                return view.getId();
            }
        }
        return -1;
    }

    public interface OnSelectChangedListener {
        void onSelectChanged(View oldView, View newView);
    }
}
