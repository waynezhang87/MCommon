package com.waynezhang.mcommon.xwidget.tabhostview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.waynezhang.mcommon.xwidget.Bindable;

import java.util.List;

/**
 * Created by waynezhang on 10/30/15.<p/>
 *
 * <p>支持动态添加的Tab页组件: 目前支持绑定List类型数据, 需实现{@link TabHostBaseView#getView(I item, int position)}并绑定List&lt;I&gt;</p>
 *
 * <p>使用方法: 继承TabHostBaseView基类并实现{@link TabHostBaseView#getView(I item, int position)}方法,
 * 在需要传入tab数组的位置调用{@link TabHostBaseView#bind(V tabs)}方法.
 * 如需监听TabItem选中则调用{@link TabHostBaseView#setOnTabItemSelectedListener(OnTabItemSelectedListener listener)}注册监听器</p>
 *
 * <p>注意: 动态创建需要指定布局文件.</p>
 *
 */
public abstract class TabHostBaseView<I, V extends List<I>> extends LinearLayout implements Bindable<V> {
    protected LinearLayout root;

    private V tabs;
    private OnTabItemSelectedListener mListener;

    protected OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            for (int i = 0; i < root.getChildCount(); i++) {
                View view = root.getChildAt(i);
                if (v.getTag().equals(view.getTag())) {
                    view.setSelected(true);
                    if (mListener != null && tabs != null) {
                        mListener.onTabItemSelected(v, tabs.get(i), i);
                    }
                } else {
                    view.setSelected(false);
                }
            }
            return false;
        }
    };

    public TabHostBaseView(Context context) {
        this(context, 0, null);
    }

    public TabHostBaseView(Context context, int resId, ViewGroup parent) {
        super(context);
        if (resId == 0) {
            throw new IllegalArgumentException("动态创建需要指定布局文件!");
        }
        root = (LinearLayout) LayoutInflater.from(context).inflate(resId, parent, false);
        parent.addView(root);
    }

    public TabHostBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        root = (LinearLayout) getRootView();
    }

    public void setOnTabItemSelectedListener(OnTabItemSelectedListener listener) {
        this.mListener = listener;
    }

    public void setSelection(I item) {
        if (tabs != null) {
            for (int i = 0; i < tabs.size(); i++) {
                if (item.equals(tabs.get(i))) {
                    root.getChildAt(i).setSelected(true);
                } else {
                    root.getChildAt(i).setSelected(false);
                }
            }
        }
    }

    public void setSelectionIndex(int position) {
        if (tabs != null) {
            for (int i = 0; i < tabs.size(); i++) {
                if (i == position) {
                    root.getChildAt(i).setSelected(true);
                } else {
                    root.getChildAt(i).setSelected(false);
                }
            }
        }
    }

    public I getSelection() {
        if (tabs != null) {
            for (int i = 0; i < tabs.size(); i++) {
                if (root.getChildAt(i).isSelected())
                    return tabs.get(i);
            }
        }
        return null;
    }

    public int getSelectionIndex() {
        if (tabs != null) {
            for (int i = 0; i < tabs.size(); i++) {
                if (root.getChildAt(i).isSelected())
                    return i;
            }
        }
        return -1;
    }

    public int getSize() {
        if (tabs != null)
            return tabs.size();
        else
            return 0;
    }

    @Override
    public void bind(V tabs) {
        if (tabs != null) {
//            try {
//                Type[] paramTypes = ReflectionUtil.getParameterizedTypes(this);
//                Class clazz = ReflectionUtil.getClass(((ParameterizedType) paramTypes[0]).getActualTypeArguments()[0]);
//                if (View.class.isAssignableFrom(clazz)) {
//                    generateTabsWithItemView(tabs);
//                } else if (SimpleTabItem.class.isAssignableFrom(clazz)) {
//                    generateTabsWithSimpleTabItem(tabs);
//                } else if (String.class.isAssignableFrom(clazz)) {
//                    generateTabsWithItemString(tabs);
//                }
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
            root.removeAllViews();
            this.tabs = tabs;
            for(int i = 0; i < tabs.size(); i++) {
                I item = tabs.get(i);
                View itemView = getView(item, i);
                itemView.setTag(item);
                if (itemView != null) {
                    itemView.setOnTouchListener(mTouchListener);
                    root.addView(itemView);
                }
            }
        }
    }

    public abstract View getView(I item, int position);

    public interface OnTabItemSelectedListener<I> {
        void onTabItemSelected(View view, I item, int position);
    }
}
