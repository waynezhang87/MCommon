package com.waynezhang.mcommon.template.interfaces;

import android.content.Intent;

/**
 * Created by don on 1/25/16.
 */
public interface TemplateImgGoOtherCallback {

    void take_phone(int requestCode, int resultCode, Intent data);

    void multi_pic_from_local(int requestCode, int resultCode, Intent data);

    void big_image(int requestCode, int resultCode, Intent data);
}
