package ccoderad.bnds.shiyiquanevent.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.beans.ClubModel;

/**
 * Created by CCoderAD on 16/5/12.
 */
public class ClubListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<ClubModel> mData;
    private LinearLayout loadingIndicator;

    public ClubListAdapter(Context context, List<ClubModel> clubData) {
        mInflater = LayoutInflater.from(context);
        mData = clubData;
    }

    public void setLoadingIndicator(LinearLayout indicator){
        loadingIndicator = indicator;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if(mData.size() !=0 ){
            loadingIndicator.setVisibility(View.GONE);
        }
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
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = mInflater.inflate(R.layout.club_list_item, null);
            vh.tvFname = (TextView) convertView.findViewById(R.id.club_list_item_name);
            vh.tvStatus = (TextView) convertView.findViewById(R.id.club_list_item_status);
            vh.avatar = (SimpleDraweeView) convertView.findViewById(R.id.club_list_avatar);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tvFname.setText(mData.get(position).club_name);
        String textStatus = "";
        List<String> mStatus = mData.get(position).status;
        int i;
        for (i = 0; i < mStatus.size() - 1; ++i) {
            textStatus = textStatus + mStatus.get(i) + " | ";
        }
        textStatus = textStatus + mStatus.get(i);
        vh.tvStatus.setText(textStatus);
        vh.avatar.setImageURI(Uri.parse(mData.get(position).mediumAvatarURL));

        return convertView;
    }

    class ViewHolder {
        TextView tvFname;
        TextView tvStatus;
        SimpleDraweeView avatar;
    }
}
