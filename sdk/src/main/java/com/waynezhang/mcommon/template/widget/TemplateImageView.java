package com.waynezhang.mcommon.template.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.support.image.Image;
import com.waynezhang.mcommon.support.image.add.BigImageFragment;
import com.waynezhang.mcommon.support.image.selector.ImageSelectorFragment;
import com.waynezhang.mcommon.template.activity.BridgeActivity;
import com.waynezhang.mcommon.template.interfaces.ITemplateImageView;
import com.waynezhang.mcommon.template.interfaces.TemplateImageCallback;
import com.waynezhang.mcommon.template.interfaces.TemplateImgGoOtherCallback;
import com.waynezhang.mcommon.template.listener.ValueChangedListener;
import com.waynezhang.mcommon.template.model.SimplateTemplateImage;
import com.waynezhang.mcommon.template.model.TemplateImageItem;
import com.waynezhang.mcommon.util.ImageSelecter;
import com.waynezhang.mcommon.util.L;
import com.waynezhang.mcommon.util.PicassoUtil;
import com.waynezhang.mcommon.xwidget.Bindable;
import com.waynezhang.mcommon.xwidget.OptionDialog;
import com.waynezhang.mcommon.xwidget.ProgressWithTextBar;
import com.waynezhang.mcommon.xwidget.SimpleArrayAdapter;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sunxinxin on 11/2/15.
 */
public class TemplateImageView extends LinearLayout implements ITemplateImageView {

    public Gson gson = new GsonBuilder().create();
    Type listType = new TypeToken<List<TemplateImageItem>>(){}.getType();
    Type simpleListType = new TypeToken<List<SimplateTemplateImage>>(){}.getType();
    private static final String TAG = "TemplateImageView";

    private SimpleArrayAdapter<TemplateImageItem, TemplateImageItemView> mPicAdapter = null;
    private View rootView;
    private GridView list;
    private TextView tip;
    private TextView desc;
    private TextView title;
    private Activity activity;
    TemplateImageCallback templateImageCallback;

    private int addImageResId = R.drawable.arrow_icon;;
    private int closeSmallIconResId = R.drawable.arrow_icon;
    private int placeholderResId = R.drawable.arrow_icon;
    private int backgroundColor = R.color.mc_white;


    private int minCount = 1;
    private int maxCount = 10;

    private List<TemplateImageItem> mTemplateImageItems = new ArrayList<>();

    ValueChangedListener mListener;


    public String getDescription() {
        return desc.getText().toString();
    }



    @Override
    public List<TemplateImageItem> getImageList() {
        return mTemplateImageItems;
    }

    @Override
    public void setTemplateImageCallback(TemplateImageCallback callback) {
        templateImageCallback = callback;
    }

    public void setDescription(String description) {
        desc.setText(description);
    }



    @Override
    public void setMiniCount(int count) {
        minCount = count;
    }

    @Override
    public void setMaxCount(int count) {
        maxCount = count;
    }

    @Override
    public int getPlaceholderResId() {
        return placeholderResId;
    }


    @Override
    public String getTip() {
        return tip.getText().toString();
    }

    @Override
    public int getAddImageResId() {
        return addImageResId;
    }
    @Override
    public View getView() {
        return rootView;
    }

    @Override
    public CharSequence getText() {
        return title.getText().toString();
    }

    @Override
    public void setText(String text) {
        if(text != null && !text.isEmpty()){
            title.setVisibility(VISIBLE);
            title.setText(text);
        }
    }

    @Override
    public void setEnable(int allowModify) {
        rootView.setEnabled(allowModify == 1);
    }

    @Override
    public void setFocus(View v) {

    }

    @Override
    public void setTip(String text) {
        if(TextUtils.isEmpty(text)){
            tip.setVisibility(View.GONE);
        } else {
            tip.setText(text);
            tip.setVisibility(View.VISIBLE);
        }
        tip.setText(text);
    }

    @Override
    public String getValue() {
        String value = gson.toJson(getSimpleTemplateImage(mTemplateImageItems),simpleListType);
        return value;
    }

    @Override
    public void setValue(String value) {
        List<TemplateImageItem> templateImageItems = gson.fromJson(value,listType);
        if(templateImageItems == null || templateImageItems.size() < 1){
            return;
        }
        addImage(templateImageItems);
        for (TemplateImageItem item : templateImageItems){
            doAfterAddImageCallback(item);
        }
        mTemplateImageItems = templateImageItems;
    }

    @Override
    public void setValueChangedListener(ValueChangedListener changedListener) {
        mListener = changedListener;
    }

    @Override
    public void setProgressWithTextBar(ProgressWithTextBar progressWithTextBar) {

    }


    public TemplateImageView(Activity activity,int addImageResId,int closeSmallIconResId,int placeholderResId,int backgroundColor){
        super(activity);
        this.addImageResId = addImageResId;
        this.closeSmallIconResId = closeSmallIconResId;
        this.placeholderResId = placeholderResId;
        this.backgroundColor = backgroundColor;

        init(activity, null);
    }

