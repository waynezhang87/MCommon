package com.waynezhang.test.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.waynezhang.test.R;
import com.waynezhang.test.service.TestTestReqCallback;
import com.waynezhang.test.service.TestServiceApi;
import com.waynezhang.mcommon.xwidget.titlebar.McTitleBarExt;
import com.waynezhang.mcommon.xwidget.titlebar.McTitleBarExtMenuItem;
import com.waynezhang.mcommon.xwidget.ToastUtil;

import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        McTitleBarExt titleBarExt = (McTitleBarExt) findViewById(R.id.title);
        titleBarExt.setOnMenuItemClickListener(new McTitleBarExt.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(McTitleBarExtMenuItem menuItem) {
                ToastUtil.showToast(getApplicationContext(), menuItem.title);
                return false;
            }
        });
        titleBarExt.setOnTitleMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ToastUtil.showToast(getApplicationContext(), item.getTitle());
                return false;
            }
        });
        titleBarExt.setOnLeftBtnClickListener(new McTitleBarExt.OnLeftButtonClickListener() {
            @Override
            public void onLeftButtonClick(View v) {
                finish();
            }
        });

        titleBarExt.notifyWithoutCount(R.id.add, true);

        titleBarExt.notifyWithCount(R.id.add, 10);

        TextView texView = (TextView) findViewById(R.id.deviceId);
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String DEVICE_ID = tm.getDeviceId();
        texView.setText("DeviceId ==> " + DEVICE_ID);

        final TextView response = (TextView) findViewById(R.id.testResponse);

        String x = null;

        Log.d("test", "result------>" + String.valueOf("http://ymm123.sdo.com/api/accountapi/config?src_code=10&_=9c22885c-8986-42c5-8785-2a129354165a&method=GetBulletinInfod41d8cd98f00b204e9800998ecf8427e".matches(".*" + x + ".*")));

        TestServiceApi.getGameOperatorAreaGroupListOnshelf2(new TestTestReqCallback<List<Object>>() {
            @Override
            protected void onSuccess(List<Object> result) {
                response.setText(result.toString());
            }
        });

        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });


    }

}
