package ccoderad.bnds.shiyiquanevent.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.util.LruCache;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.mingle.sweetpick.SweetSheet;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.Hashtable;
import java.util.List;

import ccoderad.bnds.shiyiquanevent.Beans.ClubModel;
import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.utils.ImageTools;


/**
 * Created by CCoderAD on 16/5/12.
 */
public class ClubChoiceAdapter extends BaseAdapter {
    private final String HOME_URL = "http://www.shiyiquan.net/";
    LayoutInflater mInflater;
    List<ClubModel> mData;
    SweetSheet mParent;
    WebView outer;
    DisplayImageOptions myOption;
    Context Parent;
    private LruCache<String,Bitmap> mCache;
    public ClubChoiceAdapter(Context context, List<ClubModel> datas, SweetSheet parent, WebView wb, DisplayImageOptions cacheOption){
        Fresco.initialize(context);
        Parent = context;
        mInflater = LayoutInflater.from(context);
        mData = datas;
        mParent = parent;
        outer = wb;
        mCache = new LruCache<>((int) (Runtime.getRuntime().freeMemory()/4));
        myOption = cacheOption;
    }

    public LruCache<String,Bitmap> getCache(){
        return this.mCache;
    }

    public void passCache(LruCache<String,Bitmap> mData){
        this.mCache=mData;
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
        ViewHolder vh;
        if(convertView==null){
            vh = new ViewHolder();
            convertView= mInflater.inflate(R.layout.club_chat_choice_list_item,null);
            vh.tvClubname = (TextView) convertView.findViewById(R.id.chat_club_name);
            vh.goToChat = (ImageButton) convertView.findViewById(R.id.go_to_chat);
            vh.goToShare = (ImageButton) convertView.findViewById(R.id.go_to_share);
            vh.mBackGround = (ImageView) convertView.findViewById(R.id.choice_list_background);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        final ImageView mImage = vh.mBackGround;
        mImage.setTag(mData.get(position).LargeAvatarURL);
        final TextView mText = vh.tvClubname;
        final String key = mData.get(position).LargeAvatarURL;
        if(mCache.get(mData.get(position).LargeAvatarURL)==null) {
            ImageLoader.getInstance()
                    .loadImage(mData.get(position).LargeAvatarURL, myOption, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            ImageTools tool = new ImageTools();
                            Log.i("Start Analyze:", s);
                            Bitmap set = tool.fastblur(new ImageTools().CompressBitmap(bitmap, Bitmap.CompressFormat.PNG), 5);
                            mCache.put(key,set);
                            if (mImage.getTag().equals(s)) {
                                mImage.setImageBitmap(set);
                                if (tool.isDeepColor(set)) {
                                    mText.setTextColor(Color.WHITE);
                                }
                            }
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {

                        }
                    });
        }else{
            mImage.setImageBitmap(mCache.get(key));
            if(new ImageTools().isDeepColor(mCache.get(key))){
                mText.setTextColor(Color.WHITE);
            }else{
                mText.setTextColor(Color.rgb(120,120,120));
            }
        }
        vh.tvClubname.setText(mData.get(position).club_name);
        vh.goToChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mParent.isShow()) {
                    mParent.dismiss();
                }
                Hashtable<EncodeHintType,String> hint = new Hashtable<EncodeHintType, String>();
                hint.put(EncodeHintType.CHARACTER_SET,"UTF-8");
                BitMatrix qr=null;
                try {
                    qr = new MultiFormatWriter().encode(HOME_URL+"club/"+mData.get(position).sname, BarcodeFormat.QR_CODE,800,800,hint);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                ImageTools tools = new ImageTools();
                View vv = mInflater.inflate(R.layout.alert_club_qr,null);
                TextView tvCbName = (TextView) vv.findViewById(R.id.alert_club_qr_club_name);
                ImageView ivQR = (ImageView) vv.findViewById(R.id.alert_club_qr_QR_CODE);
                ivQR.setImageBitmap(tools.toBitmap(qr));
                tvCbName.setText(mData.get(position).club_name);
                new AlertDialog.Builder(Parent).setView(vv).show();
                //outer.loadUrl(HOME_URL + "user/" + "?redirect=sender_div");
            }
        });
        vh.goToShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mParent.isShow()) {
                    mParent.dismiss();
                }
                outer.loadUrl(HOME_URL + "club/" + mData.get(position).sname + "/#share_div");
            }
        });
        vh.tvClubname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outer.loadUrl(HOME_URL + "club/" + mData.get(position).sname + "/#event_div");
                mParent.dismiss();
            }
        });
        return convertView;
    }

    class ViewHolder{
        TextView tvClubname;
        ImageButton goToChat;
        ImageButton goToShare;
        ImageView mBackGround;
    }

}
