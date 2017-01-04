package ccoderad.bnds.shiyiquanevent.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import ccoderad.bnds.shiyiquanevent.beans.ClubDetailModel;
import ccoderad.bnds.shiyiquanevent.listeners.RecyclerViewItemClickListener;
import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.viewholders.ClubSquareViewHolder;

/**
 * Created by CCoderAD on 2016/10/27.
 */

public class ClubSquareAdapter extends RecyclerView.Adapter<ClubSquareViewHolder> {

    private List<ClubDetailModel> mDataList;
    private Context mParent;
    private LayoutInflater mInflater;
    private RecyclerViewItemClickListener mListener;

    public ClubSquareAdapter(Context context, List<ClubDetailModel> DataSet) {
        mParent = context;
        mDataList = DataSet;
        mInflater = LayoutInflater.from(context);
    }

    public void setOnClubItemClickListener(RecyclerViewItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public ClubSquareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ClubSquareViewHolder Holder =
                new ClubSquareViewHolder(mInflater
                        .inflate(R.layout.club_square_info_card, parent, false), mListener);
        return Holder;
    }

    @Override
    public void onBindViewHolder(ClubSquareViewHolder holder, int position) {
        ClubDetailModel data = mDataList.get(position);
        holder.ClubName.setText(data.club_name);
        holder.ClubIntro.setText(data.SimpleIntro);
        holder.ClubAvatar.setImageURI(Uri.parse(data.LargeAvatarURL));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}
