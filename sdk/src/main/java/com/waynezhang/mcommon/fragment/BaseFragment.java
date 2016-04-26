package com.waynezhang.mcommon.fragment;

import android.support.v4.app.Fragment;

/**
 * Created by don on 3/9/15.
 */
public class BaseFragment extends Fragment {

    public boolean onBackPressed() {
        getActivity().finish();
        return true;
    }
}
