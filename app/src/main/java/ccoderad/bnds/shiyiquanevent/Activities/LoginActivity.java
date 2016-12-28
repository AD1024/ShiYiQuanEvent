package ccoderad.bnds.shiyiquanevent.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ccoderad.bnds.shiyiquanevent.Global.URLConstances;
import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.utils.Utils;


public class LoginActivity extends AppCompatActivity implements OnClickListener {

    private final String REQ_URL="http://shiyiquan.net/api/login";
    private final String HOME_URL="http://shiyiquan.net/";


    private AutoCompleteTextView mUserName;
    private EditText mPassword;
    private Button mLogin;
    private Button mSignUp;
    private ProgressBar mPgBar;
    private SharedPreferences mLoginStorage;
    private File mUserInfoStorage;
    private SharedPreferences.Editor mLoginStorageEditor;
    private Pattern mEmailJudger;
    private RequestQueue mReqQueue;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mReqQueue = Volley.newRequestQueue(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InitView();
        mSignUp.setOnClickListener(this);
        mLogin.setOnClickListener(this);
    }

    private void InitView(){
        mUserName = (AutoCompleteTextView) findViewById(R.id.login_username);
        mPassword = (EditText) findViewById(R.id.login_password);
        mLogin = (Button) findViewById(R.id.login_signIn);
        mSignUp = (Button) findViewById(R.id.login_register);
        mPgBar = (ProgressBar) findViewById(R.id.login_progress);
        mLogin = (Button) findViewById(R.id.login_signIn);
        mLoginStorage = getSharedPreferences("LoginInfo",MODE_PRIVATE);
        mLoginStorageEditor = mLoginStorage.edit();
        mUserInfoStorage = Utils.getCacheFile(this,"userInfo");
        if(!mUserInfoStorage.exists()) mUserInfoStorage.mkdir();
        mEmailJudger = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@" +
                "((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|" +
                "(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
    }

    private boolean judgeEmail(){
        String EmailData = mUserName.getText().toString();
        Matcher userNameJudger = mEmailJudger.matcher(EmailData);
        return userNameJudger.matches();
    }

    private boolean judgePasswordValidation(){
        String passData = mPassword.getText().toString();
        if(passData.isEmpty() || passData.length()<3) return false;
        else return true;
    }
    /*
    * @api: password
    *       username
    *       user-agent(secrete key)
    * */
    private boolean attemptLogin(){

        if(!judgeEmail()){
            mUserName.setError("用户名格式错误");
            mPgBar.setVisibility(View.GONE);
            return false;
        }
        if(!judgePasswordValidation()) {
            mPassword.setError("密码有问题");
            mPgBar.setVisibility(View.GONE);
            return false;
        }
        final String userName = mUserName.getText().toString();
        final String password = mPassword.getText().toString();
        Log.i("REQ",REQ_URL + "?username=" + userName + "&password=" + password + "&user-agent=" + URLConstances.USER_AGENT);
        StringRequest LoginReq = new StringRequest(Request.Method.GET,
                REQ_URL + "?username=" + userName + "&password=" + password + "&user-agent=" + URLConstances.USER_AGENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("fail")){
                    mPassword.setError("密码错误");
                    mPassword.setText("");
                    mPgBar.setVisibility(View.GONE);
                }else{
                    Log.i("REP",response);
                    parseJson(response,userName,password);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this,"登录失败，请检查网络",Toast.LENGTH_LONG).show();
                mPgBar.setVisibility(View.GONE);
            }
        });
        mReqQueue.add(LoginReq);
        return true;
    }

    private void parseJson(String jsonString,String userName,String password){
        try {
            JSONObject reqData = new JSONObject(jsonString);
            if(reqData.getString("sessionid").equals("None")){
                new Handler().postDelayed(null,1000);
                InputStream is = new URL(REQ_URL
                        + "?username=" + userName
                        + "&password=" + password
                        + "&user-agent=" + URLConstances.USER_AGENT)
                        .openStream();
                jsonString=Utils.ReadStringFromInputStream(is);
                is.close();
                Log.i("Logining","Logining");
            }
            Log.i("Logining","Logining2");
            reqData = new JSONObject(jsonString);
            mLoginStorageEditor.putBoolean("Logined",true);
            mLoginStorageEditor.putString("userName",userName);
            mLoginStorageEditor.putString("cookiePath",reqData.getString("path"));
            mLoginStorageEditor.putString("cookieSessionId",reqData.getString("sessionid"));
            mLoginStorageEditor.putString("cookieExpireTime",reqData.getString("expires"));
            mLoginStorageEditor.putBoolean("cookieHttpOnly",reqData.getBoolean("httpOnly"));
            mLoginStorageEditor.putBoolean("cookieNeedSync",true);
            mLoginStorageEditor.apply();
            mPgBar.setVisibility(View.GONE);
            setResult(8081);
            Log.i("Login Finish","Done");
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.login_register:
                Toast.makeText(this,"App暂时不支持注册功能，请前往官网注册",Toast.LENGTH_LONG).show();
                break;
            case R.id.login_signIn:
                mPgBar.setVisibility(View.VISIBLE);
                Toast.makeText(this,"登录中...",Toast.LENGTH_SHORT).show();
                attemptLogin();
                break;
        }
    }
}

