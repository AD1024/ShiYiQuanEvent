package ccoderad.bnds.shiyiquanevent.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.adapters.ClubSquareAdapter;
import ccoderad.bnds.shiyiquanevent.beans.ClubDetailModel;
import ccoderad.bnds.shiyiquanevent.global.PreferencesConstants;
import ccoderad.bnds.shiyiquanevent.global.URLConstants;
import ccoderad.bnds.shiyiquanevent.listeners.RecyclerViewItemClickListener;
import ccoderad.bnds.shiyiquanevent.utils.CacheUtils;
import ccoderad.bnds.shiyiquanevent.utils.MultiThreadUtil;
import ccoderad.bnds.shiyiquanevent.utils.ToastUtil;
import ccoderad.bnds.shiyiquanevent.utils.Utils;
import ccoderad.bnds.shiyiquanevent.utils.ViewTools;
import cn.bingoogolapple.photopicker.util.BGASpaceItemDecoration;

public class ClubSquareActivity extends AppCompatActivity implements View.OnClickListener, XRecyclerView.LoadingListener, RecyclerViewItemClickListener {
    //Constants
    private final String REQUEST_URL = URLConstants.HOME_URL + URLConstants.QUARE_URL + "?api=True";
    Set<String> mRecoder;
    // Plugins
    private XRecyclerView mClubList;
    private Button mSearchClub;
    private FloatingActionButton mBackTop;
    private EditText mClubNameSearch;
    private View mNoInfoIndicator;
    private Spinner mClubCategory;
    private RecyclerView.LayoutManager mLayoutManager;
    //Data sets
    private List<ClubDetailModel> mDataList;
    private boolean islogined;
    private int mIndex = 1;
    private LayoutInflater mInflater;
    private RequestQueue mRequestQueue;
    private ClubSquareAdapter mClubListAdapter;
    private SharedPreferences mPreference;
    // Data Writers
    private InputStream mInputDataWriter;
    private OutputStream mOutputDataWriter;
    private File mCacheDir;
    private File eventCacheDir;
    private JSONArray mCacheData;

