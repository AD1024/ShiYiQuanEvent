package ccoderad.bnds.shiyiquanevent.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.adapters.BadgeGridAdapter;
import ccoderad.bnds.shiyiquanevent.beans.BadgeModel;
import ccoderad.bnds.shiyiquanevent.beans.EventBean;
import ccoderad.bnds.shiyiquanevent.beans.PresentationModel;
import ccoderad.bnds.shiyiquanevent.global.URLConstants;
import ccoderad.bnds.shiyiquanevent.global.json.ClubInfoConstants;
import ccoderad.bnds.shiyiquanevent.global.json.JsonConstants;
import ccoderad.bnds.shiyiquanevent.utils.MultiThreadUtil;
import ccoderad.bnds.shiyiquanevent.utils.ToastUtil;
import ccoderad.bnds.shiyiquanevent.utils.Utils;
import cn.bingoogolapple.bgabanner.BGABanner;

public class ClubInfoActivity extends AppCompatActivity {

    private BGABanner mPresentationBanner;
    private TextView mClubName, mSimpIntro,
            mHeader, mVice, mVisitors, mMembers, mFollowers;
    private TextView mDescription;
    private CardView mViewEvent;
    private TextView mBadgeIndicator;
    private GridView mBadgeGrid;
    private SimpleDraweeView mClubAvatar;
    private CardView mBadgeContainer;

    private String clubUrl;

    private List<BadgeModel> mBadge;
    private List<EventBean> mClubEvent;
    private List<PresentationModel> mPresentations;

    private RequestQueue requestQueue;

    private BadgeGridAdapter mBadgeGridAdapter;

