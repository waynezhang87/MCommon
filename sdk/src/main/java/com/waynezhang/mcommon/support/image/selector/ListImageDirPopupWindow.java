package com.waynezhang.mcommon.support.image.selector;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.util.ScreenUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by liuxiaofeng02 on 2015/3/11.
 */
public class ListImageDirPopupWindow extends BasePopupWindowForListView<ImageFolder> {
    private ListView mListDir;
    private String mSelectedItemPath;

    public ListImageDirPopupWindow(int width, int height, List<ImageFolder> datas, View convertView) {
        super(convertView, width, height, true, datas);
    }

    @Override
    public void initViews() {
        mListDir = (ListView) findViewById(R.id.mc_list);
        mListDir.setAdapter(new CommonAdapter<ImageFolder>(context, mDatas, R.layout.mc_list_image_dir_item) {
            @Override
            public void convert(ViewHolder helper, ImageFolder item) {
                ImageView selectedView = helper.getView(R.id.mc_dir_sel);
                selectedView.setVisibility(View.INVISIBLE);
                helper.setText(R.id.mc_dir_item_name, item.getName());

                int width = ScreenUtil.convertDipToPixel(context, 80);
                Picasso picasso = Picasso.with(context);
                picasso.load(new File(item.getFirstImagePath())).
                        resize(width, width).into((ImageView) helper.getView(R.id.mc_image_dir_item));

                helper.setText(R.id.mc_dir_item_count, item.getCount() + "张");

                if(mSelectedItemPath != null && item.getDir().equals(mSelectedItemPath)){
                    selectedView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public interface OnImageDirSelected {
        void selected(ImageFolder folder);
    }

    private OnImageDirSelected mImageDirSelected;

    public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected) {
        this.mImageDirSelected = mImageDirSelected;
    }

    @Override
    public void initEvents() {
        mListDir.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mImageDirSelected != null) {
                    mImageDirSelected.selected(mDatas.get(position));
                }
                mSelectedItemPath = mDatas.get(position).getDir();
            }
        });
    }

    @Override
    public void init() {

    }

    @Override
    protected void beforeInitWeNeedSomeParams(Object... params) {
    }
}
