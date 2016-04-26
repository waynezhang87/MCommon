package com.waynezhang.mcommon.xwidget.titlebar;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.waynezhang.mcommon.R;

/**
 * Created by waynezhang on 3/4/16.
 */
public class McTitleBarSearch extends LinearLayout implements View.OnClickListener{
    private View btnBack;
    private View btnSearch;
    private EditText etSearch;

    private OnButtonClickListener mListener;

    public void setOnClickListener(OnButtonClickListener listener) {
        this.mListener = listener;
    }

    public McTitleBarSearch(Context context) {
        super(context);
        init(context, null);
    }

    public McTitleBarSearch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.mc_title_bar_search, this);
        btnBack = findViewById(R.id.btnBack);
        btnSearch = findViewById(R.id.btnSearch);
        etSearch = (EditText) findViewById(R.id.etSearch);

        btnBack.setOnClickListener(this);
        btnSearch.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (mListener == null) return;
        if (v.getId() == btnBack.getId()) {
            mListener.onBackButtonClick(v);
        } else if (v.getId() == btnSearch.getId()) {
            mListener.onSearchButtonClick(v, etSearch.getText());
        }
    }


    public interface OnButtonClickListener {
        void onBackButtonClick(View v);
        void onSearchButtonClick(View v, Editable content);
    }
}
