package skj.myapp.muni;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    SignInButton signInButton;
    private GoogleApiClient googleApiClient;
    public static dbmanager dbmanage=null;
    private Switch enfont;
    private Button btnlogout,btndash;
    private static final int RC_SIGN_IN = 1;
    private static dboperation dboper=null;
    private static final String AddPlace_URL="https://vista2074.com/muni/";
    private String userinfo[];
    private long pressedTime;
    private boolean changepasscheck;
    private String toasmsg;
    private String mmail;
    private String mpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnlogout=(Button)  findViewById(R.id.button9);
        btndash=(Button)  findViewById(R.id.button10);

        int rfl=0;
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){rfl=1;}
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){rfl=1;}
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){rfl=1;}

        if(rfl==1) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        //---create db ---
        dbmanage = new dbmanager(this, "muni.db", null, 4);
       if (isStoragePermissionGranted()){
       create_picfolder();}else{
           Toast.makeText(getBaseContext(), "Folder creation not granted", Toast.LENGTH_SHORT).show();}
        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        enfont= (Switch) findViewById(R.id.switch9);
        //this.setTitle("YourString");
        signInButton=(SignInButton)findViewById(R.id.sign_in_button);
        public_ver.loginmail="";
        btnlogout.setVisibility(View.GONE);
        btndash.setVisibility(View.GONE);
        signInButton.setVisibility(View.VISIBLE);

        dboper = new dboperation();
        dboper.login();
        if(public_ver.FEN==0){ enfont.setChecked(false);}
        seteng_nep_firstscreen();
        if (public_ver.lastsync.equals("0")){
            public_ver.lastsync=dboper.getdatetime();}

        if (public_ver.loginmail=="register"){
            public_ver.usertype=5;
            btnlogout.setVisibility(View.GONE);
            btndash.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
        }else{
            if (public_ver.loginmail.length()>5){
                if( isNetworkConnected()) { getusertype(public_ver.loginmail);loginsuccess();}else{
                    if(public_ver.FEN==0){toasmsg="Internet required.";}else{toasmsg="इन्टरनेट आवश्यक छ।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();}
            }
        }

       signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( isNetworkConnected()) {
                    Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                    startActivityForResult(intent, RC_SIGN_IN);
                }else{
                    if(public_ver.FEN==0){toasmsg="Internet required.";}else{toasmsg="इन्टरनेट आवश्यक छ।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();}
            }
        });

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dboper.logout(0,dboper.getdatetime());
                finish();
                startActivity(getIntent());
            }
        });
        btndash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( isNetworkConnected()) {gotoProfile();}else{
                    if(public_ver.FEN==0){toasmsg="Internet required.";}else{toasmsg="इन्टरनेट आवश्यक छ।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();}
            }
        });

        enfont.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               // Locale.getDefault().getLanguage()        ---> en
               // Locale.getDefault().getISO3Language()    ---> eng
              //  Locale.getDefault().getCountry()         ---> US
             //   Locale.getDefault().getISO3Country()     ---> USA
              //  Locale.getDefault().toString()           ---> en_US
              //  Locale.getDefault().getDisplayLanguage() ---> English
               // Locale.getDefault().getDisplayCountry()  ---> United States
               // Locale.getDefault().getDisplayName()     ---> English (United States)
                String locale = getApplicationContext().getResources().getConfiguration().locale.getDisplayLanguage();


                if(public_ver.FEN==0){
                    public_ver.FEN=1;
                    dboper.engnep(1);
                    if (!locale.equals("Nepali")){Toast.makeText(getApplicationContext(),"Select Nepali keyborad !",Toast.LENGTH_SHORT).show();}
                }else {
                    public_ver.FEN=0;
                    dboper.engnep(0);
                    if (!locale.equals("English")){Toast.makeText(getApplicationContext(),"Select English keyborad !",Toast.LENGTH_SHORT).show();}
                }
                seteng_nep_firstscreen();
            }
        });

    }
    @Override
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            if(public_ver.FEN==0){toasmsg="Press back again to exit";}else{toasmsg="बाहिर निस्कन फेरि थिच्नुहोस्";}
            Toast.makeText(getBaseContext(), toasmsg, Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount account=result.getSignInAccount();
           // public_ver.loginuser=account.getDisplayName();
            public_ver.loginmail=account.getEmail();
            handleSignInResult(result);
            dboper.logout(1,"");
            loginsuccess();
        }
    }
    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            getusertype(public_ver.loginmail);
        }else{
            if(public_ver.FEN==0){toasmsg="Sign in canceled";}else{toasmsg="साइन इन रद्द गरियो";}
            Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_LONG).show();
        }
    }
    private void gotoProfile(){
        if(public_ver.usertype < 5) {
            if( isNetworkConnected()) {
            Intent serviceIntent = new Intent(this, munimsgservices.class);
            startService(serviceIntent);
            public_ver.pview = 0;}else{
                if(public_ver.FEN==0){toasmsg="Internet required.";}else{toasmsg="इन्टरनेट आवश्यक छ।";}
                Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();}
        }
        switch (public_ver.usertype) {
            case 0:
                Intent register=new Intent(this,register.class);
                startActivity(register);
                break;
            case 1:
                Intent publicuser=new Intent(this,publicuser.class);
                startActivity(publicuser);
                break;
            case 2:

                Intent warduser=new Intent(this,warduser.class);
                startActivity(warduser);
                break;
            case 3:
                Intent munuser=new Intent(this,munuser.class);
                startActivity(munuser);
                break;
            case 4:
                Intent adminuser=new Intent(this,adminuser.class);
                startActivity(adminuser);
                break;
            case 5:
                Intent adminusernew=new Intent(this,register.class);
                startActivity(adminusernew);
                break;
        }
    }

    private void getusertype(String mmail){

        String urlSuffix = "usersearch.php?umail=" + mmail;
        class RegisterUser extends AsyncTask<String, Void, String> {
            // ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getApplicationContext(), "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {

                if (s.equals("Unable to Connect")){
                    if(public_ver.FEN==0){toasmsg="Please check internet ! server not connected to verify User.";}else{toasmsg="कृपया प्रयोगकर्ता प्रमाणित गर्न इन्टरनेट सर्भर जाँच गर्नुहोस्।";}
                    Toast.makeText(getApplicationContext(),toasmsg , Toast.LENGTH_SHORT).show();
                }else {
                    if (s.equals("Not Found")) {
                        if(public_ver.FEN==0){toasmsg="User Not Exist.";}else{toasmsg="प्रयोगकर्ता अवस्थित छैन।";}
                        Toast.makeText(getApplicationContext(),toasmsg , Toast.LENGTH_SHORT).show();
                        gotoProfile();
                    } else {
                        userinfo = s.split(":");

                        Byte nutype = Byte.parseByte(userinfo[1].substring(0, 1));
                        public_ver.muid = Integer.parseInt(userinfo[0]);
                        public_ver.muserautho = userinfo[0] + ":" + userinfo[1];
                        public_ver.useractive = Byte.parseByte(userinfo[1].substring(1, 2));
                        public_ver.replygrivward = Byte.parseByte(userinfo[1].substring(2, 3));
                        public_ver.replygrivmun = Byte.parseByte(userinfo[1].substring(3, 4));
                        public_ver.postinfoward = Byte.parseByte(userinfo[1].substring(4, 5));
                        public_ver.postinfomun = Byte.parseByte(userinfo[1].substring(5, 6));
                        if (public_ver.usertype != nutype) {
                            public_ver.usertype = nutype;
                            dboper.save_user(mmail);
                        }
                        if (public_ver.useractive == 1) {
                            dboper.logout(1, "");
                            gotoProfile();
                        } else {
                            if(public_ver.FEN==0){toasmsg="Contact Admin to activate your account.";}else{toasmsg="आफ्नो खाता सक्रिय गर्न व्यवस्थापकलाई सम्पर्क गर्नुहोस्।";}
                            Toast.makeText(getApplicationContext(),toasmsg , Toast.LENGTH_SHORT).show();
                        }
                    }

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
    private void getlogin(){
         String urlSuffix = "login.php?umail=" + mmail+"&password="+mpassword;
        class RegisterUser extends AsyncTask<String, Void, String> {
            // ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getApplicationContext(), "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                if (s.equals("Unable to Connect")){
                    if(public_ver.FEN==0){toasmsg="Please check internet ! server not connected to verify User.";}else{toasmsg="कृपया प्रयोगकर्ता प्रमाणित गर्न इन्टरनेट सर्भर जाँच गर्नुहोस्।";}
                    Toast.makeText(getApplicationContext(),toasmsg , Toast.LENGTH_SHORT).show();
                }else {
                    if (s.equals("Login fail")) {
                        if(public_ver.FEN==0){toasmsg="Login Fail.";}else{toasmsg="लगइन असफल।";}
                        Toast.makeText(getApplicationContext(),toasmsg , Toast.LENGTH_SHORT).show();
                    } else {
                        if (changepasscheck==false) {
                            userinfo = s.split(":");
                            public_ver.usertype = Byte.parseByte(userinfo[1].substring(0, 1));
                            public_ver.muid = Integer.parseInt(userinfo[0]);
                            public_ver.muserautho = userinfo[0] + ":" + userinfo[1];
                            public_ver.useractive = Byte.parseByte(userinfo[1].substring(1, 2));
                            public_ver.replygrivward = Byte.parseByte(userinfo[1].substring(2, 3));
                            public_ver.replygrivmun = Byte.parseByte(userinfo[1].substring(3, 4));
                            public_ver.postinfoward = Byte.parseByte(userinfo[1].substring(4, 5));
                            public_ver.postinfomun = Byte.parseByte(userinfo[1].substring(5, 6));
                            dboper.logout(1, "");
                            public_ver.loginmail = mmail;
                            gotoProfile();
                            loginsuccess();
                        }else{//change password
                            Intent changepass=new Intent(getApplicationContext(),changepass.class);
                            startActivity(changepass);
                        }
                    }
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
    private void create_picfolder(){
      String npath="";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
             npath = create_folder_in_app_package_media_dir_new2(this);
        }else {
            npath =Environment.getExternalStorageDirectory().toString();
        }
        public_ver.picfilepath=npath;
        File direct = new File(npath, "munipic");
            try {
                if (!direct.exists()) {
                    if (direct.mkdir()) {
                        if (public_ver.FEN == 0) {
                            toasmsg = "Folder created.";
                        } else {
                            toasmsg = "फोल्डर सिर्जना गरियो।";
                        }
                        Toast.makeText(getApplicationContext(), toasmsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //  Toast.makeText(getApplicationContext(), "Directory exist."+direct, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                if (public_ver.FEN == 0) {
                    toasmsg = "Picture Directory not created.";
                } else {
                    toasmsg = "तस्विर फोल्डर सिर्जना गरिएको छैन।";
                }
                Toast.makeText(getApplicationContext(), toasmsg, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
    }
    public static String create_folder_in_app_package_media_dir_new2(Context context) {
            File[] directory = new File[0];
            directory = context.getExternalMediaDirs();
            for(int i = 0;i<directory.length;i++){
                if(directory[i].getName().contains(context.getPackageName())){
                    return directory[i].getAbsolutePath();
                }
            }
        return null;
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }
    private void loginsuccess(){
        btnlogout.setVisibility(View.VISIBLE);
        btndash.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.GONE);
    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    private void seteng_nep_firstscreen(){
        if(public_ver.FEN==0){
            enfont.setText("English Language");
            setGooglePlusButtonText(signInButton,"Sign in with Google mail");
            btnlogout.setText("Logout");
            btndash.setText("Dash-Board");
        }else{
            enfont.setChecked(true); enfont.setText("नेपाली भाषा");
            setGooglePlusButtonText(signInButton,"गुगल इमेलबाट साइन इन गर्नुहोस्");
            btnlogout.setText("बाहिर निस्कनु");
            btndash.setText("ड्यास-बोर्ड");}
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.ulogin:
                get_login_with_userid();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void get_login_with_userid(){
        String Y,C,N,T,U,P;
        if(public_ver.FEN==0){
            toasmsg="Login or Change Password";Y="Login";C="Change Password";N="Cancel";T="Login or Change Password";U="Enter User ID";P="Enter Password";}else{
            toasmsg="लगइन गर्नुहोस् वा पासवर्ड परिवर्तन गर्नुहोस्";Y="लगइन गर्नुहोस्";C="पासवर्ड परिवर्तन गर्नुहोस्";N="रद्द गर्नुहोस्";T="लगइन गर्नुहोस् वा पासवर्ड परिवर्तन गर्नुहोस्";U="आईडी प्रविष्ट गर्नुहोस्";P="पासवर्ड प्रविष्ट गर्नुहोस्";}
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.login, null);
        final EditText userid = (EditText) textEntryView.findViewById(R.id.Txtuid);
        final EditText password = (EditText) textEntryView.findViewById(R.id.txtpassword);
        userid.setHint(U);
        password.setHint(P);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(T);
        builder.setView(textEntryView);
        userid.setText(public_ver.loginmail);
        builder.setPositiveButton(Y, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changepasscheck=false;
                mmail=userid.getText().toString();
                mpassword=password.getText().toString();
                if(mmail.length()==0||mpassword.length()==0) {
                    if(public_ver.FEN==0){toasmsg="Userid and Password required.";}else{toasmsg="आईडी तथा पासवर्ड प्रविष्ट गर्नुहोस्";}
                    Toast.makeText(getApplicationContext(),toasmsg , Toast.LENGTH_SHORT).show();
                }else{
                    loginandchangepassword();}
            }
        });
        builder.setNeutralButton(C,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                changepasscheck=true;
                mmail=userid.getText().toString();

                mpassword=password.getText().toString();
                if(mmail.length()==0||mpassword.length()==0) {
                    if(public_ver.FEN==0){toasmsg="Userid and Password required.";}else{toasmsg="आईडी तथा पासवर्ड प्रविष्ट गर्नुहोस्";}
                    Toast.makeText(getApplicationContext(),toasmsg , Toast.LENGTH_SHORT).show();}else{
                   if(public_ver.loginmail.equals(mmail)){
                    loginandchangepassword();}else{
                       if(public_ver.FEN==0){toasmsg="Login User can only change password.";}else{toasmsg="लगइन प्रयोगकर्ताले मात्र पासवर्ड परिवर्तन गर्न सक्छ";}
                       Toast.makeText(getApplicationContext(),toasmsg , Toast.LENGTH_SHORT).show();
                   }
                }
            }
        });
        builder.setNegativeButton(N, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void loginandchangepassword(){
        if( isNetworkConnected()) {
            getlogin();
        }else{
            if(public_ver.FEN==0){toasmsg="Internet required.";}else{toasmsg="इन्टरनेट आवश्यक छ।";}
            Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();}
    }

    }