package com.waynezhang.mcommon.xwidget;

/**
 * Created by liuyagang on 15-4-14.
 */
public interface Buildable<D, IV extends Bindable<D>> {
    IV build(D data);
}
