package com.waynezhang.mcommon.xwidget.titlebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.util.PopupMenuHelper;
import com.waynezhang.mcommon.util.ScreenUtil;
import com.waynezhang.mcommon.util.XmlPullParserUtil;

import java.util.Iterator;
import java.util.List;

/**
 * Created by waynezhang on 2/25/16.
 */
public class McTitleBarExt extends LinearLayout {
    private ImageView leftBtn;
    private ViewGroup titleLayout;
    private TextView tvTitle;
    private TextView tvSubTitle;
    private ImageView ivTitleMore;
    private ViewGroup rightLayout;

    private List<McTitleBarExtMenuItem> menuList;
    private McTitleBarExtMenuItemView rightBtnFirst;
    private McTitleBarExtMenuItemView rightBtnSecond;

    private OnLeftButtonClickListener leftBtnClickListener;
    private OnMenuItemClickListener menuItemClickListener;
    private PopupMenu.OnMenuItemClickListener titleMenuItemClickListener;
    private OnClickListener titleClickListener;
    
    private Context mContext;

    public void setOnTitleClickListener(OnClickListener titleClickListener) {
        ivTitleMore.setVisibility(VISIBLE);
        this.titleClickListener = titleClickListener;
    }

    public void setOnTitleMenuItemClickListener(PopupMenu.OnMenuItemClickListener titleMenuItemClickListener) {
        this.titleMenuItemClickListener = titleMenuItemClickListener;
    }

    public void setOnLeftBtnClickListener(OnLeftButtonClickListener leftBtnClickListener) {
        this.leftBtnClickListener = leftBtnClickListener;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener mListener) {
        this.menuItemClickListener = mListener;
    }

    public McTitleBarExt(Context context) {
        super(context);
        init(context, null);
    }

