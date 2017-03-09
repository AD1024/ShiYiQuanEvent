package ccoderad.bnds.shiyiquanevent.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.adapters.FavEventListAdapter;
import ccoderad.bnds.shiyiquanevent.beans.EventBean;
import ccoderad.bnds.shiyiquanevent.global.SearchTypeConstants;
import ccoderad.bnds.shiyiquanevent.utils.ToastUtil;
import ccoderad.bnds.shiyiquanevent.utils.Utils;

public class FavEventActivity extends AppCompatActivity {

    private final String FAV_FILE = "FavedEvents.json";
    SearchBox mSearchBox;
    FavEventListAdapter mAdapter;
    private long timeGap = 0;
    private Toolbar mToolbar;
    private List<EventBean> mData;
    private ListView mListView;
    private Map<String, Boolean> kv;
    private TextView mNoFavIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ToastUtil.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_event);
        mNoFavIndicator = (TextView) findViewById(R.id.faved_event_none_indicator);
        mToolbar = (Toolbar) findViewById(R.id.favd_toolbar);
        mToolbar.inflateMenu(R.menu.faved_toolbar);
        mToolbar.setTitle("我的收藏");
        mListView = (ListView) findViewById(R.id.faved_list);
        mSearchBox = (SearchBox) findViewById(R.id.faved_event_search_box);
        mSearchBox.enableVoiceRecognition(this);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openSearch();
                return true;
            }
        });
        mToolbar.setTitleTextColor(Color.WHITE);
        this.setSupportActionBar(mToolbar);
        LoadFavData();
        if (mData.size() > 0) {
            mAdapter = new FavEventListAdapter(this, mData);
            mListView.setAdapter(mAdapter);
        } else {
            mNoFavIndicator.setVisibility(View.VISIBLE);
        }
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(FavEventActivity.this)
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ToastUtil.makeText("呜呜呜，竟然抛弃伦家", false);
                                EventBean del = mData.get(position);
                                File cacheFav = new File(Utils.getCacheFile(FavEventActivity.this, "event"), FAV_FILE);
                                InputStream in;
                                try {
                                    in = new FileInputStream(cacheFav);
                                    String result = Utils.ReadStringFromInputStream(in);
                                    Log.i("Fav", result);
                                    JSONArray saved;
                                    if (TextUtils.isEmpty(result)) saved = new JSONArray();
                                    else saved = new JSONArray(result);
                                    JSONObject obj;
                                    JSONObject data;
                                    for (int i = 0; i < saved.length(); i++) {
                                        if (saved.get(i).equals(null)) continue;
                                        obj = saved.getJSONObject(i);
                                        data = obj.getJSONObject("data");
                                        if (data.getString("content").equals(del.eventContent)
                                                && obj.getString("sponsor_fname").equals(del.sponsorName)) {
                                            saved.remove(i);
                                            break;
                                        }
                                    }
                                    Log.i("FavDelete", saved.toString());
                                    if (cacheFav.exists()) {
                                        cacheFav.delete();
                                    }
                                    cacheFav.createNewFile();
                                    PrintStream writer = new PrintStream(new FileOutputStream(cacheFav));
                                    writer.print(saved.toString());
                                    writer.close();
                                    in.close();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                mData.remove(position);
                                if (mData.size() == 0) {
                                    mNoFavIndicator.setVisibility(View.VISIBLE);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        }).setNegativeButton("保留", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setTitle("删除Or保留QwQ?").setMessage("要和宝宝说再见了吗QAQ?").show();
                return false;
            }
        });
    }

    private void LoadFavData() {
        File favFile = new File(Utils.getCacheFile(this, "event"), FAV_FILE);
        InputStream is;
        mData = new ArrayList<>();
        try {
            is = new FileInputStream(favFile);
            String data = Utils.ReadStringFromInputStream(is);
            Log.i("FavData", data);
            JSONArray array = new JSONArray(data);
            mData = Utils.parseEvent(array);
            return;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            FavEventActivity.this.finish();
        } else {
            openSearch();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.faved_toolbar, menu);
        return true;
    }

    private boolean judgeExistence(String s) {
        return kv.containsKey(s);
    }

    private void openSearch() {
        // mToolbar.setTitle("");
        mSearchBox.setLogoText("输入搜索内容(日期,地点,发起社团)");
        mSearchBox.revealFromMenuItem(R.id.searchButton, this);
        mSearchBox.setMenuListener(new SearchBox.MenuListener() {
            @Override
            public void onMenuClick() {
                hideSearch();
            }
        });
        mListView.setPadding(10, 30, 10, 0);
        kv = new HashMap<String, Boolean>();
        mListView.setVisibility(View.INVISIBLE);
        mSearchBox.setSearchListener(new SearchBox.SearchListener() {
            @Override
            public void onSearchOpened() {
                kv.clear();
            }

            @Override
            public void onSearchCleared() {
                mSearchBox.setLogoText("输入搜索内容(日期,地点,发起社团)");
            }

            @Override
            public void onSearchClosed() {
                hideSearch();
                mToolbar.setTitle("我的收藏");
            }

            @Override
            public void onSearchTermChanged(String s) {
                if (System.currentTimeMillis() - timeGap < 1500) {
                    timeGap = System.currentTimeMillis();
                    return;
                }
                timeGap = System.currentTimeMillis();
                if (TextUtils.isEmpty(s) || s.equals(" ")) {
                    mSearchBox.clearSearchable();
                }

                kv.clear();

                mSearchBox.clearSearchable();

                for (int i = 0; i < mData.size(); i++) {
                    EventBean bean = mData.get(i);
                    if (bean.eventTitle.contains(s) && !s.isEmpty() && !s.equals(" ")) {
                        if (!judgeExistence(bean.eventTitle)) {
                            SearchResult result = new SearchResult(
                                    bean.eventTitle,
                                    getResources().getDrawable(R.drawable.ic_history_blue_grey_500_18dp));
                            result.viewType = SearchTypeConstants.TITLE;
                            mSearchBox.addSearchable(result);
                            kv.put(bean.eventTitle, true);
                        }
                    }

                    if (bean.eventLocation.contains(s) && !s.isEmpty() && !s.equals(" ")) {
                        if (!judgeExistence(bean.eventLocation)) {
                            SearchResult result = new SearchResult(bean.eventLocation, getResources().getDrawable(R.drawable.ic_location));
                            result.viewType = SearchTypeConstants.LOCATION;
                            mSearchBox.addSearchable(result);
                            kv.put(bean.eventLocation, true);
                        }
                    }

                    if (bean.eventDate.contains(s) && !s.isEmpty() && !s.equals(" ")) {
                        if (!judgeExistence(bean.eventDate)) {
                            SearchResult result = new SearchResult(bean.eventDate, getResources().getDrawable(R.drawable.ic_date));
                            result.viewType = SearchTypeConstants.DATE;
                            mSearchBox.addSearchable(result);
                            kv.put(bean.eventDate, true);
                        }
                    }

                    if (bean.sponsorName.contains(s) && !s.isEmpty() && !s.equals(" ")) {
                        if (!judgeExistence(bean.sponsorName)) {
                            SearchResult result = new SearchResult(bean.sponsorName, getResources().getDrawable(R.drawable.ic_sponsor_in_search));
                            result.viewType = SearchTypeConstants.CLUB;
                            mSearchBox.addSearchable(result);
                            kv.put(bean.sponsorName, true);
                        }
                    }
                }
            }

            @Override
            public void onSearch(String s) {
                hideSearch();
                List<EventBean> searchs = new ArrayList<EventBean>();
                for (int i = 0; i < mData.size(); i++) {
                    EventBean bean = mData.get(i);
                    if (bean.eventTitle.contains(s) || bean.eventLocation.contains(s)
                            || bean.eventDate.contains(s) || bean.sponsorName.contains(s)) {
                        searchs.add(bean);
                    }
                }
                Intent intent = new Intent(FavEventActivity.this, FavEventSearchResultActivity.class);
                intent.putExtra("SearchData", (Serializable) searchs);
                intent.putExtra("ViewType", SearchTypeConstants.UNIVERSAL);
                startActivity(intent);
            }

            @Override
            public void onResultClick(SearchResult searchResult) {
                hideSearch();
                List<EventBean> mSearchData = new ArrayList<EventBean>();
                switch (searchResult.viewType) {
                    case SearchTypeConstants.TITLE:
                        for (int i = 0; i < mData.size(); i++) {
                            if (mData.get(i).eventTitle.equals(searchResult.title)) {
                                mSearchData.add(mData.get(i));
                            }
                        }
                        break;
                    case SearchTypeConstants.LOCATION:
                        for (int i = 0; i < mData.size(); i++) {
                            if (mData.get(i).eventLocation.equals(searchResult.title)) {
                                mSearchData.add(mData.get(i));
                            }
                        }
                        break;
                    case SearchTypeConstants.DATE:
                        for (int i = 0; i < mData.size(); i++) {
                            if (mData.get(i).eventDate.equals(searchResult.title)) {
                                mSearchData.add(mData.get(i));
                            }
                        }
                        break;
                    case SearchTypeConstants.CLUB:
                        for (int i = 0; i < mData.size(); i++) {
                            if (mData.get(i).sponsorName.equals(searchResult.title)) {
                                mSearchData.add(mData.get(i));
                            }
                        }
                        break;
                }
                Intent jump = new Intent(FavEventActivity.this, FavEventSearchResultActivity.class);
                jump.putExtra("SearchData", (Serializable) mSearchData);
                jump.putExtra("ViewType", searchResult.viewType);
                startActivity(jump);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SearchBox.VOICE_RECOGNITION_CODE && resultCode == RESULT_OK){
            ArrayList<String> voiceData = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String msg = voiceData.get(0);
            String real = msg.substring(0,msg.length()-1);
            mSearchBox.populateEditText(real);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (mSearchBox.getSearchOpen()) {
            hideSearch();
            mToolbar.setTitle("我的收藏");
        } else {
            super.onBackPressed();
            ToastUtil.cancel();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        ToastUtil.cancel();
    }

    protected void hideSearch() {
        mListView.setVisibility(View.VISIBLE);
        mSearchBox.hideCircularly(this);
        mListView.setPadding(10, 0, 10, 0);
        mToolbar.setTitle("我的收藏");
        mListView.setAdapter(mAdapter);
    }
}
