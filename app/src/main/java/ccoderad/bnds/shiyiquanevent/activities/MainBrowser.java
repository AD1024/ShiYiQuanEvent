package ccoderad.bnds.shiyiquanevent.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.mingle.entity.MenuEntity;
import com.mingle.sweetpick.CustomDelegate;
import com.mingle.sweetpick.DimEffect;
import com.mingle.sweetpick.SweetSheet;
import com.mingle.sweetpick.ViewPagerDelegate;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.adapters.ClubChoiceAdapter;
import ccoderad.bnds.shiyiquanevent.adapters.ClubListAdapter;
import ccoderad.bnds.shiyiquanevent.beans.ClubModel;
import ccoderad.bnds.shiyiquanevent.global.MultiThreadConstants;
import ccoderad.bnds.shiyiquanevent.global.PreferencesConstants;
import ccoderad.bnds.shiyiquanevent.global.URLConstants;
import ccoderad.bnds.shiyiquanevent.utils.ImageTools;
import ccoderad.bnds.shiyiquanevent.utils.MultiThreadUtil;
import ccoderad.bnds.shiyiquanevent.utils.PreferenceUtils;
import ccoderad.bnds.shiyiquanevent.utils.ToastUtil;
import ccoderad.bnds.shiyiquanevent.utils.Utils;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import terranovaproductions.newcomicreader.FloatingActionMenu;

public class MainBrowser extends AppCompatActivity implements FloatingActionMenu.OnMenuItemClickListener, DrawerLayout.DrawerListener {

    private final String HOME_URL = URLConstants.HOME_URL;
    /*
    * Primitive data type
    * */
    private long last_back_press = 0;
    private long last_exit_press = 0;
    private boolean ClubLoaded;
    private boolean isLogin;
    private boolean adminedLoaded;
    private String host_id = "";

    /*
    * Adapters
    * */
    private ClubListAdapter mAdapter;
    private ClubChoiceAdapter Choiceadapter;


