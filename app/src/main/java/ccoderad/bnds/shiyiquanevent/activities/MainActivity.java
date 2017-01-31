package ccoderad.bnds.shiyiquanevent.activities;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.zxing.BarcodeFormat;
import com.jakewharton.disklrucache.DiskLruCache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.adapters.EventListAdapter;
import ccoderad.bnds.shiyiquanevent.beans.EventBean;
import ccoderad.bnds.shiyiquanevent.broadcast.DownloadBroadcastReceiver;
import ccoderad.bnds.shiyiquanevent.db.DataBaseManager;
import ccoderad.bnds.shiyiquanevent.db.DatabaseHelper;
import ccoderad.bnds.shiyiquanevent.global.PreferencesConstants;
import ccoderad.bnds.shiyiquanevent.global.URLConstants;
import ccoderad.bnds.shiyiquanevent.utils.DownloadUtil;
import ccoderad.bnds.shiyiquanevent.utils.ImageTools;
import ccoderad.bnds.shiyiquanevent.utils.MD5Util;
import ccoderad.bnds.shiyiquanevent.utils.PreferenceUtils;
import ccoderad.bnds.shiyiquanevent.utils.ToastUtil;
import ccoderad.bnds.shiyiquanevent.utils.Utils;
import ccoderad.bnds.shiyiquanevent.utils.ViewTools;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {

    private static final String URL_PREFIX = "<!-- 安卓版本";
    final long MAX_MEM = 30 * ByteConstants.MB;
    final long MAX_LOW_MEM = 10 * ByteConstants.MB;
    final long MAX_VERY_LOW_MEM = 5 * ByteConstants.MB;
    final long MAX_STRING_CACHE = 3 * ByteConstants.MB;
    final int LOGIN_REQCODE = 8080;
    final int LOGOUT_REQCODE = 8090;
    private final String REQ_URL = URLConstants.HOME_URL + "api/?category=event&time=latest";
    private final String CACHE_FILE_NAME = "cacheEvent.json";
    private final String HOME_URL = URLConstants.HOME_URL;
    private String mNewVersionDownloadURL;

    private DiskLruCache mCache;
    private DiskCacheConfig mImageCacheConfig;

    private ImagePipelineConfig mImageCachePiplineConfig;
    private FloatingActionButton fab;

    private File cacheFile;
    private File favedEvents;
    private JSONArray mRawData;

    private List<EventBean> favd_stub;
    private List<EventBean> mData;

    private boolean Logined;
    private int mY = 0;
    private boolean isLongClicked = false;
    private boolean mIsConnected;
    private long time = 0;

    private View Nav_Header_stub;
    private ListView mListView;
    private SimpleDraweeView LoginClick;
    private SwipeRefreshLayout mRefersh;

    private GetEventTask mTask;
    private DatabaseHelper mDataBaseHelper;
    private boolean Paused = false;

    private SharedPreferences SettingPref;

    private RequestQueue mRequestQueue;

    private void ShowUpdateInfo() {
        final SharedPreferences versionInfo = getSharedPreferences("VersionInfo", MODE_PRIVATE);
        String version = versionInfo.getString("VersionCode", "NoInfo");
        View Header = ViewTools.Inflate(this, R.layout.update_info_header, null);
        View window = ViewTools.Inflate(this, R.layout.update_info_msg, null);
        TextView updateInfo = (TextView) window.findViewById(R.id.update_info_msg);
        updateInfo.setText(R.string.content_update_info);

        TextView headerText = (TextView) Header.findViewById(R.id.update_info_header_text);

        headerText.setTextSize(25f);

        headerText.setPadding(10, 10, 10, 10);

        int[] color = ImageTools.RandomColor();

        Header.setBackgroundColor(Color.rgb(color[0], color[1], color[2]));

        if (ImageTools.isDeepColor(color)) {
            headerText.setTextColor(Color.WHITE);
        } else {
            headerText.setTextColor(Color.BLACK);
        }

        if (!version.equals(URLConstants.CURRENT_VERSION)) {
            new AlertDialog.Builder(this)
                    .setView(window)
                    .setCustomTitle(Header)
                    .setPositiveButton("不再显示", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor edit = versionInfo.edit();
                            edit.putString("VersionCode", URLConstants.CURRENT_VERSION);
                            edit.apply();
                        }
                    })
                    .setNegativeButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        ToastUtil.initialize(this);
        mRequestQueue = Volley.newRequestQueue(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mIsConnected = isNetWorkAvailable();
        ShowUpdateInfo();

        /*
        * Register Download brodcast listener for downloading the new version file
        * */
        DownloadBroadcastReceiver mReceiver = new DownloadBroadcastReceiver();
        registerReceiver(mReceiver
                , new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        mDataBaseHelper = DataBaseManager.getInstance(this);

        SettingPref = getSharedPreferences(PreferencesConstants.SETTING_PREF, MODE_PRIVATE);
        final SharedPreferences LoginInfo = getSharedPreferences(PreferencesConstants.LOGIN_INFO, MODE_PRIVATE);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        View nav_header = navView.getHeaderView(0);
        Nav_Header_stub = nav_header;

        TextView userName = (TextView) nav_header.findViewById(R.id.user_name_holder);
        Logined = LoginInfo.getBoolean(PreferencesConstants.LOGIN_STATUS, false);
        if (Logined) {
            String set = "登录账户:" + LoginInfo.getString(PreferencesConstants.USER_REAL_NAME_TAG, "点击登录");
            userName.setText(set);
        } else {
            String set = userName.getText().toString() + "-" + "点击登录";
            userName.setText(set);
        }

        LoginClick = (SimpleDraweeView) nav_header.findViewById(R.id.ic_logo);
        LoginClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Logined) {
                    startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), LOGIN_REQCODE);
                } else {
                    startActivityForResult(new Intent(MainActivity.this, UserInfoActivity.class), LOGOUT_REQCODE);
                }
            }
        });
        boolean isHighQuality = SettingPref.getBoolean(PreferencesConstants.SETTING_HIGH_QUALITY_AVATAR_TAG, false);
        String UserAvatarURL = LoginInfo.getString(isHighQuality ?
                        PreferencesConstants.USER_RAW_AVATAR_URL_TAG
                        : PreferencesConstants.USER_AVATAR_URL_TAG
                , "NULL");
        if (!UserAvatarURL.equals("NULL") && Logined) {
            Log.i("AVATAR", UserAvatarURL);
            LoginClick.setImageURI(Uri.parse(URLConstants.HOME_URL_WITHOUT_DASH + UserAvatarURL));
        } else {
            /*
            * Reset the SimpleDrawee View in the nav header
            * */
            Resources r = this.getResources();
            LoginClick.setImageURI(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + r.getResourcePackageName(R.mipmap.logo_2)
                    + "/"
                    + r.getResourceTypeName(R.mipmap.logo_2)
                    + "/"
                    + r.getResourceEntryName(R.mipmap.logo_2)));
        }

        initDiskLruCache();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        setTitle("十一活动");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.smoothScrollToPosition(0);
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mListView = (ListView) findViewById(R.id.event_list);
        mListView.setOnItemClickListener(this);
        favd_stub = new ArrayList<>();

        //ListView Motion Listener:
        /*
        * It is used to detect the direction of sliding of the listview
        * */
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_MOVE:
                        int y = (int) event.getY();
                        if (y > mY) {
                            fab.show();
                        } else {
                            fab.hide();
                        }
                        mY = y;
                }
                return false;
            }
        });


        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                isLongClicked = true;
                ImageView alter = (ImageView) view.findViewById(R.id.event_list_item_fav);
                if (mData.get(position).isFaved) {
                    Snackbar.make(view, "已取消收藏" + mData.get(position).eventTitle,
                            Snackbar.LENGTH_SHORT).show();
                    mData.get(position).isFaved = false;
                    alter.setImageResource(R.drawable.ic_favorite_border);
                    InputStream is;
                    String saved;
                    try {
                        is = new FileInputStream(favedEvents);
                        saved = ReadStringFromInputStream(is);
                        JSONArray array;
                        if (saved.isEmpty()) {
                            array = new JSONArray();
                        } else {
                            array = new JSONArray(saved);
                        }
                        EventBean del = mData.get(position);
                        JSONObject obj;
                        JSONObject data;
                        for (int i = 0; i < array.length(); i++) {
                            if (array.get(i).equals(null)) continue;
                            obj = array.getJSONObject(i);
                            data = obj.getJSONObject("data");
                            if (data.getString("content").equals(del.eventContent)
                                    && obj.getString("sponsor_fname").equals(del.sponsorName)) {
                                array.remove(i);
                                break;
                            }
                        }
                        saved = array.toString();
                        PrintStream stream = new PrintStream(new FileOutputStream(favedEvents));
                        favedEvents.createNewFile();
                        stream.print(saved);
                        LoadFav();
                        stream.close();
                        is.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Snackbar.make(view, "已收藏" + mData.get(position).eventTitle,
                            Snackbar.LENGTH_SHORT).show();
                    mData.get(position).isFaved = true;
                    alter.setImageResource(R.drawable.ic_favorite);
                    if (!favedEvents.exists()) {
                        try {
                            favedEvents.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    InputStream is;
                    String saved;
                    try {
                        is = new FileInputStream(favedEvents);
                        saved = ReadStringFromInputStream(is);
                        JSONArray array;
                        if (saved.isEmpty()) {
                            array = new JSONArray();
                        } else {
                            array = new JSONArray(saved);
                        }
                        array.put(position, mRawData.get(position));
                        saved = array.toString();
                        favedEvents.createNewFile();
                        PrintStream printer = new PrintStream(new FileOutputStream(favedEvents));
                        printer.print(saved);
                        Log.i("Fav", "Fav_Saved!");
                        printer.close();
                        is.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        mRefersh = (SwipeRefreshLayout) findViewById(R.id.event_refresh);
        mRefersh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Cancel current asynctack and start a new fetching
                mIsConnected = isNetWorkAvailable();
                if (mIsConnected) {
                    mTask.cancel(true);
                    mTask = new GetEventTask();
                    mTask.execute(REQ_URL);
                } else {
                    ReadFromCache();
                    mRefersh.setRefreshing(false);
                }
            }
        });

        mTask = new GetEventTask();

        mData = new ArrayList<>();
        mRefersh.setColorSchemeColors(Color.rgb(10, 180, 226), Color.rgb(3, 133, 167));
        mRefersh.setSize(25);

        mImageCacheConfig = DiskCacheConfig.newBuilder(this)
                .setBaseDirectoryPath(Environment.getExternalStorageDirectory().getAbsoluteFile())
                .setBaseDirectoryName("ShiyiquanEvent")
                .setMaxCacheSize(MAX_MEM)
                .setMaxCacheSizeOnLowDiskSpace(MAX_LOW_MEM)
                .setMaxCacheSizeOnVeryLowDiskSpace(MAX_VERY_LOW_MEM)
                .build();

        mImageCachePiplineConfig = ImagePipelineConfig.newBuilder(this).setMainDiskCacheConfig(mImageCacheConfig).build();
        cacheFile = Utils.getCacheFile(this, "event");
        if (!cacheFile.exists())
            cacheFile.mkdir();
        favedEvents = new File(cacheFile, "FavedEvents.json");

        if (!favedEvents.exists()) try {
            favedEvents.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LoadFav();

        if (mIsConnected) {
            mTask.execute(REQ_URL);
            CheckUpdate();
        } else {
            ReadFromCache();
        }
    }

    /*
    * Check App update
    * */
    private String CheckUpdate() {
        HTMLFetcher fetcher = new HTMLFetcher();
        fetcher.execute(HOME_URL);
        return null;
    }

    /*
    * Load Favourite Event from disk cache
    * */
    private void LoadFav() {
        InputStream is;
        try {
            is = new FileInputStream(favedEvents);
            String res = ReadStringFromInputStream(is);
            JSONArray array = new JSONArray(res);
            favd_stub = Utils.parseEvent(array);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Name:Read String From InputStream
     */
    private String ReadStringFromInputStream(InputStream is) {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String tmp = "";
        String ans = "";
        try {
            while ((tmp = br.readLine()) != null) {
                ans += tmp;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ans;
    }

    /**
     * Name:SaveToCache
     * Function:Save Latest Event information to disk
     */
    private void SaveToCache(JSONArray data) {
        JSONObject mInner;
        for (int i = 0; i < data.length(); ++i) {
            try {
                mInner = data.getJSONObject(i);
                String fname = mInner.getString("sponsor_fname");
                if (fname.length() > 30) {
                    fname = fname.substring(0, 20) + "-\n" + fname.substring(20, fname.length());
                    mInner.put("sponsor_fname", fname);
                    data.put(i, mInner);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String cacheString = data.toString();
        File cacheJSON = new File(cacheFile, CACHE_FILE_NAME);
        if (cacheJSON.exists()) cacheJSON.delete();
        try {
            cacheJSON.createNewFile();
            PrintStream printer = new PrintStream(new FileOutputStream(cacheJSON));
            printer.print(cacheString);
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CACHE_ERROR", "UnhandledError Occurs");
        }
    }

    /**
     * Name:ReadFromCache
     * Function:Read data from external cache
     */
    private void ReadFromCache() {
        File cacheEvent = new File(cacheFile, CACHE_FILE_NAME);
        if (!cacheEvent.exists()) {
            Log.e("FILE_NOT_FOUND", "cacheEvent.json NOT FOUND");
        }
        InputStream in = null;
        List<EventBean> mcacheData = new ArrayList<>();
        try {
            in = new FileInputStream(cacheEvent);
            String result = ReadStringFromInputStream(in);
            JSONArray cachedData = new JSONArray(result);
            mcacheData = parseEvent(cachedData);
            mListView.setAdapter(new EventListAdapter(MainActivity.this, mcacheData));
            setTitle("十一活动");
            mData = mcacheData;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * This Function is used to judge wether the network is available
     *
     * @return NetWorkkStat
     */
    private boolean isNetWorkAvailable() {
        Context currContext = this;
        if (currContext != null) {
            ConnectivityManager manager = (ConnectivityManager) currContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netWorkInfo = manager.getActiveNetworkInfo();
            if (netWorkInfo != null) {
                return netWorkInfo.isAvailable();
            }

        }
        return false;
    }
    //ListView OnItemCLickListener

    /**
     * Name:initDiskLruCache
     * Function:Initialize the DiskLruCache
     */

    private void initDiskLruCache() {
        File cacheFile = Utils.getCacheFile(this, "string");
        if (!cacheFile.exists()) cacheFile.mkdir();
        try {
            Log.i("DISK_LRU_INIT", "INITING");
            mCache = DiskLruCache.open(cacheFile, Utils.getAppVersion(this), 1, MAX_STRING_CACHE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if (isLongClicked) {
            isLongClicked = false;
            return;
        }

        View window = ViewTools.Inflate(this, R.layout.event_alert, null);
        View Header = ViewTools.Inflate(this, R.layout.event_alert_header, null);

        //ViewInjection
        TextView tvTitle = (TextView) window.findViewById(R.id.event_alert_title);
        TextView tvStart = (TextView) window.findViewById(R.id.event_alert_start);
        TextView tvDuration = (TextView) window.findViewById(R.id.event_alert_duration);
        TextView tvDescription = (TextView) window.findViewById(R.id.event_alert_description);
        TextView tvDate = (TextView) window.findViewById(R.id.event_alert_date);
        TextView tvSponsor = (TextView) window.findViewById(R.id.event_alert_sponsor);
        TextView tvLocation = (TextView) window.findViewById(R.id.event_alert_location);
        SimpleDraweeView pic = (SimpleDraweeView) window.findViewById(R.id.event_alert_pic);

        //DataAdaption
        EventBean bean = mData.get(position);
        tvTitle.setText(bean.eventTitle);
        tvDescription.setText(bean.eventContent);
        tvDate.setText(bean.eventDate);
        tvSponsor.setText(bean.sponsorName);
        tvStart.setText(bean.eventTime);
        tvDuration.setText(bean.eventDuration);
        tvLocation.setText(bean.eventLocation);
        bean.parseUrl();
        pic.setImageURI(Uri.parse(bean.eventAvatar));
        new AlertDialog.Builder(this).setView(window)
                .setCustomTitle(Header)
                .setNegativeButton("朕晓得了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).setPositiveButton("去看看", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent jump = new Intent(MainActivity.this, MainBrowser.class);
                jump.putExtra("QR_CONTENT", mData.get(position).eventURL);
                startActivity(jump);
            }
        }).setNeutralButton("分享活动", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                View viewQRShareAlert = ViewTools.Inflate(MainActivity.this, R.layout.alert_event_qr_share, null);
                View QRShareTitle = ViewTools.Inflate(MainActivity.this, R.layout.event_qr_share_header, null);

                ImageView EventQRImage = (ImageView) viewQRShareAlert.findViewById(R.id.event_share_qr_img);

                TextView EventShareTitle = (TextView) viewQRShareAlert.findViewById(R.id.event_share_title);

                EventShareTitle.setText(mData.get(position).eventTitle);

                EventQRImage.setImageBitmap(ImageTools.String2QR(mData.get(position).eventURL, BarcodeFormat.QR_CODE, 800, 800));

                new AlertDialog.Builder(MainActivity.this).setView(viewQRShareAlert)
                        .setCustomTitle(QRShareTitle)
                        .show();
            }
        }).show();

    }

    private boolean writeCache(InputStream is, OutputStream cache) {
        BufferedOutputStream out;
        out = new BufferedOutputStream(cache, 8 * 1024);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String tmp = "";
        String result = "";
        try {
            while ((tmp = br.readLine()) != null) {
                result += tmp;
            }
            Log.i("WriteByte", result);
            out.write(result.getBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    //End OF listener

    /*
    * Function: Parse JSONArray to List(Used in GetEventTask)
    * */
    public List<EventBean> parseEvent(JSONArray jsonArray) {
        JSONObject jsonObject;
        mRawData = jsonArray;
        LoadFav();
        List<EventBean> ans = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                if (mTask.isCancelled()) {
                    return null;
                }
                EventBean bean = new EventBean();
                jsonObject = jsonArray.getJSONObject(i);
                JSONObject content = jsonObject.getJSONObject("data");
                bean.eventTitle = content.getString("subject");
                bean.eventContent = content.getString("content");
                bean.eventLocation = content.getString("location");
                bean.sponsorName = jsonObject.getString("sponsor_fname");
                bean.eventAvatar = jsonObject.getString("avatar");
                bean.eventDate = jsonObject.getString("day_set");
                bean.eventTime = jsonObject.getString("time_set");
                bean.eventDuration = jsonObject.getString("time_last");
                bean.eventFollower = jsonObject.getInt("follower");
                bean.eventURL = URLConstants.HOME_URL + URLConstants.EVENT_URL + Integer.toString(content.getInt("id")) + "/";
                bean.parseUrl();
                for (int j = 0; j < favd_stub.size(); j++) {
                    if (bean.eventTitle.equals(favd_stub.get(j).eventTitle)) {
                        bean.isFaved = true;
                        break;
                    }
                }
                ans.add(bean);
            }
            return ans;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ans;
    }

    /*
    * Back press Handler
    * */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (System.currentTimeMillis() - time > 2000) {
            ToastUtil.makeText("再按一次退出", false);
            time = System.currentTimeMillis();
        } else if (!drawer.isDrawerOpen(GravityCompat.START)) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings: {
                startActivity(new Intent(this, SettingActivity.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * Navigation Item OnClick Handler
    * */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.go_to_browser:
                if (isNetWorkAvailable())
                    startActivity(new Intent(MainActivity.this, MainBrowser.class));
                else {
                    ToastUtil.makeText("无网络连接，请连接网络后再操作", false);
                }
                break;
            case R.id.go_to_about_me:
                startActivity(new Intent(MainActivity.this, AboutMeActivity.class));
                break;
            case R.id.go_to_scan:
                if (isNetWorkAvailable()) {
                    startActivityForResult(new Intent(this, ScannerActivity.class), 6666);
                } else {
                    ToastUtil.makeText("无网络连接，请连接网络后再操作", false);
                }
                //startActivity(new Intent(this,CaptrueActivity.class));
                break;
            case R.id.go_to_faved_events:
                startActivity(new Intent(this, FavEventActivity.class));
                break;
            case R.id.go_to_settings:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.go_to_square:
                if (isNetWorkAvailable()) {
                    startActivity(new Intent(this, ClubSquareActivity.class));
                } else {
                    ToastUtil.makeText("无网络连接，请连接网络后再操作", false);
                }
                break;
            case R.id.go_to_moment: {
                if (isNetWorkAvailable()) {
                    startActivity(new Intent(this, MomentActivity.class));
                } else {
                    ToastUtil.makeText("无网络连接，请连接网络后再操作", false);
                }
            }

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateCsrfToken(String rawHtml) {
        String csrfToken = Utils.getCsrfToken(rawHtml);
        Log.i("CSRF", csrfToken);
        if (csrfToken.equals("error") || TextUtils.isEmpty(csrfToken)) return;
        PreferenceUtils.initialize(this, PreferencesConstants.LOGIN_INFO, MODE_PRIVATE);
        PreferenceUtils.putString(PreferencesConstants.CSRF_TOKEN_TAG, csrfToken);
    }

    /*
    * Activity Result handler
    * 8081: login request
    * 8091: logout request
    * 6666: scan QR request
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQCODE && resultCode == 8081) {
            ToastUtil.makeText("登录成功啦", true);
            Logined = true;
            TextView tvName = (TextView) Nav_Header_stub.findViewById(R.id.user_name_holder);
            SharedPreferences LoginInfo = getSharedPreferences(PreferencesConstants.LOGIN_INFO, MODE_PRIVATE);
            String indicatorText = LoginInfo.getString(PreferencesConstants.USER_REAL_NAME_TAG, "NULL");
            if (!indicatorText.equals("NULL")) {
                tvName.setText("登录账户:" + indicatorText);
            } else {
                tvName.setText("十一圈Event-点击登录");
            }

            boolean isHighQuality = SettingPref.getBoolean(PreferencesConstants.SETTING_HIGH_QUALITY_AVATAR_TAG, false);
            String AvatarUrl = LoginInfo.getString((isHighQuality ?
                            PreferencesConstants.USER_RAW_AVATAR_URL_TAG
                            : PreferencesConstants.USER_AVATAR_URL_TAG)
                    , URLConstants.HOME_URL + URLConstants.DEFAULT_AVATAR_URL);
            LoginClick.setImageURI(Uri.parse(URLConstants.HOME_URL_WITHOUT_DASH + AvatarUrl));
            StringRequest csrfRequest = new StringRequest(HOME_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    updateCsrfToken(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            mRequestQueue.add(csrfRequest);
            SharedPreferences LoginStatus = getSharedPreferences("LoginInfo", MODE_PRIVATE);
            if (LoginStatus.getBoolean("Logined", false) && LoginStatus.getBoolean("cookieNeedSync", false)) {
                CookieSyncManager.createInstance(this);
                CookieManager syncManager = CookieManager.getInstance();
                String sessionId = LoginStatus.getString("cookieSessionId", null);
                String path = LoginStatus.getString("cookiePath", null);
                String expireTime = LoginStatus.getString("cookieExpireTime", null);
                boolean HttpOnly = LoginStatus.getBoolean("cookieHttpOnly", true);
                syncManager.setAcceptCookie(true);
                StringBuilder cookieSyncer = new StringBuilder();
                cookieSyncer.append(String.format("sessionid=%s", sessionId));
                cookieSyncer.append(String.format(";path=%s", path));
                cookieSyncer.append(String.format(";expires=%s", expireTime));
                cookieSyncer.append(String.format(";HttpOnly=%s", HttpOnly));
                Log.i("CookieSet", cookieSyncer.toString());
                LoginStatus.edit().putBoolean("cookieNeedSync", false).apply();
                syncManager.setCookie(HOME_URL, cookieSyncer.toString());
            }
            return;
        } else if (requestCode == LOGOUT_REQCODE && resultCode == 8091) {
            TextView tvName = (TextView) Nav_Header_stub.findViewById(R.id.user_name_holder);
            // Reset the label
            tvName.setText("十一圈Event-点击登录");
            Resources r = this.getResources();
            LoginClick.setImageURI(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + r.getResourcePackageName(R.mipmap.logo_2)
                    + "/"
                    + r.getResourceTypeName(R.mipmap.logo_2)
                    + "/"
                    + r.getResourceEntryName(R.mipmap.logo_2)));
            Logined = false;
            return;
        }

        // Handle QR Scan result
        if (requestCode == 6666) {
            if (resultCode == 6666) {
                Intent it = new Intent(this, MainBrowser.class);
                it.putExtra("QR_CONTENT", data.getStringExtra("QRContent"));
                startActivity(it);
            }
        } else if (requestCode == 9999) {
            ToastUtil.makeText("啊哦，扫描出错了呢", true);
        }
    }

    @Override
    protected void onDestroy() {
        if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
            mTask.cancel(true);
        }
        ToastUtil.cancel();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
            mTask.cancel(true);
        }
        ToastUtil.cancel();
        super.onStop();
    }


    //Task recycler

    @Override
    protected void onPause() {
        super.onPause();
        ToastUtil.cancel();
        Paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ToastUtil.initialize(this);
        LoadFav();
        if (Paused) {
            if (mIsConnected) {
                new GetEventTask().execute(REQ_URL);
            } else {
                ReadFromCache();
            }
            Paused = false;
        }
    }

    /*
    * Function: Check App Update By parsing HTML
    * */
    private class HTMLFetcher extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String ret = "NULL";
            try {
                String HTML = "";
                InputStream is = new URL(params[0]).openStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String tmp;
                while ((tmp = br.readLine()) != null) {
                    HTML += tmp;
                }
                updateCsrfToken(HTML);
                int idx = HTML.indexOf(URL_PREFIX);
                idx += URL_PREFIX.length();
                ret = "";
                for (int i = idx; HTML.charAt(i) >= '0' && HTML.charAt(i) <= '9' || HTML.charAt(i) == '.'; ++i) {
                    ret += HTML.charAt(i);
                }
                // Fetch file download url
                idx = HTML.indexOf(URLConstants.FINAL_VERSION_URL_PREFIX);
                idx += URLConstants.FINAL_VERSION_URL_PREFIX.length();
                mNewVersionDownloadURL = "";
                while (HTML.charAt(idx) >= '0' && HTML.charAt(idx) <= '9') {
                    mNewVersionDownloadURL += HTML.charAt(idx++);
                }
                mNewVersionDownloadURL = URLConstants
                        .FINAL_VERSION_URL_PREFIX + mNewVersionDownloadURL;
                Log.i("Download URL", mNewVersionDownloadURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("VerCode:", ret);
            return ret;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // Initialize Preference Utility
            PreferenceUtils
                    .initialize(MainActivity.this
                            , PreferencesConstants.UPDATE_CHECKER_PREF
                            , MODE_PRIVATE);
            PreferenceUtils.putString(PreferencesConstants.UPDATE_CHECKER_NEW_VERSION_CODE, s);
            if (!s.equals(URLConstants.CURRENT_VERSION) && !s.equals("NULL")) {

                // Update needUpdate Tag to true
                PreferenceUtils.putBoolean(PreferencesConstants.UPDATE_CHECKER_NEED_UPDATE, true);
                PreferenceUtils.putString(PreferencesConstants.UPDATE_CHECKER_DOWNLOAD_LINK
                        , mNewVersionDownloadURL);

                // Check Configurations
                PreferenceUtils.shiftTable(MainActivity.this, PreferencesConstants.SETTING_PREF, MODE_PRIVATE);

                if (PreferenceUtils.getBool(PreferencesConstants.SETTING_ENABLE_AUTO_UPDATE_TAG, false)) {
                    // Notification Header View
                    View updateNotiHeader = ViewTools
                            .Inflate(MainActivity.this, R.layout.update_info_header, null);

                    TextView tvHeaderText = (TextView) updateNotiHeader
                            .findViewById(R.id.update_info_header_text);
                    tvHeaderText.setText("发现更新喵~");

                    // Alert Content View
                    View contentView = ViewTools
                            .Inflate(MainActivity.this, R.layout.alert_update_download, null);
                    TextView tvCurrentVersion = (TextView) contentView
                            .findViewById(R.id.alert_update_download_current_version);
                    TextView tvNewVersion = (TextView) contentView
                            .findViewById(R.id.alert_update_download_new_version);
                    tvCurrentVersion.setText(URLConstants.CURRENT_VERSION);
                    tvNewVersion.setText(s);

                    int[] color = ImageTools.RandomColor();
                    tvHeaderText.setBackgroundColor(Color.rgb(color[0], color[1], color[2]));
                    if (ImageTools.isDeepColor(color)) {
                        tvHeaderText.setTextColor(Color.WHITE);
                    }

                    // Show Alert
                    new AlertDialog.Builder(MainActivity.this)
                            .setCustomTitle(updateNotiHeader)
                            .setView(contentView)
                            .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DownloadUtil.initialize(MainActivity.this);
                                    DownloadManager.Request mRequest =
                                            new DownloadUtil.RequestBuilder(mNewVersionDownloadURL)
                                                    .setTitle("ShiYiQuanEvent-Update.apk")
                                                    .setDescription("正在下载新版十一圈")
                                                    .setDownloadDirectory(Environment.DIRECTORY_DOWNLOADS
                                                            , "ShiYiQuanEvent-Update.apk")
                                                    .setVisibilityInUi(true)
                                                    .setMimeType("application/vnd.android.package-archive")
                                                    .build();
                                    DownloadUtil.startDownload(mRequest);

                                }
                            })
                            .setNegativeButton("暂不下载", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            }).show();
                }
            }
        }
    }

    /*
    * ClassName: GetEventTask
    * Function: Get Latest Function Data List
    * */
    class GetEventTask extends AsyncTask<String, Void, List<EventBean>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mRefersh.isRefreshing()) {
                mRefersh.setEnabled(false);
            }
            setTitle("加载中...");
        }

        @Override
        protected List<EventBean> doInBackground(String... params) {
            List<EventBean> ans = new ArrayList<>();
            try {
                InputStream is = new URL(params[0]).openStream();
                String key = MD5Util.HASH(params[0]);
                DiskLruCache.Editor editor = mCache.edit(key);
                OutputStream cacheStream;
                cacheStream = editor.newOutputStream(0);
                if (writeCache(new URL(params[0]).openStream(), cacheStream)) {
                    Log.i("DISK_CACHE:", "Saved Successfully");
                    editor.commit();
                } else {
                    Log.e("DISK_CACHE", "ABORT WRITTING!");
                    editor.abort();
                }
                mCache.flush();
                String result = ReadStringFromInputStream(is);
                JSONArray jsonArray = new JSONArray(result);
                SaveToCache(jsonArray);
                ans = parseEvent(jsonArray);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("NetWorkError", "CheckNetWork");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSONError", "CheckJSON");
            }

            return ans;
        }

        @Override
        protected void onPostExecute(List<EventBean> eventBeans) {
            super.onPostExecute(eventBeans);
            if (mRefersh.isRefreshing()) {
                mRefersh.setRefreshing(false);
            }
            if (eventBeans.size() == 0) {
                setTitle("加载失败了呢...QAQ");
                ToastUtil.makeText("进入没有网络的异次元啦QAQ", false);
                if (!mRefersh.isEnabled()) {
                    mRefersh.setEnabled(true);
                }
                return;
            }
            if (!mRefersh.isEnabled()) {
                mRefersh.setEnabled(true);
            }
            setTitle("十一活动");
            mData = eventBeans;
            EventListAdapter adapter = new EventListAdapter(MainActivity.this, eventBeans);
            mListView.setAdapter(adapter);
        }
    }
}
