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

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by jc0mm0n on 10/26/14.
 */
public class Safeguard {
    /**
     * <pre>
     * 判断是否需要忽略回调
     * 在对服务器发送请求后，页面已经关闭，但是回调没有及时销毁
     * 而这时候产生的回调如果对页面进行了某些操作都会导致应用异常
     *
     * 处理方法：
     * 在产生回调时先用此方法检查相关状态
     * </pre>
     * @param objects 可以是Activity, Fragment, View元素
     * @return true表示不需要再执行回调动作, false表示可以接着执行回调动作
     */
    public static boolean ignorable(Object... objects){
        if(objects==null)
            return true;

        for(Object obj : objects){
            if(obj instanceof Activity && !isValid((Activity)obj)){
                return true;
            } else if (obj instanceof Fragment && !isValid((Fragment)obj)){
                return true;
            } else if(!isValid(obj)){
                return true;
            }
        }
        return false;
    }

    private static boolean isValid(Activity activity){
        if(activity==null)
            return false;

        return !activity.isFinishing();
    }

    private static boolean isValid(Fragment fragment){
        if(fragment == null)
            return false;

        if(fragment.isDetached()){
            return true;
        }

        return isValid(fragment.getActivity());
    }

    private static boolean isValid(Object obj){
        return obj != null;
    }
}
