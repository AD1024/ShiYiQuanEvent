package ccoderad.bnds.shiyiquanevent.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.adapters.ClubSimpListAdapter;
import ccoderad.bnds.shiyiquanevent.beans.ClubModel;
import ccoderad.bnds.shiyiquanevent.utils.ToastUtil;

public class ClubSimpListActivity extends AppCompatActivity {

    private ListView mClubList;
    private List<ClubModel> userClubData;
    private TextView mNoDataIndicator;

    private ClubSimpListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ToastUtil.initialize(this);
        setContentView(R.layout.activity_club_simp_list);
        Intent itData = getIntent();
        String type = itData.getStringExtra("IntentType");
        if(type == null) {
            ToastUtil.makeText("出错啦", true);
        }else if(type.equals("follow")){
            setTitle("我关注的社团");
        }else if(type.equals("join")){
            setTitle("我参加的社团");
        }

        mClubList = (ListView) findViewById(R.id.simp_club_list);
        mNoDataIndicator = (TextView) findViewById(R.id.club_simp_no_data_indicator);
        userClubData = (List<ClubModel>) itData.getSerializableExtra("clubData");
        if(userClubData.size() == 0){
            mNoDataIndicator.setVisibility(View.VISIBLE);
        }
        mAdapter = new ClubSimpListAdapter(this, userClubData);
        mClubList.setAdapter(mAdapter);

        mClubList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(ClubSimpListActivity.this, ClubInfoActivity.class);
                it.putExtra("clubUrl", userClubData.get(position).sname);
                startActivity(it);
            }
        });
    }
}
