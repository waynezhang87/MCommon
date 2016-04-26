package com.waynezhang.mcommon.xwidget;

import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.waynezhang.mcommon.compt.ArrayAdapterCompat;

import java.util.List;

import thirdpart.com.handmark.pulltorefresh.library.PullToRefreshBase;
import thirdpart.com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by liuyagang on 15-6-8.
 * <p/>
 * 产品要求系统消息界面操作要做修改，为了防止产品要求再改回老版本，这里做了一个PageManager的特化类，业务类只需要创建不同的实例即可
 * 1. 排序新的条目要显示在最下面
 * 2. 向上拉动到顶部翻页
 * 3. 页面底部要留一部分空白
 */
public class MsgPageManager<T> extends PageManager<T> {
    public MsgPageManager(PullToRefreshListView ptrList, ArrayAdapterCompat<T> adapter, int startPage) {
        super(ptrList, adapter, startPage);

        View footerView = new View(ptrList.getContext());
        footerView.setLayoutParams(new AbsListView.LayoutParams(50, 50));
        ptrList.getRefreshableView().addFooterView(footerView);
        ptrList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        ptrList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadMorePage();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }

    @Override
    public void bind(List<T> list, int pageNo) {
        if (pageNo == mStartPage) {
            mAdapter.clear();
        } else if (list.isEmpty()) {
            mCurrentPageNo--;
            ToastUtil.showToast("没有更多结果了");
        }

        // 新的消息加在后面，老的消息加在最前面
        mAdapter.setNotifyOnChange(false);
        for (T item : list) {
            mAdapter.insert(item, 0);
        }
        mAdapter.setNotifyOnChange(true);
        mAdapter.notifyDataSetChanged();

        final PullToRefreshListView ptrList = mPtrList.get();
        if(ptrList == null){
            return;
        }
        // 第一页需要滚动到页面底部
        if (pageNo == mStartPage) {
            ptrList.post(new Runnable() {
                @Override
                public void run() {
                    ptrList.getRefreshableView().setSelection(mAdapter.getCount() - 1);
                }
            });
        }
        ptrList.onRefreshComplete();
    }
}
