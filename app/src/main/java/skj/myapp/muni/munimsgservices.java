package skj.myapp.muni;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class munimsgservices extends Service {
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    private static dboperation dboper=null;
    private String arrayuser[] = {"ALL","Public", "Ward", "Municipality", "Admin"};
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                          //  Log.e("Service", "Service is running...");
                         if(isNetworkConnected()){
                           sync_info();
                             }
                            try {
                                Thread.sleep(30000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).start();
        return super.onStartCommand(intent, flags, startId);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sync_info(){
       public_ver.pview=0;
       dboper = new dboperation();
       Long lastsync=Long.parseLong(public_ver.lastsync)-6;
       syncserver.sync_griv(arrayuser[public_ver.usertype],lastsync+"");
        if (public_ver.newgrivmessage.length()>0 ) {
            String grivval[] = public_ver.newgrivmessage.split("/::/");
            for (int i = 0; i < grivval.length; i++) {
                String colval[] = grivval[i].split("/ww/");
                addNotification(colval[0], "-: "+colval[1]);
            }
        }
        syncserver.sync_information(arrayuser[public_ver.usertype],lastsync+"");
        if (public_ver.newinfomessage.length()>0 ) {
            String infoval[] = public_ver.newinfomessage.split("/::/");
            for (int i = 0; i < infoval.length; i++) {
                String colval[] = infoval[i].split("/ww/");
                addNotification(colval[1], colval[0]);
            }
        }
    }
    public void addNotification(String mtitle, String mmessgae) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(),
                default_notification_channel_id )
                .setSmallIcon(R.drawable. ic_launcher_foreground )
                .setContentTitle( mtitle)
                .setContentText(mmessgae)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mmessgae));

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context. NOTIFICATION_SERVICE ) ;

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes. CONTENT_TYPE_SONIFICATION )
                    .setUsage(AudioAttributes. USAGE_ALARM )
                    .build() ;
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "munichannel" , importance) ;
            notificationChannel.enableLights( true ) ;
            notificationChannel.setLightColor(Color. RED ) ;
            notificationChannel.enableVibration( true ) ;
            notificationChannel.setVibrationPattern( new long []{ 100 , 200 , 300 , 400 , 500 , 400 , 300 , 200 , 400 }) ;
            mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(( int ) System. currentTimeMillis (), mBuilder.build()) ;
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}