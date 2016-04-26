/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 jc0mm0n
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.waynezhang.mcommon.xwidget;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.compt.ArgumentsCompt;

public abstract class McDialogTwoBtn extends McDialog {

    private static final String DEFAULT_POSITIVE_TEXT = "确定";

    private static final String DEFAULT_NEGATIVE_TEXT = "取消";

    public static McDialogTwoBtn newInstance(String content, final View.OnClickListener positiveListener,
                                             final View.OnClickListener negativeListener) {
        return newInstance(content, null, null, positiveListener, negativeListener);
    }

    public static McDialogTwoBtn newInstance(String content, String positiveText,
                                                 String negativeText, final View.OnClickListener positiveListener,
                                                 final View.OnClickListener negativeListener) {
        McDialogTwoBtn jcDialogTwoBtn = new McDialogTwoBtn() {
            @Override
            public OnClickListener getPositiveListener() {
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dismiss();
                        if (positiveListener != null) {
                            positiveListener.onClick(v);
                        }
                    }
                };
            }

            @Override
            public OnClickListener getNegativeListener() {
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dismiss();
                        if (negativeListener != null) {
                            negativeListener.onClick(v);
                        }
                    }
                };
            }
        };

        Bundle args = new Bundle();
        args.putString("content", content);
        args.putString("positiveText", positiveText);
        args.putString("negativeText", negativeText);
        jcDialogTwoBtn.setArguments(args);
        return jcDialogTwoBtn;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mc__dialog_two_btn, container,
                false);

        String content = ArgumentsCompt.getString(getArguments(), "content", "");
        String positiveBtnText = ArgumentsCompt.getString(getArguments(), "positiveText",
                DEFAULT_POSITIVE_TEXT);
        String negativeBtnText = ArgumentsCompt.getString(getArguments(), "negativeText",
                DEFAULT_NEGATIVE_TEXT);

        TextView mContentTv = (TextView) view.findViewById(R.id.content_text);
        mContentTv.setText(content);
        mContentTv.setGravity(Gravity.CENTER);

        TextView mRightBtn = (TextView) view.findViewById(R.id.right_btn);
        TextView mLeftBtn = (TextView) view.findViewById(R.id.lef_btn);

        String forRightText = negativeBtnText;
        View.OnClickListener forRightListener = getNegativeListener();
        String forLeftText = positiveBtnText;
        View.OnClickListener forLeftListener = getPositiveListener();
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            forRightText = positiveBtnText;
            forRightListener = getPositiveListener();
            forLeftText = negativeBtnText;
            forLeftListener = getNegativeListener();
        }

        mRightBtn.setText(forRightText);
        mRightBtn.setOnClickListener(forRightListener);
        mLeftBtn.setText(forLeftText);
        mLeftBtn.setOnClickListener(forLeftListener);

        return view;
    }

    protected abstract View.OnClickListener getPositiveListener();

    protected abstract View.OnClickListener getNegativeListener();

}
