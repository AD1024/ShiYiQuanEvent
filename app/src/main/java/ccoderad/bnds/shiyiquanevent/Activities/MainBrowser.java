package ccoderad.bnds.shiyiquanevent.Activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.LruCache;
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
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
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
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
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
import java.util.List;

import ccoderad.bnds.shiyiquanevent.Adapters.ClubChoiceAdapter;
import ccoderad.bnds.shiyiquanevent.Adapters.ClubListAdapter;
import ccoderad.bnds.shiyiquanevent.Beans.ClubModel;
import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.Services.EventUpdateService;
import terranovaproductions.newcomicreader.FloatingActionMenu;

public class MainBrowser extends AppCompatActivity implements FloatingActionMenu.OnMenuItemClickListener, DrawerLayout.DrawerListener {

    private WebView mDisplay;
    private WebView mRelation;
    private Toolbar toolbar;
    private ListView mClubList;
    private SweetSheet mChatChoice;
    private SweetSheet mAddActivity;
    private boolean ClubLoaded;
    private String host_id = "";
    private long last_back_press = 0;
    private long last_exit_press = 0;
    private ClubListAdapter mAdapter;
    private ClubChoiceAdapter Choiceadapter;
    private DrawerLayout mClubContainer;
    private FloatingActionMenu mFunctionGroupContainer;
    private Handler updateTask;
    private RequestQueue mQueue;
    private Runnable taskMain;
    private SharedPreferences host_id_provider;
    private List<ClubModel> mMyClubs;
    private boolean adminedLoaded;
    private List<MenuEntity> myAdmined = new ArrayList<>();
    private List<String> AdminedClubURLs = new ArrayList<>();
    private ImageLoaderConfiguration mConfiguration;
    private final String HOME_URL = "http://shiyiquan.net/";
    private EventUpdateService.EventListenerBinder mBinder;
    private boolean isPortait;
    private View VideoPlayerView;
    private FrameLayout PlayerHolder;
    private WebChromeClient.CustomViewCallback mPlayerCallBack;
    private boolean mHaveCache=false;
    private LruCache<String,Bitmap> mCacheStub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        mQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_main_browser);
        init();
        Toast.makeText(this, "连按三次返回可回到活动界面", Toast.LENGTH_LONG).show();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        isPortait = true;
        PlayerHolder = (FrameLayout) findViewById(R.id.browser_player_holder);
        Intent bindIntent = new Intent(this, EventUpdateService.class);
        startService(bindIntent);
        ServiceConnection sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBinder = (EventUpdateService.EventListenerBinder) service;
                mBinder.modifyParent(MainBrowser.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(bindIntent, sc, BIND_AUTO_CREATE);
        getIntents();
    }

    private void getIntents() {
        Intent it = getIntent();
        String Content = it.getStringExtra("QR_CONTENT");
        if (Content == null) return;
        if (!Content.equals("")) {
            mDisplay.loadUrl(Content);
        }
    }

    private void spawnId() {
        if (!host_id_provider.getString("host_id", "None").equals("None")) {
            host_id = host_id_provider.getString("host_id", "None");
            Log.i("SpawnIdLog", "Get Host id from localStorage:" + host_id);
        } else {
            host_id += "android_";
            host_id += Long.toString(System.currentTimeMillis());
            Log.i("Host_id", host_id);
            SharedPreferences.Editor editor = host_id_provider.edit();
            editor.putString("host_id", host_id).apply();
        }
    }

    private void updateId() {
        mRelation.loadUrl("http://www.shiyiquan.net/mobile/save/?host_id=" + host_id);
        Log.i("UpdateId", host_id);
    }

    private String newId() {
        return "android_" + Long.toString(System.currentTimeMillis());
    }

    private ImageLoaderConfiguration buildConfig4UIL() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(this, "ShiyiquanImgs/Cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCacheExtraOptions(480, 800)  // maxwidth, max height
                .threadPoolSize(3)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheFileCount(100)
                .memoryCacheSize(30 * ByteConstants.MB)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
//                .writeDebugLogs() // Remove for releaseapp
                .build(); //Link start!~
        return config;
    }

    private void init() {
        mDisplay = (WebView) findViewById(R.id.display_browser);
        mRelation = new WebView(this);

        mClubList = (ListView) findViewById(R.id.club_list);

        toolbar = (Toolbar) findViewById(R.id.browser_toolbar);
        toolbar.setTitle("十一圈");
        toolbar.setSubtitle("V1.0");
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

        mAdapter = new ClubListAdapter(this, mMyClubs);
        mClubContainer = (DrawerLayout) findViewById(R.id.club_list_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mClubContainer, toolbar
                , R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mClubContainer.setDrawerListener(toggle);
        toggle.syncState();

        mFunctionGroupContainer = (FloatingActionMenu) findViewById(R.id.browser_fam);
        mFunctionGroupContainer.setIsCircle(false);
        mFunctionGroupContainer.setmItemGap(30);

        host_id_provider = this.getPreferences(MODE_PRIVATE);

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
                mDisplay.loadUrl(HOME_URL + "club/" + mMyClubs.get(position - 1).sname);
            }
        });


        mDisplay.loadUrl("http://www.shiyiquan.net/");
        mDisplay.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
