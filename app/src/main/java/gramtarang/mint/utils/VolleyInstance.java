package gramtarang.mint.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyInstance {

    private RequestQueue mRequestQueue;
    private static VolleyInstance volleyInstance;
    private static Context mContext;

    private RequestQueue getRequestQueue() {
        mRequestQueue = Volley.newRequestQueue(mContext);
        return mRequestQueue;
    }

    private VolleyInstance(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();

    }
    public  static VolleyInstance getInstance(Context context){
        if(volleyInstance==null){
            volleyInstance=new VolleyInstance(context);
        }
        return volleyInstance;
    }
    public <T> void addToRequestQueue(Request<T> req) {
        req.setShouldCache(false);
        getRequestQueue().add(req);
    }
}
