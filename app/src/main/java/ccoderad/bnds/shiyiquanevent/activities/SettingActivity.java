package ccoderad.bnds.shiyiquanevent.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.utils.Utils;


public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView CardSkipSplash;
    private CardView ClearPicCache;
    private CardView ClearEvent;
    private Switch SwitchSkipSplash;
    private SharedPreferences Preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(this).diskCache(new UnlimitedDiskCache(StorageUtils.getOwnCacheDirectory(this, "ShiyiquanImgs/Cache"))).build());

        // Init Preference
        Preferences = getSharedPreferences("Skip_Splash",MODE_PRIVATE);

        //ViewById
        CardSkipSplash = (CardView) findViewById(R.id.setting_skip_splash);
        ClearEvent = (CardView) findViewById(R.id.setting_clear_event_cache);
        ClearPicCache = (CardView) findViewById(R.id.setting_clear_pic_cache);
        SwitchSkipSplash = (Switch) findViewById(R.id.setting_switch_skip_splash);
        // OnClickListener
        ClearPicCache.setOnClickListener(this);
        CardSkipSplash.setOnClickListener(this);
        ClearEvent.setOnClickListener(this);
        SwitchSkipSplash.setChecked(Preferences.getBoolean("skip_splash_screen",false));

        //Switch Listener
        SwitchSkipSplash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSplashAvailability(isChecked);
            }
        });
    }

    private void setSplashAvailability(boolean check){
        boolean SplashA=check;
        SharedPreferences.Editor editor = Preferences.edit();
        editor.putBoolean("skip_splash_screen",SplashA);
        editor.apply();
    }

    private void ClearPicCache(){
        ImageLoader.getInstance().clearDiskCache();
        ImagePipeline pipe = Fresco.getImagePipeline();
        pipe.clearDiskCaches();
        pipe.clearMemoryCaches();
    }

    private void ClearEventCache(){
        File cacheDir = Utils.getCacheFile(this,"event");
        if(cacheDir.exists()){
            File del = new File(cacheDir.toString()+File.separator+"cacheEvent.json");
            if(del.exists()){
                del.delete();
            }
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.setting_skip_splash:{
                boolean ischeck = SwitchSkipSplash.isChecked();
                ischeck = !ischeck;
                SwitchSkipSplash.setChecked(ischeck);
                setSplashAvailability(ischeck);
                break;
            }
            case R.id.setting_clear_pic_cache:ClearPicCache();break;
            case R.id.setting_clear_event_cache:ClearEventCache();break;
        }
    }
}
