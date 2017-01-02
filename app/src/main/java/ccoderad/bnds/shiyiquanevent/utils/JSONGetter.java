package ccoderad.bnds.shiyiquanevent.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by CCoderAD on 16/5/13.
 */
public class JSONGetter {

    private final String ERROR_EXCEPTION = "Utils.JSONGetterError";
    private String URL;
    private boolean isObject = false;
    private boolean URLSetted = false;
    private RequestQueue TASK_Q;
    private Context PARENT;
    private Object ret;

    public JSONGetter(Context context) {
        this.PARENT = context;
        TASK_Q = Volley.newRequestQueue(PARENT);
    }

    public JSONGetter(String URL, boolean isObject, Context context) {
        SetURL(URL);
        this.isObject = isObject;
        PARENT = context;
        TASK_Q = Volley.newRequestQueue(PARENT);
        fetchJSON();
    }

    public Object getResult() {
        return this.ret;
    }

    public void SetURL(String URL) {
        this.URL = URL;
        URLSetted = true;
    }

    public void setIsObject(boolean Bl) {
        this.isObject = Bl;
    }

    private void sendRet(Object ret) {
        this.ret = ret;
    }

    private void fetchJSON() {
        if (URLSetted) {
            if (isObject) {
                JsonObjectRequest request = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        sendRet(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(ERROR_EXCEPTION, "JSONObject Fetch Error!");
                    }
                });
                TASK_Q.add(request);
            } else {
                JsonArrayRequest request = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        sendRet(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(ERROR_EXCEPTION, "JSONArray Fetch Error!");
                    }
                });
                TASK_Q.add(request);
            }
        } else {
            Log.e(ERROR_EXCEPTION, "Url Not Set");
        }
    }
}
