package ccoderad.bnds.shiyiquanevent.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.beans.MomentDataModel;
import ccoderad.bnds.shiyiquanevent.global.URLConstants;
import ccoderad.bnds.shiyiquanevent.listeners.RecyclerViewItemClickListener;
import ccoderad.bnds.shiyiquanevent.utils.ViewTools;
import ccoderad.bnds.shiyiquanevent.viewholders.MomentListViewHolder;

/**
 * Created by CCoderAD on 2017/1/3.
 */

public class MomentListAdapter extends RecyclerView.Adapter<MomentListViewHolder> {

    private Context mParent;
    private List<MomentDataModel> mDataList;
    private RecyclerViewItemClickListener mListener;

    public MomentListAdapter(Context parent, List<MomentDataModel> DataList) {
        mParent = parent;
        mDataList = DataList;
    }

    public void setListItemOnClickListener(RecyclerViewItemClickListener listItemOnClickListener) {
        mListener = listItemOnClickListener;
    }

    @Override
    public MomentListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MomentListViewHolder holder =
                new MomentListViewHolder(ViewTools
                        .Inflate(mParent, R.layout.moment_list_item, parent), mListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(MomentListViewHolder holder, int position) {
        MomentDataModel data = mDataList.get(position);
        holder.mMajorAvatar.setImageURI(Uri.parse(URLConstants.HOME_URL_WITHOUT_DASH + data.majorAvatarURL));
        holder.tvMajor.setText(data.majorText);
        holder.tvAction.setText(data.majorText
                + data.bodyText + data.minorText + data.tailText);
        holder.tvTimeAgo.setText(data.timeAgo);
        holder.tvPlatform.setText(data.platformText);
        holder.tvMinor.setText(data.minorText);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}
