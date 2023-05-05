package skj.myapp.muni;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.core.app.NotificationCompat;

public class syncserver {
    private static dboperation dboper=null;
    private static final String AddPlace_URL="https://vista2074.com/muni/";

    public static void sync_information(String mtype, String mdate){
        public_ver.newinfomessage="";

        String urlSuffix = "syncinfo.php?utype=" + mtype+"&datet="+mdate+"&ucode="+public_ver.muid;
        class RegisterUser extends AsyncTask<String, Void, String> {
            // ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getApplicationContext(), "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                dboper = new dboperation();
                if (s.equals("nodata")){
                    public_ver.newinfomessage=dboper.get_newmessage();
                    if (public_ver.newinfomessage.length()>0 ) { dboper. message_displayed("info");}
                }else{
                    dboper.sync_info(s);
                    public_ver.newinfomessage=dboper.get_newmessage();
                    if (public_ver.newinfomessage.length()>0 ) { dboper. message_displayed("info");}
                }
            }
            @Override
            protected String doInBackground(String... params) {
                String s = params[0];
                BufferedReader bufferReader=null;
                try {
                    URL url=new URL(AddPlace_URL+s);
                    HttpURLConnection con=(HttpURLConnection)url.openConnection();
                    bufferReader=new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String result;
                    result=bufferReader.readLine();
                    return  result;
                }catch (Exception e){
                    return null;
                }
            }

        }
        RegisterUser ur=new RegisterUser();
        ur.execute(urlSuffix);
    }
    public static void sync_griv(String mtype, String mdate){
        public_ver.newgrivmessage="";
        String urlSuffix = "syncgriv2.php?utype=" + mtype+"&datet="+mdate+"&ucode="+public_ver.muid+"&umail="+public_ver.loginmail;
        class RegisterUser extends AsyncTask<String, Void, String> {
            // ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getApplicationContext(), "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                dboper = new dboperation();
                if (s.equals("nodata")){
                    public_ver.newgrivmessage=dboper.get_grivnewmessage();
                    if (public_ver.newgrivmessage.length()>0 ) {dboper. message_displayed("griv");}
                }else{
                    dboper.sync_griv(s);
                    public_ver.newgrivmessage=dboper.get_grivnewmessage();
                    if (public_ver.newgrivmessage.length()>0 ) {dboper. message_displayed("griv");}
                }
            }

            @Override
            protected String doInBackground(String... params) {
                String s = params[0];
                BufferedReader bufferReader=null;
                try {
                    URL url=new URL(AddPlace_URL+s);
                    HttpURLConnection con=(HttpURLConnection)url.openConnection();
                    bufferReader=new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String result;
                    result=bufferReader.readLine();
                    return  result;
                }catch (Exception e){
                    return null;
                }
            }

        }
        RegisterUser ur=new RegisterUser();
        ur.execute(urlSuffix);
    }


}
