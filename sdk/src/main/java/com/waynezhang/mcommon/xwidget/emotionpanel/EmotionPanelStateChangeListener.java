package com.waynezhang.mcommon.xwidget.emotionpanel;

import com.waynezhang.mcommon.xwidget.emotionpanel.container.EmotionInfo;

/**
 * Created by waynezhang on 7/13/15.
 */
public interface EmotionPanelStateChangeListener {
    void onPageChange (int groupIndex, int pageIndex);
    void onEmotionItemSelected (EmotionInfo emotionItem);
}
