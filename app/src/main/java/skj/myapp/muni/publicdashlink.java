package skj.myapp.muni;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;

public class publicdashlink extends AppCompatActivity {
    private Button btnadd, btnupdate;
    private String promunward;
    private ListView lstlink;
    private EditText lneng,lnnep,lndes;
    private String toasmsg;
    private static final String AddPlace_URL="https://vista2074.com/muni/";
    private static dboperation dboper=null;
    private ImageButton btndelete;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publicdashlink);

        lstlink=(ListView)  findViewById(R.id.lstlink);
        lneng= (EditText) findViewById(R.id.editTextTextPersonName5);
        lnnep= (EditText) findViewById(R.id.editTextTextPersonName6);
        lndes= (EditText) findViewById(R.id.editTextTextPersonName7);
        btnupdate=(Button)  findViewById(R.id.button3);
        btnadd=(Button)  findViewById(R.id.button12);
        btndelete=(ImageButton)  findViewById(R.id.button7);
        dboper = new dboperation();
        promunward=  public_ver.muid+"";
        if(public_ver.FEN==0) {
            this.setTitle("ADMIN DASHBOARD");
            btnupdate.setText("UPDATE LINK");
            btnadd.setText("ADD LINK ");
            lndes.setHint("Enter Link address");
        }else{
            this.setTitle("व्यवस्थापक ड्यासबोर्ड");
            btnupdate.setText("लिंक अपडेट गर्नुहोस्");
            btnadd.setText("लिंक थप्नुहोस्");
            lndes.setHint("लिङ्क ठेगाना प्रविष्ट गर्नुहोस्");
        }
        load_link();
        lstlink.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                String symun=public_ver.muid+"";
                int newid=Integer.parseInt(symun.substring(0,3))*100+position+1;
                dboper.get_mun_selectedlink(newid+"");
                lneng.setText(public_ver.mlneng);
                lnnep.setText(public_ver.mlnnep);
                lndes.setText(public_ver.mlindes);
            }});
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(lneng.getText().toString().length()<5||lnnep.getText().toString().length()<5||lndes.getText().toString().length()<5) {
                if(public_ver.FEN==0){toasmsg="Enter all details .";}else{toasmsg="डाटा प्रविष्ट गर्नुहोस्।";}
                Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
            }else {
                if( isNetworkConnected()) {
                    addedit_munlink(public_ver.mlinkid + "");
                }else{
                    if(public_ver.FEN==0){toasmsg="Internet required.";}else{toasmsg="इन्टरनेट आवश्यक छ।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();}

            }
            }
        });
        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lneng.getText().toString().length()<5||lnnep.getText().toString().length()<5||lndes.getText().toString().length()<5) {
                    if(public_ver.FEN==0){toasmsg="Select Link to delete.";}else{toasmsg="डिलिट गर्ने लिंक छान्होस";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                }else {

                    if( isNetworkConnected()) {
                        AlertDialog YNbox = DelYNbox();
                        YNbox.show();

                    }else{
                        if(public_ver.FEN==0){toasmsg="Internet required.";}else{toasmsg="इन्टरनेट आवश्यक छ।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();}
                }
            }
        });
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dboper.get_mun_selectedlink("");
                if (public_ver.mlinkid==0){public_ver.mlinkid= Integer.parseInt(promunward);}
                if(lneng.getText().toString().length()<5||lnnep.getText().toString().length()<5||lndes.getText().toString().length()<5) {
                    if(public_ver.FEN==0){toasmsg="Enter all details .";}else{toasmsg="डाटा प्रविष्ट गर्नुहोस्।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                }else {

                    if( isNetworkConnected()) {
                        addedit_munlink((public_ver.mlinkid+1)+"");
                    }else{
                        if(public_ver.FEN==0){toasmsg="Internet required.";}else{toasmsg="इन्टरनेट आवश्यक छ।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();}
                }
            }
        });
    }
    private void load_link(){
        public_ver.weblink="";
        public_ver.wimageid="";
        dboper.get_mun_linklist();
        String[] web =public_ver.weblink.split(",");
        String[] imageId =public_ver.wimageid.split(",");
        if (public_ver.weblink.equals("")) {
            String symun=public_ver.muid+"";
            if( isNetworkConnected()) {
                syncmunlinklist(symun.substring(0,3));
                addedit_munlink((public_ver.mlinkid+1)+"");
            }else{
                if(public_ver.FEN==0){toasmsg="Internet required.";}else{toasmsg="इन्टरनेट आवश्यक छ।";}
                Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();}
        }else{
            custlinkadopter adapterlinklist = new custlinkadopter(getApplicationContext(), web, imageId);
            lstlink.setAdapter(adapterlinklist);
        }
    }
    private void syncmunlinklist(String lnkid){
        String urlSuffix = "synclink.php?lnkid=" + lnkid;

        class RegisterUser extends AsyncTask<String, Void, String> {
            // ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String s) {
                dboper = new dboperation();
                if (s.equals("nodata")){

                }else{
                    dboper.sync_munlink(s);
                    load_link();
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
    private void addedit_munlink(String lnkid){
        String urlSuffix = "addmunlink.php?lnkid=" + lnkid+"&leng="+lneng.getText().toString()+"&lnep="+lnnep.getText().toString()+"&llink="+lndes.getText().toString();

        class RegisterUser extends AsyncTask<String, Void, String> {
            // ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getApplicationContext(), "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                if (s.equals("Exist")) {
                    if(public_ver.FEN==0){toasmsg="Web Link Detail Update done.";}else{toasmsg="वेब लिङ्क विवरण अद्यावधिक गरियो।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                    String newmunlink=lnkid+"/ww/"+lneng.getText().toString()+"/ww/"+lnnep.getText().toString()+"/ww/"+lndes.getText().toString();
                    dboper.sync_munlink(newmunlink);
                    load_link();
                }
                if (s.equals("Added")) {
                    if(public_ver.FEN==0){toasmsg="Web Link  Detail Added.";}else{toasmsg="वेब लिङ्क विवरण थपियो।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                    String newmunlink=lnkid+"/ww/"+lneng.getText().toString()+"/ww/"+lnnep.getText().toString()+"/ww/"+lndes.getText().toString();
                    dboper.sync_munlink(newmunlink);
                    load_link();
                }
                if (s.equals("Error")) {
                    if(public_ver.FEN==0){toasmsg="Web Link  Detail Inserting failed.";}else{toasmsg="वेब लिङ्क विवरण सम्मिलित गर्न असफल भयो।";}
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
    private void delete_munlink(String lnkid){
        String urlSuffix = "deletemunlink.php?lnkid=" + lnkid;

        class RegisterUser extends AsyncTask<String, Void, String> {
            // ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getApplicationContext(), "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {

                if (s.equals("Deleted")) {
                    if(public_ver.FEN==0){toasmsg="Web Link Deleted.";}else{toasmsg="वेब लिङ्क हटाइयो।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                    dboper.delete_munlink(lnkid);
                    load_link();
                }
                if (s.equals("Error")) {
                    if(public_ver.FEN==0){toasmsg="Web Link Not deleted.";}else{toasmsg="वेब लिङ्क विवरण हटौना असफल भयो।";}
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
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    private AlertDialog DelYNbox(){
        String Y,N;
        if(public_ver.FEN==0){toasmsg="Want to Delete selected link?";Y="YES";N="NO";}else{toasmsg="चयन गरिएको लिंक मेटाउन चाहनुहुन्छ?";Y="हो";N="होइन";}

        AlertDialog QuitYNbox = new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(toasmsg)
                .setPositiveButton(Y, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int Sb) {
                        delete_munlink(public_ver.mlinkid + "");
                    }})
                .setNegativeButton(N, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int Sb) {
                        if(public_ver.FEN==0){toasmsg="Delete Canceled.";}else{toasmsg="मेटाउन रद्द गरियो।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                    }})
                .create();
        return QuitYNbox;
    }
}