    public TemplateImageView(Activity activity, AttributeSet attrs) {
        super(activity, attrs);
        init(activity, attrs);
    }

    public void init(Activity mactivity, AttributeSet attrs) {
        activity = mactivity;
        ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.mc_template_image_view, this);
        rootView = findViewById(R.id.rootView);
        title = (TextView)findViewById(R.id.title);
        tip = (TextView)findViewById(R.id.mc_tv_hint);
        desc = (TextView)findViewById(R.id.desc);


        mPicAdapter = new SimpleArrayAdapter<TemplateImageItem, TemplateImageItemView>(activity) {
            @Override
            protected TemplateImageItemView build(Context context) {
                return new TemplateImageItemView(context);
            }
        };
        list = (GridView) rootView.findViewById(R.id.mc_list);
        list.setAdapter(mPicAdapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TemplateImageItem item = (TemplateImageItem) mPicAdapter.getItem(position);
                if (!item.isLoading && position <= maxCount - 1) { // 处在loading当中不能点击
                    if (item.res_id == getAddImageResId()) {
                        show_add_pic_dialog();
                    } else {
                        ArrayList<Image> result = new ArrayList<Image>(mPicAdapter.getCount());
                        //在图片上传过程中如果图片上传慢的话，这些图片则不能查看大图，因而点击查看大图时位置要重新定位
                        int convertPosition = getImageList(item.url, result);
                        BridgeActivity.goBigImageFragment(activity, templateImgcallback, convertPosition, result);
                    }
                }
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                templateImageCallback.OnItemLongClickListener(mPicAdapter.getItem(position), mPicAdapter);
                return true;
            }
        });
        add_pic_btn();
    }

    /**
     * adapter设置添加图片按钮
     */
    private void add_pic_btn() {
        TemplateImageItem add_pic = new TemplateImageItem();
        add_pic.res_id = getAddImageResId();
        mPicAdapter.add(add_pic);
        mPicAdapter.notifyDataSetChanged();
        //refreshGridViewHeight();
    }

    /**
     * 弹窗让用户选择图片获取方式
     */
    private void show_add_pic_dialog() {
        OptionDialog.build((FragmentActivity)activity, R.layout.mc_add_pic_dialog).onClickListener(R.id.mc_take_photo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BridgeActivity.goImageSelector(activity, templateImgcallback);
            }
        }).onClickListener(R.id.mc_take_pic_from_local, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BridgeActivity.goMultiImageSelector(activity, maxCount - (mPicAdapter.getCount() - 1), templateImgcallback);
            }
        }).show();
    }

    /**
     * 外部给组件动态添加一组图片
     *
     * @param list
     * @return
     */
    public boolean addImage(List<TemplateImageItem> list) {
        if (list == null || list.size() < 1) {
            return true;
        }

        int count = mPicAdapter.getCount();
        if (count > 0) {
            TemplateImageItem item = mPicAdapter.getItem(count - 1);
            //如果最后一张图是添加图片按钮，则实际图片数需要减1
            if (TextUtils.isEmpty(item.url)) {
                mPicAdapter.remove(item);
                count -= 1;
            }
        }

        int toCount = count + list.size();
        if (toCount > maxCount) {
            L.e("AddImageFragment", "addImage(List<ImageItem> list) : the picture size " + toCount + " after add is greater than maxPicCount " + maxCount);
            return false;
        }

        mPicAdapter.addAll(list);
        if (toCount < maxCount) {
            add_pic_btn();
        }
        //refreshGridViewHeight();
        return true;
    }

    private TemplateImageItem addOnePic(String path) {
        TemplateImageItem item = new TemplateImageItem();
        item.isLoading = true;
        item.localPath = path;

        return item;
    }

    private int getImageList(String selectedUrl, ArrayList<Image> result) {
        int size = mPicAdapter.getCount();
        int selectedPosition = 0;
        for (int i = 0; i < size; i++) {
            TemplateImageItem item = mPicAdapter.getItem(i);
            if (!TextUtils.isEmpty(item.url)) {
                result.add(item);
                if (item.url.equals(selectedUrl)) {
                    selectedPosition = result.size() - 1;
                }
            }
        }
        return selectedPosition;
    }



    //----------------------------------------------------------------------------------------------
    public class TemplateImageItemView extends LinearLayout implements Bindable<TemplateImageItem> {
        private ImageView mImageView;
        private ProgressBar mLoadingView;
        private ImageView mDeleteView;

        public TemplateImageItemView(Context context) {
            super(context);
            init(context, null);
        }

        public TemplateImageItemView(Context context, AttributeSet attrs) {
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
        public void bind(final TemplateImageItem item) {
            mDeleteView.setVisibility(GONE);
            if (item.isLoading) {
                PicassoUtil.show(mImageView, getContext(), getPlaceholderResId());
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
                    templateImageCallback.deleteImgCallback(item, new TemplateImageCallback.AfterCallback() {
                        @Override
                        public void callback(TemplateImageItem item) {
                            mTemplateImageItems.remove(item);
                            if (mListener != null){
                                mListener.onValueChanged(TemplateImageView.this,getValue(),null);
                            }
                        }
                    });
                }
            });
        }
    }

    TemplateImgGoOtherCallback templateImgcallback = new TemplateImgGoOtherCallback() {
        @Override
        public void take_phone(int requestCode, int resultCode, Intent data) {
            //把添加图片按钮移动到最后一个位置（先删除，后添加）
            mPicAdapter.remove(mPicAdapter.getItem(mPicAdapter.getCount() - 1));

            tip.setVisibility(View.GONE);

            TemplateImageItem imageItem = addOnePic(ImageSelecter.getPhotoImagePathWithRotate(activity));
            mPicAdapter.add(imageItem);
            templateImageCallback.addImgCallback(imageItem,new TemplateImageCallback.AfterCallback() {
                @Override
                public void callback(TemplateImageItem item) {
                    mTemplateImageItems.add(item);
                    if (mListener != null){
                        mListener.onValueChanged(TemplateImageView.this,getValue(),null);
                    }
                    doAfterAddImageCallback(item);
                }
            });

            //如果最后一张图不是添加图片按钮且还未达到最大图片数，添加图片按钮供用户继续添加
            int count = mPicAdapter.getCount();
            if (count < maxCount && count > 0) {
                TemplateImageItem lastItem = mPicAdapter.getItem(count - 1);
                if (lastItem.res_id != getAddImageResId()) {
                    add_pic_btn();
                }
            }
        }

        @Override
        public void multi_pic_from_local(int requestCode, int resultCode, Intent data) {
            //把添加图片按钮移动到最后一个位置（先删除，后添加）
            mPicAdapter.remove(mPicAdapter.getItem(mPicAdapter.getCount() - 1));

            String[] sImages = data.getStringArrayExtra(ImageSelectorFragment.RETURN_IMAGE_ARRAY_KEY);

            tip.setVisibility(View.GONE);

            for (int i = 0; i < sImages.length; i++) {
                TemplateImageItem imageItem = addOnePic(sImages[i]);
                mPicAdapter.add(imageItem);
                mPicAdapter.notifyDataSetChanged();
                if (templateImageCallback != null){
                    templateImageCallback.addImgCallback(imageItem, new TemplateImageCallback.AfterCallback() {
                        @Override
                        public void callback(TemplateImageItem item) {
                            mTemplateImageItems.add(item);
                            if (mListener != null){
                                mListener.onValueChanged(TemplateImageView.this,getValue(),null);
                            }
                            doAfterAddImageCallback(item);
                        }
                    });
                }
            }

            //如果最后一张图不是添加图片按钮且还未达到最大图片数，添加图片按钮供用户继续添加
            int count = mPicAdapter.getCount();
            if (count < maxCount && count > 0) {
                TemplateImageItem lastItem = mPicAdapter.getItem(count - 1);
                if (lastItem.res_id != getAddImageResId()) {
                    add_pic_btn();
                }
            }
        }

        @Override
        public void big_image(int requestCode, int resultCode, Intent data) {
            List<Image> deleteList = (List<Image>)data.getSerializableExtra(BigImageFragment.KEY_OUTPUT_DELETE_IMAGE_ARRAY);
            Set<String> deleteSet = new HashSet<>(deleteList.size());
            for (Image image : deleteList) {
                deleteSet.add(image.url);
            }
            List<TemplateImageItem> toDeleteList = new ArrayList<>(deleteList.size());
            for (int i = 0; i < mPicAdapter.getCount(); i++) {
                TemplateImageItem imageItem = mPicAdapter.getItem(i);
                if (deleteSet.contains(imageItem.url)) {
                    toDeleteList.add(imageItem);
                }
            }
            for (TemplateImageItem item : toDeleteList) {
                if(templateImageCallback != null){
                    templateImageCallback.deleteImgCallback(item, new TemplateImageCallback.AfterCallback() {
                        @Override
                        public void callback(TemplateImageItem item) {
                            if (mListener != null){
                                mListener.onValueChanged(TemplateImageView.this,getValue(),getValue());
                            }
                            mPicAdapter.remove(item);
                            doAfterDeleteImage();
                        }
                    });
                }
            }
        }
    };

    private void doAfterAddImageCallback(final TemplateImageItem item){
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
        if (count < maxCount) {
            if (count == 0) {
                add_pic_btn();
                if(!TextUtils.isEmpty(tip.getText())){
                    tip.setVisibility(View.VISIBLE);
                }
            } else if (count > 0) {
                TemplateImageItem lastItem = mPicAdapter.getItem(count - 1);
                if (lastItem.res_id != getAddImageResId()) {
                    add_pic_btn();
                } else if(count == 1 && !TextUtils.isEmpty(tip.getText())){
                    tip.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private List<SimplateTemplateImage> getSimpleTemplateImage(List<TemplateImageItem> lists){
        List<SimplateTemplateImage> simpleList = new ArrayList<>();
        for (TemplateImageItem templateImageItem : lists){
            simpleList.add(new SimplateTemplateImage(templateImageItem.url,templateImageItem.smallUrl));
        }
        return simpleList;
    }
}
