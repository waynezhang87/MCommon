package com.waynezhang.mcommon.xwidget.emotionpanel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.util.ScreenUtil;
import com.waynezhang.mcommon.xwidget.emotionpanel.container.EmotionInfo;
import com.waynezhang.mcommon.xwidget.emotionpanel.container.EmotionInfoContainer;
import com.waynezhang.mcommon.xwidget.emotionpanel.container.EmotionPageInfo;
import com.waynezhang.mcommon.xwidget.gif.GifView;


public class EmotionGridAdapter extends BaseAdapter{

    public class ViewHolder {
		public ImageView txtIcon;
		public GifView txtGif;
		public EmotionInfo emotionInfo;
	}

	Context context;
	EmotionPageInfo emotionPageInfo;
    EmotionPanelView.InternalListener listener;
    OnClickListener mListener;

	public EmotionGridAdapter(Context context, EmotionPageInfo emotionPageInfo, final EmotionPanelView.InternalListener listener) {
		this.context = context;
		this.emotionPageInfo = emotionPageInfo;
		this.listener = listener;
        this.mListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onEmotionItemSelected((EmotionInfo) v.getTag());
                }
            }
        };
	}

	@Override
	public int getCount() {
		if(emotionPageInfo.getVecEmotionInfo()==null){
			return 0;
		}
		
		return emotionPageInfo.getVecEmotionInfo().size();
		
	}

	@Override
	public Object getItem(int position) {		
		Object obj=emotionPageInfo.getVecEmotionInfo().get(position);
		return obj;	
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {	
		EmotionInfo emotionInfo = emotionPageInfo.getVecEmotionInfo().get(position);
		
		if (convertView == null || !((ViewHolder)convertView.getTag()).emotionInfo.equals(emotionInfo)) {
			if(emotionPageInfo.getType()==EmotionPageInfo.PNG_SMALL) {
				convertView = LayoutInflater.from(context).inflate(R.layout.mc_emoticon_group_small_item, null);
			} else if (emotionPageInfo.getType()==EmotionPageInfo.PNG_BIG) {
				convertView = LayoutInflater.from(context).inflate(R.layout.mc_emoticon_group_big_item, null);
			}else{
				convertView = LayoutInflater.from(context).inflate(R.layout.mc_emoticon_gif_item, null);
				
			}
			
			ViewHolder holder = new ViewHolder();

			
			if(emotionPageInfo.getType()==EmotionPageInfo.PNG_SMALL||emotionPageInfo.getType()==EmotionPageInfo.PNG_BIG){
				holder.txtIcon=(ImageView)convertView.findViewById(R.id.image_iv);
				if(emotionInfo.getType()==EmotionInfo.EMOTION_SMALL||emotionInfo.getType()==EmotionInfo.EMOTION_BIG){
					String name=emotionInfo.getName();
					if(EmotionInfoContainer.mapPngEmotionInfoGlobal.containsKey(name)){
						int resId=EmotionInfoContainer.mapPngEmotionInfoGlobal.get(name);
						holder.txtIcon.setImageResource(resId);
					}
					
				}else if(emotionInfo.getType()==EmotionInfo.DELETE){
					holder.txtIcon.setImageResource(R.drawable.mc_del_btn);
					
				}
				
				LayoutParams ps = holder.txtIcon.getLayoutParams();
		        ps.height = ScreenUtil.dip2px(context, emotionPageInfo.getItemHeight());
		        ps.width= ScreenUtil.dip2px(context,  emotionPageInfo.getItemWidth());
		        holder.txtIcon.setLayoutParams(ps);

                holder.txtIcon.setTag(emotionInfo);
                holder.txtIcon.setOnClickListener(mListener);
			}else{
				holder.txtGif=(GifView)convertView.findViewById(R.id.image_iv);
				holder.txtGif.setGifImage(EmotionInfoContainer.mapGifEmotionInfoGlobal.get(emotionInfo.getName()));
				holder.txtGif.showCover();

                holder.txtGif.setTag(emotionInfo);
                holder.txtGif.setOnClickListener(mListener);
			}

			holder.emotionInfo=emotionInfo;
			convertView.setTag(holder);
		}
		return convertView;
	}
}
