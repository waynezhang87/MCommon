package com.waynezhang.mcommon.xwidget.titlebar;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.xwidget.Bindable;

/**
 * Created by waynezhang on 3/3/16.
 */
public class McTitleBarExtMenuItemView extends RelativeLayout implements Bindable<McTitleBarExtMenuItem>{
    private TextView view;
    private TextView notifyView;
    private int id;

    private RelativeLayout layout;

    private boolean disable = false;    //当item为单纯的文字时, 不允许显示红点

    public McTitleBarExtMenuItemView(Context context) {
        super(context);
        init(context, null);
    }

    public McTitleBarExtMenuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.mc_title_bar_ext_menu_item, this);
        layout = (RelativeLayout) findViewById(R.id.layout);
        view = (TextView) findViewById(R.id.textView);
        notifyView = (TextView) findViewById(R.id.notify_unread);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, widthMeasureSpec);

        super.onMeasure(newWidthMeasureSpec, heightMeasureSpec);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        switch (specMode) {
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.AT_MOST:
                view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                LayoutParams params = (LayoutParams) view.getLayoutParams();
                result = view.getMeasuredWidth() + params.leftMargin + params.rightMargin;
                result = Math.min(result, specSize);
                result = Math.max(result, layout.getMinimumWidth());
                break;
            case MeasureSpec.UNSPECIFIED:
                view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                result = view.getMeasuredWidth();
                break;
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public void bind(McTitleBarExtMenuItem item) {
        this.id = item.id;
        if (item.iconLargeResId != 0) {
            disable = false;
            view.setBackgroundResource(item.iconLargeResId);
        } else {
            disable = true;
            view.setText(item.title);
        }
    }

    public void notifyWithoutCount(boolean enable) {
        notifyView.setVisibility(enable && !disable ? VISIBLE : GONE);
    }

    public void notifyWithCount(int count) {
        if (disable) return;
        if (count <= 0) {
            notifyView.setVisibility(GONE);
        } else if (count < 100) {
            notifyView.setVisibility(VISIBLE);
            notifyView.setText(String.valueOf(count));
        } else {
            notifyView.setVisibility(VISIBLE);
            notifyView.setText("99+");
        }
    }

    @Override
    public int getId() {
        return id;
    }
}
