package skj.myapp.muni;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class publicuser extends AppCompatActivity {

    private Button btnprofile,btngrievance,btninfo;
    private ListView lstlink;
    private GridView glstlink;
    private String arrayuser[] = {"ALL","Public", "Ward", "Municipality", "Admin"};
    private static ArrayAdapter<CharSequence> adapterlinklist ;
    private static dboperation dboper=null;
    private static final String AddPlace_URL="https://vista2074.com/muni/";
    private String toasmsg;
    String msyncdate="";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publicdash);
        dboper = new dboperation();
        btnprofile=(Button)  findViewById(R.id.button4);
        btngrievance=(Button)  findViewById(R.id.button5);
        btninfo=(Button)  findViewById(R.id.button6);
        lstlink=(ListView)  findViewById(R.id.lstlink);
        glstlink=(GridView)  findViewById(R.id.glstlink);

        if(public_ver.FEN==0) {
            this.setTitle("PUBLIC DASHBOARD");
            btnprofile.setText("Profile");
            btngrievance.setText("Griev..");
            btninfo.setText("Info");
        }else{
            this.setTitle("सार्वजनिक ड्यासबोर्ड");

            btnprofile.setText("प्रोफाइल");
            btngrievance.setText("गुनासो");
            btninfo.setText("जानकारी");
       }

        if (public_ver.colno==1){ public_ver.webfontsize=20;}else{public_ver.webfontsize=14;}
        load_link();
        lstlink.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                String symun=public_ver.muid+"";
                int newid=Integer.parseInt(symun.substring(0,3))*100+position+1;
              dboper.get_mun_selectedlink(newid+"");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(public_ver.mlindes));
              startActivity(browserIntent);
            }});
        glstlink.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                String symun=public_ver.muid+"";
                int newid=Integer.parseInt(symun.substring(0,3))*100+position+1;
                dboper.get_mun_selectedlink(newid+"");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(public_ver.mlindes));
                startActivity(browserIntent);
            }});
        btnprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register=new Intent(getApplicationContext(),register.class);
                startActivity(register);

            }
        });
        btngrievance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent grievances=new Intent(getApplicationContext(),grievances.class);
                startActivity(grievances);

            }
        });
        btninfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent grievances=new Intent(getApplicationContext(),publishinfo.class);
                startActivity(grievances);
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
            syncmunlinklist(symun.substring(0,3));

        }else{
            custlinkadopter adapterlinklist = new custlinkadopter(getApplicationContext(), web, imageId);
            if (public_ver.colno == 1) {
                lstlink.setAdapter(adapterlinklist);
                glstlink.setVisibility(View.GONE);
                lstlink.setVisibility(View.VISIBLE);
            } else {
                glstlink.setNumColumns(public_ver.colno);
                glstlink.setAdapter(adapterlinklist);
                lstlink.setVisibility(View.GONE);
                glstlink.setVisibility(View.VISIBLE);
            }
        }
    }
    private void syncmunlinklist(String lnkid){

        String urlSuffix = "synclink.php?lnkid=" + lnkid;
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.search:
                if (public_ver.colno==1){ public_ver.colno=2;public_ver.webfontsize=14;}else{public_ver.colno=1;public_ver.webfontsize=20;}
                load_link();
                return true;
            case R.id.sync:
                get_sync_dt_from_user();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void get_sync_dt_from_user(){
        String Y,N,t="";
        if(public_ver.FEN==0){toasmsg="Want to Sync details?";Y="YES";N="NO";t="Sync upto Below date";}else{toasmsg="विवरणहरू सिङ्क गर्न चाहनुहुन्छ?";Y="हो";N="होइन";t="तलको मिति सम्म सिङ्क गर्नुहोस्";}

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(t);
        final EditText input = new EditText(this);
        input.setText(dboper.getdate_timeoooo());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton(Y, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                msyncdate = input.getText().toString();
                public_ver.pview=1;
                if(msyncdate.length()==8) {
                    syncserver.sync_griv(arrayuser[public_ver.usertype],msyncdate+"0000");
                    syncserver.sync_information(arrayuser[public_ver.usertype],msyncdate+"0000");
                    String symun=public_ver.muid+"";
                    dboper. delete_mun_link();
                    syncmunlinklist(symun.substring(0,3));
                    load_link();
                }else{
                    if(public_ver.FEN==0){toasmsg="Enter date for sync old data.";}else{toasmsg="पुरानो डाटा सिंक गर्न मिति प्रविष्ट गर्नुहोस्।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
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

}