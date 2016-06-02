package ccoderad.bnds.shiyiquanevent.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.jakewharton.disklrucache.DiskLruCache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ccoderad.bnds.shiyiquanevent.Adapters.EventListAdapter;
import ccoderad.bnds.shiyiquanevent.Beans.EventBean;
import ccoderad.bnds.shiyiquanevent.Beans.IntentResult;
import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.db.DatabaseHelper;
import ccoderad.bnds.shiyiquanevent.utils.IntentIntegrator;
import com.google.zxing.client.android.*;
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {
    final long MAX_MEM = 30* ByteConstants.MB;
    final long MAX_LOW_MEM = 10* ByteConstants.MB;
    final long MAX_VERY_LOW_MEM = 5* ByteConstants.MB;

    private ListView mListView;
    private SwipeRefreshLayout mRefersh;
    private long time=0;
    private List<EventBean> mData;
    private GetEventTask mTask;
    private int mY=0;
    private boolean isLongClicked=false;
    private DiskLruCache mCache;
    private DiskCacheConfig mImageCacheConfig;
    private ImagePipelineConfig mImageCachePiplineConfig;
    private  FloatingActionButton fab;
    private DatabaseHelper mHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mCache = null;
        fab = (FloatingActionButton) findViewById(R.id.fab);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.smoothScrollToPosition(0);
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mListView = (ListView) findViewById(R.id.event_list);
        mListView.setOnItemClickListener(this);
        //ListView Motion Listener:
        /*
        * It is used to detect the direction of sliding of the listview
        * */
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_MOVE:
                        int y = (int) event.getY();
                        if (y > mY) {
                            fab.show();
                        } else {
                            fab.hide();
                        }
                        mY = y;
                }
                return false;
            }
        });
        //Interaction of FAB
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                isLongClicked=true;
                ImageView alter = (ImageView) view.findViewById(R.id.event_list_item_fav);
                if (mData.get(position).isFaved) {
                    Snackbar.make(view, "已取消关注" + mData.get(position).eventTitle,
                                    Snackbar.LENGTH_SHORT).show();
                    mData.get(position).isFaved = false;
                    alter.setImageResource(R.drawable.ic_favorite_border);
                } else {
                    Snackbar.make(view, "已关注" + mData.get(position).eventTitle,
                                    Snackbar.LENGTH_SHORT).show();
                    mData.get(position).isFaved = true;
                    alter.setImageResource(R.drawable.ic_favorite);
                }
                return false;
            }
        });
        mRefersh = (SwipeRefreshLayout) findViewById(R.id.event_refresh);
        mRefersh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Cancel current asynctack and start a new fetching
                mTask.cancel(true);
                mTask=new GetEventTask();
                mTask.execute("http://shiyiquan.net/api/?category=event&time=latest");
            }
        });
        mTask=new GetEventTask();
        mTask.execute("http://shiyiquan.net/api/?category=event&time=latest");
        mData = new ArrayList<>();
        mRefersh.setColorSchemeColors(Color.rgb(10, 180, 226), Color.rgb(3, 133, 167));
        mRefersh.setSize(25);
        mImageCacheConfig = DiskCacheConfig.newBuilder(this)
                .setBaseDirectoryPath(Environment.getExternalStorageDirectory().getAbsoluteFile())
                .setBaseDirectoryName("ShiyiquanEvent")
                .setMaxCacheSize(MAX_MEM)
                .setMaxCacheSizeOnLowDiskSpace(MAX_LOW_MEM)
                .setMaxCacheSizeOnVeryLowDiskSpace(MAX_VERY_LOW_MEM)
                .build();
        mImageCachePiplineConfig = ImagePipelineConfig.newBuilder(this).setMainDiskCacheConfig(mImageCacheConfig).build();

    }
    //ListView OnItemCLickListener

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(isLongClicked){
            isLongClicked=false;
            return;
        }

        View window = LayoutInflater.from(this).inflate(R.layout.event_alert,null);
        View Header = LayoutInflater.from(this).inflate(R.layout.event_alert_header,null);
        //ViewInjection
        TextView tvTitle = (TextView) window.findViewById(R.id.event_alert_title);
        TextView tvStart = (TextView) window.findViewById(R.id.event_alert_start);
        TextView tvDuration = (TextView) window.findViewById(R.id.event_alert_duration);
        TextView tvDescription = (TextView) window.findViewById(R.id.event_alert_description);
        TextView tvDate = (TextView) window.findViewById(R.id.event_alert_date);
        TextView tvSponsor = (TextView) window.findViewById(R.id.event_alert_sponsor);
        TextView tvLocation = (TextView) window.findViewById(R.id.event_alert_location);
        SimpleDraweeView pic = (SimpleDraweeView) window.findViewById(R.id.event_alert_pic);
        //DataAdaption
        EventBean bean = mData.get(position);
        tvTitle.setText(bean.eventTitle);
        tvDescription.setText(bean.eventContent);
        tvDate.setText(bean.eventDate);
        tvSponsor.setText(bean.sponsorName);
        tvStart.setText(bean.eventTime);
        tvDuration.setText(bean.eventDuration);
        tvLocation.setText(bean.eventLocation);
        bean.parseUrl();
        pic.setImageURI(Uri.parse(bean.eventAvatar));
        new AlertDialog.Builder(this).setView(window)
                .setCustomTitle(Header)
                .setPositiveButton("朕晓得了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();

    }
    //End OF listener

    class GetEventTask extends AsyncTask<String,Void,List<EventBean>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setTitle("加载中...");
        }

        @Override
        protected List<EventBean> doInBackground(String... params) {
            List<EventBean> ans = new ArrayList<>();
            try {
                InputStream is = new URL(params[0]).openStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String tmp="";
                String result = "";
                while((tmp=br.readLine())!=null){
                    result+=tmp;
                }
                JSONArray jsonArray = new JSONArray(result);
                JSONObject jsonObject;
                for(int i=0;i<jsonArray.length();i++){
                    if(mTask.isCancelled()){
                        return null;
                    }
                    EventBean bean = new EventBean();
                    jsonObject = jsonArray.getJSONObject(i);
                    JSONObject content = jsonObject.getJSONObject("data");
                    bean.eventTitle = content.getString("subject");
                    bean.eventContent = content.getString("content");
                    bean.eventLocation = content.getString("location");
                    bean.sponsorName = jsonObject.getString("sponsor_fname");
                    bean.eventAvatar = jsonObject.getString("avatar");
                    bean.eventDate = jsonObject.getString("day_set");
                    bean.eventTime = jsonObject.getString("time_set");
                    bean.eventDuration = jsonObject.getString("time_last");
                    bean.eventFollower = jsonObject.getInt("follower");
                    bean.parseUrl();
                    ans.add(bean);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("NetWorkError","CheckNetWork");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSONError","CheckJSON");
            }

            return ans;
        }

        @Override
        protected void onPostExecute(List<EventBean> eventBeans) {
            super.onPostExecute(eventBeans);
            if(mRefersh.isRefreshing()){
                mRefersh.setRefreshing(false);
            }
            setTitle("十一活动");
            for(int i=0;i<eventBeans.size();i++){
                if(eventBeans.get(i).sponsorName.length()>30){
                    EventBean bean=eventBeans.get(i);
                    String tar = bean.sponsorName.substring(0,20)+"\n"+"-"
                            +bean.sponsorName.substring(20,bean.sponsorName.length());
                    bean.sponsorName = tar;
                    eventBeans.remove(i);
                    eventBeans.add(i,bean);
                }
            }
            mData=eventBeans;
            EventListAdapter adapter = new EventListAdapter(MainActivity.this,eventBeans);
            mListView.setAdapter(adapter);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(System.currentTimeMillis()-time>2000){
             Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT).show();
              time=System.currentTimeMillis();
        }else if(!drawer.isDrawerOpen(GravityCompat.START)) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.go_to_browser:
                startActivity(new Intent(MainActivity.this,MainBrowser.class));
                break;
            case R.id.go_to_about_me:
                startActivity(new Intent(MainActivity.this,about_me.class));
                break;
            case R.id.go_to_scan:
                startActivity(new Intent(this,CaptureActivity.class));
                //startActivity(new Intent(this,CaptrueActivity.class));

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null){
            Intent it = new Intent(MainActivity.this,MainBrowser.class);
            it.putExtra("QR_CONTENT",result.getContents());
            startActivity(it);
        }
    }

    //Task recycler

    @Override
    protected void onDestroy() {
        if(mTask!=null && mTask.getStatus()== AsyncTask.Status.RUNNING){
            mTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        if(mTask!=null && mTask.getStatus()== AsyncTask.Status.RUNNING){
            mTask.cancel(true);
        }
        super.onStop();
    }
}
