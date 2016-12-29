package ccoderad.bnds.shiyiquanevent.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

import ccoderad.bnds.shiyiquanevent.adapters.FavEventListAdapter;
import ccoderad.bnds.shiyiquanevent.beans.EventBean;
import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.global.SearchTypeConstances;

public class FavEventSearchResultActivity extends AppCompatActivity {


    private ListView mSearchResultList;
    private List<EventBean> mSearchData;
    private FavEventListAdapter mAdapter;
    private int ViewType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_event_search_result);
        mSearchResultList = (ListView) findViewById(R.id.fav_event_search_result_list);
        Intent intent = getIntent();
        mSearchData = (List<EventBean>) intent.getSerializableExtra("SearchData");
        mAdapter = new FavEventListAdapter(this,mSearchData);
        mSearchResultList.setAdapter(mAdapter);
        ViewType = intent.getIntExtra("ViewType",-1);
        switch (ViewType){
            case -1:{
                setTitle("Error呀QAQ");break;
            }
            case SearchTypeConstances.TITLE:{
                setTitle("标题搜索结果");break;
            }
            case SearchTypeConstances.LOCATION:{
                setTitle("地点搜索结果");break;
            }
            case SearchTypeConstances.DATE:{
                setTitle("日期搜索结果");break;
            }
            case SearchTypeConstances.CLUB:{
                setTitle("发起社团搜索结果");break;
            }
            case SearchTypeConstances.UNIVERSAL:{
                setTitle("综合查询结果");break;
            }
        }
    }
}
