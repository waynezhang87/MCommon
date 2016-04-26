package com.waynezhang.mcommon.xwidget;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.waynezhang.mcommon.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by liuyagang on 16-1-29.
 */
public class MenuDialog extends McDialog {
    private int layoutId = R.layout.mc_dialog_select_list;
    private int titleId = R.id.mc_title;
    private int listId = R.id.mc_list;
    private int itemLayoutId = R.layout.mc_item_textview;
    private int textId = R.id.item_text;
    private String title;
    private List<String> data;
    private boolean cancelAble = true;
    private ColorDrawable bkColor = null;
    private DialogInterface.OnCancelListener onCancelListener;
    private OnItemClickListener onItemClickListener;
    private SparseArray<CharSequence> textSparseArray = new SparseArray<>();

    public static Builder build(FragmentActivity activity) {
        return new Builder(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layoutId, container, false);

        ListView list = (ListView) view.findViewById(listId);
        TextView tvTitle = (TextView) view.findViewById(titleId);
        if (TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
        }
        if (bkColor != null) {
            getDialog().getWindow().setBackgroundDrawable(bkColor);
        }
        if (cancelAble) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().cancel();
                }
            });
        }
        if (onItemClickListener != null) {
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dismiss();
                    onItemClickListener.onItemClick(position);
                    onItemClickListener.onItemClick(data.get(position));
                }
            });
        }

        SimpleArrayAdapter<String, MenuItemView> adapter = new SimpleArrayAdapter<String, MenuItemView>(getActivity()) {
            @Override
            protected MenuItemView build(Context context) {
                return new MenuItemView(context, itemLayoutId);
            }
        };
        adapter.bind(data);
        list.setAdapter(adapter);

        return view;
    }

    public static class Builder {
        FragmentActivity activity;
        MenuDialog dialog = new MenuDialog();

        public Builder(FragmentActivity activity) {
            this.activity = activity;
        }

        public Builder layout(int layoutId, int titleId, int listId) {
            dialog.layoutId = layoutId;
            dialog.titleId = titleId;
            dialog.listId = listId;
            return this;
        }

        public Builder itemLayout(int layoutId, int textId) {
            dialog.itemLayoutId = layoutId;
            dialog.textId = textId;
            return this;
        }

        public Builder title(String title) {
            dialog.title = title;
            return this;
        }

        public Builder data(List<String> data) {
            dialog.data = data;
            return this;
        }

        public Builder data(String[] data) {
            dialog.data = Arrays.asList(data);
            return this;
        }

        public Builder cancelAble(boolean cancelAble) {
            dialog.cancelAble = cancelAble;
            return this;
        }

        public Builder bkColor(int bkColor) {
            dialog.bkColor = new ColorDrawable(bkColor);
            return this;
        }

        public Builder onCancelListener(DialogInterface.OnCancelListener listener) {
            dialog.onCancelListener = listener;
            return this;
        }

        public Builder onItemClickListener(OnItemClickListener listener) {
            dialog.onItemClickListener = listener;
            return this;
        }

        public MenuDialog show() {
            dialog.fixedShow(activity);
            return dialog;
        }
    }

    public class MenuItemView extends FrameLayout implements Bindable<String> {
        TextView mc_text;

        public MenuItemView(Context context, int layoutId) {
            super(context);
            inflate(context, layoutId, this);
            mc_text = (TextView) findViewById(textId);
        }

        @Override
        public void bind(String item) {
            mc_text.setText(item);
        }
    }

    public abstract static class OnItemClickListener {
        public void onItemClick(int position) {
        }

        public void onItemClick(String item) {
        }
    }
}
