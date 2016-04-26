package com.waynezhang.mcommon.support.image;

import android.app.Activity;
import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.waynezhang.mcommon.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by liuzimao.sanders on 8/26/15.
 */
public class ShowAllPicturesAdapter extends BaseAdapter {

    private final static String TAG = "ShowAllPicturesAdapter";

    public class ItemInfo {
        public ItemInfo() {
            idxs = new Vector<Integer>();
            type = -1;
        }

        Vector<Integer> idxs;
        int type; // -1 is default, 0 is this week, 1 is this month, 2 is other month
        String dateString;
    }

    private ArrayList<Image> _imageList; // 排序过后的版本。imagelist
    private Map<Integer, ItemInfo> posData = new HashMap<Integer, ItemInfo>(); // 按照月分对象的队列
    private Context _context;

    private void InitData(List<Image> imageList) {
        Vector<Integer> data = new Vector<Integer>();

        int pos = 0;
        int startIndex = 0;
        Time curTime = new Time();
        curTime.setToNow();

        ItemInfo info = new ItemInfo();

        // this week
        boolean flag = false;
        for (; startIndex < imageList.size(); startIndex++) {
            Time tmp = new Time();
            tmp.set(imageList.get(startIndex).timestamp.getTime());

            if (tmp.year == curTime.year && tmp.month == curTime.month && tmp.getWeekNumber() == curTime.getWeekNumber()) {
                info.idxs.add(startIndex);
                flag = true;
                continue;
            }
            break;
        }
        if (flag)
        {
            info.type = 0;
            posData.put(pos, info);
            pos++;
        }


        info = new ItemInfo();
        flag = false;

        // this month
        for (; startIndex < imageList.size(); startIndex++) {
            Time tmp = new Time();
            tmp.set(imageList.get(startIndex).timestamp.getTime());

            if (tmp.year == curTime.year && tmp.month == curTime.month) {
                info.idxs.add(startIndex);
                flag = true;
                continue;
            }
            break;
        }
        if (flag)
        {
            info.type = 1;
            posData.put(pos, info);
            pos++;
        }

        Map<Integer, ItemInfo> posDataStr = new HashMap<Integer , ItemInfo>(); //

        //-------- normal month
        for (; startIndex < imageList.size(); startIndex++) {

            Time tmp = new Time();
            tmp.set(imageList.get(startIndex).timestamp.getTime());

            ItemInfo tmpInfo = posDataStr.get(tmp.year * 100 + tmp.month);

            if (tmpInfo == null)
            {
                tmpInfo = new ItemInfo();
            }
            tmpInfo.type = 2;
            tmpInfo.idxs.add(startIndex);
            posDataStr.put(tmp.year * 100 + tmp.month, tmpInfo);
        }

        for (Map.Entry<Integer , ItemInfo> entry : posDataStr.entrySet()) {
            Integer key = entry.getKey();
            Object obj = entry.getValue();

            posData.put(pos, (ItemInfo)obj);
            pos++;
        }
    }

    public String getDateString(Date timestamp){
        if (timestamp == null) return "";

        Time curTime = new Time();
        curTime.setToNow();

        Time tmp = new Time();
        tmp.set(timestamp.getTime());

        if (tmp.year == curTime.year && tmp.month == curTime.month && tmp.getWeekNumber() == curTime.getWeekNumber()) {
            return  "本周";
        }else if( tmp.year == curTime.year && tmp.month == curTime.month) {
            return  "这个月";
        }else {
            return tmp.year + " 年 " + tmp.month + " 月 ";
        }
    }

    public Image getItemInfo(int position)
    {
        if (_imageList == null || _imageList.size() <= 0) return  null;
        return _imageList.get(position);
    }

    public ShowAllPicturesAdapter(Context context, ArrayList<Image> imageList) {
        this._imageList = imageList;
        _context = context;

        // image item sort by date
        Collections.sort(this._imageList, new Comparator<Image>() {
            @Override
            public int compare(Image lhs, Image rhs) {
                Date date1 = lhs.timestamp;
                Date date2 = rhs.timestamp;

                if (date1.after(date2)) {
                    return 1;
                }
                return -1;
            }
        });

        InitData(_imageList);
    }

    @Override
    public int getCount() {
        return _imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return _imageList.get(position);
        // return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (_imageList == null || _imageList.size() == 0){
            Log.e(TAG, "getView image list is null image=" + _imageList);
            return null;
        }
        Image img = _imageList.get(position);
        if ( img == null){
            Log.e(TAG, "getView item is null, pos = " + position);
            return null;
        }

        Log.d(TAG, "position=" + position + "img=" + img.smallUrl + ",width=" + img.smallWidth);

        if (convertView == null)
            convertView = LayoutInflater.from(_context).inflate(R.layout.mc_picture_item, null);

        final int curPos = position;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowImageActivity.go((Activity)_context, curPos, _imageList, true);
            }
        });

        ImageView imageView = (ImageView) convertView.findViewById(R.id.picture_view);
        Picasso.with(_context).load(img.smallUrl).resize(img.smallWidth, img.smallHeight).centerCrop().into(imageView);

        convertView.setTag(position);
        return convertView;
    }
}
