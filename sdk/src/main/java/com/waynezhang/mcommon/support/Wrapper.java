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
package com.waynezhang.mcommon.support;

import java.util.ArrayList;
import java.util.List;

public class Wrapper<T> {
	private T mValue;
	private Object mExtra;

	public Wrapper(T t) {
		mValue = t;
	}

	public Wrapper(T t, Object extra) {
		mValue = t;
		mExtra = extra;
	}

    public void setValue(T value) {
        mValue = value;
    }

    public void setExtra(Object obj) {
        mExtra = obj;
    }

	public boolean hasValue() {
		return mValue != null;
	}

	public T value() {
		return mValue;
	}

	public Object getExtra() {
		return mExtra;
	}

	public static <T> List<Wrapper<T>> toWrapList(T[] array) {
		List<Wrapper<T>> wrapList = new ArrayList<Wrapper<T>>();
		for (T t : array) {
			wrapList.add(new Wrapper<T>(t));
		}
		return wrapList;
	}

	public static <T> List<Wrapper<T>> toWrapList(List<T> list) {
		List<Wrapper<T>> wrapList = new ArrayList<Wrapper<T>>();
		for (T t : list) {
			wrapList.add(new Wrapper<T>(t));
		}
		return wrapList;
	}
}
