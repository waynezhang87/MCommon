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

/**
 * Created by jc0mm0n on 10/26/14.
 */
public class ClickChecker {
    private ClickChecker(){};

    private static final long MAX_MULTI_CHECK_INTERVAL = 10;
    private static final long MIN_INTERVAL = 500;

    private static long lastClickTime=0;

    public static boolean isFastFollowedClick() {
        long now = System.currentTimeMillis();
        long clickInterval = now - lastClickTime;

        if (MAX_MULTI_CHECK_INTERVAL < clickInterval){
            Log.w("ClickChecker", "You are doing duplicate ClickCheck in the same place");
        }

        if (0 < clickInterval && clickInterval < MIN_INTERVAL) {
            return true;
        }

        lastClickTime = now;
        return false;
    }
}
