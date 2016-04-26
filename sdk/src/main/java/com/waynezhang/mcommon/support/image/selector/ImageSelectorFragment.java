package com.waynezhang.mcommon.support.image.selector;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.activity.GenericFragmentActivity;
import com.waynezhang.mcommon.activity.RequestCode;
import com.waynezhang.mcommon.cache.Cache;
import com.waynezhang.mcommon.fragment.BaseFragment;
import com.waynezhang.mcommon.xwidget.LoadingLayout;
import com.waynezhang.mcommon.xwidget.McTitleBar;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Created by liuxiaofeng02 on 2015/3/11.
 */
public class ImageSelectorFragment extends BaseFragment implements ListImageDirPopupWindow.OnImageDirSelected, GridItemClickListener<String> {
    private static final String TAG = ImageSelectorFragment.class.getSimpleName();
    public static final String MAX_COUNT_KEY = "MAX_IMAGE_COUNT";
    public static String RETURN_IMAGE_ARRAY_KEY = ".image";
    private int MAX_IMAGE_COUNT = 10;

    /**
     * 所有的图片
     */
    private static List<String> mImgs;

    private GridView mGirdView;
    private ImageSelectorAdapter mAdapter;
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<String>();
    /**
     * 扫描拿到所有的图片文件夹
     */
    private List<ImageFolder> mImageFolders = new ArrayList<ImageFolder>();
//    /**
//     * 选中的图片数组，存储的是图片地址
//     */
//    private List<String> mSelectedImage;

    private RelativeLayout mBottomBar;
    private McTitleBar titleBar;
    private TextView mChooseDir;
    private ListImageDirPopupWindow mListImageDirPopupWindow;
    private LoadingLayout viewLoading;
    private TextView mChoosePreview;

    private int mScreenHeight;
    private Cache cache;

    public static void go(Fragment from, int requestCode, int maxCount) {
        Bundle data = new Bundle();
        data.putInt(MAX_COUNT_KEY, maxCount);
        GenericFragmentActivity.startForResult(from, requestCode, ImageSelectorFragment.class, data);
    }

