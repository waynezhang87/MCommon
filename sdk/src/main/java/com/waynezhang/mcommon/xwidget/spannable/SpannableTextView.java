package com.waynezhang.mcommon.xwidget.spannable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.waynezhang.mcommon.util.ScreenUtil;
import com.waynezhang.mcommon.util.StringUtil;
import com.waynezhang.mcommon.xwidget.emotionpanel.container.EmotionInfoContainer;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Created by waynezhang on 7/7/15.
 */
public class SpannableTextView extends TextView{
    private final static String TAG = "SpannableTextView";
    private CharSequence text;
    private boolean _isEscapeEnable = false;
    private boolean _isSpanableEnable = false;
    private boolean isOnlySupportText;

    public void setEscapeEnable(boolean flag) {
        _isEscapeEnable = flag;
    }

    public SpannableTextView(Context context) {
        super(context);
        EmotionInfoContainer.initGlobalEmotionInfo(context, null);
    }

    public SpannableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        EmotionInfoContainer.initGlobalEmotionInfo(context, null);
    }

    public SpannableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setNoSpannalbel(boolean enable){
        _isSpanableEnable = enable;
    }

    @Override
    public CharSequence getText() {
        return text == null ? "" : text;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        this.text = text;
        if (_isSpanableEnable){
            super.setText(text, type);
            return;
        }

        if (_isEscapeEnable){
            this.text = StringUtil.removeEscapeEnable(text.toString());
            Log.d(TAG, "setText this.text=" + this.text + ",text=" + text);
        }

        SpannableString spannable = new SpannableString(getText());
        if (isOnlySupportText) {
            if (EmotionInfoContainer.mapPngEmotionTxtInfoGlobal != null) {
                Set<Map.Entry<String, String>> set = EmotionInfoContainer.mapPngEmotionTxtInfoGlobal.entrySet();
                Vector<Integer> vecHas=new Vector<Integer>();
                for (Iterator<Map.Entry<String, String>> it = set.iterator(); it.hasNext();) {
                    Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();

                    Vector<Integer> vec= StringUtil.findStringIndex(getText().toString(), entry.getKey());
                    for(int i=0;i<vec.size();++i){
                        if(!vecHas.contains(vec.get(i))){
                            String span = new String(EmotionInfoContainer.mapPngEmotionTxtInfoGlobal.get(entry.getKey()));
                            spannable.setSpan(span, vec.get(i),vec.get(i)+entry.getKey().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            vecHas.add(vec.get(i));
                        }
                    }
                }
            }
        } else {
            if (EmotionInfoContainer.mapPngEmotionInfoGlobal != null) {
                Set<Map.Entry<String, Integer>> set = EmotionInfoContainer.mapPngEmotionInfoGlobal.entrySet();
                Vector<Integer> vecHas = new Vector<Integer>();
                for (Iterator<Map.Entry<String, Integer>> it = set.iterator(); it.hasNext(); ) {
                    Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();

                    Vector<Integer> vec = null;

                    if (_isEscapeEnable) {
                        vec = StringUtil.findStringIndexForEscape(text
                                .toString(), entry.getKey());
                    } else {
//                    vec = StringUtil.findStringIndex(getText1()
//                            .toString(), entry.getKey());

                        vec = StringUtil.findStringIndex(getText().toString(), entry.getKey());
                    }

                    for (int i = 0; i < vec.size(); ++i) {
                        if (!vecHas.contains(vec.get(i))) {
                            Drawable drawable = getResources().getDrawable(entry.getValue());
                            if (drawable != null) {
                                int padding = ScreenUtil.dip2px(getContext(), 2.5f);
                                MarginImageSpan span = new MarginImageSpan(drawable, ImageSpan.ALIGN_BASELINE, padding);
                                spannable.setSpan(span, vec.get(i), vec.get(i) + entry.getKey().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            } else {
                                String span = new String(EmotionInfoContainer.mapPngEmotionTxtInfoGlobal.get(entry.getKey()));
                                spannable.setSpan(span, vec.get(i), vec.get(i) + entry.getKey().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            vecHas.add(vec.get(i));
                        }
                    }
                }
            }
        }
        super.setText(spannable, type);
    }

    public void setSpannableType(boolean isOnlySupportText) {
        this.isOnlySupportText = isOnlySupportText;
    }

}