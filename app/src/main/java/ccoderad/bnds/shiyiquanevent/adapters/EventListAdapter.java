package ccoderad.bnds.shiyiquanevent.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.beans.EventBean;


/**
 * Created by CCoderAD on 16/3/17.
 */
public class EventListAdapter extends BaseAdapter {
    private List<EventBean> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private ViewHolder Vh;

    public EventListAdapter(Context context, List<EventBean> Data) {
        mData = Data;
        mContext = context;
        mInflater = LayoutInflater.from(context);
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
    public View getView(int position, View convertView, ViewGroup parent) {
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
