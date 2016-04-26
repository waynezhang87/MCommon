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
package com.waynezhang.mcommon.util;

import android.util.Log;

public class L {
    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;

    private static int level = 5;

    public static void setLevel(int level) {
        L.level = level;
    }

    public static int getLevel() {
        return level;
    }

    public static int v(String tag, String msg) {
        if (VERBOSE < level)
            return 0;

        return Log.v(tag, msg);
    }

    public static int v(String tag, String msg, Throwable tr) {
        if (VERBOSE < level)
            return 0;

        return Log.v(tag, msg, tr);
    }

    public static int d(String tag, String msg) {
        if (DEBUG < level)
            return 0;

        return Log.d(tag, msg);
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (DEBUG < level)
            return 0;

        return Log.d(tag, msg, tr);
    }

    public static int i(String tag, String msg) {
        if (INFO < level)
            return 0;

        return Log.i(tag, msg);
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (INFO < level)
            return 0;

        return Log.i(tag, msg, tr);
    }

    public static int w(String tag, String msg) {
        if (WARN < level)
            return 0;

        return Log.w(tag, msg);
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (WARN < level)
            return 0;

        return Log.w(tag, msg, tr);
    }

    public static int w(String tag, Throwable tr) {
        if (WARN < level)
            return 0;

        return Log.w(tag, tr);
    }

    public static int e(String tag, String msg) {
        if (ERROR < level)
            return 0;

        return Log.e(tag, msg);
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (ERROR < level)
            return 0;

        return Log.e(tag, msg, tr);
    }

}
