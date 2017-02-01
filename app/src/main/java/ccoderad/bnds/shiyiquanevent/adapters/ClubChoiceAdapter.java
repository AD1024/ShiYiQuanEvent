package ccoderad.bnds.shiyiquanevent.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.mingle.sweetpick.SweetSheet;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.Hashtable;
import java.util.List;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.beans.ClubModel;
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

    Postprocessor mImagePostProcessor;
    PipelineDraweeController mController;

    public ClubChoiceAdapter(Context context, List<ClubModel> datas, SweetSheet parent, WebView wb, DisplayImageOptions cacheOption) {
        Fresco.initialize(context);
        Parent = context;
        mInflater = LayoutInflater.from(context);
        mData = datas;
        mParent = parent;
        outer = wb;
        myOption = cacheOption;
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
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = mInflater.inflate(R.layout.club_chat_choice_list_item, null);
            vh.tvClubname = (TextView) convertView.findViewById(R.id.chat_club_name);
            vh.goToChat = (ImageButton) convertView.findViewById(R.id.go_to_chat);
            vh.goToShare = (ImageButton) convertView.findViewById(R.id.go_to_share);
            vh.mBackGround = (SimpleDraweeView) convertView.findViewById(R.id.choice_list_background);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final ImageView mImage = vh.mBackGround;
        mImage.setTag(mData.get(position).LargeAvatarURL);

        ImageRequest mImageRequest;
        // Configuration For Simple Drawee View
        mImagePostProcessor = new BasePostprocessor() {

            @Override
            public void process(Bitmap bitmap) {
                bitmap = ImageTools.fastblur(bitmap, 15);
            }

            @Override
            public String getName() {
//                return super.getName();
                return "BlurProcessor";
            }
        };
        mImageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(mData.get(position).LargeAvatarURL))
                .setPostprocessor(mImagePostProcessor)
                .build();
        mController = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setImageRequest(mImageRequest)
                .setOldController(vh.mBackGround.getController())
                .build();
        vh.mBackGround.setController(mController);

        vh.tvClubname.setText(mData.get(position).club_name);
        vh.goToChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mParent.isShow()) {
                    mParent.dismiss();
                }
                Hashtable<EncodeHintType, String> hint = new Hashtable<EncodeHintType, String>();
                hint.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                BitMatrix qr = null;
                try {
                    qr = new MultiFormatWriter().encode(HOME_URL + "club/" + mData.get(position).sname, BarcodeFormat.QR_CODE, 800, 800, hint);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                ImageTools tools = new ImageTools();
                View vv = mInflater.inflate(R.layout.alert_club_qr, null);
                TextView tvCbName = (TextView) vv.findViewById(R.id.alert_club_qr_club_name);
                ImageView ivQR = (ImageView) vv.findViewById(R.id.alert_club_qr_QR_CODE);
                ivQR.setImageBitmap(tools.toBitmap(qr));
                tvCbName.setText(mData.get(position).club_name);
                new AlertDialog.Builder(Parent).setView(vv).show();
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

    class ViewHolder {
        TextView tvClubname;
        ImageButton goToChat;
        ImageButton goToShare;
        SimpleDraweeView mBackGround;
    }

}
