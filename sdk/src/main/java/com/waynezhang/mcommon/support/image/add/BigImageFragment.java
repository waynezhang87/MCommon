package com.waynezhang.mcommon.support.image.add;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.activity.GenericFragmentActivity;
import com.waynezhang.mcommon.activity.RequestCode;
import com.waynezhang.mcommon.fragment.BaseFragment;
import com.waynezhang.mcommon.support.image.Image;
import com.waynezhang.mcommon.support.image.ImagePagerAdapter;
import com.waynezhang.mcommon.xwidget.McTitleBar;

import java.util.ArrayList;
import java.util.List;

import thirdpart.uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by liuxiaofeng02 on 2015/5/18.
 */
public class BigImageFragment extends BaseFragment {
    private static final String KEY_CURRENT_IMAGE_INDEX = "CURRENT_IMAGE_INDEX";
    private static final String KEY_IMAGE_ARRAY = "IMAGE_ARRAY";
    public static final String KEY_OUTPUT_DELETE_IMAGE_ARRAY = "KEY_OUTPUT_DELETE_IMAGE_ARRAY";
    private McTitleBar mTitleBar;
    private ViewPager mViewPager;

    private ImagePagerAdapter mPagerAdapter;
    private List<Image> mAllImageList;
    private ArrayList<Image> mDeleteList;

    public static void go(Fragment from, int currentImageIndex, ArrayList imageList) {
        Bundle args = new Bundle();
        args.putInt(KEY_CURRENT_IMAGE_INDEX, currentImageIndex);

        args.putSerializable(KEY_IMAGE_ARRAY, imageList);
        GenericFragmentActivity.startForResult(from, RequestCode.BIG_IMAGE, BigImageFragment.class, args);
    }

    public static void go(Activity from, int currentImageIndex, ArrayList imageList) {
        Bundle args = new Bundle();
        args.putInt(KEY_CURRENT_IMAGE_INDEX, currentImageIndex);
        args.putSerializable(KEY_IMAGE_ARRAY, imageList);
        GenericFragmentActivity.startForResult(from, RequestCode.BIG_IMAGE, BigImageFragment.class, args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mc_fragment_big_image, container, false);
        initView(rootView);

        Bundle args = getArguments();
        int currentImageIndex = args.getInt(KEY_CURRENT_IMAGE_INDEX);
        mAllImageList = (List<Image>)args.getSerializable(KEY_IMAGE_ARRAY);
        mDeleteList = new ArrayList<>(mAllImageList.size());
        refreshTitleView(currentImageIndex + 1, mAllImageList.size());

        //mViewPager.setOffscreenPageLimit(1);
        mPagerAdapter = new ImagePagerAdapter(mAllImageList, new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                toggle();
            }
        });
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                refreshTitleView(position + 1, mAllImageList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setCurrentItem(currentImageIndex);

        return rootView;
    }

    private void initView(View rootView){
        mTitleBar = (McTitleBar)rootView.findViewById(R.id.mc_titleBar);
        mTitleBar.setLeftButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });
        mViewPager = (ViewPager)rootView.findViewById(R.id.mc_view_pager);

        mTitleBar.setRightButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = mViewPager.getCurrentItem();
                mDeleteList.add(mAllImageList.get(currentItem));
                int size = mAllImageList.size();
                if(size == 1){
                    exit();
                } else {
                    refreshView(currentItem);
                    mAllImageList.remove(currentItem);
                    mPagerAdapter.notifyDataSetChanged(currentItem);
                    if(currentItem < size - 1){
                        //将图片向后移一张
                        mViewPager.setCurrentItem(currentItem);
                    } else {
                        mViewPager.setCurrentItem(currentItem - 1);
                    }
                }
            }
        });
    }

    private void exit(){
        Intent data = new Intent();
        data.putExtra(KEY_OUTPUT_DELETE_IMAGE_ARRAY, mDeleteList);
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();
    }

    private void refreshView(final int currentPosition){
        int position = currentPosition + 1;
        int size = mAllImageList.size();
        if(position == size){
            position -= 1;
        }
        size -= 1;
        refreshTitleView(position, size);
    }

    private void refreshTitleView(int position, int size){
        mTitleBar.setTitle(position + "/" + size);
    }

    private void toggle() {
        if (mTitleBar.getVisibility() == View.VISIBLE) {
            mTitleBar.setVisibility(View.GONE);
        } else {
            mTitleBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onBackPressed() {
        exit();
        return true;
    }
}
