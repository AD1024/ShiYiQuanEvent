package ccoderad.bnds.shiyiquanevent.broadcast;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

import ccoderad.bnds.shiyiquanevent.global.PreferencesConstants;
import ccoderad.bnds.shiyiquanevent.global.URLConstants;
import ccoderad.bnds.shiyiquanevent.utils.DownloadUtil;
import ccoderad.bnds.shiyiquanevent.utils.PreferenceUtils;

/**
 * Created by CCoderAD on 2017/1/6.
 */

public class DownloadBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id != -1) {
                if (DownloadUtil.isInQueue(id)) {
                    DownloadUtil.notifyFinished(id);
                    Uri fin = DownloadUtil.getUriById(id);
                    PreferenceUtils.initialize(context
                            , PreferencesConstants.UPDATE_CHECKER_PREF, Context.MODE_PRIVATE);
                    Intent installIntent = new Intent();
                    installIntent.setDataAndType(Uri
                                    .fromFile(new File(Environment.getExternalStorageDirectory()
                                            + "/download/"
                                            + "ShiYiQuanEvent-Update.apk"))
                            , "application/vnd.android.package-archive");
                    installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(installIntent);
                }
            }
        }
    }
}