    private void handleRawData(String response) {
        try {
            JSONObject clubData = new JSONObject(response);
            JSONArray badgeData = clubData.getJSONArray(ClubInfoConstants.BADGE_LIST_TAG);
            JSONArray eventData = clubData.getJSONArray(ClubInfoConstants.EVENT_LIST_TAG);
            JSONArray presentationData = clubData.getJSONArray(ClubInfoConstants.PRESENTATION_TAG);
            JSONObject detailData = clubData.getJSONObject("club");
            String clubHead = clubData.getString(ClubInfoConstants.HEAD_TAG);
            String clubVice = "无副社长";
            if(clubData.has(ClubInfoConstants.VICE_TAG)){
                clubVice = clubData.getString(ClubInfoConstants.VICE_TAG);
            }
            String clubAvatar = URLConstants.HOME_URL_WITHOUT_DASH
                    + clubData.getString(ClubInfoConstants.AVATAR_TAG);
            String followerNum = Utils.Int2String(clubData.getInt(ClubInfoConstants.FOLLOWER_TAG));
            String visitorNum = Utils.Int2String(clubData.getInt(ClubInfoConstants.VISITORS_TAG));
            String memberNum = Utils.Int2String(clubData.getInt(ClubInfoConstants.MEMBER_TAG));
            String fullIntro = detailData.getString(ClubInfoConstants.FULL_INTRO_TAG);
            String clubName = detailData.getString(ClubInfoConstants.FULL_NAME_TAG);
            String simpIntro = detailData.getString(ClubInfoConstants.SIMP_INTRO_TAG);
            mDescription.setText(fullIntro);
            mClubName.setText(clubName);
            mHeader.setText(clubHead);
            mVice.setText(clubVice);
            mVisitors.setText(visitorNum);
            mMembers.setText(memberNum);
            mFollowers.setText(followerNum);
            mClubAvatar.setImageURI(Uri.parse(clubAvatar));
            mSimpIntro.setText(simpIntro);

            for (int i = 0; i < badgeData.length(); ++i) {
                JSONObject badge = badgeData.getJSONObject(i);
                BadgeModel model = new BadgeModel();
                model.description = badge.getString(ClubInfoConstants.Badge.DESC);
                model.name = badge.getString(ClubInfoConstants.Badge.NAME);
                model.rank = badge.getString(ClubInfoConstants.Badge.RANK);
                mBadge.add(model);
            }

            for (int i = 0; i < eventData.length(); ++i) {
                EventBean bean = new EventBean();
                JSONObject event = eventData.getJSONObject(i);
                JSONObject data = event.getJSONObject(JsonConstants.DATA_TAG);
                bean.eventDate = event.getString(ClubInfoConstants.Event.DATE);
                bean.eventTime = event.getString(ClubInfoConstants.Event.TIME);
                bean.eventTitle = data.getString(ClubInfoConstants.Event.SUBJECT);
                bean.eventContent = data.getString(ClubInfoConstants.Event.CONTENT);
                bean.eventLocation = data.getString(ClubInfoConstants.Event.LOCATION);
                mClubEvent.add(bean);
            }

            for (int i = 0; i < presentationData.length(); ++i) {
                PresentationModel model = new PresentationModel();
                JSONObject presentation = presentationData.getJSONObject(i);
                model.title = presentation.getString(ClubInfoConstants.Presentation.TITLE);
                // Low quality image
                String rawLink = presentation.getString(ClubInfoConstants.Presentation.PIC_URL);
                int idx = rawLink.indexOf("large");
                String pre = rawLink.substring(0, idx);
                String post = rawLink.substring(idx + 5);
                model.imageUrl = URLConstants.HOME_URL_WITHOUT_DASH + pre + "exlarge" + post;
                mPresentations.add(model);
            }
            mPresentationBanner.setData(R.layout.presentation_banner_item, mPresentations, null);
            mBadgeGridAdapter = new BadgeGridAdapter(this, mBadge);
            mBadgeGrid.setAdapter(mBadgeGridAdapter);
            if(mBadge.size() == 0){
                mBadgeGrid.setVisibility(View.GONE);
                mBadgeContainer.setVisibility(View.GONE);
            }else{
                mBadgeIndicator.setVisibility(View.VISIBLE);
            }
            if(mPresentations.size() == 0){
                mPresentationBanner.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void renderView(String subUrl) {
        String reqUrl = Utils.makeRequest(subUrl, new String[]{"user-agent"}
                , new String[]{URLConstants.USER_AGENT});
        Log.i("ReqUrl", reqUrl);
        StringRequest request = new StringRequest
                (Request.Method.GET, reqUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                handleRawData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.makeText("出错啦，请检查网络连接", true);
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> extraHeader = new HashMap<>();
                extraHeader.put("Json", "true");
                return extraHeader;
            }
        };
        request.setRetryPolicy(MultiThreadUtil.createDefaultRetryPolicy());
        requestQueue.add(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ToastUtil.initialize(this);
        requestQueue = Volley.newRequestQueue(this);
        Fresco.initialize(this);
        setContentView(R.layout.activity_club_info);
        Intent it = getIntent();
        clubUrl = it.getStringExtra("clubUrl");
        setTitle("社团信息");
        // View by Id
        mPresentationBanner = (BGABanner) findViewById(R.id.activity_club_info_presentation_banner);
        mClubName = (TextView) findViewById(R.id.activity_club_info_name);
        mSimpIntro = (TextView) findViewById(R.id.activity_club_info_simp_intro);
        mHeader = (TextView) findViewById(R.id.activity_club_info_head);
        mVice = (TextView) findViewById(R.id.activity_club_info_vice);
        mVisitors = (TextView) findViewById(R.id.activity_club_info_visitor_num);
        mMembers = (TextView) findViewById(R.id.activity_club_info_member_num);
        mFollowers = (TextView) findViewById(R.id.activity_club_info_follower_num);
        mDescription = (TextView) findViewById(R.id.activity_club_intro_full_intro);
        mBadgeIndicator = (TextView) findViewById(R.id.activity_club_info_badge_indicator);
        mViewEvent = (CardView) findViewById(R.id.activity_club_info_event);
        mBadgeGrid = (GridView) findViewById(R.id.activity_club_info_badge_grid);
        mClubAvatar = (SimpleDraweeView) findViewById(R.id.actvity_club_info_avatar);
        mBadgeContainer = (CardView) findViewById(R.id.activity_club_info_badge_container);

        mBadge = new ArrayList<>();
        mClubEvent = new ArrayList<>();
        mPresentations = new ArrayList<>();

        mViewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ClubInfoActivity.this, EventSimpListActivity.class);
                it.putExtra("eventData", (Serializable) mClubEvent);
                it.putExtra("clubName", mClubName.getText().toString());
                startActivity(it);
            }
        });

        mPresentationBanner.setAdapter(new BGABanner.Adapter<CardView, PresentationModel>() {
            @Override
            public void fillBannerItem(BGABanner banner
                    , CardView itemView, PresentationModel model, int position) {
                SimpleDraweeView pic = (SimpleDraweeView) itemView
                        .findViewById(R.id.presentation_banner_item_picture);
                TextView bannerText = (TextView) itemView.findViewById(R.id.presentation_item_text);
                pic.setImageURI(Uri.parse(model.imageUrl));
                bannerText.setText(model.title);
            }
        });

        mBadgeGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(view, mBadge.get(position).description,Snackbar.LENGTH_SHORT).show();
            }
        });
        if(clubUrl.contains(URLConstants.HOME_URL)){
            renderView(clubUrl);
        }else {
            renderView("club/" + clubUrl);
        }
    }
}
