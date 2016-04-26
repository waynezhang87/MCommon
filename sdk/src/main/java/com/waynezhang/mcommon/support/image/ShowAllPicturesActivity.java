package com.waynezhang.mcommon.support.image;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.xwidget.McTitleBar;

import java.util.ArrayList;

/**
 * Created by liuzimao.sanders on 2015/8/26.
 */
public class ShowAllPicturesActivity extends Activity {

    private static final String TAG = "ShowAllPicturesActivity";
    private static final String KEY_IMAGE_LIST = "KEY_IMAGE_LIST";

    private GridView all_pics_list = null;
    private ShowAllPicturesAdapter _gridAdapter = null;

    private ImageView _btnLeft = null;

    private McTitleBar _titleBar = null;
    private TextView _datetimeBarTxt = null;
    static int visibleStatus = View.GONE;

    public static void go(Activity from, ArrayList<Image> imageList) {
        Log.d(TAG, "go image count=" + imageList.size());

        Intent intent = new Intent(from, ShowAllPicturesActivity.class);
        intent.putExtra(KEY_IMAGE_LIST, imageList);
        from.startActivity(intent);
    }

    private void setDateTimeVisible(final int visible){
        if (_datetimeBarTxt == null) return;

        if (visibleStatus != visible) {
            visibleStatus = visible;

            int barH = _datetimeBarTxt.getHeight() / 2;
            Animation showBarAnimation = null;
            if (visible == View.VISIBLE)
                showBarAnimation = new TranslateAnimation(0, 0, 0, 0);
            else
                showBarAnimation = new TranslateAnimation(0, 0, 0, 0);
            showBarAnimation.setDuration(300);
            showBarAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    _datetimeBarTxt.setVisibility(visible);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            _datetimeBarTxt.startAnimation(showBarAnimation);
        }

        int position = all_pics_list.getFirstVisiblePosition();
        Log.d(TAG, "setDateTimeVisible visible=" + visible + ", pos=" + position);
        Image ii = _gridAdapter.getItemInfo(position);

        if (_datetimeBarTxt != null && ii != null) {
            Log.d(TAG, "setDateTimeVisible ii timestamp=" + ii.timestamp.toString());
            _datetimeBarTxt.setText(_gridAdapter.getDateString(ii.timestamp));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        setContentView(R.layout.mc_activity_show_all_pictures);

        _titleBar = (McTitleBar)findViewById(R.id.mc_titleBar);

        _datetimeBarTxt = (TextView)findViewById(R.id.datetime_bar_txt);

        Intent intent = getIntent();
        ArrayList<Image> imagelist = (ArrayList<Image>) intent.getSerializableExtra(KEY_IMAGE_LIST);

        _gridAdapter = new ShowAllPicturesAdapter(this, imagelist);
        all_pics_list = (GridView) findViewById(R.id.all_pics_list);
        if (all_pics_list != null) {
            all_pics_list.setAdapter(_gridAdapter);
            all_pics_list.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch action=" + event.getAction());

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        setDateTimeVisible(View.VISIBLE);
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        setDateTimeVisible(View.VISIBLE);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        setDateTimeVisible(View.INVISIBLE);
                    } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                        setDateTimeVisible(View.INVISIBLE);
                    }
                    return false;
                }
            });

        }

        _titleBar.setRightButtonVisibility(View.GONE);
        _titleBar.setLeftButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAllPicturesActivity.this.finish();
            }
        });
        _gridAdapter.notifyDataSetChanged();

        if (all_pics_list != null) {
            Log.d(TAG, "onCreate bottom=" + all_pics_list.getBottom());
            all_pics_list.setSelection(imagelist.size() - 1);
            // all_pics_list.setTranscriptMode(GridView.TRANSCRIPT_MODE_NORMAL);
        }

    }

}
