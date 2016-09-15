package ccoderad.bnds.shiyiquanevent.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.TextView;

import ccoderad.bnds.shiyiquanevent.R;

public class UserInfoActivity extends AppCompatActivity {

    private TextView mUserName;
    private Button mLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        mUserName = (TextView) findViewById(R.id.user_info_userEmail);
        mLogout = (Button) findViewById(R.id.user_info_logout);
        String userName = getSharedPreferences("LoginInfo",MODE_PRIVATE).getString("userName",null);
        mUserName.setText("登录账户:"+userName);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CookieSyncManager.createInstance(UserInfoActivity.this);
                CookieManager manager = CookieManager.getInstance();
                manager.removeAllCookie();
                SharedPreferences.Editor del = getSharedPreferences("LoginInfo",MODE_PRIVATE).edit();
                del.remove("userName");
                del.putBoolean("Logined",false);
                del.apply();
                setResult(8091);
                finish();
            }
        });
    }
}
