package com.waynezhang.mcommon.support.image.selector;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import thirdpart.uk.co.senab.photoview.PhotoView;
import thirdpart.uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 图片查看器
 * <p/>
 * Created by panhongchao.payne on 2015/3/12.
 */
public class ImageViewPager extends PagerAdapter {
    private ArrayList<ItemSelectPic> pics;
    private ViewPagerCallBack callBack;
    private PhotoViewAttacher.OnViewTapListener tapListener;

    public ImageViewPager(ArrayList<ItemSelectPic> imageList, PhotoViewAttacher.OnViewTapListener tapListener, ViewPagerCallBack callback) {
        this.pics = imageList;
        this.tapListener = tapListener;
        this.callBack = callback;
    }

    @Override
    public int getCount() {
        return pics.size();
    }

    // 来判断显示的是否是同一张图片
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    // PagerAdapter只缓存三张要显示的图片，如果滑动的图片超出了缓存的范围，就会调用这个方法，将图片销毁
    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView((View) object);
    }

    // 当要显示的图片可以进行缓存的时候，会调用这个方法进行显示图片的初始化，我们将要显示的ImageView加入到ViewGroup中，然后作为返回值返回即可
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final Context ctx = container.getContext();
        View view = LayoutInflater.from(ctx).inflate(com.waynezhang.mcommon.R.layout.mc__view_show_image, container, false);
        Picasso picasso = Picasso.with(ctx);
        final ImageView smallImage = (ImageView) view.findViewById(com.waynezhang.mcommon.R.id.smallImage);

        final ItemSelectPic item = pics.get(position);
        if (!TextUtils.isEmpty(item.small_url)) {
            picasso.load(item.small_url).into(smallImage);
        }

        final PhotoView photoView = (PhotoView) view.findViewById(com.waynezhang.mcommon.R.id.photoView);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(com.waynezhang.mcommon.R.id.progressBar);
        photoView.setOnViewTapListener(tapListener);

        final View failView = view.findViewById(com.waynezhang.mcommon.R.id.failView);
        failView.setVisibility(View.GONE);
        String url = pics.get(position).url;
        Size size = calcMaxSize(url);
        picasso.load(url)
                .placeholder(com.waynezhang.mcommon.R.drawable.mc__image_placeholder).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .resize(size.width, size.height)
                .into(photoView, new Callback() {
                    @Override
                    public void onSuccess() {
                        item.checkable = true;
                        progressBar.setVisibility(View.GONE);
                        smallImage.setVisibility(View.GONE);
                        failView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        item.checkable = false;
                        progressBar.setVisibility(View.GONE);
                        failView.setVisibility(View.VISIBLE);
                    }
                });
        container.addView(view);
        //callBack.selectedItem(position-1);  // 因为instantiateItem会调用两次，缓存下一张图片，所以当前的图片应该是当前位置减1
        return view;
    }

    private Size calcMaxSize(String url) {
        url = url.replace("file://", "");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(url, options);
        if (options.outWidth <= 0 || options.outHeight <= 0) {
            return new Size(1200, 1920);
        }
        Size maxSize = new Size(options.outWidth, options.outHeight);
        double xscale = (double) maxSize.width / 1200;
        double yscale = (double) maxSize.height / 1920;
        double scale = 1.0;
        if (xscale > 1.0 || yscale > 1.0) {
            scale = Math.max(xscale, yscale);
        } else {
            return maxSize;
        }
        maxSize.width /= scale;
        maxSize.height /= scale;
        return maxSize;
    }

    /**
     * 回调
     */
    public static abstract class ViewPagerCallBack {
        public void selectedItem(int position) {
        }
    }

    class Size {
        public int width;
        public int height;

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    /**
     * 事件监听器
     */
    class ImageListener implements ViewPager.OnPageChangeListener {
        //当页面在滑动的时候会调用此方法，在滑动被停止之前，此方法回一直得到调用。
        // 其中三个参数的含义分别为：arg0 :当前页面，及你点击滑动的页面。arg1:当前页面偏移的百分比。arg2:当前页面偏移的像素位置。
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            return;
        }

        // 此方法是页面跳转完后得到调用，arg0是你当前选中的页面的position。
        @Override
        public void onPageSelected(int position) {
        }

        // 此方法是在状态改变的时候调用
        // 其中arg0这个参数有三种状态（0，1，2）。arg0 ==1表示正在滑动，arg0==2表示滑动完毕了，arg0==0表示什么都没做。当页面开始滑动的时候，三种状态的变化顺序为（1，2，0）。
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