    public static void go(Activity from, int requestCode, int maxCount) {
        Bundle data = new Bundle();
        data.putInt(MAX_COUNT_KEY, maxCount);
        GenericFragmentActivity.startForResult(from, requestCode, ImageSelectorFragment.class, data);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if(getActivity() == null || getActivity().isFinishing()){
                return;
            }
            // 为View绑定数据
            data2View();
            // 初始化展示文件夹的popupWindw
            initListDirPopupWindw();
            viewLoading.hideLoadingView();
        }
    };

    /**
     * 为View绑定数据
     */
    private void data2View() {
        if (mImgs.size() == 0) {
            Toast.makeText(getActivity(), "没有扫描到图片！", Toast.LENGTH_SHORT).show();
            return;
        }

        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new ImageSelectorAdapter(getActivity(), mImgs, R.layout.mc_image_grid_item, null, MAX_IMAGE_COUNT);
        mAdapter.setItemClickListener(this);
        mGirdView.setAdapter(mAdapter);
    }

    /**
     * 初始化展示文件夹的popupWindw
     */
    private void initListDirPopupWindw() {
        mListImageDirPopupWindow = new ListImageDirPopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7), mImageFolders,
                LayoutInflater.from(getActivity()).inflate(R.layout.mc_list_image_dir, null));

        mListImageDirPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1.0f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mc_image_selector, container, false);

        DisplayMetrics outMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;

        RETURN_IMAGE_ARRAY_KEY = getActivity().getPackageName() + RETURN_IMAGE_ARRAY_KEY;
        MAX_IMAGE_COUNT = getArguments().getInt(MAX_COUNT_KEY);

        try{
            cache = Cache.newInstance(getActivity());
        }catch (Exception e){
            e.printStackTrace();
        }

        initView(rootView);
        if(null == mImgs){
            getImages();
        } else {
            // 为View绑定数据
            data2View();
            // 初始化展示文件夹的popupWindw
            initListDirPopupWindw();
        }
        initEvent();

        return rootView;
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中完成图片的扫描
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getActivity(), "无外部存储卡!", Toast.LENGTH_SHORT).show();
            return;
        }

        mImgs = new ArrayList<String>(1024);
        viewLoading.showLoadingView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = getActivity().getContentResolver();

                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?", new String[] { "image/jpeg", "image/png" },
                        MediaStore.Images.Media.DATE_MODIFIED);

                if(mCursor == null){
                    return;
                }
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    //检查图片是否已损坏
//                    if(cache.getCache(CacheKey.IMAGE_DAMAGED_PREFIX + path) != null){
//                        continue;
//                    }

                    mImgs.add(path);
                    // 获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    String dirPath = parentFile.getAbsolutePath();
                    ImageFolder imageFolder = null;
                    // 利用一个HashSet防止多次扫描同一个文件夹
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mDirPaths.add(dirPath);
                        // 初始化ImageFolder
                        imageFolder = new ImageFolder();
                        imageFolder.setDir(dirPath);
                        imageFolder.setFirstImagePath(path);
                    }

                    if (parentFile.list() == null)
                        continue;

                    int picSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg"))
                                return true;
                            return false;
                        }
                    }).length;

                    imageFolder.setCount(picSize);
                    mImageFolders.add(imageFolder);
                }
                mCursor.close();

                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;

                Collections.reverse(mImgs);
                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0x110);
            }
        }).start();

    }

    /**
     * 初始化View
     */
    private void initView(View view) {
        mGirdView = (GridView) view.findViewById(R.id.mc_gridView);
        mChooseDir = (TextView) view.findViewById(R.id.mc_choose_dir);
        mBottomBar = (RelativeLayout) view.findViewById(R.id.mc_bottomBar);
        viewLoading = (LoadingLayout) view.findViewById(R.id.viewLoading);
        mChoosePreview = (TextView) view.findViewById(R.id.mc_choose_preview);
        titleBar = (McTitleBar) view.findViewById(R.id.mc_titleBar);

        titleBar.setLeftButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitNoResult();
            }
        });

        StringBuilder sb = new StringBuilder();
        sb.append("完成(0/");
        sb.append(MAX_IMAGE_COUNT);
        sb.append(")");
        titleBar.setRightButtonText(sb.toString());
        titleBar.setRightButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });

        mChoosePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAdapter == null){
                    return;
                }
                List<String> selectedImage = mAdapter.getSelectedImages();
                if(null!=selectedImage && !selectedImage.isEmpty()){
                    List<String> absolutePathPics = new ArrayList<String>();
                    for (String uri : selectedImage){
                        absolutePathPics.add("file://"+uri);
                    }
                    ArrayList<ItemSelectPic> selectPics = Convertor.convert(absolutePathPics);
                    for(ItemSelectPic pic : selectPics){
                        pic.__isPicked = true;
                    }
                    ImagePreviewFragment.go(ImageSelectorFragment.this, selectPics, selectedImage.size(), 0, mAdapter.getmMaxSelectedCount());
                }
            }
        });
    }

    private void initEvent() {
        /**
         * 为底部的布局设置点击事件，弹出popupWindow
         */
        mChooseDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListImageDirPopupWindow != null) {
                    mListImageDirPopupWindow.setAnimationStyle(R.style.mc_anim_popup_dir);
                    mListImageDirPopupWindow.showAsDropDown(mBottomBar, 0, 0);

                    // 设置背景颜色变暗
                    WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                    lp.alpha = .5f;
                    getActivity().getWindow().setAttributes(lp);
                }
            }
        });
    }

    @Override
    public void selected(ImageFolder folder) {
        final File imgDir = new File(folder.getDir());
        mImgs = Arrays.asList(imgDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg"))
                    return true;
                return false;
            }
        }));
        Collections.reverse(mImgs);
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new ImageSelectorAdapter(getActivity(), mImgs, R.layout.mc_image_grid_item, imgDir.getAbsolutePath(), MAX_IMAGE_COUNT);
        mAdapter.setItemClickListener(this);
        mGirdView.setAdapter(mAdapter);
        refreshView(mAdapter.getSelectedImages());
        mListImageDirPopupWindow.dismiss();
    }

    @Override
    public void gridItemClick(List<String> selectedList) {
        refreshView(selectedList);
    }

    private void refreshView(List<String> selectedList){
        mChoosePreview.setText(ExpCalculate.calculate(mChoosePreview.getText().toString(), ExpCalculate.EXP_3, selectedList.size()+""));
        titleBar.setRightButtonText(ExpCalculate.calculate(titleBar.getRightButtonText().toString(), ExpCalculate.EXP_1, selectedList.size() + ""));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == RequestCode.IMAGE_PREVIEW_REQUEST_CODE && mAdapter != null){
            List<String> images = data.getStringArrayListExtra(ImagePreviewFragment.OUTPUT_SELECTED_IMAGES);
            refreshView(images);
            mAdapter.setSelectedImages(images);
        }
    }

    private void exitNoResult(){
        getActivity().finish();
        mImgs = null;
    }

    private void exit(){
        if(mAdapter != null){
            Intent outData = new Intent();
            outData.putExtra(ImageSelectorFragment.RETURN_IMAGE_ARRAY_KEY, mAdapter.getSelectedImages().toArray(new String[0]));
            getActivity().setResult(Activity.RESULT_OK, outData);
        }
        getActivity().finish();
        mImgs = null;
    }

    @Override
    public boolean onBackPressed() {
        exitNoResult();
        return true;
    }
}