    /*
    * Initializer
    * */
    private void Initialize() {

        ToastUtil.initialize(this);

        mInflater = LayoutInflater.from(this);
        mDataList = new ArrayList<>();
        mClubListAdapter = new ClubSquareAdapter(this, mDataList);
        mClubListAdapter.setOnClubItemClickListener(this);
        mPreference = getSharedPreferences(PreferencesConstants.LOGIN_INFO, MODE_PRIVATE);
        islogined = mPreference.getBoolean(PreferencesConstants.LOGIN_INFO, false);
        mBackTop = (FloatingActionButton) findViewById(R.id.club_square_fab);
        mClubList = (XRecyclerView) findViewById(R.id.club_square_list);
        View headerView = mInflater.inflate(R.layout.club_square_header, null);
        mRecoder = new HashSet<>();

        // View Injection
        mSearchClub = (Button) headerView.findViewById(R.id.club_square_list_header_btn);
        mClubNameSearch = (EditText) headerView.findViewById(R.id.club_square_search_text);
        // mClubList.addHeaderView(headerView);
        mClubCategory = (Spinner) findViewById(R.id.club_square_category);
        mSearchClub.setOnClickListener(this);
        mClubList.setAdapter(mClubListAdapter);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mClubList.setLayoutManager(mLayoutManager);
        mClubList.addItemDecoration(new BGASpaceItemDecoration(10));
        mClubList.setLoadingMoreEnabled(true);
        mBackTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClubList.smoothScrollToPosition(0);
            }
        });
        mNoInfoIndicator = findViewById(R.id.club_square_no_info_indicator);
        // Cache Configuration
        mCacheDir = Utils.getCacheFileDir(this);
        eventCacheDir = new File(mCacheDir, CacheUtils.CLUB_SQUARE_CACHE_DIR_NAME);
    }

    /*
    * Logic Methods
    * */

    @NonNull
    private String GetClubType() {
        String TypeName = mClubCategory.getSelectedItem().toString();
        if (TypeName.equals("社团一览")) {
            return "all";
        } else if (TypeName.equals("冻鳗")) {
            return "interest";
        } else if (TypeName.equals("信息技术")) {
            return "technology";
        } else if (TypeName.equals("影视新闻传媒")) {
            return "media";
        } else if (TypeName.equals("体育")) {
            return "sport";
        } else if (TypeName.equals("艺术")) {
            return "art";
        } else if (TypeName.equals("校园")) {
            return "campus";
        } else if (TypeName.equals("慈善")) {
            return "charity";
        } else if (TypeName.equals("自然科学")) {
            return "nature-sci";
        } else if (TypeName.equals("社会科学")) {
            return "social-sci";
        } else if (TypeName.equals("商业")) {
            return "business";
        } else if (TypeName.equals("舞台设计")) {
            return "stage";
        } else if (TypeName.equals("手工")) {
            return "manufacture";
        } else if (TypeName.equals("文学")) {
            return "publishment";
        } else if (TypeName.equals("语言")) {
            return "language";
        } else if (TypeName.equals("其他")) {
            return "other";
        } else {
            return "other";
        }
    }

    private void GetSquareData() {
        mClubList.setLoadingMoreEnabled(true);
        setTitle("少女祈祷中...");
        String RequestURL = "";
        RequestURL = REQUEST_URL + "&index=" + Utils.Int2String(mIndex) + "&category=" + GetClubType();
        Log.i("RequestURL", RequestURL);
        JsonArrayRequest mRequest = new JsonArrayRequest(RequestURL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                HandleSquareData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ErrorFetchingData", error.toString());
                ToastUtil.makeText("好似出现了些小问题QAQ", false);
                if (Utils.isNetWorkAvailable(ClubSquareActivity.this)) {
                    setTitle("AD1024被折寿1s");
                } else {
                    setTitle("进入了没有网络的异次元...");
                }
            }
        });
        mRequest.setRetryPolicy(MultiThreadUtil.createDefaultRetryPolicy());
        mRequestQueue.add(mRequest);
    }

    private void WriteCache(JSONArray Data) {
        File eventCacheDir = new File(mCacheDir, CacheUtils.CLUB_SQUARE_CACHE_DIR_NAME);
        if (!eventCacheDir.exists()) eventCacheDir.mkdir();
        File eventCache = new File(eventCacheDir, CacheUtils.CLUB_SQUARE_CACHE_NAME);
        CacheUtils.flushToCache(eventCache, Data.toString());
    }

    @Nullable
    private JSONArray ReadCache() {
        File cacheFile = new File(eventCacheDir, CacheUtils.EVENT_CACHE_NAME);
        try {
            JSONArray ret = new JSONArray(CacheUtils.readStringFromCache(cacheFile));
            return ret;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void HandleSquareData(JSONArray DataSet) {
        WriteCache(DataSet);
        JSONObject ClubData;
        JSONArray Followee;
        JSONObject Info;
        if (DataSet.length() == 0) {
            setTitle("社团广场");
            mNoInfoIndicator.setVisibility(View.VISIBLE);
            mClubList.refreshComplete();
            return;
        }else{
            mNoInfoIndicator.setVisibility(View.GONE);
        }
        for (int i = 0; i < DataSet.length(); ++i) {
            try {
                // Get Out One Club Data
                ClubData = DataSet.getJSONObject(i);
                ClubDetailModel infoData = new ClubDetailModel();

                // Handle Outer data
                infoData.Like = ClubData.getInt("follow");
                infoData.Category = ClubData.getString("category");
                infoData.Followed = ClubData.getBoolean("followed");

                // Handle Followees' data
                Followee = ClubData.getJSONArray("followee");
                for (int j = 0; j < Followee.length(); ++j) {
                    infoData.Followee.add(Followee.getString(j));
                }

                // Get Inner Data
                Info = ClubData.getJSONObject("data"); // Inner Dataset
                infoData.club_name = Info.getString("full_name"); // Full club name
                infoData.sname = Info.getString("simp_name"); // Short name(can be used to parse homepage)
                infoData.SimpleIntro = Info.getString("simp_intro"); // short intro
                infoData.ClubDescription = Info.getString("full_intro"); // full intro

                // Put into mRecoder to judge the page id
                if (mRecoder.contains(infoData.club_name)) {
                    mClubList.refreshComplete();
                    mClubList.loadMoreComplete();
                    ToastUtil.makeText("到头啦", false);
                    mClubList.setLoadingMoreEnabled(false);
                    setTitle("社团广场");
                    return;
                } else {
                    mRecoder.add(infoData.club_name);
                }

                // Handle Avatar Data
                Info = ClubData.getJSONObject("avatar");
                infoData.smallAvatarURL = Info.getString("small");
                infoData.mediumAvatarURL = Info.getString("medium");
                infoData.LargeAvatarURL = Info.getString("large");

                // Handle Avatar URL and HomePage URL
                infoData.parseURL();
                infoData.ChangeHomePage("club/" + infoData.sname);

                // Insert data
                mDataList.add(infoData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mClubList.refreshComplete();
            mClubList.loadMoreComplete();
            setTitle("社团广场");
        }
    }

    /*
    * Global Onclick Listener
    * */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.club_square_list_header_btn: {
                // Stub
                String ClubName = mClubNameSearch.getText().toString();
                break;
            }
        }
    }

    /*
    * XRecyclerView Listeners
    *
    * */
    @Override
    public void onRefresh() {
        mDataList.clear();
        mRecoder.clear();
        mIndex = 1;
        GetSquareData();
        mClubListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onLoadMore() {
        mIndex++;
        GetSquareData();
        mClubListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClubItemClick(View v, int position) {
        Bundle Data = new Bundle();
        ClubDetailModel model = mDataList.get(position - 1);
        Data.putSerializable("ClubData", model);
        Intent it = new Intent(this, ClubInfoDetailActivity.class);
        it.putExtras(Data);
        startActivity(it);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        mRequestQueue = Volley.newRequestQueue(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_square);
        Initialize();
        if (!Utils.isNetWorkAvailable(this)) {
            JSONArray cacheData = ReadCache();
            if (cacheData != null) {
                HandleSquareData(cacheData);
            }
        }

        // Configure XRecyclerView
        mClubList.setRefreshProgressStyle(ProgressStyle.BallPulse);
        mClubList.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        mClubList.setLoadingListener(this);
        // Spinner Configuration
        mClubCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mIndex = 1;
                mDataList.clear();
                GetSquareData();
                mClubListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToastUtil.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ToastUtil.cancel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ToastUtil.cancel();
    }
}
