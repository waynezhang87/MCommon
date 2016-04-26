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

public abstract class McDialogOneBtn extends McDialog {

    private static final String DEFAULT_BTN_TEXT = "知道了";

    public static McDialogOneBtn newInstance(String content, final View.OnClickListener listener) {
        return newInstance(content, null, listener);
    }

    public static McDialogOneBtn newInstance(String content, String btnText,
                                                 final View.OnClickListener listener) {
        McDialogOneBtn jcDialogOneBtn = new McDialogOneBtn() {
            @Override
            public OnClickListener getBtnListener() {
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dismiss();
                        if (listener != null) {
                            listener.onClick(v);
                        }
                    }
                };
            }
        };

        Bundle args = new Bundle();
        args.putString("content", content);
        args.putString("btnText", btnText);
        jcDialogOneBtn.setArguments(args);
        return jcDialogOneBtn;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mc__dialog_one_btn, container, false);

        String content = ArgumentsCompt.getString(getArguments(), "content", "");
        String btnText = ArgumentsCompt.getString(getArguments(), "btnText",
                DEFAULT_BTN_TEXT);

        TextView mContentTv = (TextView) view.findViewById(R.id.content_text);
        mContentTv.setText(content);
        mContentTv.setGravity(Gravity.CENTER);

        TextView btn = (TextView) view.findViewById(R.id.single_btn);
        btn.setText(btnText);
        btn.setOnClickListener(getBtnListener());

        return view;
    }

    protected abstract View.OnClickListener getBtnListener();

}
