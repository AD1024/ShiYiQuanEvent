package ccoderad.bnds.shiyiquanevent.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import ccoderad.bnds.shiyiquanevent.beans.ClubDetailModel;
import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.utils.Utils;

public class ClubInfoDetailActivity extends AppCompatActivity {


    private String CLUB_HOME;
    private TextView mTextViewClubName;
    private TextView mLikeNumberText;
    private TextView mFollowNumberText;
    private TextView mFullDescriptionText;

    private String mClubName;
    private String mFollowNumber;
    private String mFullDescription;
    private String mLikeNumber;

    void init(){
        mTextViewClubName = (TextView) findViewById(R.id.club_info_detail_club_name);
        mLikeNumberText = (TextView) findViewById(R.id.club_info_detail_like_number);
        mFollowNumberText = (TextView) findViewById(R.id.club_info_detail_follow_nuhmber);
        mFullDescriptionText = (TextView) findViewById(R.id.club_info_detail_full_description);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_info_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ClubDetailModel Data = (ClubDetailModel) getIntent().getSerializableExtra("ClubData");
        CLUB_HOME = Data.ClubHomePage;

        init();

        SimpleDraweeView Img = (SimpleDraweeView) findViewById(R.id.club_info_detail_avatar);
        Img.setImageURI(Uri.parse(Data.LargeAvatarURL));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.club_info_detail_home);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent IntentJump = new Intent(ClubInfoDetailActivity.this,MainBrowser.class);
                IntentJump.putExtra("QR_CONTENT",CLUB_HOME);
                Log.i("ClubURL",Data.ClubHomePage);
                startActivity(IntentJump);
            }
        });
        mClubName = Data.club_name;
        mFollowNumber = Utils.Int2String(Data.Followee.size());
        mFullDescription = Data.ClubDescription;
        mLikeNumber = Utils.Int2String(Data.Like);
        setTitle(mClubName);

        mTextViewClubName.setText(mClubName);
        mFullDescriptionText.setText(mFullDescription);
        mFollowNumberText.setText(mFollowNumber);
        mLikeNumberText.setText(mLikeNumber);

    }
}
