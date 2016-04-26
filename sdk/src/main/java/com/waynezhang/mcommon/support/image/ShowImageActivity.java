/**
 * ****************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package com.waynezhang.mcommon.support.image;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.file.FileUtil;
import com.waynezhang.mcommon.util.PicassoUtil;
import com.waynezhang.mcommon.xwidget.HackyViewPager;
import com.waynezhang.mcommon.xwidget.OptionDialog;

import java.io.File;
import java.util.ArrayList;

import thirdpart.com.viewpagerindicator.CirclePageIndicator;
import thirdpart.uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Lock/Unlock button is added to the ActionBar.
 * Use it to temporarily disable ViewPager navigation in order to correctly interact with ImageView by gestures.
 * Lock/Unlock state of ViewPager is saved and restored on configuration changes.
 * <p/>
 * Julia Zudikova
 */

public class ShowImageActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {
    public static String IMAGE_SAVE_PATH = "gfriend";
    private static final String TAG = "ShowImageActivity";
    private static final String KEY_IMAGE_INDEX = "KEY_IMAGE_INDEX";
    private static final String KEY_IMAGE_LIST = "KEY_IMAGE_LIST";
    private static final String KEY_IS_NEW_STYLE = "KEY_IS_NEW_STYLE";

    private static final String ISLOCKED_ARG = "isLocked";

    private ViewPager mViewPager;
    private CirclePageIndicator circlePageIndicator;
    private TextView page_text = null;
    private TextView all_pic_btn = null;

