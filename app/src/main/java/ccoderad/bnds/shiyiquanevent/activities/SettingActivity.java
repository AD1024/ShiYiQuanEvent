package ccoderad.bnds.shiyiquanevent.activities;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.global.PreferencesConstants;
import ccoderad.bnds.shiyiquanevent.global.URLConstants;
import ccoderad.bnds.shiyiquanevent.utils.DownloadUtil;
import ccoderad.bnds.shiyiquanevent.utils.ImageTools;
import ccoderad.bnds.shiyiquanevent.utils.PreferenceUtils;
import ccoderad.bnds.shiyiquanevent.utils.ToastUtil;
import ccoderad.bnds.shiyiquanevent.utils.Utils;
import ccoderad.bnds.shiyiquanevent.utils.ViewTools;


public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView CardSkipSplash;
    private CardView ClearPicCache;
    private CardView ClearEvent;
    private CardView mEnableHighQAvatar;
    private CardView mShowUpdateInfo;
    private CardView mAutoCheckUpdate;
    private CardView mCheckUpdate;

    private Switch SwitchEnableHighQAvatar;
    private Switch SwitchSkipSplash;
    private Switch SwitchAutoCheckUpdate;

    private SharedPreferences Preferences;
    private SharedPreferences.Editor gEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        ToastUtil.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(this).diskCache(new UnlimitedDiskCache(StorageUtils.getOwnCacheDirectory(this, "ShiyiquanImgs/Cache"))).build());

        // Init Preference
        Preferences = getSharedPreferences(PreferencesConstants.SETTING_PREF, MODE_PRIVATE);
        gEditor = Preferences.edit();

        //ViewById
        CardSkipSplash = (CardView) findViewById(R.id.setting_skip_splash);
        ClearEvent = (CardView) findViewById(R.id.setting_clear_event_cache);
        ClearPicCache = (CardView) findViewById(R.id.setting_clear_pic_cache);

        SwitchSkipSplash = (Switch) findViewById(R.id.setting_switch_skip_splash);
        mShowUpdateInfo = (CardView) findViewById(R.id.setting_show_update_info);

        mCheckUpdate = (CardView) findViewById(R.id.setting_check_update);
        mAutoCheckUpdate = (CardView) findViewById(R.id.setting_enable_auto_check_update);
        SwitchAutoCheckUpdate = (Switch) findViewById(R.id.setting_auto_check_update_switch);

        mEnableHighQAvatar = (CardView) findViewById(R.id.setting_enable_high_quality_avatar);
        SwitchEnableHighQAvatar = (Switch) findViewById(R.id.setting_high_quality_avatar_switch);

        // OnClickListener
        ClearPicCache.setOnClickListener(this);
        CardSkipSplash.setOnClickListener(this);
        ClearEvent.setOnClickListener(this);
        mShowUpdateInfo.setOnClickListener(this);
        mEnableHighQAvatar.setOnClickListener(this);
        mAutoCheckUpdate.setOnClickListener(this);
        mCheckUpdate.setOnClickListener(this);
        SwitchSkipSplash.setChecked(Preferences
                .getBoolean(PreferencesConstants.SETTING_SKIP_SPLASH_SCREEN_TAG, false));
        SwitchEnableHighQAvatar.setChecked(Preferences
                .getBoolean(PreferencesConstants.SETTING_HIGH_QUALITY_AVATAR_TAG, false));
        SwitchAutoCheckUpdate
                .setChecked(Preferences.getBoolean(PreferencesConstants
                        .SETTING_ENABLE_AUTO_UPDATE_TAG, false));


        //Switch Listener
        SwitchSkipSplash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSplashAvailability(isChecked);
            }
        });
        SwitchEnableHighQAvatar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                gEditor.putBoolean(PreferencesConstants.SETTING_HIGH_QUALITY_AVATAR_TAG, isChecked);
                gEditor.apply();
                if (isChecked) {
                    ToastUtil.makeText("已切换高质量头像，重启应用生效", false);
                } else {
                    ToastUtil.makeText("已切换普通质量头像，重启应用生效", false);
                }
            }
        });
        SwitchAutoCheckUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setAutoUpdateCheckAvailability(isChecked);
            }
        });
    }

    private void setAutoUpdateCheckAvailability(boolean check) {
        if (check) {
            ToastUtil.makeText("已开启自动检查更新", false);
        } else {
            ToastUtil.makeText("已关闭自动检查更新", false);
        }
        gEditor.putBoolean(PreferencesConstants.SETTING_ENABLE_AUTO_UPDATE_TAG, check);
        gEditor.apply();
    }

    private void setSplashAvailability(boolean check) {
        gEditor.putBoolean(PreferencesConstants.SETTING_SKIP_SPLASH_SCREEN_TAG, check);
        gEditor.apply();
    }

    private void ShowUpdateInfo() {
        final SharedPreferences versionInfo = getSharedPreferences("VersionInfo", MODE_PRIVATE);
        String version = versionInfo.getString("VersionCode", "NoInfo");
        View Header = ViewTools.Inflate(this, R.layout.update_info_header, null);
        View window = ViewTools.Inflate(this, R.layout.update_info_msg, null);
        TextView updateInfo = (TextView) window.findViewById(R.id.update_info_msg);
        updateInfo.setText(R.string.content_update_info);

        TextView headerText = (TextView) Header.findViewById(R.id.update_info_header_text);

        headerText.setTextSize(25f);

        headerText.setPadding(10, 10, 10, 10);

        int[] color = ImageTools.RandomColor();

        Header.setBackgroundColor(Color.rgb(color[0], color[1], color[2]));

        if (ImageTools.isDeepColor(color)) {
            headerText.setTextColor(Color.WHITE);
        } else {
            headerText.setTextColor(Color.BLACK);
        }

        new AlertDialog.Builder(this)
                .setView(window)
                .setCustomTitle(Header)
                .setNegativeButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void ClearPicCache() {
        ImageLoader.getInstance().clearDiskCache();
        ImagePipeline pipe = Fresco.getImagePipeline();
        pipe.clearDiskCaches();
        pipe.clearMemoryCaches();
        ToastUtil.makeText("已清理图片缓存", false);
    }

    private void ClearEventCache() {
        File cacheDir = Utils.getCacheFile(this, "event");
        if (cacheDir.exists()) {
            File del = new File(cacheDir.toString() + File.separator + "cacheEvent.json");
            if (del.exists()) {
                del.delete();
            }
        }
        ToastUtil.makeText("已删除活动缓存", false);
    }

    private void showUpdateDownloadAlert(final String newVersionCode) {
        PreferenceUtils.shiftTable(this, PreferencesConstants.UPDATE_CHECKER_PREF, MODE_PRIVATE);

        // Notification Header View
        View updateNotiHeader = ViewTools
                .Inflate(SettingActivity.this, R.layout.update_info_header, null);

        TextView tvHeaderText = (TextView) updateNotiHeader
                .findViewById(R.id.update_info_header_text);
        tvHeaderText.setText("更新吗?");

        // Alert Content View
        View contentView = ViewTools
                .Inflate(SettingActivity.this, R.layout.alert_update_download, null);
        TextView tvCurrentVersion = (TextView) contentView
                .findViewById(R.id.alert_update_download_current_version);
        TextView tvNewVersion = (TextView) contentView
                .findViewById(R.id.alert_update_download_new_version);
        tvCurrentVersion.setText(URLConstants.CURRENT_VERSION);
        tvNewVersion.setText(PreferenceUtils
                .getString(PreferencesConstants.UPDATE_CHECKER_NEW_VERSION_CODE, "NAN"));

        int[] color = ImageTools.RandomColor();
        tvHeaderText.setBackgroundColor(Color.rgb(color[0], color[1], color[2]));
        if (ImageTools.isDeepColor(color)) {
            tvHeaderText.setTextColor(Color.WHITE);
        }

        final String fileUrl = PreferenceUtils.getString(PreferencesConstants.UPDATE_CHECKER_DOWNLOAD_LINK, "NAN");

        if (fileUrl == null || fileUrl.equals("NAN")) {
            ToastUtil.makeText("当前为最新版本", false);
            return;
        }

        // Show Alert
        new AlertDialog.Builder(SettingActivity.this)
                .setCustomTitle(updateNotiHeader)
                .setView(contentView)
                .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DownloadUtil.initialize(SettingActivity.this);
                        DownloadManager.Request mRequest = new DownloadUtil.RequestBuilder(fileUrl)
                                .setTitle("ShiYiQuanEvent" + newVersionCode + ".apk")
                                .setDescription("正在下载新版十一圈")
                                .setDownloadDirectory(Environment.DIRECTORY_DOWNLOADS
                                        , "ShiYiQuanEvent-Update.apk")
                                .setVisibilityInUi(true)
                                .setMimeType("application/vnd.android.package-archive")
                                .build();
                        DownloadUtil.startDownload(mRequest);

                    }
                })
                .setNegativeButton("暂不下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.setting_skip_splash: {
                boolean ischeck = SwitchSkipSplash.isChecked();
                ischeck = !ischeck;
                SwitchSkipSplash.setChecked(ischeck);
                setSplashAvailability(ischeck);
                break;
            }
            case R.id.setting_clear_pic_cache:
                ClearPicCache();
                break;
            case R.id.setting_clear_event_cache:
                ClearEventCache();
                break;
            case R.id.setting_show_update_info: {
                ShowUpdateInfo();
                break;
            }
            case R.id.setting_enable_high_quality_avatar: {
                boolean checked = SwitchEnableHighQAvatar.isChecked();
                checked = !checked;
                SwitchEnableHighQAvatar.setChecked(checked);
                gEditor.putBoolean(PreferencesConstants
                        .SETTING_HIGH_QUALITY_AVATAR_TAG, checked);
                gEditor.apply();
                if (checked) {
                    ToastUtil.makeText("已切换高质量头像，重启应用生效", false);
                } else {
                    ToastUtil.makeText("已切换普通质量头像，重启应用生效", false);
                }
                break;
            }
            case R.id.setting_enable_auto_check_update: {
                boolean isChecked = SwitchAutoCheckUpdate.isChecked();
                isChecked = !isChecked;
                SwitchAutoCheckUpdate.setChecked(isChecked);
                break;
            }
            case R.id.setting_check_update: {
                PreferenceUtils.initialize(this
                        , PreferencesConstants.UPDATE_CHECKER_PREF
                        , MODE_PRIVATE);
                String newVersion = PreferenceUtils
                        .getString(PreferencesConstants.UPDATE_CHECKER_NEW_VERSION_CODE
                                , URLConstants.CURRENT_VERSION);
                if (!URLConstants.CURRENT_VERSION.equals(newVersion)) {
                    showUpdateDownloadAlert(newVersion);
                } else {
                    ToastUtil.makeText("当前为最新版本", false);
                }
                break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ToastUtil.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToastUtil.cancel();
    }
}
