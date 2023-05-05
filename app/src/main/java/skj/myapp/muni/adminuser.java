package skj.myapp.muni;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;

public class adminuser extends AppCompatActivity {
    private  Spinner spinnerutype;
    private ImageButton btnaddnew,btnedit,btnsearch,btnmundash,btnwarddash,btnuserdash;

    private Switch swact,swuser,swward,swmun,swmsg;
    private String userinfo[];
    private EditText umail,upass,upassc;
    private int utypeid;
    private static Spinner spinnerpro,spinnermun,spinnerward,lstuser;
    private static ArrayAdapter<CharSequence> adaptermun,adapterward ;
    private static dboperation dboper=null;
    private String promunward;
    private String promunwardnew;
    private String mmail;
    private static final String AddPlace_URL="https://vista2074.com/muni/";
    private String toasmsg;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admindash);
        dboper = new dboperation();
        spinnerutype= (Spinner) findViewById(R.id.spinner);
        btnaddnew= (ImageButton) findViewById(R.id.imageButton3);
        btnsearch= (ImageButton) findViewById(R.id.imageButton4);
        btnedit= (ImageButton) findViewById(R.id.imageButton5);
        btnmundash= (ImageButton) findViewById(R.id.imageButton6);
        btnwarddash= (ImageButton) findViewById(R.id.imageButton11);
        btnuserdash= (ImageButton) findViewById(R.id.imageButton12);

        swact= (Switch) findViewById(R.id.switch5);
        swuser= (Switch) findViewById(R.id.switch6);
        swward= (Switch) findViewById(R.id.switch7);
        swmun= (Switch) findViewById(R.id.switch8);
        swmsg= (Switch) findViewById(R.id.switch1);

        umail= (EditText) findViewById(R.id.editTextTextEmailAddress);
        upass= (EditText) findViewById(R.id.editTextTextPassword);
        upassc= (EditText) findViewById(R.id.editTextTextPassword2);
        spinnerpro= (Spinner) findViewById(R.id.spinner2);
        spinnermun= (Spinner) findViewById(R.id.spinner3);
        spinnerward= (Spinner) findViewById(R.id.spinner4);

        lstuser= (Spinner) findViewById(R.id.spinner8);

        promunward=  public_ver.muid+"";
        userinfo=public_ver.muserautho.split(":");
        umail.setText(public_ver.loginmail);
        userlistonoff(false);

        if(public_ver.FEN==0) {
            this.setTitle("ADMIN DASHBOARD");
            swact.setText("Set user Active/Inactive");
            swuser.setText("Reply Ward Level Grievances ");
            swward.setText("Reply Municipality Level Grievances");
            swmun.setText("Post Ward Level Information");
            swmsg.setText("Post Municipality Level Information");
            umail.setHint("Enter User Id");
            upass.setHint("Enter Password");
            upassc.setHint("Re-Enter Password");

        }else{
            this.setTitle("व्यवस्थापक ड्यासबोर्ड");
            swact.setText("प्रयोगकर्ता सक्रिय/निष्क्रिय सेट गर्नुहोस्");
            swuser.setText("वार्ड स्तरीय गुनासोहरूको जवाफ दिनुहोस्");
            swward.setText("नगरपालिका स्तरका गुनासोहरूको जवाफ दिनुहोस्");
            swmun.setText("वार्ड स्तर जानकारी पोस्ट गर्नुहोस्");
            swmsg.setText("नगरपालिका स्तर जानकारी पोस्ट गर्नुहोस्");
            umail.setHint("प्रयोगकर्ता आईडी प्रविष्ट गर्नुहोस्");
            upass.setHint("नयाँ पासवर्ड प्रविष्ट गर्नुहोस्");
            upassc.setHint("नयाँ पासवर्ड पुन: प्रविष्ट गर्नुहोस्");

        }
        loadspinner_usertype();
        loadspinner_province();
        fill_userinfo();
        spinnerutype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                utypeid=pos+1;
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });


        btnaddnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 mmail=umail.getText().toString();
                if (utypeid <3 && public_ver.wardno==0){
                    if(public_ver.FEN==0){toasmsg="Ward selection required.";}else{toasmsg="वडा छनोट आवश्यक छ।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                }
                else{
                    if( isNetworkConnected()) {
                        user_search(0);
                    }else{
                        if(public_ver.FEN==0){toasmsg="Internet required.";}else{toasmsg="इन्टरनेट आवश्यक छ।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();}
                }
            }
        });

        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 mmail=umail.getText().toString();
                if( isNetworkConnected()) {
                    if (public_ver.loginmail.equals("admin")){ userlist_search(utypeid+"","Admin");}else{
                        String muward=public_ver.muid+"";
                        userlist_search(utypeid+"",muward.substring(0,3));
                    }
                }else{
                    if(public_ver.FEN==0){toasmsg="Internet required.";}else{toasmsg="इन्टरनेट आवश्यक छ।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();}


            }
        });
        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mmail=umail.getText().toString();
                promunwardnew=(public_ver.province*100+public_ver.municipality)*100+ public_ver.wardno+"";
                if (utypeid <3 && public_ver.wardno==0){
                    if(public_ver.FEN==0){toasmsg="Ward selection required.";}else{toasmsg="वडा छनोट आवश्यक छ।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                    }
                else {
                    if (utypeid > 2 && public_ver.wardno > 0) {
                        if (public_ver.FEN == 0) {
                            toasmsg = "Ward selection All required.";
                        } else {
                            toasmsg = "सबै वडा छनोट आवश्यक।";
                        }
                        Toast.makeText(getApplicationContext(), toasmsg, Toast.LENGTH_SHORT).show();
                    } else {

                        if( isNetworkConnected()) {
                            if(public_ver.loginmail.equals("admin")){  update_user( promunwardnew);} else{
                                user_search(3);}
                        }else{
                            if(public_ver.FEN==0){toasmsg="Internet required.";}else{toasmsg="इन्टरनेट आवश्यक छ।";}
                            Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();}


                    }
                }
            }
        });

        btnmundash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent munuser=new Intent(getApplicationContext(),munuser.class);
                startActivity(munuser);
            }
        });
        btnwarddash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent munuser=new Intent(getApplicationContext(),publicdashlink.class);
                startActivity(munuser);
            }
        });
        btnuserdash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent munuser=new Intent(getApplicationContext(),publicuser.class);
                startActivity(munuser);
            }
        });

        spinnerpro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                public_ver.province=pos+1;
                loadspinner_Municipality(public_ver.province);
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        spinnermun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                public_ver.municipality=pos+1;
                loadspinner_Ward();
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        spinnerward.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                public_ver.wardno=pos;
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        lstuser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String s=lstuser.getSelectedItem().toString();
                userinfo = s.split(":");
                promunward = userinfo[0];
                fill_userinfo();
                umail.setText(userinfo[2]);
                int j =lstuser.getAdapter().getCount();
              if(j==1){userlistonoff(false);}
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }
    private void loadspinner_province(){
        String[] arraySpinner;
        if(public_ver.FEN==0) {
            arraySpinner = new String[]{"Province-1", "Madhesh", "Bagmati", "Gandaki", "Lumbini", "Karnali", "Sudurpashchim"};
        }else{
            arraySpinner = new String[]{"प्रदेश-१", "मधेश", "वाग्मती", "गण्डकी", "लुम्बिनी", "कर्णाली", "सुदूरपश्चिम"};
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.custspinner, arraySpinner);
        spinnerpro.setAdapter(adapter);
        if(public_ver.loginmail.equals("admin")){
        spinnerpro.setEnabled(true);}else{
        spinnerpro.setEnabled(false);
        }
    }
    private void loadspinner_Ward(){
        adapterward = new ArrayAdapter <CharSequence> (this, R.layout.custspinner );
        int pid =public_ver.province*100+public_ver.municipality;
        dboper.ward_list_all(pid,adapterward);
        spinnerward.setAdapter(adapterward);
        spinnerward.setSelection(Integer.parseInt(promunward.substring(3,5)));
    }
    private void loadspinner_Municipality(int Prno){
        adaptermun = new ArrayAdapter <CharSequence> (this, R.layout.custspinner );
        dboper.Get_mun_list(Prno,adaptermun);
        spinnermun.setAdapter(adaptermun);
        spinnermun.setSelection(Integer.parseInt(promunward.substring(1,3))-1);

        if(public_ver.loginmail.equals("admin")){
            spinnermun.setEnabled(true);}else{
            spinnermun.setEnabled(false);
        }
    }
    private void loadspinner_usertype(){
        String[] arraySpinner = null;
        if(public_ver.FEN==0) {
            arraySpinner= new String[]{"Public", "Ward", "Municipality", "Admin"};
        }else{
            arraySpinner = new String[]{"नागरिकहरू", "वाड", "नगरपालिका", "व्यवस्थापक"};
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.custspinner, arraySpinner);
        spinnerutype.setAdapter(adapter);
    }
    private void user_search(int jobtype){

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
                if (s.equals("Not Found")){
                    if(jobtype==0){ create_user();}
                    if(jobtype==1){
                        if(public_ver.FEN==0){toasmsg="User Not Exist.";}else{toasmsg="प्रयोगकर्ता अवस्थित छैन।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if(jobtype==0){
                        if(public_ver.FEN==0){toasmsg="User Already Exist.";}else{toasmsg="प्रयोगकर्ता पहिले नै अवस्थित छ।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                      }
                    if(jobtype==1) {
                        if(public_ver.loginmail.equals("admin")){
                            userinfo = s.split(":");
                            promunward = userinfo[0];
                            fill_userinfo();}
                        else{
                            String logmunid=public_ver.muid+"";
                            userinfo = s.split(":");
                            if(logmunid.substring(0,3).equals(userinfo[0].substring(0,3))) {
                                promunward = userinfo[0];
                                fill_userinfo();
                            }else{
                                if(public_ver.FEN==0){toasmsg="You are Not allowed to see other Municipality User Details.";}else{toasmsg="तपाईलाई अन्य नगरपालिका प्रयोगकर्ता विवरणहरू खोज्न अनुमति छैन।";}
                                Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if(jobtype==3) {
                            String logmunid=public_ver.muid+"";
                            userinfo = s.split(":");
                            if(logmunid.substring(0,3).equals(userinfo[0].substring(0,3))) {
                                if( isNetworkConnected()) {
                                    update_user(promunwardnew);
                                }else{
                                    if(public_ver.FEN==0){toasmsg="Internet required.";}else{toasmsg="इन्टरनेट आवश्यक छ।";}
                                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();}
                            }else{
                                if(public_ver.FEN==0){toasmsg="You are Not allowed to edit other Municipality User Details.";}else{toasmsg="तपाईलाई अन्य नगरपालिका प्रयोगकर्ता विवरणहरू सम्पादन गर्न अनुमति छैन।";}
                                Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();}
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
    private void update_user(String muward){
        String utype=getutypecode()+"";
        String urlSuffix = "updateusertype.php?umail=" + mmail+"&utype="+utype+"&upmw="+muward;
        class RegisterUser extends AsyncTask<String, Void, String> {
            // ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getApplicationContext(), "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                if (s.equals("fail")){
                    if(public_ver.FEN==0){toasmsg="Update failed.";}else{toasmsg="अपडेट असफल भयो।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                }else{
                    if(public_ver.FEN==0){toasmsg="Update done successfully.";}else{toasmsg="अद्यावधिक सफलतापूर्वक सम्पन्न भयो।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
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
    private void create_user(){
        String mpass=upass.getText().toString();
        String utype=getutypecode()+"";
        promunwardnew=(public_ver.province*100+public_ver.municipality)*100+ public_ver.wardno+"";
        String urlSuffix = "createuser.php?umail=" + mmail+"&utype="+utype+"&password="+mpass+"&upmw="+promunwardnew;
        class RegisterUser extends AsyncTask<String, Void, String> {
            // ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getApplicationContext(), "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                if (s.equals("User Exist")) {
                    if(public_ver.FEN==0){toasmsg="User Update completed.";}else{toasmsg="प्रयोगकर्ता अपडेट पूरा भयो।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                  }
                if (s.equals("User added")) {
                    if(public_ver.FEN==0){toasmsg="User creation completed.";}else{toasmsg="प्रयोगकर्ता सिर्जना पूरा भयो।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                  }
                if (s.equals("Error")) {
                    if(public_ver.FEN==0){toasmsg="User creation failed.";}else{toasmsg="प्रयोगकर्ता सिर्जना असफल भयो।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
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
    private void fill_userinfo(){
        if (userinfo[1].substring(1,2).equals("0")) {swact.setChecked(false);}else{swact.setChecked(true);}
        if (userinfo[1].substring(2,3).equals("0")) {swuser.setChecked(false);}else{swuser.setChecked(true);}
        if (userinfo[1].substring(3,4).equals("0")) {swward.setChecked(false);}else{swward.setChecked(true);}
        if (userinfo[1].substring(4,5).equals("0")) {swmun.setChecked(false);}else{swmun.setChecked(true);}
        if (userinfo[1].substring(5,6).equals("0")) {swmsg.setChecked(false);}else{swmsg.setChecked(true);}

        spinnerutype.setSelection(Integer.parseInt(userinfo[1].substring(0,1))-1);

        if(Integer.parseInt(promunward.substring(0,1))-1==spinnerpro.getSelectedItemPosition()){
            loadspinner_Municipality(public_ver.province);}else{
        spinnerpro.setSelection(Integer.parseInt(promunward.substring(0,1))-1);}


    }
private int getutypecode(){
    int uservalue=0;
    uservalue=utypeid*10;
    if(swact.isChecked()){uservalue=uservalue+1;}
    uservalue=uservalue*10;
    if(swuser.isChecked()){uservalue=uservalue+1;}
    uservalue=uservalue*10;
    if(swward.isChecked()){uservalue=uservalue+1;}
    uservalue=uservalue*10;
    if(swmun.isChecked()){uservalue=uservalue+1;}
    uservalue=uservalue*10;
    if(swmsg.isChecked()){uservalue=uservalue+1;}
    return  uservalue;
}

    private void loaduserlist(String userlist){
        String[] arraySpinner = userlist.split(",");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listtextviewadmin);
        adapter.clear();
        int j=arraySpinner.length;
        for(int i=0;i<=j-1;i++){
            adapter.add(arraySpinner[i]);
        }          
        lstuser.setAdapter(adapter);
    }

    private void userlist_search(String utype,String muward){

        String urlSuffix = "usersearchlist.php?umail=" + mmail+"&utype="+utype+"&upmw="+muward;

        class RegisterUser extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getApplicationContext(), "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                dboper = new dboperation();
                if (s.equals("nodata")){
                    if(public_ver.FEN==0){toasmsg="User Not Exist.";}else{toasmsg="प्रयोगकर्ता अवस्थित छैन।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                }else{
                    userlistonoff(true);
                    loaduserlist(s);
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
    private void userlistonoff(Boolean onoff){
        if (onoff){
            lstuser.setVisibility(View.VISIBLE);
        }else{
            lstuser.setVisibility(View.GONE);
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}