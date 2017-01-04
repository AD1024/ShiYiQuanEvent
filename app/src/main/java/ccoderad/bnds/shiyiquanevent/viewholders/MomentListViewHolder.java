package ccoderad.bnds.shiyiquanevent.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.w3c.dom.Text;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.listeners.RecyclerViewItemClickListener;

/**
 * Created by CCoderAD on 2017/1/3.
 */

public class MomentListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public SimpleDraweeView mMajorAvatar;
    public TextView tvMajor;
    public TextView tvAction;
    public TextView tvTimeAgo;
    public TextView tvPlatform;
    public TextView tvMinor;
    public RecyclerViewItemClickListener mListener;

    public MomentListViewHolder(View itemView, RecyclerViewItemClickListener listener) {
        super(itemView);
        mListener = listener;
        tvMajor = (TextView) itemView.findViewById(R.id.moment_list_item_sponsor);
        tvAction = (TextView) itemView.findViewById(R.id.moment_list_item_action);
        mMajorAvatar = (SimpleDraweeView) itemView.findViewById(R.id.moment_list_item_avatar);
        tvTimeAgo = (TextView) itemView.findViewById(R.id.moment_list_item_time);
        tvPlatform = (TextView) itemView.findViewById(R.id.moment_list_item_platform);
        tvMinor = (TextView) itemView.findViewById(R.id.moment_list_item_minor);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(mListener != null){
            mListener.onClubItemClick(v,getPosition());
        }
    }
}
