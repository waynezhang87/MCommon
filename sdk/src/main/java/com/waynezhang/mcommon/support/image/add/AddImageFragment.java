package com.waynezhang.mcommon.support.image.add;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.activity.RequestCode;
import com.waynezhang.mcommon.support.image.Image;
import com.waynezhang.mcommon.support.image.selector.ImageSelectorFragment;
import com.waynezhang.mcommon.util.ImageSelecter;
import com.waynezhang.mcommon.util.L;
import com.waynezhang.mcommon.util.PicassoUtil;
import com.waynezhang.mcommon.xwidget.Bindable;
import com.waynezhang.mcommon.xwidget.OptionDialog;
import com.waynezhang.mcommon.xwidget.SimpleArrayAdapter;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by liuxiaofeng02 on 2015/4/13.
 */
public class AddImageFragment extends Fragment {
    private static final String TAG = "AddImageFragment";

    private SimpleArrayAdapter<ImageItem, ImageItemView> mPicAdapter = null;
    private GridView list;
    private TextView tvHintView;
    private int addImageResId;
    private int closeSmallIconResId;
//    private int singleLinePicCount = 5;
//    private int gridColumnHeight = 0;
    private int maxPicCount = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mc_fragment_add_image, container, false);
        closeSmallIconResId = getInitializeCallback().getCloseSmallIconResId();
        addImageResId = R.drawable.mc_icon_pic;
        {
            int resId = getInitializeCallback().getAddImageResId();
            if(resId > 0){
                addImageResId = resId;
            }
        }

        int backgroundColor = getInitializeCallback().getBackgroundColor();
        if (backgroundColor > 0) {
            rootView.setBackgroundResource(backgroundColor);
        }


        {
            String hint = getInitializeCallback().getHint();
            tvHintView = (TextView) rootView.findViewById(R.id.mc_tv_hint);
            if(TextUtils.isEmpty(hint)){
                tvHintView.setVisibility(View.GONE);
            } else {
                tvHintView.setText(hint);
                tvHintView.setVisibility(View.VISIBLE);
            }
        }

        mPicAdapter = new SimpleArrayAdapter<ImageItem, ImageItemView>(getActivity()) {
            @Override
            protected ImageItemView build(Context context) {
                return new ImageItemView(context);
            }
        };
        list = (GridView) rootView.findViewById(R.id.mc_list);
        list.setAdapter(mPicAdapter);
        add_pic_btn();

        maxPicCount = getInitializeCallback().getMaxImageCount();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageItem item = (ImageItem) mPicAdapter.getItem(position);
                if (!item.isLoading && position <= maxPicCount - 1) { // 处在loading当中不能点击
                    if (item.res_id == addImageResId) {
                        show_add_pic_dialog();
                    } else {
                        ArrayList<Image> result = new ArrayList<Image>(mPicAdapter.getCount());
                        //在图片上传过程中如果图片上传慢的话，这些图片则不能查看大图，因而点击查看大图时位置要重新定位
                        int convertPosition = getImageList(item.url, result);
                        BigImageFragment.go(AddImageFragment.this, convertPosition, result);
                    }
                }
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                getInitializeCallback().OnItemLongClickListener(mPicAdapter.getItem(position),mPicAdapter);
                return true;
            }
        });

        return rootView;
    }

    /**
     * adapter设置添加图片按钮
     */
    private void add_pic_btn() {
        ImageItem add_pic = new ImageItem();
        add_pic.res_id = addImageResId;
        mPicAdapter.add(add_pic);
        mPicAdapter.notifyDataSetChanged();
        //refreshGridViewHeight();
    }

    /**
     * 弹窗让用户选择图片获取方式
     */
    private void show_add_pic_dialog() {
        OptionDialog.build(getActivity(), R.layout.mc_add_pic_dialog).onClickListener(R.id.mc_take_photo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageSelecter.imageFromPhoto(AddImageFragment.this);
            }
        }).onClickListener(R.id.mc_take_pic_from_local, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageSelecter.multiImageFromLocal(AddImageFragment.this, maxPicCount - (mPicAdapter.getCount() - 1));
            }
        }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        L.d("onActivityResult", "requestCode=" + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == ImageSelecter.TAKE_PHONE) {
            //把添加图片按钮移动到最后一个位置（先删除，后添加）
            mPicAdapter.remove(mPicAdapter.getItem(mPicAdapter.getCount() - 1));

            tvHintView.setVisibility(View.GONE);

            ImageItem imageItem = addOnePic(ImageSelecter.getPhotoImagePathWithRotate(getActivity()));
            mPicAdapter.add(imageItem);
            getInitializeCallback().addImageCallback(imageItem, new AfterCallback() {
                @Override
                public void callback(ImageItem item) {
                    doAfterAddImageCallback(item);
                }
            });

            //如果最后一张图不是添加图片按钮且还未达到最大图片数，添加图片按钮供用户继续添加
            int count = mPicAdapter.getCount();
            if (count < maxPicCount && count > 0) {
                ImageItem lastItem = mPicAdapter.getItem(count - 1);
                if (lastItem.res_id != addImageResId) {
                    add_pic_btn();
                }
            }
        } else if (requestCode == ImageSelecter.MULTI_PIC_FROM_LOCAL) {
            //把添加图片按钮移动到最后一个位置（先删除，后添加）
            mPicAdapter.remove(mPicAdapter.getItem(mPicAdapter.getCount() - 1));

            String[] sImages = data.getStringArrayExtra(ImageSelectorFragment.RETURN_IMAGE_ARRAY_KEY);

            tvHintView.setVisibility(View.GONE);

            for (int i = 0; i < sImages.length; i++) {
                ImageItem imageItem = addOnePic(sImages[i]);
                mPicAdapter.add(imageItem);
                mPicAdapter.notifyDataSetChanged();
                getInitializeCallback().addImageCallback(imageItem, new AfterCallback() {
                    @Override
                    public void callback(ImageItem item) {
                        doAfterAddImageCallback(item);
                    }
                });
            }

            //如果最后一张图不是添加图片按钮且还未达到最大图片数，添加图片按钮供用户继续添加
            int count = mPicAdapter.getCount();
            if (count < maxPicCount && count > 0) {
                ImageItem lastItem = mPicAdapter.getItem(count - 1);
                if (lastItem.res_id != addImageResId) {
                    add_pic_btn();
                }
            }
        } else if(requestCode == RequestCode.BIG_IMAGE){
            List<Image> deleteList = (List<Image>)data.getSerializableExtra(BigImageFragment.KEY_OUTPUT_DELETE_IMAGE_ARRAY);
            Set<String> deleteSet = new HashSet<>(deleteList.size());
            for (Image image : deleteList) {
                deleteSet.add(image.url);
            }
            List<ImageItem> toDeleteList = new ArrayList<>(deleteList.size());
            for (int i = 0; i < mPicAdapter.getCount(); i++) {
                ImageItem imageItem = mPicAdapter.getItem(i);
                if (deleteSet.contains(imageItem.url)) {
                    toDeleteList.add(imageItem);
                }
            }
            for (ImageItem item : toDeleteList) {
                getInitializeCallback().deleteImageCallback(item, new AfterCallback() {
                    @Override
                    public void callback(ImageItem item) {
                        mPicAdapter.remove(item);
                        doAfterDeleteImage();
                    }
                });
            }
        }
    }

    private void doAfterAddImageCallback(final ImageItem item){
        //上层做完图片处理（上传）工作后，此处做收尾工作
        item.isLoading = false;
        if(TextUtils.isEmpty(item.smallUrl)){
            mPicAdapter.remove(item);
            doAfterDeleteImage();
        } else {
            mPicAdapter.notifyDataSetChanged();
        }
    }


    private void doAfterDeleteImage(){
        int count = mPicAdapter.getCount();
        if (count < maxPicCount) {
            if (count == 0) {
                add_pic_btn();
                if(!TextUtils.isEmpty(tvHintView.getText())){
                    tvHintView.setVisibility(View.VISIBLE);
                }
            } else if (count > 0) {
                ImageItem lastItem = mPicAdapter.getItem(count - 1);
                if (lastItem.res_id != addImageResId) {
                    add_pic_btn();
                } else if(count == 1 && !TextUtils.isEmpty(tvHintView.getText())){
                    tvHintView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 外部给组件动态添加一组图片
     *
     * @param list
     * @return
     */
    public boolean addImage(List<ImageItem> list) {
        if (list == null || list.size() < 1) {
            return true;
        }

        int count = mPicAdapter.getCount();
        if (count > 0) {
            ImageItem item = mPicAdapter.getItem(count - 1);
            //如果最后一张图是添加图片按钮，则实际图片数需要减1
            if (TextUtils.isEmpty(item.url)) {
                mPicAdapter.remove(item);
                count -= 1;
            }
        }

        int toCount = count + list.size();
        if (toCount > maxPicCount) {
            L.e("AddImageFragment", "addImage(List<ImageItem> list) : the picture size " + toCount + " after add is greater than maxPicCount " + maxPicCount);
            return false;
        }

        mPicAdapter.addAll(list);
        if (toCount < maxPicCount) {
            add_pic_btn();
        }
        //refreshGridViewHeight();
        return true;
    }

    private ImageItem addOnePic(String path) {
        ImageItem item = new ImageItem();
        item.isLoading = true;
        item.localPath = path;

        return item;
    }

    // 动态设置gridview的高度
//    private void refreshGridViewHeight() {
//        int size = list.getCount();
//        int verticalSpacing = 20;
//
//        singleLinePicCount = list.getNumColumns();
//        int lines = size / singleLinePicCount;
//        if (size % singleLinePicCount != 0) {
//            lines += 1;
//        }
//
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, lines * gridColumnHeight + (lines - 1) * verticalSpacing);
//        list.setLayoutParams(params);
//    }

    private int getImageList(String selectedUrl, ArrayList<Image> result) {
        int size = mPicAdapter.getCount();
        int selectedPosition = 0;
        for (int i = 0; i < size; i++) {
            ImageItem item = mPicAdapter.getItem(i);
            if (!TextUtils.isEmpty(item.url)) {
                result.add(item);
                if (item.url.equals(selectedUrl)) {
                    selectedPosition = result.size() - 1;
                }
            }
        }
        return selectedPosition;
    }

    public static interface Initialize {
        //可选择的最大图片数
        public int getMaxImageCount();

        //图片加载过程中的占位图
        public int getPlaceholderResId();

        //添加图片的回调
        public void addImageCallback(ImageItem item, AfterCallback callback);

        //删除图片的回调
        public void deleteImageCallback(ImageItem item, AfterCallback callback);

        //长按
        void OnItemLongClickListener(ImageItem item , SimpleArrayAdapter<ImageItem, ImageItemView> PicAdapter);

        //获取背景色资源ID
        public int getBackgroundColor();

        //获取提示
        public String getHint();

        //获取添加图片的资源id
        public int getAddImageResId();

        //获取小图右上角关闭图标的资源id
        public int getCloseSmallIconResId();
    }

    public static interface AfterCallback {
        public void callback(ImageItem item);
    }

    public static class ImageItem extends Image implements Serializable {
        //添加图片按钮的图片资源id
        public int res_id;
        // 是否显示loading
        public boolean isLoading;

        //本地图片地址
        public String localPath;

        public String coverPath;

        public void setUrl(String small_url, String url) {
            this.url = url;
            this.smallUrl = small_url;
        }

        public ImageItem() {
        }
    }

    public class ImageItemView extends LinearLayout implements Bindable<ImageItem> {
        private ImageView mImageView;
        private ProgressBar mLoadingView;
        private ImageView mDeleteView;

        public ImageItemView(Context context) {
            super(context);
            init(context, null);
        }

        public ImageItemView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context, attrs);
        }

        private void init(Context context, AttributeSet attrs) {
            ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                    R.layout.mc_fragment_add_image_item, this);
            mImageView = (ImageView) findViewById(R.id.mc_image);
            mLoadingView = (ProgressBar) findViewById(R.id.mc_loading);
            mDeleteView = (ImageView) findViewById(R.id.mc_iv_delete);
        }

        @Override
        public void bind(final ImageItem item) {
            mDeleteView.setVisibility(GONE);
            if (item.isLoading) {
                PicassoUtil.show(mImageView, getContext(), getInitializeCallback().getPlaceholderResId());
                mLoadingView.setVisibility(View.VISIBLE);
            } else if (TextUtils.isEmpty(item.smallUrl) && TextUtils.isEmpty(item.coverPath)) {
                if (!TextUtils.isEmpty(item.localPath)) {
                    PicassoUtil.show(mImageView, getContext(), new File(item.localPath));
                    if(closeSmallIconResId > 0){
                        mDeleteView.setImageResource(closeSmallIconResId);
                        mDeleteView.setVisibility(VISIBLE);
                    }
                } else {
                    PicassoUtil.show(mImageView, getContext(), item.res_id);
                }
                mLoadingView.setVisibility(View.GONE);
            } else if(!TextUtils.isEmpty(item.coverPath)){
                PicassoUtil.show(mImageView, this.getContext(), item.coverPath);
                mLoadingView.setVisibility(View.GONE);
                if(closeSmallIconResId > 0){
                    mDeleteView.setImageResource(closeSmallIconResId);
                    mDeleteView.setVisibility(VISIBLE);
                }
            } else if (!TextUtils.isEmpty(item.smallUrl)) {

                    PicassoUtil.show(mImageView, this.getContext(), item.smallUrl);
                    mLoadingView.setVisibility(View.GONE);
                    if(closeSmallIconResId > 0){
                        mDeleteView.setImageResource(closeSmallIconResId);
                        mDeleteView.setVisibility(VISIBLE);
                }
            }

            mDeleteView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    getInitializeCallback().deleteImageCallback(item, new AfterCallback() {
                        @Override
                        public void callback(ImageItem item) {
                            mPicAdapter.remove(item);
                            doAfterDeleteImage();
                        }
                    });
                }
            });
        }
    }

    private Initialize getInitializeCallback() {
        if (Initialize.class.isAssignableFrom(getActivity().getClass())) {
            return (Initialize) getActivity();
        } else if (Initialize.class.isAssignableFrom(getParentFragment().getClass())) {
            return (Initialize) getParentFragment();
        } else try {
            throw new Exception("AddImageFragment.getInitializeCallback: AddImageFragment所在的FragmentActivity或者其父类Fragment必须实现Initialize接口方法");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
