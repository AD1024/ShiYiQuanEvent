package ccoderad.bnds.shiyiquanevent.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.beans.ClubModel;
import ccoderad.bnds.shiyiquanevent.utils.ViewTools;

/**
 * Created by CCoderAD on 2017/3/8.
 */

public class ClubSimpListAdapter extends BaseAdapter {

    private List<ClubModel> mData;
    private Context parent;

    public ClubSimpListAdapter(Context context, List<ClubModel> data) {
        mData = data;
        parent = context;
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
        ClubSimpListViewHolder holder;
        if (convertView == null) {
            holder = new ClubSimpListViewHolder();
            convertView = ViewTools.Inflate(this.parent, R.layout.club_simp_list_item, null);
            holder.clubAvatar = (SimpleDraweeView) convertView
                    .findViewById(R.id.club_simp_item_avatar);
            holder.clubName = (TextView) convertView.findViewById(R.id.club_simp_item_name);
            holder.clubSimpIntro = (TextView) convertView.findViewById(R.id.club_simp_item_intro);
            holder.clubMember = (TextView) convertView.findViewById(R.id.club_simp_item_members);
            holder.clubFollower = (TextView) convertView.findViewById(R.id.club_simp_item_followers);
            holder.userPosition = (TextView) convertView.findViewById(R.id.club_simp_item_position);
        }else{
            holder = (ClubSimpListViewHolder) convertView.getTag();
        }
        ClubModel data = mData.get(position);
        holder.clubName.setText(data.club_name);
        holder.clubSimpIntro.setText(data.simpIntro);
        holder.userPosition.setText(data.status.get(0));
        holder.clubFollower.setText(data.followerCount);
        holder.clubMember.setText(data.memberCount);
        holder.clubAvatar.setImageURI(Uri.parse(data.LargeAvatarURL));
        convertView.setTag(holder);
        return convertView;
    }

    private class ClubSimpListViewHolder {
        TextView clubName;
        TextView clubSimpIntro;
        TextView clubMember;
        TextView clubFollower;
        TextView userPosition;
        SimpleDraweeView clubAvatar;
    }
}
