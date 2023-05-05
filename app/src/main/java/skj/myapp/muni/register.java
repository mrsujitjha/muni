package skj.myapp.muni;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import static skj.myapp.muni.MainActivity.create_folder_in_app_package_media_dir_new2;

public class register extends AppCompatActivity {
    private static Spinner spinnerpro,spinnermun,spinnerward;
    private Button btnpic,btnsave;

    private EditText uemail,uname,uphone,uctz,ufname,ufctz,umname,umctz,uhusno;
    private static ArrayAdapter<CharSequence> adaptermun,adapterward ;
    private static dboperation dboper=null;
    private String promunward;
    private static final String AddPlace_URL="https://vista2074.com/muni/";

    private boolean geotagready;
    private GPSTracker gps;
    private ImageView iphoto,iadd;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int doc_selected = 1;
    private File photoFile;
    private String imageFileName;
    private String welcomemsgeng,welcomemsgnep;
    private String toasmsg;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        dboper = new dboperation();
        spinnerpro= (Spinner) findViewById(R.id.spinner_pro);
        spinnermun= (Spinner) findViewById(R.id.spinner_mun);
        spinnerward= (Spinner) findViewById(R.id.spinner_ward);
        btnpic=(Button)  findViewById(R.id.button);
        btnsave=(Button)  findViewById(R.id.button2);


        uemail=(EditText)  findViewById(R.id.editTextTextEmailAddress2);
        uname=(EditText)  findViewById(R.id.editTxtname);
        uphone=(EditText)  findViewById(R.id.editTextPhone);
        uctz=(EditText)  findViewById(R.id.editTxtctz);
        ufname=(EditText)  findViewById(R.id.editTxtctz2);
        ufctz=(EditText)  findViewById(R.id.editTxtctz3);
        umname=(EditText)  findViewById(R.id.editTxtctz4);
        umctz=(EditText)  findViewById(R.id.editTxtctz5);
        uhusno=(EditText)  findViewById(R.id.editTxtctz6);
        iphoto=(ImageView)  findViewById(R.id.iphoto);
        iadd=(ImageView)  findViewById(R.id.imageButton21);
        uemail.setFocusable(false);
        geotagready=false;
        welcomemsgeng="Thank you for joining Integrated Mobile App. Now you can create your profile and upload your documents on Municipality server. Publish your suggestion/grievances to concerned ward and Municipality. Receive latest information/messages from ward and municipality.";
        welcomemsgnep="एकीकृत मोबाइल एप् मा सहभागी भएकोमा धन्यवाद। अब तपाईंले आफ्नो प्रोफाइल बनाउन सक्नुहुन्छ र नगरपालिकाको सर्भरमा आफ्ना कागजातहरू अपलोड गर्न सक्नुहुनेछ। आफ्नो वडा र नगरपालिकामा सुझाव/गुनासाहरू ब्यक्त गर्न सक्नुहुनेछ । वडा र नगरपालिकाबाट सुचना तथा जानकारीहरु प्राप्त गर्न सक्नुहुनेछ।";

        if(public_ver.FEN==0) {
            this.setTitle("USER REGISTRATION");
            uname.setHint("Type Name");
            uphone.setHint("Enter phone number");
            uctz.setHint("Enter Citizenship no");
            ufname.setHint("Enter Fathers Name");
            ufctz.setHint("Enter Fathers Citizenship no");
            umname.setHint("Enter Mother Name");
            umctz.setHint("Enter Mother Citizenship no");
            uhusno.setHint("Enter House Number");
            btnpic.setText("UPLOAD DOCUMENTS");
            btnsave.setText("SAVE/UPDATE");
            }else{
            this.setTitle("प्रयोगकर्ता दर्ता");
            uname.setHint("नाम टाइप गर्नुहोस्");
            uphone.setHint("फोन नम्बर प्रविष्ट गर्नुहोस्");
            uctz.setHint("नागरिकता नम्बर प्रविष्ट गर्नुहोस्");
            ufname.setHint("बुबाको नाम प्रविष्ट गर्नुहोस्");
            ufctz.setHint("बुबाको नागरिकता नम्बर प्रविष्ट गर्नुहोस्");
            umname.setHint("आमाको नाम प्रविष्ट गर्नुहोस्");
            umctz.setHint("आमाको नागरिकता नम्बर प्रविष्ट गर्नुहोस्");
            uhusno.setHint("घर नम्बर प्रविष्ट गर्नुहोस्");
            btnpic.setText("कागजातहरू अपलोड गर्नुहोस्");
            btnsave.setText("सेव/अपडेट");
                }
        uname.setText(public_ver.loginuser);
        uemail.setText( public_ver.loginmail);

        if(public_ver.loginmail.contains("@gmail.com")){
            spinnerpro.setEnabled(true);
            spinnermun.setEnabled(true);
            iadd.setVisibility(View.VISIBLE);
            spinnerward.setEnabled(true);}else{
            iadd.setVisibility(View.GONE);
            spinnerpro.setEnabled(false);
            spinnermun.setEnabled(false);
            spinnerward.setEnabled(false);
        }

