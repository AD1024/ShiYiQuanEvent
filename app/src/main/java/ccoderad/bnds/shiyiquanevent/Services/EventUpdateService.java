package ccoderad.bnds.shiyiquanevent.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ccoderad.bnds.shiyiquanevent.Activities.MainActivity;
import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.utils.JSONGetter;

public class EventUpdateService extends Service {

    private static final String HOME_URL = "http://shiyiquan.net/api/?category=event&time=latest";
    List<String> mData;
    Context mParent;
    private JSONArray myData;
    NotificationCompat.Builder mBuilder;
    EventListenerBinder mBinder = new EventListenerBinder();
    public EventUpdateService(){

    }

    private JSONArray EventFetchTask(){
        JSONGetter mGetter = new JSONGetter(HOME_URL,false,mParent);
        return (JSONArray)mGetter.getResult();
    }

    private void listener(){
        JSONArray array = EventFetchTask();
        if(!array.equals(myData)) {
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    JSONObject cmp = myData.getJSONObject(i);
                    if(!object.equals(cmp)){
                        for (String name:mData
                             ) {
                            if(name.equals(object.getString("sponsor_fname"))){
                                mBuilder.setContentTitle("来自"+object.getString("sponsor_fname")+"的新活动")
                                        .setContentText(object.getJSONObject("data").getString("content"))
                                        .setSmallIcon(R.mipmap.logo)
                                        .setVibrate(new long[]{300,100})
                                        .setContentIntent(PendingIntent.getActivity(mParent,0,new Intent(mParent,MainActivity.class),0));
                                Notification noti=mBuilder.build();
                                noti.flags=Notification.FLAG_AUTO_CANCEL;
                                noti.notify();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Read Club info
        File file = new File(Environment.getExternalStorageDirectory().toString()
                +File.separator+"ShiYiQuan"+File.separator+"MyClub.json");
        InputStream is = null;
        mData=new ArrayList<>();
        try {
            is = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br  = new BufferedReader(isr);
            String res="";
            while((res=br.readLine())!=null){
                mData.add(res);
            }
        } catch (FileNotFoundException e) {
            Log.e("ServieErr","MyClub.json not found");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Runnable run = new Runnable() {
            @Override
            public void run() {
                listener();
                new Handler().postDelayed(this,600*1000);
            }
        };
    }

    public class EventListenerBinder extends Binder{
        public void modifyParent(Context content){
            mParent=content;
        }
    }
}
