package com.waynezhang.mcommon.support.image.selector;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.activity.GenericFragmentActivity;
import com.waynezhang.mcommon.cache.Cache;
import com.waynezhang.mcommon.util.L;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by liuxiaofeng02 on 2015/3/11.
 */
public class ImageSelectorAdapter extends CommonAdapter<String> {
    private static final String TAG = ImageSelectorAdapter.class.getSimpleName();
    private static final int IMAGE_DAMAGED_ID = R.id.mc_image_item;
    /**
     * 用户选择的图片，存储为图片的完整路径
     */
    public List<String> mSelectedImage = new LinkedList<String>();

    /**
     * 文件夹路径
     */
    private String mDirPath;
    /**
     * 最大选择图片数
     */
    private int mMaxSelectedCount;
    private GridItemClickListener mItemClickListener;
    private Context mContext;
    private Cache cache;

    public ImageSelectorAdapter(Context context, List<String> mDatas, int itemLayoutId, String dirPath, int maxSelectedCount) {
        super(context, mDatas, itemLayoutId);
        this.mContext = context;
        this.mDirPath = dirPath;
        this.mMaxSelectedCount = maxSelectedCount;
        try{
            cache = Cache.newInstance(context);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void convert(final ViewHolder helper, final String item) {
        // 设置no_selected
        helper.setImageResource(R.id.mc_image_item_select, R.drawable.mc_btn_choosepic);
        // 设置图片
        String tempPath = item;
        if (mDirPath != null) {
            tempPath = mDirPath + "/" + item;
        }
        final String imagePath = tempPath;

        final ImageView mImageView = helper.getView(R.id.mc_image_item);
        final ImageView mSelect = helper.getView(R.id.mc_image_item_select);

        Picasso picasso = Picasso.with(mContext);
        picasso.load(new File(imagePath)).placeholder(R.drawable.mc_no_picture).fit().centerCrop().into(mImageView, new Callback() {
            @Override
            public void onSuccess() {
                mImageView.setTag(IMAGE_DAMAGED_ID, false);
            }

            @Override
            public void onError() {
                //表示图已经损坏
                L.e(TAG, "Image[" + imagePath + "] is damaged, can not load.");
                mImageView.setTag(IMAGE_DAMAGED_ID, true);
                //cache.putCache(CacheKey.IMAGE_DAMAGED_PREFIX + imagePath, "1");
            }
        });

        mImageView.setTag(IMAGE_DAMAGED_ID, null);
        mImageView.setColorFilter(null);
        // 设置ImageView的点击事件
        mSelect.setOnClickListener(new View.OnClickListener() {
            // 选择，则将图片变暗，反之则反之
            @Override
            public void onClick(View v) {
                if(isImageDamaged(mImageView)){
                    Toast.makeText(mContext, "图片损坏，不能选择！", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 已经选择过该图片
                if (mSelectedImage.contains(imagePath)) {
                    mSelectedImage.remove(imagePath);
                    mSelect.setImageResource(R.drawable.mc_btn_choosepic);
                    mImageView.setColorFilter(null);
                } else
                // 未选择该图片
                {
                    if (mSelectedImage.size() > mMaxSelectedCount - 1) {
                        Toast.makeText(mContext, "最多选择" + mMaxSelectedCount + "张图片！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mSelectedImage.add(imagePath);
                    mSelect.setImageResource(R.drawable.mc_btn_choosepic_sel);
                    mImageView.setColorFilter(Color.parseColor("#77000000"));
                }

                if (mItemClickListener != null) {
                    mItemClickListener.gridItemClick(mSelectedImage);
                }
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isImageDamaged(mImageView)){
                    Toast.makeText(mContext, "图片损坏，无法查看大图！", Toast.LENGTH_SHORT).show();
                    return;
                }

                GenericFragmentActivity activity = (GenericFragmentActivity) mContext;

                ArrayList<ItemSelectPic> picsList = new ArrayList<ItemSelectPic>();
                for (String uri : mDatas) {
                    ItemSelectPic pic = new ItemSelectPic();
                    if (TextUtils.isEmpty(mDirPath)) {
                        pic.url = uri;
                    } else {
                        pic.url = mDirPath + "/" + uri;
                    }
                    for (String selectedUrl : mSelectedImage) {
                        if (selectedUrl.equals(pic.url)) {
                            pic.__isPicked = true;
                            break;
                        }
                    }

                    pic.url = "file://" + pic.url;
                    picsList.add(pic);
                }
                ImagePreviewFragment.go(activity.getCurrentFragment(), picsList, mSelectedImage.size(), helper.getPosition(), mMaxSelectedCount);
            }
        });

        /**
         * 已经选择过的图片，显示出选择过的效果
         */
        if (mSelectedImage.contains(imagePath)) {
            mSelect.setImageResource(R.drawable.mc_btn_choosepic_sel);
            mImageView.setColorFilter(Color.parseColor("#77000000"));
        }
    }

    private boolean isImageDamaged(ImageView imageView){
        Object tag = imageView.getTag(IMAGE_DAMAGED_ID);
        if(tag != null && tag instanceof Boolean){
            boolean isDamaged = (Boolean) tag;
            return isDamaged;
        }
        return false;
    }

    public void setItemClickListener(GridItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public int getmMaxSelectedCount() {
        return mMaxSelectedCount;
    }

    private int getPos(List<String> pics, String cur) {
        for (int i = 0; i < pics.size(); i++) {
            if (pics.get(i).equalsIgnoreCase(cur)) {
                return i;
            }
        }
        return 0;
    }

    public void setSelectedImages(List<String> selectedImage){
        this.mSelectedImage = selectedImage;
        notifyDataSetChanged();
    }

    public List<String> getSelectedImages(){
        return mSelectedImage;
    }
}