        loadspinner_province();

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
                public_ver.wardno=pos+1;
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        iphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_gps();
                if (geotagready) {
                    AlertDialog YNbox = loadphotoorcamera_YNbox();
                    YNbox.show();
              }
            }
        });
        iadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog YNbox = Createnewpro_YNbox();
                YNbox.show();}
        });
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String mmail=String.valueOf(uemail.getText());
                public_ver.musername= String.valueOf(uname.getText());
                public_ver.muserphone=String.valueOf(uphone.getText());
                public_ver.muserctz=String.valueOf(uctz.getText());
                public_ver.muserfather=String.valueOf(ufname.getText());
                public_ver.muserfctz=String.valueOf(ufctz.getText());
                public_ver.musermother=String.valueOf(umname.getText());
                public_ver.musermctz=String.valueOf(umctz.getText());
                public_ver.muserhno=String.valueOf(uhusno.getText());
               if(public_ver.usertype>2){public_ver.wardno=0;}
                public_ver.muid= (public_ver.province*100+public_ver.municipality)*100+public_ver.wardno;
                promunward=  public_ver.muid+"";
               if( isNetworkConnected()) {user_registration(mmail);}else{
                   if(public_ver.FEN==0){toasmsg="Internet required.";}else{toasmsg="इन्टरनेट आवश्यक छ।";}
                   Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();}
                if(imageFileName!=null){
                    dboper.Savepicinforecord(imageFileName,"Profilepic");}
            }
        });
        btnpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent uploads=new Intent(getApplicationContext(),uploaddoc.class);
                startActivity(uploads);
            }
        });
        load_user();
    }
    public void loadspinner_province(){
        String[] arraySpinner;
      if(public_ver.FEN==0) {
          arraySpinner = new String[]{"Province-1", "Madhesh", "Bagmati", "Gandaki", "Lumbini", "Karnali", "Sudurpashchim"};
      }else{
          arraySpinner = new String[]{"प्रदेश-१", "मधेश", "वाग्मती", "गण्डकी", "लुम्बिनी", "कर्णाली", "सुदूरपश्चिम"};
      }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.custspinner, arraySpinner);
        spinnerpro.setAdapter(adapter);
    }
    public void loadspinner_Ward(){
        adapterward = new ArrayAdapter <CharSequence> (this, R.layout.custspinner );
        int pid =public_ver.province*100+public_ver.municipality;
        dboper.ward_list(pid,adapterward);
        spinnerward.setAdapter(adapterward);
        int i=Integer.parseInt(promunward.substring(3,5))-1;
        int j=adapterward.getCount();
        if (i<=j){spinnerward.setSelection(i);}
    }
    public void loadspinner_Municipality(int Prno){
        adaptermun = new ArrayAdapter <CharSequence> (this, R.layout.custspinner );
        dboper.Get_mun_list(Prno,adaptermun);
        spinnermun.setAdapter(adaptermun);
        int i=Integer.parseInt(promunward.substring(1,3))-1;
        int j=adaptermun.getCount();
        if (i<=j){spinnermun.setSelection(i);}
    }
    public void load_user(){

        dboper.getprofile(public_ver.loginmail);
        uname.setText(public_ver.musername);
        uphone.setText(public_ver.muserphone);
        uctz.setText(public_ver.muserctz);
        ufname.setText(public_ver.muserfather);
        ufctz.setText(public_ver.muserfctz);
        umname.setText(public_ver.musermother);
        umctz.setText(public_ver.musermctz);
        uhusno.setText(public_ver.muserhno);
        promunward=  public_ver.muid+"";
        if (public_ver.muid==0){promunward="10101";}else {
            spinnerpro.setSelection(Integer.parseInt(promunward.substring(0,1))-1);
        }
        load_profile_pic();
    }
    private void user_registration(String mmail){

         String urlSuffix = "adduser.php?umail="+ mmail+"&uname="+public_ver.musername+"&uphone="+public_ver.muserphone+"&uctz="+public_ver.muserctz+"&fname="+public_ver.muserfather+"&fctz="+public_ver.muserfctz+"&mname="+public_ver.musermother+"&mctz="+public_ver.musermctz+"&uhno="+public_ver.muserhno+"&upmw="+promunward+"&utype=110000&password=123";
        class RegisterUser extends AsyncTask<String, Void, String> {
            // ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getApplicationContext(), "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                if (s.equals("User added")||s.equals("User Exist")){
                    dboper.save_user(mmail);
                    if (s.equals("User added")) {
                        if(public_ver.FEN==0){toasmsg="Registration Successful.";}else{toasmsg="दर्ता सफल।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                        dboper.Welcome_message(welcomemsgeng);
                        dboper.Welcome_message(welcomemsgnep);
                    }
                    if (s.equals("User Exist")) {
                    if(public_ver.FEN==0){toasmsg="User detail updated Successful.";}else{toasmsg="प्रयोगकर्ता विवरण सफलतापूर्वक अद्यावधिक गरियो।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();}
                }else{
                    if(public_ver.FEN==0){toasmsg="Fail to Register/update information.";}else{toasmsg="साइन इन रद्द गरियो";}
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
    void get_gps(){
        // create class object
        gps = new GPSTracker(getApplicationContext());
        geotagready=false;
        // check if GPS enabled
        if(gps.canGetLocation()){
            public_ver.lat = gps.getLatitude();
            public_ver.lng = gps.getLongitude();
            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + public_ver.lat + "\nLong: " +  public_ver.lng  , Toast.LENGTH_LONG).show();
            geotagready=true;
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            // gps.showSettingsAlert();
            if(public_ver.FEN==0){toasmsg="GPS NOT ON.";}else{toasmsg="GPS अन छैन";}
            Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
        }
    }
    public void strata_camera(){
        imageFileName=public_ver.loginmail.replace("@gmail.com","_gm")+"_myphoto.jpg";
        photoFile = new File(public_ver.picfilepath + "/munipic/"+ imageFileName);
    }
    //camera code updated from here
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                strata_camera();
                    Uri fileProvider = FileProvider.getUriForFile(register.this, "skj.myapp.muni.fileprovider", photoFile);
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, fileProvider);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                if(public_ver.FEN==0){toasmsg="camera permission denied.";}else{toasmsg="क्यामेरा अनुमति अस्वीकार गरियो";}
                Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            iphoto.setImageBitmap(myBitmap);
        }
        if(requestCode==doc_selected){
            Uri uri = data.getData();
                try {
                    copyFile(uri);
                    Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                    iphoto.setImageBitmap(myBitmap);
                } catch (IOException e) {
                    e.printStackTrace(); }
        }
    }

    private void load_profile_pic(){
       imageFileName=public_ver.loginmail.replace("@gmail.com","_gm")+"_myphoto.jpg";
       photoFile = new File(public_ver.picfilepath + "/munipic/"+ imageFileName);
       Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
      if(myBitmap==null){
       iphoto.setImageResource(R.drawable.clickme);}else{
          iphoto.setImageBitmap(myBitmap);
      }
    }
    private void copyFile(Uri pathFrom) throws IOException {
        Uri pathTo = Uri.fromFile(photoFile);
        try (InputStream in = getContentResolver().openInputStream(pathFrom)) {
            if(in == null) return;
            try (OutputStream out = getContentResolver().openOutputStream(pathTo)) {
                if(out == null) return;
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }
    private AlertDialog Createnewpro_YNbox(){
        String Y,N;
        if(public_ver.FEN==0){toasmsg="Want to create New profile ID?";Y="YES";N="NO";}else{toasmsg="नयाँ प्रोफाइल आईडी बनाउन चाहनुहुन्छ?";Y="हो";N="होइन";}
        AlertDialog QuitYNbox = new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(toasmsg)
                .setPositiveButton(Y, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int Sb) {
                      int  mcount= dboper.getusercount();
                        String newproid=public_ver.loginmail.replace("@gmail.com","_")+mcount;
                        uemail.setText( newproid);
                        spinnerpro.setEnabled(false);
                        spinnermun.setEnabled(false);
                        spinnerward.setEnabled(false);
                    }})
                .setNegativeButton(N, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int Sb) {
                        if(public_ver.FEN==0){toasmsg="Profile creation Canceled.";}else{toasmsg="प्रोफाइल सिर्जना रद्द गरियो।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                    }})
                .create();
        return QuitYNbox;
    }
    private AlertDialog loadphotoorcamera_YNbox(){
        String Y,N;
        if(public_ver.FEN==0){toasmsg="Want to capture photo?";Y="YES";N="Select Image";}else{toasmsg="नयाँ फोटो लिन चाहनुहुन्छ?";Y="चाहन्छु";N="फोटो छानुस";}
        AlertDialog QuitYNbox = new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(toasmsg)
                .setPositiveButton(Y, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int Sb) {
                        open_cameraphoto();
                    }})
                .setNegativeButton(N, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int Sb) {
                        if(public_ver.FEN==0){toasmsg="Choose a file";}else{toasmsg="एउटा फाइल छान्नुहोस्।";}
                        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                        chooseFile.setType("*/*");
                        chooseFile = Intent.createChooser(chooseFile,toasmsg );
                        startActivityForResult(chooseFile, doc_selected);
                    }})
                .create();
        return QuitYNbox;
    }
    private void open_cameraphoto(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            }
            else {
                strata_camera();
                Uri fileProvider = FileProvider.getUriForFile(register.this, "skj.myapp.muni.fileprovider", photoFile);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }else{  strata_camera();
            Uri fileProvider = FileProvider.getUriForFile(register.this, "skj.myapp.muni.fileprovider", photoFile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

}