    /*
    * Multi-thread related
    * */
    private Handler updateTask;
    private Handler getClub;
    private Runnable taskMain;
    private Runnable firstFetchTask;
    /*
    * Halt the thread that loop to fetch information
    * */
    private final Handler handleStop = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MultiThreadConstants.RUNNABLE_HALT: {
                    getClub.removeCallbacks(firstFetchTask);
                    break;
                }
            }
            super.handleMessage(msg);
        }
    };
    /*
    * Java Library variables
    * */
    private List<ClubModel> mMyClubs; // Club information list
    private List<String> AdminedClubURLs = new ArrayList<>(); // Club urls that is managed by the user
    private List<MenuEntity> myAdmined = new ArrayList<>(); // Menu entities
    // identities maintained by a user.
    private Map<String, Integer> mClubNameIndex; // There might be several
    private Map<String, String> mHttpExraHeader;
    /*
    * Views
    * */
    private WebView mDisplay;
    private WebView mRelation;
    private ListView mClubList;
    private SweetSheet mChatChoice;
    private SweetSheet mAddActivity;
    private Toolbar toolbar;
    private DrawerLayout mClubContainer;
    private LinearLayout mClubListIndicatorContainer;
    private FloatingActionMenu mFunctionGroupContainer;
    private LinearLayout mDataLoadingIndicator;
    /*
    * Other variables
    * */
    private ImageLoaderConfiguration mConfiguration;
    private DisplayImageOptions mDisplayOption;
    private ValueCallback<Uri> mUploadMessage;
    private SharedPreferences host_id_provider;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        ToastUtil.initialize(this);
        mQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_main_browser);
        mClubListIndicatorContainer = (LinearLayout) findViewById(R.id.browser_club_list_indicator_container);
        mDataLoadingIndicator = (LinearLayout) findViewById(R.id.browser_club_list_loading_indicator);
        isLogin = getSharedPreferences(PreferencesConstants.LOGIN_INFO, MODE_PRIVATE).getBoolean(PreferencesConstants.LOGIN_STATUS, false);
        if (!isLogin) mClubListIndicatorContainer.setVisibility(View.VISIBLE);
        mHttpExraHeader = new HashMap<>();
        mHttpExraHeader.put("Mobile-Avoid-Nav", "True");
        init();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getIntents();
    }

    private void getIntents() {
        Intent it = getIntent();
        String Content = it.getStringExtra("QR_CONTENT");
        if (Content == null) return;
        if (!Content.equals("")) {
            Log.i("PassURLValue", Content);
            if (!Content.contains("shiyiquan")) {
                Log.e("None ShiyiquanURL", "非十一圈链接，跳过处理");
                ToastUtil.makeText("啊哦，好像不是十一圈的链接哟", true);
                mDisplay.loadUrl(HOME_URL, mHttpExraHeader);
            } else mDisplay.loadUrl(Content, mHttpExraHeader);
        }
    }

    private void spawnId() {
        if (!host_id_provider.getString(PreferencesConstants.HOST_ID_TAG, "None").equals("None")) {
            host_id = host_id_provider.getString(PreferencesConstants.HOST_ID_TAG, "None");
            Log.i("SpawnIdLog", "Get Host id from localStorage:" + host_id);
        } else {
            host_id += "android_";
            host_id += Long.toString(System.currentTimeMillis());
            Log.i("Host_id", host_id);
            SharedPreferences.Editor editor = host_id_provider.edit();
            editor.putString(PreferencesConstants.HOST_ID_TAG, host_id).apply();
        }
    }

    private void updateId() {
        mRelation.loadUrl(URLConstants.HOME_URL + "mobile/save/?host_id=" + host_id);
        Log.i("UpdateId", host_id);
    }

    private String newId() {
        return "android_" + Long.toString(System.currentTimeMillis());
    }

    private ImageLoaderConfiguration buildConfig4UIL() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(this, "ShiyiquanImgs/Cache");
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true) //cache on disk
                .cacheInMemory(false) //cache in memory
                .preProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        return ImageTools
                                .fastblur(ImageTools
                                        .CompressBitmap(bitmap
                                                , Bitmap.CompressFormat.PNG), 5);
                    }
                })
                .displayer(new FadeInBitmapDisplayer(300))  //delay when displaying
                .bitmapConfig(Bitmap.Config.RGB_565).build(); //This will reduce memory consumption
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCacheExtraOptions(480, 800)  // max width, max height
                .threadPoolSize(3)                  //线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheFileCount(100)
                .memoryCacheSize(30 * ByteConstants.MB)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .defaultDisplayImageOptions(options)
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)
                .build(); //Link start!
        mDisplayOption = options;
        return config;
    }

    private void init() {
        mDisplay = (WebView) findViewById(R.id.display_browser);
        mRelation = new WebView(this);

        mClubList = (ListView) findViewById(R.id.club_list);

        toolbar = (Toolbar) findViewById(R.id.browser_toolbar);
        toolbar.setTitle("十一圈");
        toolbar.setSubtitle("V1.1");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mChatChoice = new SweetSheet((RelativeLayout) findViewById(R.id.browser_relative));
        mChatChoice.setBackgroundClickEnable(true);
        mChatChoice.setBackgroundEffect(new DimEffect(8.0F));

        mAddActivity = new SweetSheet((RelativeLayout) findViewById(R.id.browser_relative));
        mAddActivity.setBackgroundEffect(new DimEffect(8.0F));
        mAddActivity.setBackgroundClickEnable(true);
        mAddActivity.setDelegate(new ViewPagerDelegate());

        mMyClubs = new ArrayList<>();
        mClubNameIndex = new HashMap<>();

        mAdapter = new ClubListAdapter(this, mMyClubs);
        mAdapter.setLoadingIndicator(mDataLoadingIndicator);
        mClubContainer = (DrawerLayout) findViewById(R.id.club_list_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mClubContainer, toolbar
                , R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mClubContainer.setDrawerListener(toggle);
        toggle.syncState();

        mFunctionGroupContainer = (FloatingActionMenu) findViewById(R.id.browser_fam);
        mFunctionGroupContainer.setIsCircle(false);
        mFunctionGroupContainer.setmItemGap(30);

        host_id_provider = getSharedPreferences(PreferencesConstants.HOST_ID_PREF, MODE_PRIVATE);

        mConfiguration = buildConfig4UIL();
        ImageLoader.getInstance().init(mConfiguration);
        mFunctionGroupContainer.setOnMenuItemClickListener(this);

        mClubList.addHeaderView(LayoutInflater.from(this)
                .inflate(R.layout.club_list_header, null), null, false);
        mClubList.setAdapter(mAdapter);
        mClubList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mClubContainer.closeDrawer(GravityCompat.START);
                mDisplay.loadUrl(HOME_URL + "club/" + mMyClubs.get(position - 1).sname, mHttpExraHeader);
            }
        });


        mDisplay.loadUrl(HOME_URL, mHttpExraHeader);
        mDisplay.setWebChromeClient(new WebChromeClient() {

            public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                        String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                startActivityForResult(BGAPhotoPickerActivity.newIntent(MainBrowser.this, null, 1, null, false), 1000);


            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                toolbar.setTitle(title);
            }
        });
        //Fix  JavaScript Executaion
        mDisplay.getSettings().setJavaScriptEnabled(true);
        mDisplay.getSettings().setDomStorageEnabled(true);
        mDisplay.getSettings().setAppCacheEnabled(true);
        mDisplay.getSettings().setSupportMultipleWindows(true);
        mDisplay.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url, mHttpExraHeader);
                return true;
            }
        });

        mRelation.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.equals(HOME_URL)) {
                    asyncGetClubInfo();
                }
            }
        });

        spawnId();
        updateId();
        taskMain = new Runnable() {
            @Override
            public void run() {
                updateId();
                new Handler().postDelayed(this, 30 * 1000);
            }
        };

        getClub = new Handler();
        firstFetchTask = new Runnable() {
            @Override
            public void run() {
                if (mMyClubs.size() == 0) {
                    Log.i("FirstFetchTask", "Fetching Data");
                    asyncGetClubInfo();
                    getClub.postDelayed(this, 8 * 1000);
                } else {
                    Log.i("FirstFetchTask", "Data Fetched, halting");
                    Message msg = new Message();
                    msg.what = MultiThreadConstants.RUNNABLE_HALT;
                    handleStop.sendMessage(msg);
                }
            }
        };
        if (isLogin) {
            getClub.post(firstFetchTask);
            if (mMyClubs.size() == 0) mDataLoadingIndicator.setVisibility(View.VISIBLE);
            updateTask = new Handler();
            updateTask.post(taskMain);

            updateId();
            AddToAdminList();
            asyncGetClubInfo();
        }
        ClubLoaded = false;
        adminedLoaded = false;

    }

    /**
     * @return Void
     * @description Used to fetch and adapt the data of current user
     */

    void asyncGetClubInfo() {
        String URL_REQ = HOME_URL + "mobile/club/?host_id=" + host_id + "&time=" + newId() + "&user-agent=" + URLConstants.USER_AGENT;
        Log.i("CLUB_REQ", URL_REQ);
        if (mMyClubs.size() > 0) {
            mClubListIndicatorContainer.setVisibility(View.GONE);
        }

        //Async Task->used to fetch info of the club that current user has engaged

//        final JsonArrayRequest request = new JsonArrayRequest(URL_REQ,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        if (ClubLoaded && mMyClubs.size() != 0) return;
//                        mClubListIndicatorContainer.setVisibility(View.GONE);
//                        ClubLoaded = true;
//                        for (int i = 0; i < response.length(); i++) {
//                            ClubModel bean = new ClubModel();
//                            try {
//                                JSONObject jsonObject = response.getJSONObject(i);
//                                bean.club_name = jsonObject.getString("fname");
//                                bean.sname = jsonObject.getString("sname");
//                                if (mClubNameIndex.containsKey(bean.sname)) {
//                                    int idx = mClubNameIndex.get(bean.sname);
//                                    mMyClubs.get(idx).status
//                                            .add(jsonObject
//                                                    .getJSONArray("status")
//                                                    .getString(1));
//                                } else {
//                                    JSONArray status = jsonObject.getJSONArray("status");
//                                    bean.LargeAvatarURL = HOME_URL
//                                            + "media/images/avatar/large/"
//                                            + "club-" + bean.sname
//                                            + ".png";
//                                    bean.mediumAvatarURL = HOME_URL
//                                            + "media/images/avatar/"
//                                            + "club-"
//                                            + bean.sname
//                                            + ".png";
//                                /*
//                                * 0: Key : Status indicator(Might be useless for Client)
//                                * 1: Value: Real value of the status
//                                *
//                                * */
//                                    bean.status.add(status.getString(1));
//                                /*
//                                * Optimization for administrability(:P) check
//                                * */
//                                    if (status.getString(0).equals("head")
//                                            || status.getString(0).equals("vice")) {
//                                        bean.isAdmin = true;
//                                    }
//                                    mClubNameIndex.put(bean.sname, i);
//                                    mMyClubs.add(bean);
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        //Notify the listview in drawer to update its views.
//                        mAdapter.notifyDataSetChanged();
//
//                        //Write Club Data to Stroage
//                        if (mMyClubs.size() != 0) {
//                            JSONArray my_club_info = new JSONArray();
//                            for (int i = 0; i < mMyClubs.size(); i++) {
//                                ClubModel model = mMyClubs.get(i);
//                                String club_name = model.club_name;
//                                JSONObject club = new JSONObject();
//                                try {
//                                    club.put("club_name", club_name);
//                                    my_club_info.put(club);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                            File file = new File(Environment.getExternalStorageDirectory().toString()
//                                    + File.separator + "ShiYiQuan");
//                            if (!file.exists()) file.mkdirs();
//                            File JSON = new File(file.toString() + File.separator + "MyClub.json");
//                            if (!JSON.exists()) try {
//                                JSON.createNewFile();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            PrintStream writer = null;
//                            try {
//                                writer = new PrintStream(new FileOutputStream(JSON));
//                                writer.print(my_club_info.toString());
//                            } catch (FileNotFoundException e) {
//                                e.printStackTrace();
//                            } finally {
//                                if (writer != null) writer.close();
//                            }
//
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                ToastUtil.makeText("请检查网络连接", true);
//            }
//        });
        StringRequest request = new StringRequest(URL_REQ, new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                JSONArray response = new JSONArray();
                JSONObject obj = new JSONObject();
                try {
                    obj = new JSONObject(resp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    response = obj.getJSONArray("club_list");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ClubLoaded && mMyClubs.size() != 0) return;
                mClubListIndicatorContainer.setVisibility(View.GONE);
                ClubLoaded = true;
                for (int i = 0; i < response.length(); i++) {
                    ClubModel bean = new ClubModel();
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        bean.club_name = jsonObject.getString("fname");
                        bean.sname = jsonObject.getString("sname");
                        if (mClubNameIndex.containsKey(bean.sname)) {
                            int idx = mClubNameIndex.get(bean.sname);
                            mMyClubs.get(idx).status
                                    .add(jsonObject
                                            .getJSONArray("status")
                                            .getString(1));
                        } else {
                            JSONArray status = jsonObject.getJSONArray("status");
                            bean.LargeAvatarURL = HOME_URL
                                    + "media/images/avatar/large/"
                                    + "club-" + bean.sname
                                    + ".png";
                            bean.mediumAvatarURL = HOME_URL
                                    + "media/images/avatar/"
                                    + "club-"
                                    + bean.sname
                                    + ".png";
                                /*
                                * 0: Key : Status indicator(Might be useless for Client)
                                * 1: Value: Real value of the status
                                *
                                * */
                            bean.status.add(status.getString(1));
                                /*
                                * Optimization for administrability(:P) check
                                * */
                            if (status.getString(0).equals("head")
                                    || status.getString(0).equals("vice")) {
                                bean.isAdmin = true;
                            }
                            mClubNameIndex.put(bean.sname, i);
                            mMyClubs.add(bean);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //Notify the listview in drawer to update its views.
                mAdapter.notifyDataSetChanged();

                //Write Club Data to Stroage
                if (mMyClubs.size() != 0) {
                    JSONArray my_club_info = new JSONArray();
                    for (int i = 0; i < mMyClubs.size(); i++) {
                        ClubModel model = mMyClubs.get(i);
                        String club_name = model.club_name;
                        JSONObject club = new JSONObject();
                        try {
                            club.put("club_name", club_name);
                            my_club_info.put(club);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    File file = new File(Environment.getExternalStorageDirectory().toString()
                            + File.separator + "ShiYiQuan");
                    if (!file.exists()) file.mkdirs();
                    File JSON = new File(file.toString() + File.separator + "MyClub.json");
                    if (!JSON.exists()) try {
                        JSON.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    PrintStream writer = null;
                    try {
                        writer = new PrintStream(new FileOutputStream(JSON));
                        writer.print(my_club_info.toString());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        if (writer != null) writer.close();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.makeText("请检查网络", true);
            }
        });
        request.setRetryPolicy(MultiThreadUtil.createDefaultRetryPolicy());
        mQueue.add(request);
    }

    //Add Club that current user has engaged to the Sweet View List.
    private void LoadClub2Choice() {
        View v = LayoutInflater.from(this).inflate(R.layout.club_chat_choice_list, null);
        ListView Lv = (ListView) v.findViewById(R.id.club_chat_choice_list);
        Choiceadapter = new ClubChoiceAdapter(this, mMyClubs, mChatChoice, mDisplay, mDisplayOption);
        Lv.setAdapter(Choiceadapter);
        mChatChoice.setDelegate(new CustomDelegate(true, CustomDelegate.AnimationType.DuangAnimation).setCustomView(v));
    }


    //Add club info that is under current user control
    private void AddToAdminList() {
        if (mMyClubs.size() != 0) {
            for (ClubModel club : mMyClubs
                    ) {
                if (club.isAdmin) {
                    AdminedClubURLs.add(HOME_URL + "/club/" + club.sname);
                    final MenuEntity M = new MenuEntity();
                    M.title = club.club_name;
                    ImageRequest request = new ImageRequest(this, club.mediumAvatarURL, new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            M.icon = new BitmapDrawable(getResources(), response);
                        }
                    }, 130, 130, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    mQueue.add(request);
                    myAdmined.add(M);
                }
            }
            adminedLoaded = true;
            mAddActivity.setMenuList(myAdmined);
            mAddActivity.setOnMenuItemClickListener(new SweetSheet.OnMenuItemClickListener() {
                @Override
                public boolean onItemClick(int position, MenuEntity menuEntity) {
                    mDisplay.loadUrl(AdminedClubURLs.get(position), mHttpExraHeader);
                    return true;
                }
            });
        }
    }

    //Listener for FAM's fab
    @Override
    public void onMenuItemClick(FloatingActionMenu floatingActionMenu, int i, FloatingActionButton floatingActionButton) {
        int id = floatingActionButton.getId();
        switch (id) {
            //Switch to the search page
            case R.id.fab_search:
                mDisplay.loadUrl(HOME_URL + "search/", mHttpExraHeader);
                break;

            //Switch to HOME_URL
            case R.id.fab_go_home:
                mDisplay.clearHistory();
                mDisplay.loadUrl(HOME_URL, mHttpExraHeader);
                break;

            //Switch to Home(If Logined)
            case R.id.fab_my_home_page:
                if (isLogin)
                    mDisplay.loadUrl(HOME_URL + "user/", mHttpExraHeader);
                else
                    ToastUtil.makeText("请先登录", true);
                break;

            //Open drawer(it can be used to grab club list info while it hasn't been displayed in the list)
            case R.id.fab_my_club:
                if (isLogin) {
                    asyncGetClubInfo();
                    mClubContainer.openDrawer(GravityCompat.START);
                } else {
                    ToastUtil.makeText("请先登录", true);
                }
                break;

            //Club Info List
            case R.id.fab_sender:
                Log.i("Club Num" , Utils.Int2String(mMyClubs.size()));
                if (mMyClubs.size() == 0 && !isLogin) {
                    ToastUtil.makeText("请先登录", true);
                    break;
                } else if (mMyClubs.size() == 0) {
                    ToastUtil.makeText("无信息...", false);
                    break;
                }
                LoadClub2Choice();
                mChatChoice.show();
                break;

            //Add club activity
            case R.id.fab_add_club_activity:
                if (adminedLoaded) {
                    if (myAdmined.size() != 0) {
                        mAddActivity.show();
                    } else ToastUtil.makeText("你还不是任何社团的管理者", true);
                    break;
                } else {
                    AddToAdminList();
                    if (myAdmined.size() != 0)
                        mAddActivity.show();
                    else {
                        adminedLoaded = false;
                    }
                }
                break;
            case R.id.fab_back_to_main:
                this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (mClubContainer.isDrawerOpen(GravityCompat.START)) {
            mClubContainer.closeDrawer(GravityCompat.START);
        }
        if (mAddActivity.isShow()) {
            mAddActivity.dismiss();
            return;
        }
        if (mChatChoice.isShow()) {
            mChatChoice.dismiss();
            return;
        }
        boolean maintain = false;
        if (System.currentTimeMillis() - last_exit_press > 800) {
            if (mDisplay.canGoBack()) {
                mDisplay.goBack();
            }
            last_exit_press = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            String filedir = BGAPhotoPickerActivity.getSelectedImages(data).get(0);
            Uri callback = Uri.parse(filedir);
            mUploadMessage.onReceiveValue(callback);
        } else if (requestCode == 8080) {
            isLogin = PreferenceUtils.getBool(this, PreferencesConstants.LOGIN_INFO, PreferencesConstants.LOGIN_STATUS, false);
            if (isLogin) {
                mClubListIndicatorContainer.setVisibility(View.INVISIBLE);
                spawnId();
                updateId();
                asyncGetClubInfo();
                LoadClub2Choice();
            }
        }
    }

    @Override
    protected void onStop() {
        ToastUtil.cancel();
        super.onStop();
    }

    //Drawer Listener

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        if (mMyClubs.size() < 1) {
            asyncGetClubInfo();
        }
    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
    //DrawerListener

}
