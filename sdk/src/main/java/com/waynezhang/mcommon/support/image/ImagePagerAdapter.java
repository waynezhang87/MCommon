package com.waynezhang.mcommon.support.image;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.waynezhang.mcommon.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.lang.ref.WeakReference;
import java.util.List;

import thirdpart.uk.co.senab.photoview.PhotoView;
import thirdpart.uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by don on 1/16/15.
 */
public class ImagePagerAdapter extends PagerAdapter {
    private int changedPageIndex = -1;
    private List<Image> imageList;
    private PhotoViewAttacher.OnViewTapListener tapListener;
    private View.OnLongClickListener _longClick;
    private Context _context;
    //    private Callback _callback;
//    private ImageView _smallImage;
    private PhotoView _photoView;
//    private ProgressBar _progressBar;
//    private View _failView;

    public final static int DOWNLOADING = 1;
    public final static int DOWNLOAD_OK = 2;
    public final static int DOWNLOAD_FAILED = 3;

    private int status = 0;// status 0 is default, 1 image loading, 2 image load ok, 3 image load failed.

    private LinkCallback cb;

    public ImagePagerAdapter(List<Image> imageList, PhotoViewAttacher.OnViewTapListener tapListener) {

        this.imageList = imageList;
        this.tapListener = tapListener;
    }

    public ImagePagerAdapter(List<Image> imageList, PhotoViewAttacher.OnViewTapListener tapListener, LinkCallback cb) {
        this.imageList = imageList;
        this.tapListener = tapListener;
        this.cb = cb;
    }

    public ImagePagerAdapter(Context context, List<Image> imageList, PhotoViewAttacher.OnViewTapListener tapListener) {
        this._context = context;
        this.imageList = imageList;
        this.tapListener = tapListener;
    }

    public ImagePagerAdapter(Context context, List<Image> imageList, PhotoViewAttacher.OnViewTapListener tapListener, View.OnLongClickListener longClick) {
        this._context = context;
        this.imageList = imageList;
        this.tapListener = tapListener;
        _longClick = longClick;
    }

    public ImagePagerAdapter(Context context, List<Image> imageList, PhotoViewAttacher.OnViewTapListener tapListener, View.OnLongClickListener longClick, LinkCallback cb) {
        this._context = context;
        this.imageList = imageList;
        this.tapListener = tapListener;
        _longClick = longClick;
        this.cb = cb;
    }

    public void deostroy(){
        Picasso picasso = Picasso.with(_context);
        if (_photoView != null)
            picasso.cancelRequest(_photoView);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        final Context ctx = container.getContext();
        View view = LayoutInflater.from(ctx).inflate(R.layout.mc__view_show_image, container, false);
        view.setTag(position);
        Picasso picasso = Picasso.with(ctx);
//        if (_photoView != null)
//            picasso.cancelRequest(_photoView);

        final WeakReference<ImageView> _smallImage = new WeakReference<ImageView>((ImageView) view.findViewById(R.id.smallImage));
        _photoView = (PhotoView) view.findViewById(R.id.photoView);
        view.setTag(R.id.photoView, _photoView);

        // final ProgressBar _progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        // final View _failView = view.findViewById(R.id.failView);

        final Image item = imageList.get(position);
        if (!TextUtils.isEmpty(item.smallUrl) && _smallImage.get() !=null) {
            picasso.load(item.smallUrl).into(_smallImage.get());
        } else if(item.smallImageResId != 0){
            picasso.load(item.smallImageResId);
        }

        // _photoView.setOnViewTapListener(tapListener);
        if(item.link != null){
            _photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    cb.onSuccess(item.link);
                }
            });
        }else {
            _photoView.setOnViewTapListener(tapListener);
        }

        _photoView.setOnLongClickListener( _longClick );
        // _failView.setVisibility(View.GONE);
        RequestCreator requestCreator = null;
        if(item.imageResId != 0){
            requestCreator = picasso.load(item.imageResId);
        } else if(!TextUtils.isEmpty(item.url)){
            requestCreator = picasso.load(item.url);
        }
        Log.d("ImagePagerAdapter", "instantiateItem small=" + item.smallUrl + ", largeurl=" + item.url);
        if(requestCreator != null){
            requestCreator.placeholder(R.drawable.mc__image_placeholder).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(_photoView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("ImagePagerAdapter", "onSuccess");
                            try{
                                status = DOWNLOAD_OK;
                                // _progressBar.setVisibility(View.GONE);
                                if (_smallImage.get() != null)
                                    _smallImage.get().setVisibility(View.GONE);
                                // _failView.setVisibility(View.GONE);
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }

                        }

                        @Override
                        public void onError() {
                            try{
                                status = DOWNLOAD_FAILED;
                                // _progressBar.setVisibility(View.GONE);
                                // _failView.setVisibility(View.GONE);
                                if (_context != null) {
                                    Toast.makeText(_context, "图片加载失败", Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    });
        }
        
        // Now just add PhotoView to ViewPager and return it
        container.addView(view);
        return view;
    }

    public int getDownloadStatus(){
        return status;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View)object;
        Picasso picasso = Picasso.with(_context);
        picasso.cancelRequest((ImageView)view.getTag(R.id.photoView));
        container.removeView(view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object){
        View view = (View)object;
        int index = (Integer)view.getTag();
        int lastIndex = getCount() - 1;
        if(changedPageIndex != -1 && index >= changedPageIndex){
            if(index == lastIndex){
                changedPageIndex = -1;
            }
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    public void notifyDataSetChanged(int changedPageIndex) {
        this.changedPageIndex = changedPageIndex;
        Log.d("ImagePagerAdapter", "notifyDataSetChanged changedPageIndex" + changedPageIndex);

        status = DOWNLOADING;

        super.notifyDataSetChanged();
    }

    public interface LinkCallback {
        void onSuccess(String link);
    }
}