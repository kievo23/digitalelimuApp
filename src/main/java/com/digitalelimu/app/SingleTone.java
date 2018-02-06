package com.digitalelimu.app;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by kev on 1/19/17.
 */

public class SingleTone {
    /**
     */
    private RequestQueue que;
    private static SingleTone mInstance;
    private static Context cntxt;

    public SingleTone(Context context){
        cntxt =context;
        que = getRequestQue();
    }

    public RequestQueue getRequestQue(){
        if(que == null){
            que = Volley.newRequestQueue(cntxt.getApplicationContext());
        }
        return que;
    }

    public static synchronized SingleTone getInstance(Context context){
        if (mInstance == null){
            mInstance = new SingleTone(context);
        }
        return mInstance;
    }

    public <T> void addRequestQue(Request<T> request){
        que.add(request);
    }
}
