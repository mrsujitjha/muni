package skj.myapp.muni;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class publishinfo extends AppCompatActivity {
private ImageButton imsend;
private EditText editinfo;
private TextView txtinf;
private Spinner spreceiver,spward;
private String infoid,uinfo,infosender,inforeceiver;
private ListView lstinfo;
private static dboperation dboper=null;
private long pressedTime;
private String [] arrayuser;
private String []infouser;
private String toasmsg;
private int wawrdreceiver;
private static ArrayAdapter<CharSequence> adapterward ;
private static final String AddPlace_URL="https://vista2074.com/muni/";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publishinfo);

        dboper = new dboperation();
        spreceiver= (Spinner) findViewById(R.id.spinner5);
        spward= (Spinner) findViewById(R.id.spinner7);
        imsend=(ImageButton)  findViewById(R.id.imageButton13);
        editinfo =(EditText)  findViewById(R.id.editTextTextMultiLine2);
        lstinfo =(ListView)  findViewById(R.id.listview);
        txtinf =(TextView)  findViewById(R.id.textView2);
        infouser= new String[]{"ALL","Public", "Ward", "Municipality", "Admin"};
        spward.setVisibility(View.GONE);
        wawrdreceiver=0;
        if(public_ver.FEN==0) { this.setTitle("VIEW/POST INFORMATION");txtinf.setText("Enter Information Description (Max 150 )");
            arrayuser= new String[]{"ALL","Public", "Ward", "Municipality", "Admin"};
        }else{
            this.setTitle("हेर्नुहोस् वा पोस्ट गर्नुहोस्");txtinf.setText("जानकारी विवरण प्रविष्ट गर्नुहोस् (अधिकतम 150)");
            arrayuser = new String[]{"सबै प्रयोगकर्ता","नागरिकहरू मात्र", "वार्ड प्रयोगकर्ता", "नगरपालिका प्रयोगकर्ता", "व्यवस्थापक प्रयोगकर्ता"};
        }
        if (public_ver.postinfomun==1 || public_ver.postinfoward==1){
            imsend.setVisibility(View.VISIBLE);
            spreceiver.setVisibility(View.VISIBLE);
        }else{
            if(public_ver.FEN==0) {  this.setTitle("VIEW OLD INFORMATION"); }else{this.setTitle("पुरानो जानकारी हेर्नुहोस्");}
            imsend.setVisibility(View.GONE);
            spreceiver.setVisibility(View.GONE);
            txtinf.setVisibility(View.GONE);
            editinfo.setVisibility(View.GONE);
        }
        loadspinner_usertype(arrayuser);
        load_sent_message("");
        imsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String curdate=dboper.getdatetime();
                uinfo=editinfo.getText().toString().replace(":"," ");
                infoid=public_ver.muid+wawrdreceiver+"-"+curdate+"-"+public_ver.loginmail;
                infosender=infouser[public_ver.usertype];
                if(uinfo.length()>0) {
                    Send_info();  editinfo.setText("");wawrdreceiver=0; }else{
                    if(public_ver.FEN==0){toasmsg="Enter Information to be published.";}else{toasmsg="प्रकाशित गर्न जानकारी प्रविष्ट गर्नुहोस्।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                }
            }
        });

        spreceiver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                inforeceiver=infouser[pos];
               if(pos==2 && public_ver.usertype >2) {spward.setVisibility(View.VISIBLE);loadspinner_Ward();}else{ spward.setVisibility(View.GONE);wawrdreceiver=0;}
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        spward.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                wawrdreceiver=pos;
                if(pos>0) {
                    inforeceiver="Ward**";}else{inforeceiver="Ward";}
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        lstinfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (pressedTime + 2000 > System.currentTimeMillis()) {
                    dboper.Delete_info(position);
                    load_sent_message("");
                } else {
                    if(public_ver.FEN==0){toasmsg="To delete message tap again";}else{toasmsg="सन्देश मेटाउन फेरि ट्याप गर्नुहोस्";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
               }
               pressedTime = System.currentTimeMillis();
            }

        });

    }
    public void loadspinner_usertype( String[] arraySpinner){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spreceiver.setAdapter(adapter);
    }
    private void loadspinner_Ward(){
        adapterward = new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
        adapterward.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        int pid =public_ver.province*100+public_ver.municipality;
        dboper.ward_list_all(pid,adapterward);
        spward.setAdapter(adapterward);
    }
    private void save_info(){
        dboper.SAVE_INFO(infoid,uinfo,infosender,inforeceiver);
        load_sent_message("");
    }
    private void Send_info(){

        String urlSuffix = "saveinfo.php?infoid=" + infoid+"&infotext="+uinfo+"&infosender="+infosender+"&inforeceiver="+inforeceiver;
        class RegisterUser extends AsyncTask<String, Void, String> {
            // ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getApplicationContext(), "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                if (s.equals("Error")){
                    Toast.makeText(getApplicationContext(), "info not sent.", Toast.LENGTH_SHORT).show();
                }else{
                    if (s.equals("Info created")){ save_info();
                        if(public_ver.FEN==0){toasmsg="Info published successfully.";}else{toasmsg="जानकारी सफलतापूर्वक प्रकाशित भयो।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                    }
                    if (s.equals("Info update")){ save_info();
                        if(public_ver.FEN==0){toasmsg="Info updated successfully.";}else{toasmsg="जानकारी सफलतापूर्वक अपडेट गरियो।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
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
    public void load_sent_message(String msgfilter){
        final ArrayList<infocustomview> arrayList = new ArrayList<infocustomview>();
        dboper.get_msg_send_arraylist(msgfilter,arrayList);
        customadaptor customadaptor = new customadaptor(this, arrayList);
        lstinfo.setAdapter(customadaptor);
    }

}
