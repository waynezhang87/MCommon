package com.waynezhang.mcommon.support.image.selector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.activity.GenericFragmentActivity;
import com.waynezhang.mcommon.activity.RequestCode;
import com.waynezhang.mcommon.fragment.BaseFragment;
import com.waynezhang.mcommon.xwidget.McTitleBar;

import java.util.ArrayList;
import java.util.List;

import thirdpart.uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 大图预览
 */
public class ImagePreviewFragment extends BaseFragment {
    public static final String OUTPUT_SELECTED_IMAGES = "OUTPUT_SELECTED_IMAGES";
    //使用静态变量保存传入的所有图片，防止intent中图片过多导致崩溃
    private static ArrayList<ItemSelectPic> allPics;
    public static String PICS_KEY = "pics";
    public static String PICS_MAX = "max";
    public static String PICS_COUNT = "count";
    public static String PICS_POS = "position";
    ItemSelectPic curSelectedPic;
    boolean _isChecked;
    List<String> selectPics;
    private ViewPager view_pager;
    private McTitleBar titleBar;
    private LinearLayout bottomBar;
    private CheckBox ckbSelect;

    /**
     * 2种预览：
     * <p/>
     * 1、只预览选择了的图片，这时count传入选择的图片张数
     * 2、预览相册中的所有图片，这时count传入0
     * <p/>
     * count：针对情况1，选择了多少张作为预览
     * position：针对情况2，选择的图片的位置
     * max：是用户可选择的最大图片数量
     */
    public static void go(Fragment from, ArrayList<ItemSelectPic> pics, int count, int position, int max) {
        Bundle args = createArguments(pics, count, position, max);
        GenericFragmentActivity.startForResult(from, RequestCode.IMAGE_PREVIEW_REQUEST_CODE, ImagePreviewFragment.class, args);
    }

    public static void go(Activity from, ArrayList<ItemSelectPic> pics, int count, int position, int max) {
        Bundle args = createArguments(pics, count, position, max);
        GenericFragmentActivity.startForResult(from, RequestCode.IMAGE_PREVIEW_REQUEST_CODE, ImagePreviewFragment.class, args);
    }

    private static Bundle createArguments(ArrayList<ItemSelectPic> pics, int count, int position, int max) {
        Bundle args = new Bundle();
        allPics = pics;
        args.putInt(PICS_COUNT, count < 0 ? 0 : count);
        args.putInt(PICS_MAX, max);
        args.putInt(PICS_POS, position < 0 ? 0 : position);
        return args;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mc__preview_image, container, false);
        initView(rootView);

        Bundle bundle = getArguments();
        selectPics = getSelectedPics(allPics);
        final int max = bundle.getInt(PICS_MAX);
        int count = bundle.getInt(PICS_COUNT);
        int pos = bundle.getInt(PICS_POS);
        titleBar.setRightButtonText("完成(" + count + "/" + max + ")");
        titleBar.setTitle((pos + 1) + "/" + allPics.size());

        view_pager.setAdapter(new ImageViewPager(allPics, new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                toggle();
            }
        }, new ImageViewPager.ViewPagerCallBack() {
            @Override
            public void selectedItem(int i) {
            }
        }));

        // 初始显示
        curSelectedPic = allPics.get(pos);
        view_pager.setCurrentItem(pos);
        ckbSelect.setChecked(curSelectedPic.__isPicked);

        view_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int prePos;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                prePos = position;
            }

            @Override
            public void onPageSelected(int position) {
                boolean isAdd = position > prePos ? true : false;
                titleBar.setTitle(ExpCalculate.calculate(titleBar.getTitle().toString(), ExpCalculate.EXP_2, isAdd));

                curSelectedPic = allPics.get(position);
                ckbSelect.setChecked(curSelectedPic.__isPicked);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        ckbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(curSelectedPic.checkable){
                    _isChecked = isChecked;
                } else {
                    ckbSelect.setChecked(false);
                }
            }
        });

        ckbSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!curSelectedPic.checkable){
                    Toast.makeText(getActivity(), "图片损坏，不能选择！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (_isChecked) {
                    if (!selectPics.contains(curSelectedPic.url)) {
                        if(selectPics.size() < max){
                            curSelectedPic.__isPicked = true;
                            selectPics.add(curSelectedPic.url);
                        } else {
                            Toast.makeText(getActivity(), "最多选择" + max + "张图片！", Toast.LENGTH_SHORT).show();
                            ckbSelect.setChecked(false);
                            return;
                        }
                    }
                } else {
                    if (selectPics.contains(curSelectedPic.url)) {
                        selectPics.remove(curSelectedPic.url);
                        curSelectedPic.__isPicked = false;
                    }
                }
                titleBar.setRightButtonText(ExpCalculate.calculate(titleBar.getRightButtonText().toString(), ExpCalculate.EXP_1, _isChecked));
            }
        });

        titleBar.setLeftButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        titleBar.setRightButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> outputSelectPics = new ArrayList<String>();
                for (String pic : selectPics) {
                    outputSelectPics.add(pic.replace("file://", ""));
                }
                Intent data = new Intent();
                data.putStringArrayListExtra(OUTPUT_SELECTED_IMAGES, outputSelectPics);
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
            }
        });

        return rootView;
    }

    private List<String> getSelectedPics(List<ItemSelectPic> pics) {
        List<String> result = new ArrayList<>();
        if (pics != null && pics.size() > 0) {
            for (ItemSelectPic pic : pics) {
                if (pic.__isPicked) {
                    result.add(pic.url);
                }
            }
        }
        return result;
    }

    private void initView(View rootView) {
        view_pager = (ViewPager) rootView.findViewById(R.id.view_pager);
        titleBar = (McTitleBar) rootView.findViewById(R.id.mc_titleBar);
        bottomBar = (LinearLayout) rootView.findViewById(R.id.bottomBar);
        ckbSelect = (CheckBox) rootView.findViewById(R.id.ckbSelect);
    }

    private void toggle() {
        if (titleBar.getVisibility() == View.VISIBLE) {
            titleBar.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
            //Screen.setFullScreen(getActivity());
        } else {
            //Screen.quitFullScreen(getActivity());
            titleBar.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
        }
    }

    private void replaceItem(ItemSelectPic newItem, ArrayList<ItemSelectPic> items) {
        for (int i = 0; i < items.size(); i++) {
            ItemSelectPic pic = items.get(i);
            if (newItem.equals(pic)) {
                items.remove(pic);
                items.add(i, pic);
                return;
            }
        }
    }

    @Override
    public void onDestroy() {
        allPics = null;
        super.onDestroy();
    }
}
