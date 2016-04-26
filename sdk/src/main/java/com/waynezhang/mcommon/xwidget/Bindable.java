package com.waynezhang.mcommon.xwidget;

/**
 * View是否带bind方法，通过bind方法可以将数据绑定到相应的View上
 */
public interface Bindable<T>{
    public void bind(T item);
}