    public McTitleBarExt(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.mc_title_bar_ext, this);
        final TypedArray ta = getContext().obtainStyledAttributes(
                attrs, R.styleable.McTitleBarExt, R.attr.mcTitleBarStyle, R.style.mcTitleBarExtStyleLight);
        initStyle(context, ta);
        ta.recycle();
    }

    private void initStyle(Context context, TypedArray ta) {
        leftBtn = (ImageView) findViewById(R.id.mc_title_btnLeft);
        titleLayout = (ViewGroup) findViewById(R.id.mc_title_layout);
        tvTitle = (TextView) findViewById(R.id.mc_title);
        tvSubTitle = (TextView) findViewById(R.id.mc_title_subtitle);
        ivTitleMore = (ImageView) findViewById(R.id.mc_title_subtitle_icon_more);
        rightLayout = (ViewGroup) findViewById(R.id.mc_title_right_layout);

        int leftDrawableResId = ta.getResourceId(R.styleable.McTitleBarExt_mc_title_left_drawable, 0);
        if (leftDrawableResId != 0) {
            leftBtn.setImageResource(leftDrawableResId);
            leftBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (leftBtnClickListener != null) {
                        leftBtnClickListener.onLeftButtonClick(v);
                    }
                }
            });
        } else {
            leftBtn.setVisibility(GONE);
            titleLayout.setPadding(ScreenUtil.dip2px(context, 15), 0, 0, 0);
        }

        String titleText = ta.getString(R.styleable.McTitleBarExt_mc_title_text);
        if (!TextUtils.isEmpty(titleText)) {
            tvTitle.setText(titleText);
        }

        String subTitleText = ta.getString(R.styleable.McTitleBarExt_mc_title_sub_text);
        if (TextUtils.isEmpty(subTitleText)) {
            tvSubTitle.setVisibility(GONE);
        } else {
            tvSubTitle.setText(subTitleText);
        }

        final int titleMenuResId = ta.getResourceId(R.styleable.McTitleBarExt_mc_title_title_menu_resourceId, 0);
        if (titleMenuResId == 0) {
            ivTitleMore.setVisibility(GONE);
        }

        final int PopupMenuWidth = ta.getDimensionPixelSize(R.styleable.McTitleBarExt_mc_title_action_menu_width, 0);

        titleLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleClickListener != null) {
                    titleClickListener.onClick(v);
                } else if (titleMenuResId != 0) {
                    PopupMenu popup = PopupMenuHelper.createWithIcon(titleMenuResId, mContext, titleLayout);
                    if (titleMenuItemClickListener != null) {
                        popup.setOnMenuItemClickListener(titleMenuItemClickListener);
                    }
                    popup.show();
                    if(PopupMenuWidth != 0){
                        PopupMenuHelper.setMenuWidth(popup, PopupMenuWidth);
                    }
                }
            }
        });

        int background = ta.getColor(R.styleable.McTitleBarExt_android_background, getResources().getColor(R.color.mc_title_bar_background));
        setBackgroundColor(background);
        // Added by waynezhang on 3/14/16: 如果背景色为透明时底部分割线也需要设为透明
        if (background == getResources().getColor(android.R.color.transparent)) {
            findViewById(R.id.divider).setBackgroundColor(background);
        }

        int moreResId = ta.getResourceId(R.styleable.McTitleBarExt_mc_title_more_drawable, R.drawable.mc_icon_top_more);
        int menuResId = ta.getResourceId(R.styleable.McTitleBarExt_mc_title_action_menu_resourceId, 0);
        menuList = XmlPullParserUtil.parseTitleBarExtMenu(context, menuResId);
        createRightMenuLayout(context, menuList, moreResId, menuResId);
    }

    private void createRightMenuLayout(final Context context, final List<McTitleBarExtMenuItem> menuList, int moreResId, final int menuResId) {
        if (menuList == null || menuList.size() == 0 || menuResId == 0 || moreResId == 0) return;

        rightBtnFirst = createRightBtn(context, menuList.get(0));
        rightBtnFirst.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuItemClickListener != null) {
                    menuItemClickListener.onMenuItemClick(menuList.get(0));
                }
            }
        });
        rightLayout.addView(rightBtnFirst);

        if (menuList.size() == 2) {
            rightBtnSecond = createRightBtn(context, menuList.get(1));
            rightBtnSecond.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (menuItemClickListener != null) {
                        menuItemClickListener.onMenuItemClick(menuList.get(1));
                    }
                }
            });
            rightLayout.addView(rightBtnSecond);
        } else if (menuList.size() > 2) {
            McTitleBarExtMenuItem item = new McTitleBarExtMenuItem();
            item.iconLargeResId = moreResId;
            rightBtnSecond = createRightBtn(context, item);
            rightBtnSecond.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = PopupMenuHelper.createWithIcon(menuResId, mContext, rightLayout);
                    popup.getMenu().removeItem(menuList.get(0).id);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (menuItemClickListener != null) {
                                Iterator it = menuList.iterator();
                                while (it.hasNext()) {
                                    McTitleBarExtMenuItem itemExt = (McTitleBarExtMenuItem) it.next();
                                    if (item.getItemId() == itemExt.id) {
                                        return menuItemClickListener.onMenuItemClick(itemExt);
                                    }
                                }
                            }
                            return false;
                        }
                    });
                    popup.show();
                    PopupMenuHelper.setMenuWidth(popup, ScreenUtil.dip2px(context, 170));
                }
            });
            rightLayout.addView(rightBtnSecond);
        }
    }

    private McTitleBarExtMenuItemView createRightBtn(Context context, McTitleBarExtMenuItem item) {
        McTitleBarExtMenuItemView rightBtn = new McTitleBarExtMenuItemView(context);
        rightBtn.bind(item);
        return rightBtn;
    }

    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(McTitleBarExtMenuItem menuItem);
    }

    public interface OnLeftButtonClickListener {
        void onLeftButtonClick(View v);
    }

    public void notifyWithoutCount(int itemId, boolean enable) {
        if (itemId == rightBtnFirst.getId() && rightBtnFirst != null) {
            rightBtnFirst.notifyWithoutCount(enable);
        } else if (rightBtnSecond != null) {
            rightBtnSecond.notifyWithoutCount(enable);
        }
    }

    public void notifyWithCount(int itemId, int count) {
        if (itemId == rightBtnFirst.getId() && rightBtnFirst != null) {
            rightBtnFirst.notifyWithCount(count);
        } else if (rightBtnSecond != null) {
            rightBtnSecond.notifyWithCount(count);
        }
    }

    public void setTitle(CharSequence titleText) {
        if (tvTitle != null && tvTitle.getVisibility() == VISIBLE) {
            tvTitle.setText(titleText);
        }
    }

    public void setSubTitle(CharSequence subTitleText) {
        if (tvSubTitle != null && tvSubTitle.getVisibility() == VISIBLE) {
            tvSubTitle.setText(subTitleText);
        }
    }

    public CharSequence getTitle() {
        if (tvTitle != null && titleLayout.getVisibility() == VISIBLE && tvTitle.getVisibility() == VISIBLE) {
            return tvTitle.getText();
        } else return null;
    }

    public CharSequence getSubTitle() {
        if (tvSubTitle != null && titleLayout.getVisibility() == VISIBLE && tvSubTitle.getVisibility() == VISIBLE) {
            return tvSubTitle.getText();
        } else return null;
    }

    // Added by waynezhang on 3/14/16: 支持动态修改TitleBar的style
    public void setStyle(int style) {
        this.removeAllViews();
        ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.mc_title_bar_ext, this);
        TypedArray ta = getContext().obtainStyledAttributes(style, R.styleable.McTitleBarExt);
        initStyle(getContext(), ta);
        ta.recycle();
    }

}
