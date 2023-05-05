package skj.myapp.muni;

import android.app.Application;
import android.content.Context;

public class Myapp extends Application {
    private static Context mContext;

    private static Context sContext;
    @Override
    public void onCreate() {
        sContext = getApplicationContext();
        super.onCreate();
    }

    public static Context getContext() {
        return sContext;
    }
}