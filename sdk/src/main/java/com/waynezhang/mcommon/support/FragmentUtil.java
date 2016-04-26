package com.waynezhang.mcommon.support;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by liuyagang on 15-9-15.
 */
public class FragmentUtil {
    /**
     * 获取View所在的Fragment
     *
     * @param view
     * @return 如果View在Fragment中则返回对应的Fragment，否则返回null
     */
    public static Fragment getFragmentByView(View view) {
        Context context = view.getContext();
        if (!(context instanceof FragmentActivity)) {
            return null;
        }
        FragmentActivity activity = (FragmentActivity) context;
        List<Fragment> fragments = activity.getSupportFragmentManager().getFragments();
        if (fragments == null) {
            return null;
        }
        for (Fragment fragment : fragments) {
            Fragment f = getFragmentByViewRecursion(fragment, view);
            if (f != null) {
                return f;
            }
        }

        return null;
    }

    /**
     * 查找View所在Fragment
     *
     * @param fragment
     * @param view
     * @return
     */
    private static Fragment getFragmentByViewRecursion(Fragment fragment, View view) {
        //由于子Fragment中的View在父Fragment中也可以查到，因此优先查找子Fragment
        List<Fragment> fragments = fragment.getChildFragmentManager().getFragments();
        if (fragments != null && fragments.size() > 0) {
            for (Fragment childFragment : fragments) {
                Fragment f = getFragmentByViewRecursion(childFragment, view);
                if (f != null) {
                    return f;
                }
            }
        }

        //如果子Fragment中没有查到View，那么在当前Fragment中查找
        View fragmentView = fragment.getView();
        if (fragmentView instanceof ViewGroup && hasView((ViewGroup) fragmentView, view)) {
            return fragment;
        }

        return null;
    }

    /**
     * 查找View是否存在于ViewGroup中
     *
     * @param viewGroup
     * @param child
     * @return
     */
    private static boolean hasView(ViewGroup viewGroup, View child) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childView = viewGroup.getChildAt(i);
            if (childView == child) {
                return true;
            }
            if (childView instanceof ViewGroup) {
                if (hasView((ViewGroup) childView, child)) {
                    return true;
                }
            }
        }
        return false;
    }
}
