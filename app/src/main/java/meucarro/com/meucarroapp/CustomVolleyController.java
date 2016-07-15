package meucarro.com.meucarroapp;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by adm-f on 13/07/2016.
 */
public class CustomVolleyController extends Application {

    private static CustomVolleyController mInstance;
    private static RequestQueue mRequestQueue;
    private static String TAG = "DEFAULT";

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized  CustomVolleyController getmInstance()
    {
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQueue == null)
        {
            mRequestQueue = Volley.newRequestQueue(this.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request, String tag){
        request.setTag(TextUtils.isEmpty(tag)? TAG : tag);
        getRequestQueue().add(request);
    }

    public<T> void addToRequestQueue(Request<T> request){
        request.setTag(TAG);
        getRequestQueue().add(request);
    }

    public void cancelPendingRequests(Object tag){
        if(mRequestQueue != null)
        {
            mRequestQueue.cancelAll(tag);
        }
    }
}
