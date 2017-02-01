package ccoderad.bnds.shiyiquanevent.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by CCoderAD on 16/2/25.
 */

public class ImageLoader {
    private ImageView mImageView;
    private String mURL;
    private LruCache<String, Bitmap> mLruCache;

    //Init Vars and LRUCache
    public ImageLoader() {
        int MaxMem = (int) Runtime.getRuntime().maxMemory();
        MaxMem /= 4;
        mLruCache = new LruCache<String, Bitmap>(MaxMem) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public void addBitmap(String url, Bitmap bitmap) {
        if (getBitmap(url) == null) {
            mLruCache.put(url, bitmap);
        }
    }

    public Bitmap getBitmap(String url) {
        return mLruCache.get(url);
    }

    public void startLoad(String url, ImageView imageView) {
        mURL = url;
        mImageView = imageView;
        Bitmap bitmap = getBitmap(mURL);
        if (bitmap == null) {
            new LoadTask(mImageView, mURL).execute(mURL);
        } else {
            mImageView.setImageBitmap(bitmap);
        }

    }

    class LoadTask extends AsyncTask<String, Void, Bitmap> {
        ImageView mImageView;
        String mURL;

        public LoadTask(ImageView imageView, String url) {
            mURL = url;
            mImageView = imageView;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (mImageView.getTag().equals(mURL)) {
                addBitmap(mURL, bitmap);
                mImageView.setImageBitmap(bitmap);
            }
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap;
            InputStream is;
            URL url;
            try {
                url = new URL(params[0]);
                is = url.openStream();
//               BufferedInputStream bis = new BufferedInputStream(is);
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
                if (bitmap != null) {
                    bitmap = new ImageTools().fastblur(bitmap, 8);
                    mLruCache.put(mURL, bitmap);
                }
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
