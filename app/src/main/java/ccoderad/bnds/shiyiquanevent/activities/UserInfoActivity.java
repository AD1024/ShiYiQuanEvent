package ccoderad.bnds.shiyiquanevent.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import ccoderad.bnds.shiyiquanevent.beans.ClubModel;
import ccoderad.bnds.shiyiquanevent.beans.EventBean;
import ccoderad.bnds.shiyiquanevent.beans.UserModel;
import ccoderad.bnds.shiyiquanevent.global.PreferencesConstants;
import ccoderad.bnds.shiyiquanevent.global.URLConstants;
import ccoderad.bnds.shiyiquanevent.global.json.JsonConstants;
import ccoderad.bnds.shiyiquanevent.global.json.UserInfoConstants;
import ccoderad.bnds.shiyiquanevent.utils.PreferenceUtils;
import ccoderad.bnds.shiyiquanevent.utils.ToastUtil;
import ccoderad.bnds.shiyiquanevent.utils.Utils;


public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean loadSelfData = true;

    private TextView mUserName;
    private Button mLogout;
    private TextView mUserEmail;
    private TextView mUserRegTime;
    private TextView mUserVisCount;
    private SimpleDraweeView mAvatar;

    // ActionCard
    CardView mUserJoin;
    CardView mUserFollow;

    private RequestQueue mRequestQueue;

    private List<EventBean> mUserEvent;
    private List<ClubModel> mUserFollowClub;
    private List<ClubModel> mUserJoinClub;
    private List<UserModel> mFriend;


    private List<ClubModel> getClubList(JSONArray array) {
        List<ClubModel> ret = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); ++i) {
                JSONObject obj = array.getJSONObject(i);
                JSONObject data = obj.getJSONObject(JsonConstants.DATA_TAG);
                ClubModel club = new ClubModel();
                String position = obj.getString(UserInfoConstants.UserClub.POSITION_TAG);
                String memberNum = Utils.Int2String(obj.getInt(UserInfoConstants.UserClub.MEMBER_NUM_TAG));
                String followerNum = Utils.Int2String(obj.getInt(UserInfoConstants.UserClub.FOLLOWER_NUM_TAG));
                String clubName = data.getString(UserInfoConstants.UserClub.FULL_NAME_TAG);
                String clubSimpName = data.getString(UserInfoConstants.UserClub.SIMP_NAME_TAG);
                String simpIntro = data.getString(UserInfoConstants.UserClub.SIMP_INTRO_TAG);
                String avatarUrl = URLConstants.HOME_URL_WITHOUT_DASH
                        + obj.getJSONObject("avatar").getString("large");
                club.club_name = clubName;
                club.status.add(position);
                club.memberCount = memberNum;
                club.followerCount = followerNum;
                club.simpIntro = simpIntro;
                club.LargeAvatarURL = avatarUrl;
                club.sname = clubSimpName;
                ret.add(club);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /*
    * Parse user information in json format
    * */
    private boolean parseUserInfo(String jsonString) {
        try {
            JSONObject userData = new JSONObject(jsonString);
            String visCount = Utils
                    .Int2String(userData
                            .getInt(UserInfoConstants.ContactUser.VIS_COUNT_TAG));
            String regTime = userData.getString(UserInfoConstants.ContactUser.REG_TIME_TAG);
            mUserRegTime.setText(regTime);
            mUserVisCount.setText(visCount);
            Log.i("userData", userData.toString());
            if (userData.has(UserInfoConstants.FRIEND_TAG)) {
                JSONArray contactUsers = userData.getJSONArray(UserInfoConstants.FRIEND_TAG);
                for (int i = 0; i < contactUsers.length(); ++i) {
                    UserModel user = new UserModel();
                    JSONObject conUser = contactUsers.getJSONObject(i);
                    JSONObject data = conUser.getJSONObject(JsonConstants.DATA_TAG);
                    String nickName = conUser.getString(UserInfoConstants.ContactUser.NICKNAME_TAG);
                    String firstName = data.getString(UserInfoConstants.ContactUser.FIRST_NAME_TAG);
                    String lastName = data.getString(UserInfoConstants.ContactUser.LAST_NAME_TAG);
                    String avatarUrl = data.getJSONObject(UserInfoConstants
                            .ContactUser.AVATAR_TAG)
                            .getString("large");
                    user.firstName = firstName;
                    user.lastName = lastName;
                    user.nickName = nickName;
                    user.avatarUrl = avatarUrl;
                    user.userHome = URLConstants.HOME_URL + "user/" + nickName;
                    mFriend.add(user);
                }
            }
            if (userData.has(UserInfoConstants.FOLLOW_CLUB_TAG)) {
                JSONArray followClub = userData.getJSONArray(UserInfoConstants.FOLLOW_CLUB_TAG);
                mUserFollowClub = getClubList(followClub);
            }
            if (userData.has(UserInfoConstants.CLUB_JOIN_TAG)) {
                JSONArray joinClub = userData.getJSONArray(UserInfoConstants.CLUB_JOIN_TAG);
                mUserJoinClub = getClubList(joinClub);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = Volley.newRequestQueue(this);
        ToastUtil.initialize(this);
        setContentView(R.layout.activity_user_info);

        Intent data = getIntent();
        String userUrl = data.getStringExtra("USER_NAME");
        if (userUrl != null) {
            loadSelfData = false;
        }
        mUserName = (TextView) findViewById(R.id.user_info_userName);
        mUserEmail = (TextView) findViewById(R.id.user_info_userEmail);
        mLogout = (Button) findViewById(R.id.user_info_logout);
        mAvatar = (SimpleDraweeView) findViewById(R.id.user_info_avatar);
        mUserRegTime = (TextView) findViewById(R.id.user_info_reg_time);
        mUserVisCount = (TextView) findViewById(R.id.user_info_vis_count);
        mUserJoin = (CardView) findViewById(R.id.user_info_join_club);
        mUserFollow = (CardView) findViewById(R.id.user_info_follow_club);

        mUserFollowClub = new ArrayList<>();
        mUserEvent = new ArrayList<>();
        mFriend = new ArrayList<>();
        mUserJoinClub = new ArrayList<>();

        mUserFollow.setOnClickListener(this);
        mUserJoin.setOnClickListener(this);

        SharedPreferences SettingPref = getSharedPreferences(PreferencesConstants.SETTING_PREF, MODE_PRIVATE);
        SharedPreferences userInfo =
                getSharedPreferences(PreferencesConstants.LOGIN_INFO, MODE_PRIVATE);
        if (loadSelfData) {
            String userName = userInfo.getString(PreferencesConstants.USER_REAL_NAME_TAG, "Dummy");
            String userEmail = userInfo.getString(PreferencesConstants.USER_EMAIL_TAG, "DummyEmail");
            String nickName = userInfo.getString(PreferencesConstants.USER_NICK_NAME_TAG, "Dummy");
            boolean isHighQuality = SettingPref.getBoolean(PreferencesConstants.SETTING_HIGH_QUALITY_AVATAR_TAG, false);
            String userAvatarUrl = userInfo.getString(isHighQuality ?
                            PreferencesConstants.USER_RAW_AVATAR_URL_TAG
                            : PreferencesConstants.USER_AVATAR_URL_TAG
                    , "NULL");
            userAvatarUrl = URLConstants.HOME_URL_WITHOUT_DASH + userAvatarUrl;

            mUserName.setText("登录账户:" + userName);
            mUserEmail.setText("绑定邮箱:" + userEmail);
            mAvatar.setImageURI(Uri.parse(userAvatarUrl));
            mLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CookieSyncManager.createInstance(UserInfoActivity.this);
                    CookieManager manager = CookieManager.getInstance();
                    manager.removeAllCookie();
                    SharedPreferences.Editor del = getSharedPreferences(PreferencesConstants.LOGIN_INFO, MODE_PRIVATE).edit();
                    PreferenceUtils.Remove(UserInfoActivity.this, PreferencesConstants.LOGIN_INFO
                            , new String[]{PreferencesConstants.USER_AVATAR_URL_TAG
                                    , PreferencesConstants.USER_EMAIL_TAG
                                    , PreferencesConstants.USER_NICK_NAME_TAG
                                    , PreferencesConstants.USER_REAL_NAME_TAG});
                    del.putBoolean("Logined", false);
                    del.apply();
                    getSharedPreferences(PreferencesConstants.HOST_ID_PREF, MODE_PRIVATE)
                            .edit()
                            .remove(PreferencesConstants.HOST_ID_TAG)
                            .apply();
                    setResult(8091);
                    finish();
                }
            });
        } else {
            mLogout.setVisibility(View.GONE);
        }
        String nickName = loadSelfData ? userInfo.getString(PreferencesConstants.USER_NICK_NAME_TAG, "Dummy")
                : userUrl;
        /*
        * Download UserInfo
        * */
        String userHome = Utils.makeRequest("user/" + nickName
                , new String[]{"user-agent"}
                , new String[]{URLConstants.USER_AGENT});
        Log.i("UserInfo", userHome);
        StringRequest request = new StringRequest(Request.Method.GET, userHome, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseUserInfo(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("UserInfo", "ERROR WHILE FETCHING DATA");
                ToastUtil.makeText("请检查网络连接", true);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> extraHeader = new HashMap<>();
                extraHeader.put("Json", "true");
                return extraHeader;
            }
        };
        mRequestQueue.add(request);
    }

    @Override
    public void onClick(View v) {
        Intent it;
        switch (v.getId()) {
            case R.id.user_info_join_club:
                if (!Utils.isNetWorkAvailable(this)) {
                    ToastUtil.makeText("请先连接互联网", false);
                }
                if (mUserJoinClub.size() == 0) {
                    ToastUtil.makeText("加载中", false);
                    break;
                }
                it = new Intent(this, ClubSimpListActivity.class);
                it.putExtra("IntentType", "join");
                it.putExtra("clubData", (Serializable) mUserJoinClub);
                startActivity(it);
                break;
            case R.id.user_info_follow_club:
                if (!Utils.isNetWorkAvailable(this)) {
                    ToastUtil.makeText("请先连接互联网", false);
                }
                if (mUserFollowClub.size() == 0) {
                    ToastUtil.makeText("加载中", false);
                    break;
                }
                it = new Intent(this, ClubSimpListActivity.class);
                it.putExtra("IntentType", "follow");
                it.putExtra("clubData", (Serializable) mUserFollowClub);
                startActivity(it);
                break;
        }
    }
}
