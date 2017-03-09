package ccoderad.bnds.shiyiquanevent.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.beans.BadgeModel;
import ccoderad.bnds.shiyiquanevent.utils.Utils;
import ccoderad.bnds.shiyiquanevent.utils.ViewTools;

/**
 * Created by CCoderAD on 2017/3/8.
 */

public class BadgeGridAdapter extends BaseAdapter {
    private List<BadgeModel> mData;
    Context parent;

    public BadgeGridAdapter(Context context, List<BadgeModel> data) {
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
        BadgeGridViewHolder holder;
        if (convertView == null) {
            holder = new BadgeGridViewHolder();
            convertView = ViewTools.Inflate(this.parent, R.layout.badge_item, null);
            holder.badgeTitle = (TextView) convertView.findViewById(R.id.badge_text);
            holder.mBackground = (CardView) convertView.findViewById(R.id.badge_container);
        }else{
            holder = (BadgeGridViewHolder) convertView.getTag();
        }
        holder.badgeTitle.setText(mData.get(position).name);
        int color = Utils.fromTextGetColor(mData.get(position).rank);
        if(color == Color.WHITE || color == Color.YELLOW){
            holder.badgeTitle.setTextColor(Color.BLACK);
        }
        holder.mBackground.setCardBackgroundColor(color);

        convertView.setTag(holder);
        return convertView;
    }

    private class BadgeGridViewHolder {
        TextView badgeTitle;
        CardView mBackground;
    }
}
