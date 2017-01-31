package ccoderad.bnds.shiyiquanevent.activities;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.global.PreferencesConstants;
import ccoderad.bnds.shiyiquanevent.global.URLConstants;
import ccoderad.bnds.shiyiquanevent.utils.PreferenceUtils;

public class UserInfoActivity extends AppCompatActivity {

    private TextView mUserName;
    private Button mLogout;
    private TextView mUserEmail;
    private SimpleDraweeView mAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        mUserName = (TextView) findViewById(R.id.user_info_userName);
        mUserEmail = (TextView) findViewById(R.id.user_info_userEmail);
        mLogout = (Button) findViewById(R.id.user_info_logout);
        mAvatar = (SimpleDraweeView) findViewById(R.id.user_info_avatar);

        SharedPreferences SettingPref = getSharedPreferences(PreferencesConstants.SETTING_PREF, MODE_PRIVATE);
        SharedPreferences userInfo =
                getSharedPreferences(PreferencesConstants.LOGIN_INFO, MODE_PRIVATE);
        String userName = userInfo.getString(PreferencesConstants.USER_REAL_NAME_TAG, "Dummy");
        String userEmail = userInfo.getString(PreferencesConstants.USER_EMAIL_TAG, "DummyEmail");
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
                // del.remove("userName");
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
    }
}
