/*
 * Copyright (c) 2013 Shanda Corporation. All rights reserved.
 *
 * Created on 2013-10-14.
 */

package com.waynezhang.mcommon.util;

import android.content.Context;

/**
 * 资源工具类.
 *
 * @author James Shen
 */
public final class ResourceHelper {

    private ResourceHelper() {
    }

    public static int getId(Context paramContext, String paramString) {
        if (paramString != null) {
            String[] split = paramString.split("\\.", 3);
            if (split.length == 3) {
                return paramContext.getResources().getIdentifier(split[2], split[1], paramContext.getPackageName());
            }
        }
        return 0;
    }
}
