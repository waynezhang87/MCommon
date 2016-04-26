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
package com.waynezhang.mcommon.xwidget;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.util.L;

public class McDialog extends DialogFragment {
	private static final String TAG = "JcDialog";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_FRAME, R.style.ThemeDialog);
	}

	public void fixedShow(FragmentActivity activity) {
		fixedShow(activity, this.toString());
	}

	public void fixedShow(FragmentActivity activity, String tag) {
		if (activity == null || activity.isFinishing()) {
			L.d(TAG, String.format("activity [%s] is null or is finishing!",
					activity));
			return;
		}
		FragmentTransaction ft = activity.getSupportFragmentManager()
				.beginTransaction();
		ft.add(this, tag);
		ft.commitAllowingStateLoss();
	}

	/**
	 * 此方法使用commit会出现异常 java.lang.IllegalStateException: Can not perform this
	 * action after onSaveInstanceState
	 */
	@Deprecated
	@Override
	public void show(FragmentManager manager, String tag) {
		super.show(manager, tag);
	}

	/**
	 * 同 {@link #show(FragmentManager, String)}
	 */
	@Deprecated
	@Override
	public int show(FragmentTransaction transaction, String tag) {
		return super.show(transaction, tag);
	}

}
