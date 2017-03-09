package ccoderad.bnds.shiyiquanevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.adapters.EventSimpListAdapter;
import ccoderad.bnds.shiyiquanevent.beans.EventBean;

public class EventSimpListActivity extends AppCompatActivity {

    private ListView mEventList;
    private TextView mNoDataIndicator;

    private List<EventBean> mData;

    private EventSimpListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_simp_list);

        Intent data = getIntent();
        mData = (List<EventBean>) data.getSerializableExtra("eventData");
        String clubName = data.getStringExtra("clubName");
        setTitle(clubName + "的最近活动");

        mEventList = (ListView) findViewById(R.id.event_simp_list);
        mNoDataIndicator = (TextView) findViewById(R.id.event_simp_list_no_data_indicator);

        if(mData.size() == 0){
            mNoDataIndicator.setVisibility(View.VISIBLE);
        }
        mAdapter = new EventSimpListAdapter(this, mData);
        mEventList.setAdapter(mAdapter);
    }
}
