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
package com.waynezhang.mcommon.compt;

import java.util.Collection;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.widget.ArrayAdapter;

/**
 * ArrayAdapter在Android2.3版本无法使用addAll方法, 用此类代替
 *
 * @param <T>
 */
public class ArrayAdapterCompat<T> extends ArrayAdapter<T> {
    private boolean mNeedNotifyChange = true;

    public ArrayAdapterCompat(Context context){
        super(context, 0);
    }

    public ArrayAdapterCompat(Context context, int resource,
                              int textViewResourceId, List<T> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public ArrayAdapterCompat(Context context, int resource,
                              int textViewResourceId, T[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public ArrayAdapterCompat(Context context, int resource,
                              int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public ArrayAdapterCompat(Context context, int textViewResourceId,
                              List<T> objects) {
        super(context, textViewResourceId, objects);
    }

    public ArrayAdapterCompat(Context context, int textViewResourceId,
                              T[] objects) {
        super(context, textViewResourceId, objects);
    }

    public ArrayAdapterCompat(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mNeedNotifyChange = true;
    }

    @Override
    public void setNotifyOnChange(boolean notifyOnChange) {
        mNeedNotifyChange = notifyOnChange;
        super.setNotifyOnChange(notifyOnChange);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void addAll(Collection<? extends T> collection) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
        	super.addAll(collection);
            return;
        }

        setNotifyOnChange(false);
        for(T item : collection){
            add(item);
        }
        setNotifyOnChange(mNeedNotifyChange);
        if(mNeedNotifyChange)notifyDataSetChanged();
    }

}
