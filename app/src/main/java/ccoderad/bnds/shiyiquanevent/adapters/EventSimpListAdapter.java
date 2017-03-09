package ccoderad.bnds.shiyiquanevent.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.beans.EventBean;
import ccoderad.bnds.shiyiquanevent.utils.ViewTools;

/**
 * Created by CCoderAD on 2017/3/8.
 */

public class EventSimpListAdapter extends BaseAdapter {

    private List<EventBean> mData;
    private Context parent;

    public EventSimpListAdapter(Context context, List<EventBean> data) {
        this.mData = data;
        this.parent = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        EventSimpListViewHolder holder;
        if (convertView == null) {
            holder = new EventSimpListViewHolder();
            convertView = ViewTools.Inflate(this.parent, R.layout.event_simp_list_item, null);
            holder.Date = (TextView) convertView.findViewById(R.id.event_simp_list_item_date);
            holder.Time = (TextView) convertView.findViewById(R.id.event_simp_list_item_time);
            holder.Title = (TextView) convertView.findViewById(R.id.event_simp_list_item_title);
            holder.Content = (TextView) convertView.findViewById(R.id.event_simp_list_item_content);
            holder.Location = (TextView) convertView.findViewById(R.id.event_simp_list_item_location);
        }else{
            holder = (EventSimpListViewHolder) convertView.getTag();
        }
        EventBean data = mData.get(position);
        holder.Title.setText(data.eventTitle);
        holder.Date.setText(data.eventDate);
        holder.Time.setText(data.eventTime);
        holder.Content.setText(data.eventContent);
        holder.Location.setText(data.eventLocation);
        convertView.setTag(holder);
        return convertView;
    }

    private class EventSimpListViewHolder {
        public TextView Title, Date, Time, Location, Content;
    }
}
