package ccoderad.bnds.shiyiquanevent.Activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ccoderad.bnds.shiyiquanevent.Adapters.EventListAdapter;
import ccoderad.bnds.shiyiquanevent.Beans.EventBean;
import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.utils.Utils;

public class FavEventActivity extends AppCompatActivity {

    private final String FAV_FILE = "FavedEvents.json";

    private Toolbar mToolbar;
    SearchBox mSearchBox;
    private List<EventBean> mData;
    private ListView mListView;
    private Map<String, String> kv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_event);
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
                Log.i("OnClickTest", "Clicked");
                return true;
            }
        });
        mToolbar.setTitleTextColor(Color.WHITE);
        this.setSupportActionBar(mToolbar);
        LoadFavData();
        if (mData.size() > 0)
            mListView.setAdapter(new EventListAdapter(this, mData));
        else {
            Toast.makeText(this, "啊哦，你还没收藏任何活动呢", Toast.LENGTH_LONG).show();
        }
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(FavEventActivity.this)
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(FavEventActivity.this,"呜呜呜，竟然抛弃伦家",Toast.LENGTH_LONG).show();
                        EventBean del = mData.get(position);
                        File cacheFav = new File(new Utils().getCacheFile(FavEventActivity.this,"event"),FAV_FILE);
                        InputStream in;
                        try {
                            in = new FileInputStream(cacheFav);
                            String result = new Utils().ReadStringFromInputStream(in);
                            JSONArray saved;
                            if(result.isEmpty()) saved = new JSONArray();
                            else saved = new JSONArray(result);
                            JSONObject obj;
                            JSONObject data;
                            for(int i=0;i<saved.length();i++){
                                if(saved.get(i).equals(null)) continue;
                                obj = saved.getJSONObject(i);
                                data=obj.getJSONObject("data");
                                if(data.getString("content").equals(del.eventContent)
                                        && obj.getString("sponsor_fname").equals(del.sponsorName)){
                                    saved.remove(i);break;
                                }
                            }
                            PrintStream writer = new PrintStream(new FileOutputStream(cacheFav));
                            cacheFav.createNewFile();
                            writer.close();
                            in.close();
                            writer.print(saved.toString());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
        File favFile = new File(new Utils().getCacheFile(this, "event"), FAV_FILE);
        InputStream is;
        try {
            is = new FileInputStream(favFile);
            String data = new Utils().ReadStringFromInputStream(is);
            JSONArray array = new JSONArray(data);
            mData = new Utils().parseEvent(array);
            return;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mData = new ArrayList<>();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        openSearch();
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
        mToolbar.setTitle("");
        mSearchBox.setLogoText("输入搜索内容(日期,地点,发起社团)");
        mSearchBox.revealFromMenuItem(R.id.searchButton, this);
        mSearchBox.setMenuListener(new SearchBox.MenuListener() {
            @Override
            public void onMenuClick() {
                hideSearch();
            }
        });
        mListView.setPadding(10,30,10,0);
        kv = new HashMap<String, String>();
        mListView.setVisibility(View.INVISIBLE);
        mSearchBox.setSearchListener(new SearchBox.SearchListener() {
            @Override
            public void onSearchOpened() {

            }

            @Override
            public void onSearchCleared() {
                mSearchBox.setLogoText("输入搜索内容(日期,地点,发起社团)");
            }

            @Override
            public void onSearchClosed() {
                hideSearch();
            }

            @Override
            public void onSearchTermChanged(String s) {

                if(s.equals("") || s.equals(" ")){
                    mSearchBox.clearSearchable();
                }

                for (int i = 0; i < mData.size(); i++) {
                    EventBean bean = mData.get(i);
                    if (bean.eventTitle.contains(s) && !s.isEmpty() && !s.equals(" ")) {
                        if (!judgeExistence(bean.eventTitle)) {
                            SearchResult result = new SearchResult(
                                    bean.eventTitle,
                                    getResources().getDrawable(R.drawable.ic_history_blue_grey_500_18dp));
                            result.viewType = 0;
                            mSearchBox.addSearchable(result);
                            kv.put(bean.eventTitle, null);
                        }
                    }

                    if (bean.eventLocation.contains(s) && !s.isEmpty() && !s.equals(" ")) {
                        if (!judgeExistence(bean.eventLocation)) {
                            SearchResult result = new SearchResult(bean.eventLocation, getResources().getDrawable(R.drawable.ic_location));
                            result.viewType = 1;
                            mSearchBox.addSearchable(result);
                            kv.put(bean.eventLocation, null);
                        }
                    }

                    if (bean.eventDate.contains(s) && !s.isEmpty() && !s.equals(" ")) {
                        if (!judgeExistence(bean.eventDate)) {
                            SearchResult result = new SearchResult(bean.eventDate, getResources().getDrawable(R.drawable.ic_date));
                            result.viewType = 2;
                            mSearchBox.addSearchable(result);
                            kv.put(bean.eventDate,null);
                        }
                    }

                    if (bean.sponsorName.contains(s) && !s.isEmpty() && !s.equals(" ")) {
                        if (!judgeExistence(bean.sponsorName)) {
                            SearchResult result = new SearchResult(bean.sponsorName, getResources().getDrawable(R.drawable.ic_sponsor_in_search));
                            result.viewType = 3;
                            mSearchBox.addSearchable(result);
                            kv.put(bean.sponsorName,null);
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
                mListView.setAdapter(new EventListAdapter(FavEventActivity.this, searchs));
            }

            @Override
            public void onResultClick(SearchResult searchResult) {
                hideSearch();
                switch (searchResult.viewType) {
                    case 0:
                        for (int i = 0; i < mData.size(); i++) {
                            if (mData.get(i).eventTitle.equals(searchResult.title)) {
                                mListView.smoothScrollToPosition(i);
                                break;
                            }
                        }
                        break;
                    case 1:
                        for (int i = 0; i < mData.size(); i++) {
                            if (mData.get(i).eventLocation.equals(searchResult.title)) {
                                mListView.smoothScrollToPosition(i);
                                break;
                            }
                        }
                        break;
                    case 2:
                        for (int i = 0; i < mData.size(); i++) {
                            if (mData.get(i).eventDate.equals(searchResult.title)) {
                                mListView.smoothScrollToPosition(i);
                                break;
                            }
                        }
                        break;
                    case 3:
                        for (int i = 0; i < mData.size(); i++) {
                            if (mData.get(i).sponsorName.equals(searchResult.title)) {
                                mListView.smoothScrollToPosition(i);
                                break;
                            }
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mSearchBox.getSearchOpen()) {
            hideSearch();
        } else
            super.onBackPressed();
    }

    protected void hideSearch() {
        mListView.setVisibility(View.VISIBLE);
        mSearchBox.hideCircularly(this);
        mListView.setPadding(10,0,10,0);
        mToolbar.setTitle("我的收藏");
        mListView.setAdapter(new EventListAdapter(this, mData));
    }
}
