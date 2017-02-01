package ccoderad.bnds.shiyiquanevent.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ccoderad.bnds.shiyiquanevent.beans.DownloadRequestModel;

/**
 * Created by CCoderAD on 2017/1/6.
 */

public class DownloadUtil {
    private static DownloadManager mManager;

    private static List<Long> mDownloadQueue;

    public static DownloadManager getInstance(Context context) {
        if (mManager == null) {
            mManager = (DownloadManager) context
                    .getSystemService(Context.DOWNLOAD_SERVICE);
        }
        if (mDownloadQueue == null) {
            mDownloadQueue = new ArrayList<>();
        }
        return mManager;
    }

    public static void initialize(Context context) {
        mManager = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);
        mDownloadQueue = new ArrayList<>();
    }

    public static void startDownload(String url, String title, String description) {
        if (mManager == null) {
            Log.e("DownloadUtil", "Error: Uninitialized Util");
            return;
        }
        DownloadManager.Request request = createRequest(url);
        request.setDescription(description);
        request.setTitle(title);
        long id = mManager.enqueue(request);
        mDownloadQueue.add(id);
    }

    public static void startDownload(DownloadManager.Request request) {
        if (request == null) {
            Log.e("DownloadUtil", "Error: Null Request");
            return;
        }
        long id = mManager.enqueue(request);
        mDownloadQueue.add(id);
    }

    public static boolean isInQueue(Long id) {
        return mDownloadQueue.contains(id);
    }

    public static Uri getUriById(long id) {
        return mManager.getUriForDownloadedFile(id);
    }

    public static void notifyFinished(Long id) {
        mDownloadQueue.remove(id);
    }

    public static DownloadManager.Request createRequest(String url) {
        return new DownloadManager.Request(Uri.parse(url));
    }

    public static DownloadManager.Request createRequest(String url, String title) {
        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));
        req.setTitle(title);
        return req;
    }

    public static DownloadManager.Request createRequest(String url, String title, String description) {
        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));
        req.setTitle(title);
        req.setDescription(description);
        return req;
    }

    public static class RequestBuilder {
        private DownloadRequestModel mRequest;

        public RequestBuilder(String url) {
            mRequest = new DownloadRequestModel();
            mRequest.url = url;
        }

        public RequestBuilder setTitle(String title) {
            mRequest.title = title;
            return this;
        }

        public RequestBuilder setDescription(String description) {
            mRequest.description = description;
            return this;
        }

        public RequestBuilder setAllowedNetWorkType(int FLAG) {
            mRequest.allowedNetworkFlag = FLAG;
            return this;
        }

        public RequestBuilder setEnableOverMeter(boolean allowed) {
            mRequest.allowedOverMeter = allowed;
            return this;
        }

        public RequestBuilder setEnableRoaming(boolean allowed) {
            mRequest.allowedOverRoaming = allowed;
            return this;
        }

        public RequestBuilder setVisibilityInUi(boolean visible) {
            mRequest.visibilityInUi = visible;
            return this;
        }

        public RequestBuilder setDownloadDirectory(String parentPath, String subDir) {
            mRequest.dirType = parentPath;
            mRequest.subPath = subDir;
            return this;
        }

        public RequestBuilder setMimeType(String mimeType) {
            mRequest.mimeType = mimeType;
            return this;
        }

        public DownloadManager.Request build() {
            DownloadManager.Request mRet = new DownloadManager.Request(Uri.parse(mRequest.url));
            if (mRequest.title != null) {
                mRet.setTitle(mRequest.title);
            }
            if (mRequest.description != null) {
                mRet.setDescription(mRequest.description);
            }
            if (!TextUtils.isEmpty(mRequest.subPath) && !TextUtils.isEmpty(mRequest.dirType)) {
                mRet.setDestinationInExternalPublicDir(mRequest.dirType, mRequest.subPath);
            }
            if (mRequest.allowedOverRoaming) {
                mRet.setAllowedOverRoaming(true);
            } else {
                mRet.setAllowedOverRoaming(false);
            }
            if (mRequest.allowedOverMeter) {
                mRet.setAllowedOverMetered(true);
            } else {
                mRet.setAllowedOverMetered(false);
            }
            mRet.setAllowedNetworkTypes(mRequest.allowedNetworkFlag);
            if (mRequest.mimeType != null && !TextUtils.isEmpty(mRequest.mimeType)) {
                mRet.setMimeType(mRequest.mimeType);
            }
            return mRet;
        }
    }
}
