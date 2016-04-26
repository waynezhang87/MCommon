package com.waynezhang.mcommon.xwidget.emotionpanel;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.util.ResourceHelper;
import com.waynezhang.mcommon.xwidget.emotionpanel.container.EmotionInfo;
import com.waynezhang.mcommon.xwidget.emotionpanel.container.EmotionInfoContainer;
import com.waynezhang.mcommon.xwidget.emotionpanel.container.EmotionPageInfo;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by waynezhang on 7/8/15.
 */
public class EmotionPanelView extends LinearLayout {
    private ViewPager viewPager;
    private ViewGroup indicatorGroup;   //表情底部栏indicator组
    private ViewGroup emotionTabGroup;  //表情底部栏tab组
    private ImageView[][] tips;
    private ArrayList<ViewGroup> tabs;
    private View[][] emotionGrids;
    private EmotionPageAdapter emotionPageAdapter;
    private int prevGroupIndex = -1;     //前一次page页的表情组index
    private int prevPageIndex = -1;     //前一次page页的表情组pageindex
    private int curPageIndex;           //当前page页表情组中的pageindex
    private int curGroupIndex;          //当前page页表情组的index
    private View rootView;

    //对外暴露的listener, 监听viewPager的状态
    public EmotionPanelStateChangeListener listener;

