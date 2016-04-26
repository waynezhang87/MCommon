package com.waynezhang.mcommon.xwidget;

import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import com.waynezhang.mcommon.compt.TextViewCompt;

/**
 * Created by liuyagang on 16/3/1.
 */
public class TextViewUtil {
    public static void observeUrlClick(final TextView textView, final OnLinkClickListener listener) {
        Spannable sp = new SpannableString(textView.getText());
        final int highlightColor = TextViewCompt.getHighlightColor(textView);
        URLSpan[] spans = textView.getUrls();
        for (final URLSpan span : spans) {
            URLSpan newSpan = new URLSpan(span.getURL()) {
                @Override
                public void onClick(View widget) {
                    listener.onLinkClick(this.getURL());
                    Selection.removeSelection((Spannable) textView.getText());
                    textView.invalidate();
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);

                    if (((Spanned) textView.getText()).getSpanStart(this) == textView.getSelectionStart() && ((Spanned) textView.getText()).getSpanEnd(this) == textView.getSelectionEnd()) {
                        ds.setColor(highlightColor);
                    }
                }
            };
            int start = sp.getSpanStart(span);
            int end = sp.getSpanEnd(span);
            int flags = sp.getSpanFlags(span);
            sp.removeSpan(span);
            sp.setSpan(newSpan, start, end, flags);
        }
        textView.setText(sp);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(0);
    }

    public interface OnLinkClickListener {
        void onLinkClick(String url);
    }
}
