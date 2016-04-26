/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 jc0mm0n
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.waynezhang.mcommon.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.waynezhang.mcommon.fragment.BaseFragment;
import com.waynezhang.mcommon.support.Safeguard;

public class GenericFragmentActivity extends FragmentActivity {
    private static final String KEY_FRAGMENT_CLASS = "KEY_FRAGMENT_CLASS";
    private static final String KEY_FRAGMENT_ARGS = "KEY_FRAGMENT_ARGS";

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle bundle) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(bundle);
        FrameLayout layout = new FrameLayout(this.getApplicationContext());
        setContentView(layout, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        Bundle args = getIntent().getExtras();
        String fragmentClassName = args.getString(KEY_FRAGMENT_CLASS);

        try {
            Fragment fragment = (Fragment) Class.forName(fragmentClassName)
                    .newInstance();
            Bundle argument = args.getBundle(KEY_FRAGMENT_ARGS);
            fragment.setArguments(argument);

            attachFragment(getSupportFragmentManager(), fragment);
        } catch (Exception e) {
            throw new IllegalStateException("Has error in new instance of fragment");
        }
    }

    private void attachFragment(FragmentManager supportFragmentManager, Fragment fragment) {
        currentFragment = fragment;
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        transaction.add(android.R.id.content, fragment);
        transaction.commitAllowingStateLoss();
    }

    public static void start(Activity from, Class<?> fragmentClass, Bundle args) {
        if (Safeguard.ignorable(from))
            return;

        Intent intent = new Intent(from, GenericFragmentActivity.class);
        intent.putExtra(KEY_FRAGMENT_CLASS, fragmentClass.getCanonicalName());
        intent.putExtra(KEY_FRAGMENT_ARGS, args);
        from.startActivity(intent);
    }

    public static void start(Fragment from, Class<?> fragmentClass, Bundle args) {
        if (Safeguard.ignorable(from))
            return;

        Intent intent = new Intent(from.getActivity(), GenericFragmentActivity.class);
        intent.putExtra(KEY_FRAGMENT_CLASS, fragmentClass.getCanonicalName());
        intent.putExtra(KEY_FRAGMENT_ARGS, args);
        from.startActivity(intent);
    }

    public static void startForResult(Activity from, int reqCode,
                                      Class<?> fragmentClass, Bundle args) {
        if (Safeguard.ignorable(from))
            return;

        Intent intent = new Intent(from, GenericFragmentActivity.class);
        intent.putExtra(KEY_FRAGMENT_CLASS, fragmentClass.getCanonicalName());
        intent.putExtra(KEY_FRAGMENT_ARGS, args);
        from.startActivityForResult(intent, reqCode);
    }

    public static void startForResult(Fragment frag, int reqCode,
                                      Class<?> fragmentClass, Bundle args) {
        if (Safeguard.ignorable(frag))
            return;

        Intent intent = new Intent(frag.getActivity(), GenericFragmentActivity.class);
        intent.putExtra(KEY_FRAGMENT_CLASS, fragmentClass.getCanonicalName());
        intent.putExtra(KEY_FRAGMENT_ARGS, args);
        frag.startActivityForResult(intent, reqCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public Fragment getCurrentFragment(){
        return currentFragment;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && currentFragment != null && currentFragment instanceof BaseFragment) {
            return ((BaseFragment) currentFragment).onBackPressed();
        }
        return super.onKeyUp(keyCode, event);
    }
}