    //表情底部栏tab切换监听的listener
    private OnClickListener tabChangeListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();
            switchPagerGroup(tag);
        }
    };

    //表情page切换监听的listener
    private ViewPager.OnPageChangeListener pagerChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //获取当前position对应的二维数组emotionGrids中View的位置坐标i,j
            int i,j = 0;
            int len1 = 0, len2;
            for (i = 0; i < emotionGrids.length; i++) {
                len1 += emotionGrids[i].length;
                if ((i+1) >= emotionGrids.length) {
                    j = position;
                    break;
                }

                len2 = len1 + emotionGrids[i+1].length;
                if (position < len1) {
                    j = position;
                    break;
                } else if (position >= len1 && position < len2) {
                    j = position - len1;
                    i += 1;
                    break;
                } else {
                    continue;
                }
            }
            prevGroupIndex = curGroupIndex;
            prevPageIndex = curPageIndex;
            curGroupIndex = i;
            curPageIndex = j;
            if (emotionGrids.length == 0 || (curPageIndex == prevPageIndex && curGroupIndex == prevGroupIndex)) {
                return;
            }
            if (internalListener != null) {
                internalListener.onPageChange(curGroupIndex, curPageIndex);
            }
            switchPagerIndicator(curGroupIndex, curPageIndex);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private InternalListener internalListener = new InternalListener() {
        @Override
        public void onPageChange(int groupIndex, int pageIndex) {
            if (listener != null) {
                listener.onPageChange(groupIndex, pageIndex);
            }
        }

        @Override
        public void onEmotionItemSelected(EmotionInfo emotionItem) {
            if (listener != null) {
                listener.onEmotionItemSelected(emotionItem);
            }
        }
    };

    public void setEmotionPanelStateChangeListener(EmotionPanelStateChangeListener listener) {
        this.listener = listener;
    }

    public EmotionPanelView(Context context) {
        super(context);
        init(null);
    }

    public EmotionPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(getAttrs(context, attrs));
    }

    private int[] getAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EmotionPanelView);
        int emotionContainerArrayId = ta.getResourceId(R.styleable.EmotionPanelView_localEmotionContainer, 0);
        ta.recycle();

        if (emotionContainerArrayId == 0) {
            return null;
        }

        String[] rawResIdStringArray = context.getResources().getStringArray(emotionContainerArrayId);
        int[] _rawResIdArray = new int[rawResIdStringArray.length];
        for (int i = 0 ; i < rawResIdStringArray.length; i++) {
            _rawResIdArray[i] = ResourceHelper.getId(context, "R.raw." + rawResIdStringArray[i]);
        }
        return _rawResIdArray;

    }

    private void init(int[] emotionConfigs) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.mc_emotion_panel, null);
        EmotionInfoContainer.initLocalEmotionInfo(getContext(), emotionConfigs);

        if (EmotionInfoContainer.vecPageEmotionInfoVecLocal.size() == 0) {
            return;
        }
        emotionGrids = new View[EmotionInfoContainer.vecPageEmotionInfoVecLocal.size()][];
        for (int i = 0; i < EmotionInfoContainer.vecPageEmotionInfoVecLocal.size(); ++i) {
            Vector<EmotionPageInfo> vecEmotionPageInfo = EmotionInfoContainer.vecPageEmotionInfoVecLocal.get(i);
            emotionGrids[i] = new View[vecEmotionPageInfo.size()];
            for (int j = 0; j < vecEmotionPageInfo.size(); ++j) {
                EmotionPageInfo emotionPageInfo = vecEmotionPageInfo.get(j);
                View v;
                if (i==0) {
                    v = LayoutInflater.from(getContext())
                            .inflate(R.layout.mc_emotion_group_small_gridview, null);
                } else {
                    v = LayoutInflater.from(getContext())
                            .inflate(R.layout.mc_emotion_group_big_gridview, null);
                }

                GridView emotionGrid = (GridView) v.findViewById(R.id.emoticon_grid);
                EmotionGridAdapter emotionGridAdapter = new EmotionGridAdapter(getContext(), emotionPageInfo, internalListener);
                emotionGrid.setNumColumns(emotionPageInfo.getColumnSize());
                emotionGrid.setAdapter(emotionGridAdapter);
                emotionGrids[i][j] = emotionGrid;
            }
        }

        initEmotionViewPager();
        initEmotionTabGroup();

        switchPagerIndicator(0, 0);

        addView(rootView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }


    private void initEmotionTabGroup() {
        emotionTabGroup = (ViewGroup) rootView.findViewById(R.id.emotion_tab_group);
        tabs = new ArrayList<>(emotionGrids.length);
        for (int i = 0; i < emotionGrids.length; i++) {
            ViewGroup tab = initTabBut(i);
            tabs.add(tab);
            emotionTabGroup.addView(tab, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));

            //表情标签之间的分隔符
            if (i < (emotionGrids.length - 1)) {
                View div = new View(getContext());
                div.setBackgroundResource(R.color.emotion_panel_divider);
                div.setLayoutParams(new LayoutParams(1, LayoutParams.MATCH_PARENT));
                emotionTabGroup.addView(div);
            }
        }
    }

    private ViewGroup initTabBut(int i) {
        LinearLayout tabBut = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.mc_emotion_tab, null);
        ImageView tabIcon = (ImageView) tabBut.findViewById(R.id.tab_icon);
        TextView tabName = (TextView) tabBut.findViewById(R.id.tab_name);
        tabIcon.setImageResource(EmotionInfoContainer.tabIconResIdArray[i]);
        tabName.setText(EmotionInfoContainer.tabTxtArray[i]);

        tabBut.setSelected(i == 0 ? true : false);
        tabIcon.setSelected(i == 0 ? true : false);
        tabName.setSelected(i == 0 ? true : false);

        tabBut.setTag(i);
        tabBut.setOnClickListener(tabChangeListener);
        return tabBut;
    }

    private void initEmotionViewPager() {
        emotionPageAdapter = new EmotionPageAdapter();
        // 装indicator的界面容器
        indicatorGroup = (ViewGroup) rootView.findViewById(R.id.gallery_point_linear);
        // 将indicator加入到ViewGroup中
        viewPager = (ViewPager) rootView.findViewById(R.id.figure_pager);
        viewPager.setOffscreenPageLimit(EmotionInfoContainer.totalPageSizeLocal);
        tips = new ImageView[emotionGrids.length][];
        for (int i = 0; i < tips.length; i++) {
            tips[i] = new ImageView[emotionGrids[i].length];
            for (int j = 0; j < tips[i].length; j++) {
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
                tips[i][j] = imageView;
            }
        }
        curGroupIndex = 0;
        curPageIndex = 0;
        // 设置Adapter
        // 表情图标填充入该emotionPageAdapter
        viewPager.setAdapter(emotionPageAdapter);
        // 设置监听，主要是设置Indicator的背景
        viewPager.setOnPageChangeListener(pagerChangeListener);
        viewPager.setCurrentItem(0);
    }

    private void switchPagerGroup(int groupIndex) {
        if (viewPager != null) {
            int position = 0;
            for (int i = 0; i < groupIndex; i++) {
                position += emotionGrids[i].length;
            }
            viewPager.setCurrentItem(position);
        }
    }

    private void switchPagerIndicator(int groupIndex, int pageIndex) {
        //如果emotion的group未改变, 则无需更新indicatorGroup中的View
        if (groupIndex == prevGroupIndex) {
            setIndicatorBackground(groupIndex, pageIndex);
        } else {
            setIndicatorGroup(groupIndex, pageIndex);
            switchPagerGroupTab(groupIndex);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void switchPagerGroupTab(int groupIndex) {
        for (ViewGroup tab : tabs) {
            if (groupIndex == (int) tab.getTag()) {
                tab.setSelected(true);
                for(int i = 0; i < tab.getChildCount(); i ++) {
                    tab.getChildAt(i).setSelected(true);
                }
            } else {
                tab.setSelected(false);
                for(int i = 0; i < tab.getChildCount(); i ++) {
                    tab.getChildAt(i).setSelected(false);
                }
            }
        }
    }

    private void setIndicatorBackground(int groupIndex, int pageIndex) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutParams.setMargins(10, 0, 10, 0);
        for (int i = 0; i < tips[groupIndex].length; i++) {
            if (i == pageIndex) {
                tips[groupIndex][i].setBackgroundResource(R.drawable.mc_feature_point_cur);
            } else {
                tips[groupIndex][i].setBackgroundResource(R.drawable.mc_feature_point);
            }

        }
    }

    private void setIndicatorGroup(int groupIndex, int pageIndex) {
        if (indicatorGroup == null) {
            return;
        }
        indicatorGroup.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutParams.setMargins(10, 0, 10, 0);
        for (int i = 0; i < tips[groupIndex].length; i++) {
            if (i == pageIndex) {
                tips[groupIndex][i].setBackgroundResource(R.drawable.mc_feature_point_cur);
            } else {
                tips[groupIndex][i].setBackgroundResource(R.drawable.mc_feature_point);
            }
            indicatorGroup.addView(tips[groupIndex][i], layoutParams);
        }
    }

    //内部的监听listener
    protected interface InternalListener {
        void onPageChange (int groupIndex, int pageIndex);
        void onEmotionItemSelected (EmotionInfo emotionItem);
    }

    public class EmotionPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (null == emotionGrids) {
                return 1;
            }
            return EmotionInfoContainer.totalPageSizeLocal;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // ((ViewPager)container).removeView(mImageViews[position %
            // mImageViews.length]);
            View view = (View) object;
            container.removeView(view);
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (emotionGrids == null || emotionGrids.length == 0) {
                return null;
            }

            //获取当前position对应的二维数组emotionGrids中View的位置坐标i,j
            int i,j = 0;
            int len1 = 0, len2;
            for (i = 0; i < emotionGrids.length; i++) {
                if (emotionGrids.length == 1) {
                    i = 0;
                    j = position;
                    break;
                }
                len1 += emotionGrids[i].length;

                if ((i + 1) >= emotionGrids.length) break;

                len2 = len1 + emotionGrids[i+1].length;
                if (position < len1) {
                    j = position;
                    break;
                } else if (position >= len1 && position < len2) {
                    j = position - len1;
                    i += 1;
                    break;
                } else {
                    continue;
                }
            }
            container.addView(emotionGrids[i][j]);
            return emotionGrids[i][j];
        }

    }
}
