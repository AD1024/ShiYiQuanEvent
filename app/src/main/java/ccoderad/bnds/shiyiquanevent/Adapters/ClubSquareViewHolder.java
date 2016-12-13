package ccoderad.bnds.shiyiquanevent.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import ccoderad.bnds.shiyiquanevent.Listeners.ClubSuareItemClickListener;
import ccoderad.bnds.shiyiquanevent.R;

/**
 * Created by CCoderAD on 2016/10/28.
 */

public class ClubSquareViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView ClubName;
    public TextView ClubIntro;
    public SimpleDraweeView ClubAvatar;
    public ClubSuareItemClickListener mListener;


    public ClubSquareViewHolder(View itemView,ClubSuareItemClickListener listener) {
        super(itemView);
        this.mListener = listener;
        ClubName = (TextView) itemView.findViewById(R.id.club_square_info_name);
        ClubIntro = (TextView) itemView.findViewById(R.id.club_square_info_description);
        ClubAvatar = (SimpleDraweeView) itemView.findViewById(R.id.club_square_info_avatar);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(mListener!=null){
            mListener.onClubItemClick(v,getPosition());
        }
    }
}
