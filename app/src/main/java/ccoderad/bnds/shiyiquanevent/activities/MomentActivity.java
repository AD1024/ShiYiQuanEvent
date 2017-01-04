package ccoderad.bnds.shiyiquanevent.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.adapters.MomentListAdapter;
import ccoderad.bnds.shiyiquanevent.beans.MomentDataModel;
import ccoderad.bnds.shiyiquanevent.global.URLConstances;
import ccoderad.bnds.shiyiquanevent.listeners.RecyclerViewItemClickListener;
import ccoderad.bnds.shiyiquanevent.utils.ToastUtil;
import ccoderad.bnds.shiyiquanevent.utils.Utils;

public class MomentActivity extends AppCompatActivity implements RecyclerViewItemClickListener {

    private double time_last;

    private List<MomentDataModel> mDataList;
    private MomentListAdapter mAdapter;

    private RequestQueue mRequestQueue;

    private XRecyclerView mRecyclerView;
    private TextView mLoadingIndicator;
    private FloatingActionButton mBackToTop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        ToastUtil.initialize(this);
        mRequestQueue = Volley.newRequestQueue(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment);
        mRecyclerView = (XRecyclerView) findViewById(R.id.moment_list);
        mLoadingIndicator = (TextView) findViewById(R.id.moment_list_loading_indicator);
        mBackToTop = (FloatingActionButton) findViewById(R.id.moment_list_fab);
        time_last = 0.0;
        mDataList = new ArrayList<>();

        mAdapter = new MomentListAdapter(this, mDataList);
        mRecyclerView.setAdapter(mAdapter);
        // Set RecyclerView Style
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.LineScalePulseOut);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallScaleRippleMultiple);
        mRecyclerView.setLoadingMoreEnabled(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this
                ,LinearLayoutManager.VERTICAL,false));
        // RecyclerView Listeners
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                time_last = 0.0;
                getMomentData();
            }

            @Override
            public void onLoadMore() {
                getMomentData();
            }
        });
        mAdapter.setListItemOnClickListener(this);

        mBackToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.smoothScrollToPosition(0);
            }
        });

        getMomentData();
    }

    /*
    * Because this interface was designed for ClubSquareActivity. Therefore the name of the
    * interface is onClubItemClick, but its function can be totally adopted in this activity
    * */
    @Override
    public void onClubItemClick(View v, int position) {

    }

    private void handleRawMomentData(String data) {
        try {
            JSONObject dataObject = new JSONObject(data);
            List<MomentDataModel> mTemp = new ArrayList<>();
            mTemp = Utils.parseMoment(dataObject);
            time_last = mTemp.get(0).timeStamp;
            Collections.reverse(mTemp);
            mDataList.addAll(mTemp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mDataList.size() > 0) {
            mAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.makeText("出现了蜜汁错误QAQ", true);
        }
        mRecyclerView.refreshComplete();
        mRecyclerView.loadMoreComplete();
        if(mLoadingIndicator.getVisibility() == View.VISIBLE){
            mLoadingIndicator.setVisibility(View.GONE);
        }
        setTitle("天台");
    }

    /*
    * Get Moment String from beta.shiyiquan.net
    * */
    private void getMomentData() {
        setTitle("嘿咻嘿咻~");
        if(mDataList.size() == 0){
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }
        DecimalFormat format = new DecimalFormat("###0.000000");
        String param = format.format(time_last);
        param = "-" + param;
        Log.i("TimeLast",param);
        String reqUrl = "";
        if (param.equals("0.0")) {
            reqUrl = URLConstances.MOMENT_URL;
        } else {
            reqUrl = URLConstances.MOMENT_URL + "?time_update=" + param;
        }
        StringRequest request = new StringRequest(reqUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                handleRawMomentData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.makeText("出现了蜜汁错误，请检查网络连接", false);
            }
        });
        mRequestQueue.add(request);
    }
}
