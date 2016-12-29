package ccoderad.bnds.shiyiquanevent.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by CCoderAD on 2016/12/26.
 */

public class ViewTools {
    public static View Inflate(Context context, int id, ViewGroup parent){
        return LayoutInflater.from(context).inflate(id,parent);
    }

    public static void ToastInfo(Context context,String msg,boolean islong){
        Toast.makeText(context,msg,islong?Toast.LENGTH_LONG:Toast.LENGTH_SHORT);
    }
}