//                super.onReceivedTitle(view, title);
                toolbar.setTitle(title);
            }
        });
        //Fix  JavaScript Executaion
        mDisplay.getSettings().setJavaScriptEnabled(true);
        mDisplay.getSettings().setDomStorageEnabled(true);
        mDisplay.getSettings().setAppCacheEnabled(true);
        mDisplay.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                /*
                reserved
                if (url.contains("logout")) {
                    SharedPreferences.Editor editor = host_id_provider.edit();
                    editor.putBoolean("isLogged", false);
                    mMyClubs.clear();
                    myAdmined.clear();
                    Choiceadapter.notifyDataSetChanged();
                    mAdapter.notifyDataSetChanged();
                }
                if (url.contains("login")) {
                    SharedPreferences.Editor editor = host_id_provider.edit();
                    editor.putBoolean("isLogged", true);
                    spawnId();
                    updateId();
                }
                */
                view.loadUrl(url);
                return true;
            }
        });

        mDisplay.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                //return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
                Intent jumpSelection = new Intent(Intent.ACTION_GET_CONTENT);
                jumpSelection.addCategory(Intent.CATEGORY_OPENABLE);
                jumpSelection.setType("image/*");
                Intent it = new Intent(Intent.ACTION_CHOOSER);
                it.putExtra(Intent.EXTRA_INTENT, jumpSelection);
                it.putExtra(Intent.EXTRA_TITLE, "选择文件来上传~");
                startActivityForResult(it, 2);
                return true;
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                // super.onShowCustomView(view, callback);
                if (isPortait) {
                    isPortait = false;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                mDisplay.setVisibility(View.GONE);
                if (VideoPlayerView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                PlayerHolder.addView(view);
                VideoPlayerView = view;
                mPlayerCallBack = callback;
                PlayerHolder.setVisibility(View.VISIBLE);
            }

            @Override
            public void onHideCustomView() {
                //super.onHideCustomView();
                if (VideoPlayerView != null) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    VideoPlayerView.setVisibility(View.GONE);
                    PlayerHolder.removeView(VideoPlayerView);
                    VideoPlayerView = null;
                    PlayerHolder.setVisibility(View.GONE);
                    mPlayerCallBack.onCustomViewHidden();
                    mDisplay.setVisibility(View.VISIBLE);
                }
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
                    updateId();
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

        Handler getClub = new Handler();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (mMyClubs.size() == 0) {
                    asyncGetClubInfo();
                    new Handler().postDelayed(this, 10 * 1000);
                } else {
                    return;
                }
            }
        };
        getClub.post(task);

        updateTask = new Handler();
        updateTask.post(taskMain);

        updateId();
        AddToAdminList();

        ClubLoaded = false;
        adminedLoaded = false;

        asyncGetClubInfo();
    }

    /**
     * @return Void
     * @description Used to fetch and adapt the data of current user
     */

    void asyncGetClubInfo() {
        String URL_REQ = HOME_URL + "mobile/club/?host_id=" + host_id + "&time=" + newId();
        Log.i("CLUB_REQ", URL_REQ);

        /*
        StringRequest test = new StringRequest(URL_REQ, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Response",response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });*/

        //Async Task->used to fetch info of the club that current user has engaged
        final JsonArrayRequest request = new JsonArrayRequest(URL_REQ,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (ClubLoaded && mMyClubs.size() != 0) return;
                        Log.i("JSONRequestLog", "Enter" + response.length());
                        ClubLoaded = true;
                        for (int i = 0; i < response.length(); i++) {
                            ClubModel bean = new ClubModel();
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                bean.club_name = jsonObject.getString("fname");
                                bean.sname = jsonObject.getString("sname");
                                String status = jsonObject.getString("status");
                                bean.LargeAvatarURL = HOME_URL + "media/images/avatar/large/" + "club-" + bean.sname + ".png";
                                bean.mediumAvatarURL = HOME_URL + "media/images/avatar/" + "club-" + bean.sname + ".png";
                                switch (status) {
                                    case "head":
                                        bean.status = "社长";
                                        break;
                                    case "vice":
                                        bean.status = "副社长";
                                        break;
                                    default:
                                        bean.status = "社员";
                                        break;
                                }
                                Log.i("Info-" + Integer.toString(i), bean.status + "\n" + bean.club_name + "\n" + bean.sname);
                                Log.i("AvatarMed", bean.mediumAvatarURL);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mMyClubs.add(bean);
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
                Toast.makeText(MainBrowser.this, "请检查网络连接", Toast.LENGTH_LONG).show();
            }
        });
        mQueue.add(request);
    }

    //Add Club that current user has engaged to the Sweet View List.
    private void LoadClub2Choice() {
        View v = LayoutInflater.from(this).inflate(R.layout.club_chat_choice_list, null);
        ListView Lv = (ListView) v.findViewById(R.id.club_chat_choice_list);
        Choiceadapter = new ClubChoiceAdapter(this, mMyClubs, mChatChoice, mDisplay);
        if(mHaveCache) Choiceadapter.passCache(mCacheStub);
        Lv.setAdapter(Choiceadapter);
        mChatChoice.setDelegate(new CustomDelegate(true, CustomDelegate.AnimationType.AlphaAnimation).setCustomView(v));
        mCacheStub = Choiceadapter.getCache();
        if(mCacheStub.size()!=0){
            Log.i("CacheLog","Cache Size:"+mCacheStub.size());
            mHaveCache=true;
        }
    }


    //Add club info that is under current user control
    private void AddToAdminList() {
        if (mMyClubs.size() != 0) {
            for (ClubModel club : mMyClubs
                    ) {
                if (club.status.equals("社长") || club.status.equals("副社长")) {
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
                    mDisplay.loadUrl(AdminedClubURLs.get(position));
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
                mDisplay.loadUrl(HOME_URL + "search/");
                break;

            //Switch to HOME_URL
            case R.id.fab_go_home:
                mDisplay.clearHistory();
                mDisplay.loadUrl(HOME_URL);
                break;

            //Switch to Home(If Logined)
            case R.id.fab_my_home_page:
                mDisplay.loadUrl(HOME_URL + "user/");
                break;

            //Open drawer(it can be used to grab club list info while it hasn't been displayed in the list)
            case R.id.fab_my_club:
                asyncGetClubInfo();
                mClubContainer.openDrawer(GravityCompat.START);
                break;

            //Club Info List
            case R.id.fab_sender:
                if (mMyClubs.size() == 0) {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_LONG).show();
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
                    } else Toast.makeText(this, "你还不是任何社团的管理者", Toast.LENGTH_LONG).show();
                    break;
                } else {
                    AddToAdminList();
                    if (myAdmined.size() != 0)
                        mAddActivity.show();
                    else {
                        adminedLoaded = false;
                    }
                }
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
        if (System.currentTimeMillis() - last_back_press > 2000 && System.currentTimeMillis() - last_exit_press > 1000) {
            if (mDisplay.canGoBack())
                mDisplay.goBack();
            last_back_press = System.currentTimeMillis();
        } else {
            if (System.currentTimeMillis() - last_exit_press > 500) {
                Toast.makeText(MainBrowser.this, "再按一次返回", Toast.LENGTH_SHORT).show();
                last_exit_press = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //Drawer Listener

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
    //DrawerListener


    class xChromeClient extends WebChromeClient {

        ValueCallback<Uri[]> mMsg;

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            //return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            mMsg = filePathCallback;
            Intent jumpSelection = new Intent(Intent.ACTION_GET_CONTENT);
            jumpSelection.addCategory(Intent.CATEGORY_OPENABLE);
            jumpSelection.setType("image/*");
            Intent it = new Intent(Intent.ACTION_CHOOSER);
            it.putExtra(Intent.EXTRA_INTENT, jumpSelection);
            it.putExtra(Intent.EXTRA_TITLE, "选择文件来上传~");
            startActivityForResult(it, 2);
            return true;
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            // super.onShowCustomView(view, callback);
            if (isPortait) {
                isPortait = false;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            mDisplay.setVisibility(View.GONE);
            if (VideoPlayerView != null) {
                callback.onCustomViewHidden();
                return;
            }
            PlayerHolder.addView(view);
            VideoPlayerView = view;
            mPlayerCallBack = callback;
            PlayerHolder.setVisibility(View.VISIBLE);
        }

        @Override
        public void onHideCustomView() {
            //super.onHideCustomView();
            if (VideoPlayerView != null) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                VideoPlayerView.setVisibility(View.GONE);
                PlayerHolder.removeView(VideoPlayerView);
                VideoPlayerView = null;
                PlayerHolder.setVisibility(View.GONE);
                mPlayerCallBack.onCustomViewHidden();
                mDisplay.setVisibility(View.VISIBLE);
            }
        }
    }

}
