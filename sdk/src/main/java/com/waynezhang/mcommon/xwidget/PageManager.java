package com.waynezhang.mcommon.xwidget;

import android.widget.ListView;

import com.waynezhang.mcommon.compt.ArrayAdapterCompat;
import com.waynezhang.mcommon.util.ListViewUtil;

import java.lang.ref.WeakReference;
import java.util.List;

import thirdpart.com.handmark.pulltorefresh.library.PullToRefreshBase;
import thirdpart.com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by don on 1/26/15.
 */
public class PageManager<T> {
    protected final WeakReference<PullToRefreshListView> mPtrList;
    protected final ArrayAdapterCompat<T> mAdapter;
    protected final int mStartPage;
    protected final int mNumPageItem;
    protected int mCurrentPageNo;
    protected boolean enableRefresh;
    protected PageLoadListener mPageLoadListener;
    private boolean loading;

    public PageManager(PullToRefreshListView ptrList, ArrayAdapterCompat<T> adapter, int startPage, final int numPageItem) {
        //numPageItem为服务端返回的一页里面item个数
        this.mPtrList = new WeakReference<PullToRefreshListView>(ptrList);
        this.mAdapter = adapter;
        this.mStartPage = startPage;
        this.mNumPageItem = numPageItem;

        ptrList.setAdapter(mAdapter);
        ptrList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mCurrentPageNo = mStartPage - 1;

        ptrList.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                //fix 当第一页记录数的最后一条刚好可以看见，同时第二页没有数据时ptr会自动跳到底部
                if(mCurrentPageNo == mStartPage && mAdapter.getCount() < numPageItem){
                    return;
                }
//                if (mCurrentPageNo == mStartPage && mPtrList.getRefreshableView().getCount() >= 3 && mPtrList.getRefreshableView().getCount() <= 7) {
//                    return;
//                }

                loadMorePage();
            }
        });

        ptrList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (enableRefresh) {
                    refresh();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadMorePage();
            }
        });

        ptrList.setShowIndicator(false);
    }

    public PageManager(final PullToRefreshListView ptrList, ArrayAdapterCompat<T> adapter, int startPage) {
        this.mNumPageItem = 0;
        this.mPtrList = new WeakReference<PullToRefreshListView>(ptrList);
        this.mAdapter = adapter;
        this.mStartPage = startPage;

        ptrList.setAdapter(mAdapter);
        ptrList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mCurrentPageNo = mStartPage - 1;

        ptrList.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                //fix 当第一页记录数的最后一条刚好可以看见，同时第二页没有数据时ptr会自动跳到底部
                if (mCurrentPageNo == mStartPage && ptrList.getRefreshableView().getCount() >= 3 && ptrList.getRefreshableView().getCount() <= 7) {
                    return;
                }

                loadMorePage();
            }
        });

        ptrList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (enableRefresh) {
                    refresh();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadMorePage();
            }
        });

        ptrList.setShowIndicator(false);
    }

    public void enableRefresh(boolean enable) {
        this.enableRefresh = enable;
        PullToRefreshListView ptrList = mPtrList.get();
        if(ptrList != null){
            ptrList.setMode(enableRefresh ? PullToRefreshBase.Mode.BOTH : PullToRefreshBase.Mode.PULL_FROM_END);
        }
    }

    public void disableRefresh() {
        this.enableRefresh = false;
        PullToRefreshListView ptrList = mPtrList.get();
        if(ptrList != null){
            ptrList.setMode(PullToRefreshBase.Mode.DISABLED);
        }
    }

    public void setPageLoadListener(PageLoadListener pageLoadListener) {
        this.mPageLoadListener = pageLoadListener;
    }

    public void loadMorePage() {
        //loadMorePage在网上拉加载时和最后一条可见时都会触发，导致最后数据显示会出错，因此该方法要串行调用
        if(loading){
            return;
        }

        loading = true;
        mPageLoadListener.pageLoad(++mCurrentPageNo, false, false);
    }

    public void loadFirstPage() {
        mCurrentPageNo = mStartPage;
        mPageLoadListener.pageLoad(mCurrentPageNo, true, false);
    }

    public void refresh() {
        mCurrentPageNo = mStartPage;
        mPageLoadListener.pageLoad(mCurrentPageNo, true, true);
    }

    public void bind(List<T> list, int pageNo , final boolean needGoTop) {
        final PullToRefreshListView ptrList = mPtrList.get();
        if(ptrList == null){
            return;
        }

        if (pageNo == mStartPage) {
            ptrList.post(new Runnable() {
                @Override
                public void run() {
                    if(needGoTop){
                        ptrList.getRefreshableView().setSelection(0);
                    }
                    ListViewUtil.stopScrolling(ptrList.getRefreshableView());
                }
            });
            mAdapter.clear();
        } else if (list.isEmpty()) {
            mCurrentPageNo--;
            ToastUtil.showToast("没有更多结果了");
        }

        mAdapter.addAll(list);
        loading = false;
        ptrList.onRefreshComplete();
    }

    public void bind(List<T> list, int pageNo) {
        final PullToRefreshListView ptrList = mPtrList.get();
        if(ptrList == null){
            return;
        }

        if (pageNo == mStartPage) {
            ptrList.post(new Runnable() {
                @Override
                public void run() {
                    ptrList.getRefreshableView().setSelection(0);
                    ListViewUtil.stopScrolling(ptrList.getRefreshableView());
                }
            });
            mAdapter.clear();
        } else if (list.isEmpty()) {
            mCurrentPageNo--;
            ToastUtil.showToast("没有更多结果了");
        }

        mAdapter.addAll(list);
        loading = false;
        ptrList.onRefreshComplete();
    }

    public void onFailure() {
        loading = false;
        mCurrentPageNo--;
        PullToRefreshListView ptrList = mPtrList.get();
        if(ptrList != null){
            ptrList.onRefreshComplete();
        }
    }

    public ArrayAdapterCompat<T> getAdapter() {
        return mAdapter;
    }

    public interface PageLoadListener {
        void pageLoad(int pageNo, boolean isFirstPage, boolean isRefresh);
    }
}
