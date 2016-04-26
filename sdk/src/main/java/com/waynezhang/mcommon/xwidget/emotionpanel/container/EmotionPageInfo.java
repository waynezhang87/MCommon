package com.waynezhang.mcommon.xwidget.emotionpanel.container;

import java.util.Vector;

/**
 * Created by waynezhang on 7/7/15.
 */
public class EmotionPageInfo {
    public static final int PNG_SMALL=1;
    public static final int GIF=2;
    public static final int PNG_BIG=3;

    private int columnSize;
    private int itemWidth;
    private int itemHeight;
    private int type=PNG_SMALL;
    private Vector<EmotionInfo> vecEmotionInfo;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    public int getItemWidth() {
        return itemWidth;
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public Vector<EmotionInfo> getVecEmotionInfo() {
        return vecEmotionInfo;
    }

    public void setVecEmotionInfo(Vector<EmotionInfo> vecEmotionInfo) {
        this.vecEmotionInfo = vecEmotionInfo;
    }
}