    private ArrayList<Image> _imagelist;
    private boolean _isNewStyle = false;
    private static ImagePagerAdapter.LinkCallback callback;
    private ImagePagerAdapter _ipa;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (_ipa != null) {
            _ipa.deostroy();
        }
    }

    public static void go(Activity from, int currentImageIndex, ArrayList<Image> imageList) {
        Log.d(TAG, "go ");
        if (currentImageIndex < 0 || imageList == null || imageList.size() <= 0) return;
        Intent intent = new Intent(from, ShowImageActivity.class);
        intent.putExtra(KEY_IMAGE_INDEX, currentImageIndex);
        intent.putExtra(KEY_IMAGE_LIST, imageList);
        from.startActivity(intent);
    }

    public static void go(Activity from, int currentImageIndex, ArrayList<Image> imageList, ImagePagerAdapter.LinkCallback cb) {
        Log.d(TAG, "go 01");
        if (currentImageIndex < 0 || imageList == null || imageList.size() <= 0) return;
        Intent intent = new Intent(from, ShowImageActivity.class);
        intent.putExtra(KEY_IMAGE_INDEX, currentImageIndex);
        intent.putExtra(KEY_IMAGE_LIST, imageList);
        callback = cb;
        from.startActivity(intent);
    }

    //isNewStyle 使用新的带多图预览的
    public static void go(Activity from, int currentImageIndex, ArrayList<Image> imageList, boolean isNewStyle) {
        Log.d(TAG, "go isNewStyle=" + isNewStyle);
        if (currentImageIndex < 0 || imageList == null || imageList.size() <= 0) return;
        Intent intent = new Intent(from, ShowImageActivity.class);
        intent.putExtra(KEY_IMAGE_INDEX, currentImageIndex);
        intent.putExtra(KEY_IMAGE_LIST, imageList);
        intent.putExtra(KEY_IS_NEW_STYLE, isNewStyle);
        from.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent");
        super.onNewIntent(intent);
        setIntent(intent);
        InitData();
    }

    public void showItemOpt() {
        Log.d(TAG, "showItemOpt");
        if (mViewPager == null) return;
        int index = mViewPager.getCurrentItem();
        if (index < 0 || index >= _imagelist.size()) return;
        final Image img = _imagelist.get(index);
        if (img == null || img.imageResId != 0 || (img.url != null && !img.url.startsWith("http://"))) return;

        OptionDialog.build(ShowImageActivity.this, R.layout.mc_window_msg_option_onlysave)
                .onClickListener(R.id.mc_save_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick");

                        // 这时的大图必须存在否者不应该到达次逻辑
//                        if (mViewPager == null) return;
//                        PhotoView photoView = (PhotoView) mViewPager.findViewById(R.id.photoView);
//                        if (photoView == null) return;

                        Time time = new Time();
                        time.setToNow();
                        String filePath = "";
                        try {
                            String fileName = "" + time.year + time.month + time.monthDay + time.hour + time.minute + time.second;
                            String fileDir = Environment.getExternalStorageDirectory().getPath() + File.separator + IMAGE_SAVE_PATH;

                            filePath = fileDir + File.separator + fileName + ".jpg";
                            File file = new File(filePath);
                            int tmpIndex = 1;
                            while (file.exists()) {
                                filePath = fileDir + File.separator + fileName + "(" + tmpIndex + ")" + ".jpg";
                                file = new File(filePath);
                                tmpIndex++;
                            }
                            Log.d(TAG, "onClick 02 filepath=" + filePath);
                            if (0 != FileUtil.copyFile(PicassoUtil.getCachedImageFilePath(ShowImageActivity.this, img.url), filePath)) {
                                Toast.makeText(ShowImageActivity.this, "图片保存失败", Toast.LENGTH_LONG).show();
                                return;
                            }

                            Toast.makeText(ShowImageActivity.this, "图片保存成功", Toast.LENGTH_LONG).show();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Toast.makeText(ShowImageActivity.this, "图片保存失败", Toast.LENGTH_LONG).show();
                        }
                    }
                }).show();
        return;
    }

    private void InitData() {
        hideStatusBar();

        Intent intent = getIntent();
        int currentImageIndex = intent.getIntExtra(KEY_IMAGE_INDEX, 0);
        _imagelist = (ArrayList<Image>) intent.getSerializableExtra(KEY_IMAGE_LIST);

        _isNewStyle = intent.getBooleanExtra(KEY_IS_NEW_STYLE, false);

        setContentView(R.layout.mc__activity_show_image);
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(1);

        _ipa = new ImagePagerAdapter(this, _imagelist, new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                finish();
            }
        }, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "onLongClick");
                if (_ipa == null) return false;
                if (_ipa.getDownloadStatus() == ImagePagerAdapter.DOWNLOAD_OK) {
                    Log.d(TAG, "onLongClick status=" + _ipa.getDownloadStatus());
                    showItemOpt();
                }
                return false;
            }
        }, callback);

        mViewPager.setAdapter(_ipa);

        mViewPager.setCurrentItem(currentImageIndex);
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.circlePageIndicator);

        if (_isNewStyle) {
            circlePageIndicator.setVisibility(View.GONE);
            page_text = (TextView) findViewById(R.id.page_text);
            all_pic_btn = (TextView) findViewById(R.id.page_all_pic_btn);
            all_pic_btn.setVisibility(View.VISIBLE);
            all_pic_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowAllPicturesActivity.go(ShowImageActivity.this, _imagelist);
                    ShowImageActivity.this.finish();
                }
            });
            mViewPager.setOnPageChangeListener(this);
            mViewPager.setBackgroundResource(R.color.mc_page_viewer_bg);
        } else {
            circlePageIndicator.setViewPager(mViewPager);
            View bottombar = findViewById(R.id.black_ui_layout);
            bottombar.setVisibility(View.GONE);
        }

        Log.d("ShowImageActivity", "onCreate currentImageIndex=" + currentImageIndex);

        if (page_text != null)
            page_text.setText((currentImageIndex + 1) + "/" + _imagelist.size());


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        InitData();
        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG, false);
            ((HackyViewPager) mViewPager).setLocked(isLocked);
        }

    }

    private void hideStatusBar() {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            return;
        } else {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
            ActionBar actionBar = getActionBar();
            if (actionBar != null)
                actionBar.hide();
        }
    }

    private boolean isViewPagerActive() {
        return (mViewPager != null && mViewPager instanceof HackyViewPager);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");

        if (isViewPagerActive()) {
            outState.putBoolean(ISLOCKED_ARG, ((HackyViewPager) mViewPager).isLocked());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.d("ShowImageActivity", "onPageScrolled position=" + position);
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("ShowImageActivity", "onPageSelected position=" + position);

        if (page_text != null)
            page_text.setText((position + 1) + "/" + _imagelist.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.d("ShowImageActivity", "onPageScrollStateChanged state=" + state);

    }
}
