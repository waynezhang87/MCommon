package com.waynezhang.test.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.waynezhang.test.R;
import com.waynezhang.test.service.TestServiceApi;
import com.waynezhang.test.service.TestTestReqCallback;
import com.waynezhang.mcommon.network.Http;

import java.util.List;

/**
 * Created by waynezhang on 2/23/16.
 */
public class SecondActivity extends Activity {
    private String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

    }

    @Override
    protected void onResume() {
        super.onResume();
        final TextView response = (TextView) findViewById(R.id.testResponse);
        tag = TestServiceApi.getGameOperatorAreaGroupListOnshelf(new TestTestReqCallback<List<Object>>() {
            @Override
            protected void onSuccess(List<Object> result) {
                response.setText(result.toString());
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Http.cancel(tag);
    }
}
