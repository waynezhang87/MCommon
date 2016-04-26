package com.waynezhang.mcommon.xwidget;

import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.waynezhang.mcommon.util.ScreenUtil;

import java.util.HashMap;

/**
 * Created by liuyagang on 15-1-29.
 */
public class OptionDialog extends McDialog implements View.OnClickListener {
    private View atView;
    private Point atLocation;
    private int layoutId = -1;
    private boolean cancelable = true;
    private boolean disableBackKey = false;
    private ColorDrawable bkColor = null;
    private DialogInterface.OnCancelListener onCancelListener;
    private OnInitialListener onInitialListener;
    private SparseArray<TextViewUtil.OnLinkClickListener> onLinkClickListenerSparseArray = new SparseArray<>();
    private SparseArray<HashMap<String, TextViewUtil.OnLinkClickListener>> onLinkClickListenerUrlMapSparseArray = new SparseArray<>();
    private SparseArray<View.OnClickListener> onClickListenerSparseArray = new SparseArray<>();
    private SparseArray<CharSequence> textSparseArray = new SparseArray<>();
    private SparseArray<Integer> visibilitySparseArray = new SparseArray<>();

    public static Builder build(FragmentActivity activity, int layoutId) {
        return new Builder(activity, layoutId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layoutId, container, false);

        if (view instanceof ViewGroup) {
            setOnClickListener((ViewGroup) view);
            setText((ViewGroup) view);
            setOnLinkClickListener((ViewGroup) view);
            setVisibility((ViewGroup) view);
        }

        if (onInitialListener != null) {
            onInitialListener.onInitial(view instanceof ViewGroup ? (ViewGroup) view : null);
        }
        // 显示在atView的上部中心位置
        if (atView != null) {
            int[] location = new int[2];
            //读取位置atView座标
            atView.getLocationOnScreen(location);

            view.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int x = location[0] + (atView.getMeasuredWidth() / 2 + view.getMeasuredWidth() / 2);
            int y = location[1];
            view = viewToLocation(view, x, y);
        } else if (atLocation != null) {
            view.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view = viewToLocation(view, atLocation.x + view.getMeasuredWidth() / 2, atLocation.y);
        }
        if (bkColor != null) {
            getDialog().getWindow().setBackgroundDrawable(bkColor);
        }
        if (cancelable) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().cancel();
                }
            });
        }
        if (disableBackKey) {
            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        return true;
                    }
                    return false;
                }
            });
        }

        return view;
    }

    private View viewToLocation(View view, int x, int y) {
        int screenHeight = ScreenUtil.getHeight(view.getContext());
        int screenWidth = ScreenUtil.getWidth(view.getContext());

        FrameLayout layout = new FrameLayout(view.getContext());
        layout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, screenWidth - x, screenHeight - y);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        layout.addView(view, layoutParams);
        return layout;
    }

    private void setOnClickListener(ViewGroup viewGroup) {
        for (int i = 0; i < onClickListenerSparseArray.size(); i++) {
            int id = onClickListenerSparseArray.keyAt(i);
            View view = viewGroup.findViewById(id);
            if (view != null) {
                view.setOnClickListener(this);
            }
        }
    }

    private void setText(ViewGroup viewGroup) {
        for (int i = 0; i < textSparseArray.size(); i++) {
            int id = textSparseArray.keyAt(i);
            CharSequence text = textSparseArray.get(id);
            View view = viewGroup.findViewById(id);
            if (view != null && view instanceof TextView) {
                ((TextView) view).setText(text);
            }
        }
    }

    private void setOnLinkClickListener(ViewGroup viewGroup) {
        for (int i = 0; i < onLinkClickListenerSparseArray.size(); i++) {
            int id = onLinkClickListenerSparseArray.keyAt(i);
            TextViewUtil.OnLinkClickListener listener = onLinkClickListenerSparseArray.get(id);
            View view = viewGroup.findViewById(id);
            if (view != null && view instanceof TextView) {
                TextViewUtil.observeUrlClick((TextView) view, listener);
            }
        }
        for (int i = 0; i < onLinkClickListenerUrlMapSparseArray.size(); i++) {
            int id = onLinkClickListenerUrlMapSparseArray.keyAt(i);
            final HashMap<String, TextViewUtil.OnLinkClickListener> listenerMap = onLinkClickListenerUrlMapSparseArray.get(id);
            View view = viewGroup.findViewById(id);
            if (view != null && view instanceof TextView) {
                TextViewUtil.observeUrlClick((TextView) view, new TextViewUtil.OnLinkClickListener() {
                    @Override
                    public void onLinkClick(String url) {
                        TextViewUtil.OnLinkClickListener listener = listenerMap.get(url);
                        if (listener != null) {
                            listener.onLinkClick(url);
                        }
                    }
                });
            }
        }
    }

    private void setVisibility(ViewGroup viewGroup) {
        for (int i = 0; i < visibilitySparseArray.size(); i++) {
            int id = visibilitySparseArray.keyAt(i);
            int visibility = visibilitySparseArray.get(id);
            View view = viewGroup.findViewById(id);
            if (view != null) {
                //noinspection WrongConstant
                view.setVisibility(visibility);
            }
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        if (onCancelListener != null) {
            onCancelListener.onCancel(dialog);
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();

        View.OnClickListener listener = onClickListenerSparseArray.get(v.getId());
        if (listener != null) {
            listener.onClick(v);
        }
    }

    public interface OnInitialListener {
        void onInitial(ViewGroup container);
    }

    public static class Builder {
        FragmentActivity activity;
        OptionDialog dialog = new OptionDialog();

        public Builder(FragmentActivity activity, int layoutId) {
            this.activity = activity;
            this.dialog.layoutId = layoutId;
        }

        public Builder at(View view) {
            dialog.atView = view;
            return this;
        }

        public Builder at(Point location) {
            dialog.atLocation = location;
            return this;
        }

        public Builder text(int id, CharSequence text) {
            dialog.textSparseArray.put(id, text);
            return this;
        }

        public Builder visibility(int id, int visibility) {
            dialog.visibilitySparseArray.put(id, visibility);
            return this;
        }

        public Builder cancelable(boolean cancelable) {
            dialog.cancelable = cancelable;
            return this;
        }

        public Builder disableBackKey(boolean disable) {
            dialog.disableBackKey = disable;
            return this;
        }

        public Builder bkColor(int bkColor) {
            dialog.bkColor = new ColorDrawable(bkColor);
            return this;
        }

        public Builder onClickListener(int viewId, View.OnClickListener listener) {
            dialog.onClickListenerSparseArray.put(viewId, listener);
            return this;
        }

        public Builder onInitialListener(OnInitialListener listener) {
            dialog.onInitialListener = listener;
            return this;
        }

        public Builder onCancelListener(DialogInterface.OnCancelListener listener) {
            dialog.onCancelListener = listener;
            return this;
        }

        /**
         * 同一个viewId同时设置监听所有URL和监听单个URL的接口，后一次的listener会覆盖前一次的
         */
        public Builder onLinkClickListener(int viewId, TextViewUtil.OnLinkClickListener listener) {
            dialog.onLinkClickListenerSparseArray.put(viewId, listener);
            return this;
        }

        /**
         * 同一个viewId同时设置监听所有URL和监听单个URL的接口，后一次的listener会覆盖前一次的
         */
        public Builder onLinkClickListener(int viewId, String url, TextViewUtil.OnLinkClickListener listener) {
            HashMap<String, TextViewUtil.OnLinkClickListener> urlMap = dialog.onLinkClickListenerUrlMapSparseArray.get(viewId);
            if (urlMap == null) {
                urlMap = new HashMap<>();
                dialog.onLinkClickListenerUrlMapSparseArray.put(viewId, urlMap);
            }
            urlMap.put(url, listener);
            return this;
        }

        public OptionDialog show() {
            dialog.fixedShow(activity);
            return dialog;
        }
    }
}
