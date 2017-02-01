package ccoderad.bnds.shiyiquanevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.adapters.FavEventListAdapter;
import ccoderad.bnds.shiyiquanevent.beans.EventBean;
import ccoderad.bnds.shiyiquanevent.global.SearchTypeConstants;

public class FavEventSearchResultActivity extends AppCompatActivity {


    private ListView mSearchResultList;
    private List<EventBean> mSearchData;
    private FavEventListAdapter mAdapter;
    private int ViewType;
    private TextView mNoResultIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_event_search_result);
        mSearchResultList = (ListView) findViewById(R.id.fav_event_search_result_list);
        mNoResultIndicator = (TextView) findViewById(R.id.fav_event_search_result_no_result_indicator);
        Intent intent = getIntent();
        mSearchData = (List<EventBean>) intent.getSerializableExtra("SearchData");
        mAdapter = new FavEventListAdapter(this, mSearchData);
        mSearchResultList.setAdapter(mAdapter);
        ViewType = intent.getIntExtra("ViewType", -1);
        if (mSearchData.size() == 0) {
            mNoResultIndicator.setVisibility(View.VISIBLE);
        }
        switch (ViewType) {
            case -1: {
                setTitle("Error呀QAQ");
                break;
            }
            case SearchTypeConstants.TITLE: {
                setTitle("标题搜索结果");
                break;
            }
            case SearchTypeConstants.LOCATION: {
                setTitle("地点搜索结果");
                break;
            }
            case SearchTypeConstants.DATE: {
                setTitle("日期搜索结果");
                break;
            }
            case SearchTypeConstants.CLUB: {
                setTitle("发起社团搜索结果");
                break;
            }
            case SearchTypeConstants.UNIVERSAL: {
                setTitle("综合查询结果");
                break;
            }
        }
    }
}
