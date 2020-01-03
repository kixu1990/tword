package mutils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

/**
 * Created by kuxi on 2020/1/2.
 */

public class GetTopActivity {
    private  String topActivityName;

    private static GetTopActivity INSTANCE = new GetTopActivity();

    private GetTopActivity(){};

    public static GetTopActivity getINSTANCE(){
        return INSTANCE;
    }

    public void setTopActivity(String topActivityName){
        this.topActivityName = topActivityName;
    };


    public String getTopActivity(){
        return topActivityName;
    }
}
