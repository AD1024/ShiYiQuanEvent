package ccoderad.bnds.shiyiquanevent.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.File;
import java.util.List;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.beans.EventBean;
import ccoderad.bnds.shiyiquanevent.utils.CacheUtils;
import ccoderad.bnds.shiyiquanevent.utils.ToastUtil;
import ccoderad.bnds.shiyiquanevent.utils.Utils;


/**
 * Created by CCoderAD on 16/3/17.
 */
public class EventListAdapter extends BaseAdapter {
    private List<EventBean> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private ViewHolder Vh;
    private JSONArray mRawData;
    private File favedEvents;

    public EventListAdapter(Context context, List<EventBean> Data) {
        mData = Data;
        mContext = context;
        mInflater = LayoutInflater.from(context);
        File cacheFile = Utils.getCacheFile(context, "event");
        if (!cacheFile.exists())
            cacheFile.mkdir();
        favedEvents = new File(cacheFile, "FavedEvents.json");
    }

    /*
    private String getEndTime(String Start,String Duraion){
        String ans = "";
        int dHour;
        int dMinute;
        int cur=0;
        boolean containsHour=false,containsMin=false;
        containsHour = Duraion.contains("小时");
        containsMin = Duraion.contains("分钟");
        if(containsHour){
            while (Duraion.charAt(cur)!='小'){
                cur+=1;
            }
        }
        return ans;
    }
*/
    public void setRawData(JSONArray data) {
        mRawData = data;
    }

    public void setListData(List<EventBean> mData){
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            Vh = new ViewHolder();
            convertView = mInflater.inflate(R.layout.event_list_item, null);
            Vh.mDate = (TextView) convertView.findViewById(R.id.event_list_item_date);
            Vh.mDuration = (TextView) convertView.findViewById(R.id.event_list_duration);
            Vh.mSponsor = (TextView) convertView.findViewById(R.id.event_list_item_sponsor);
            Vh.mTime = (TextView) convertView.findViewById(R.id.event_list_item_time);
            Vh.mTitle = (TextView) convertView.findViewById(R.id.event_list_item_title);
//            Vh.mEndTime = (TextView) convertView.findViewById(R.id.event_list_item_end_time);
            Vh.mFollower = (TextView) convertView.findViewById(R.id.event_list_item_follower);
            Vh.mFav = (ImageView) convertView.findViewById(R.id.event_list_item_fav);
            Vh.mLocation = (TextView) convertView.findViewById(R.id.event_list_item_location);
            convertView.setTag(Vh);
        } else {
            Vh = (ViewHolder) convertView.getTag();
        }
        EventBean bean = mData.get(position);
        if (bean.isFaved) {
            Vh.mFav.setImageResource(R.drawable.ic_favorite);
        }
        Vh.mTitle.setText(bean.eventTitle);
        Vh.mSponsor.setText(bean.sponsorName);
        Vh.mTime.setText(bean.eventTime);
        Vh.mLocation.setText(bean.eventLocation);
        Vh.mFollower.setText(Integer.toString(bean.eventFollower));
        Vh.mDate.setText(bean.eventDate);
        Vh.mDuration.setText(bean.eventDuration);
        Vh.mFav.setImageResource(mData.get(position).isFaved?R.drawable.ic_favorite:R.drawable.ic_favorite_border);
        Vh.mFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mData.get(position).isFaved){
                    Log.i("adapterOp","removeFav");
                    mData.get(position).isFaved = false;
                    CacheUtils.removeFavEventCache(mData.get(position),mRawData,favedEvents,position);
                    EventListAdapter.this.notifyDataSetChanged();
                }else{
                    Log.i("adapterOp","addFav");
                    mData.get(position).isFaved = true;
                    CacheUtils.saveFavEventCache(mData.get(position), favedEvents, mRawData, position);
                    EventListAdapter.this.notifyDataSetChanged();
                }
            }
        });
//        String endTime="";
        return convertView;
    }

    class ViewHolder {
        TextView mTitle;
        TextView mSponsor;
        TextView mDate;
        TextView mTime;
        TextView mDuration;
        //        TextView mEndTime;
        TextView mFollower;
        TextView mLocation;
        ImageView mFav;
        boolean mFaved = false;
    }
}
