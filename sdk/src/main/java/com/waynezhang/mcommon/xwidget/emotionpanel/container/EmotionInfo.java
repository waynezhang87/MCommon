package com.waynezhang.mcommon.xwidget.emotionpanel.container;

/**
 * Created by waynezhang on 7/7/15.
 */
public class EmotionInfo {
    public static final int EMOTION_SMALL = 1;
    public static final int DELETE = 2;
    public static final int EMOTION_BIG = 3;

    private String name;    //对应xml配置文件中id
    private String text;    //对应xml配置文件中name
    private int type = EMOTION_SMALL; //对应xml配置文件中type
    private String icon;    //对应xml配置文件中icon
    private int resId;      //icon对应的resID

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean equals(EmotionInfo emotionInfo) {
        return (name.equals(emotionInfo.getName())
                && text.equals(emotionInfo.getName())
                && type == emotionInfo.getType()
                && icon.equals(emotionInfo.getIcon()));
    }
}
