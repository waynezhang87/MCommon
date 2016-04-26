package com.waynezhang.mcommon.xwidget.titlebar;

/**
 * Created by waynezhang on 3/2/16.
 */
public class McTitleBarExtMenuItem {
    public int id;
    public String title;
    public int iconLargeResId;  //用于显示在TitleBar中的大图
    public int iconSmallResId;  //用于显示在PopupMenu中的小图

    public McTitleBarExtMenuItem() {
    }

    public McTitleBarExtMenuItem(int id, String title, int iconLargeResId, int iconSmallResId) {
        this.id = id;
        this.title = title;
        this.iconLargeResId = iconLargeResId;
        this.iconSmallResId = iconSmallResId;
    }
}
