package skj.myapp.muni;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class grievances extends AppCompatActivity {
    private ImageButton imsend;
    private EditText editinfo;
    private TextView txtinf;
    private Spinner spreceiver;
    private ListView lstgrivence;
    private String infoid,grivtext,infosender,inforeceiver,replyid,replytext;
    private static dboperation dboper=null;
    private static final String AddPlace_URL="https://vista2074.com/muni/";
    private byte grivreplyreceiver;
    private  String[] arraySpinner;
    private long pressedTime;
    private String toasmsg;
    private String downloadfilename;

    private String docpathselected,uploadfilename;
    private static final int doc_selected = 1;
    String arrayuser[] = {"ALL","Public", "Ward", "Municipality", "Admin"};
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grievances);
        spreceiver= (Spinner) findViewById(R.id.spinner5);
        imsend=(ImageButton)  findViewById(R.id.imageButton13);
        editinfo =(EditText)  findViewById(R.id.editTextTextMultiLine2);
        lstgrivence =(ListView)  findViewById(R.id.listview);
        txtinf =(TextView)  findViewById(R.id.textView2);

        loadspinner_usertype();
        dboper = new dboperation();

        txtinf.setText("Enter  Griviences / Reply");
        if ( public_ver.usertype==1){
            if(public_ver.FEN==0) {this.setTitle("POST AND VIEW GRIEVANCES");}else{this.setTitle("गुनासोहरू पोस्ट गर्नुहोस् र हेर्नुहोस्");txtinf.setText("गुनासो प्रविष्ट गर्नुहोस्");}
            imsend.setVisibility(View.VISIBLE);
            load_sent_griv(public_ver.loginmail);
        }else{
            imsend.setVisibility(View.GONE);
            txtinf.setVisibility(View.GONE);
            editinfo.setVisibility(View.GONE);
            spreceiver.setVisibility(View.GONE);
            load_sent_griv("");
            if(public_ver.replygrivmun==1 || public_ver.replygrivward==1){
                if(public_ver.FEN==0) {this.setTitle("REPLY AND VIEW GRIEVANCES");}else{this.setTitle("जवाफ दिनुहोस् र गुनासोहरू हेर्नुहोस्");txtinf.setText("जवाफ प्रविष्ट गर्नुहोस्");}
               }else {
                if(public_ver.FEN==0) {this.setTitle("VIEW GRIEVANCES");}else{this.setTitle("गुनासोहरू हेर्नुहोस्");}
            }
        }
        lstgrivence.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

               if (pressedTime + 2000 > System.currentTimeMillis()) {
                    AlertDialog YNbox = Delete_msgYN(position);
                    YNbox.show();
                } else {
                    Toast.makeText(getBaseContext(), "To delete message tap again", Toast.LENGTH_SHORT).show();
                   downloadfilename= dboper.grivid(position);
                    if(public_ver.replygrivmun==1 || public_ver.replygrivward==1){
                        infoid= dboper.grivid(position);
                        imsend.setVisibility(View.VISIBLE);
                        txtinf.setVisibility(View.VISIBLE);
                        editinfo.setVisibility(View.VISIBLE);
                        spreceiver.setVisibility(View.VISIBLE);
                    }
                }
                pressedTime = System.currentTimeMillis();
            }});
        imsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String curdate=dboper.getdatetime();
                infosender=arrayuser[public_ver.usertype];replyid=""; public_ver.gpic="-";
                if (infosender.equals("Public") ) {
                    grivtext=editinfo.getText().toString();
                    infoid=public_ver.muid+"-"+curdate+"-"+public_ver.loginmail;
                    if(grivtext.length()>0) {
                        AlertDialog YNbox = showimageuploadYN();
                        YNbox.show();
                    }else{
                        if(public_ver.FEN==0){toasmsg="Please enter grievances/suggestion to publish.";}else{toasmsg="कृपया प्रकाशित गर्नका लागि गुनासो/सुझाव प्रविष्ट गर्नुहोस्।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                    }
                }else{// reply by ward or Municipal
                    replytext=editinfo.getText().toString();
                    replyid=public_ver.muid+"-"+curdate+"-"+public_ver.loginmail;
                    if(replytext.length()>0) {
                        replytext = replytext.replace(":", " ");
                        AlertDialog YNbox = showimageuploadYN();
                        YNbox.show();
                    }else {
                        if(public_ver.FEN==0){toasmsg="Please enter reply to publish.";}else{toasmsg="कृपया जवाफ प्रकाशित गर्न प्रविष्ट गर्नुहोस्।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        spreceiver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
             if(pos==0){inforeceiver="Municipality";}else{inforeceiver="Ward";}
                grivreplyreceiver= (byte) pos;
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        }
    private String getPath(Uri uri) {
        String path = null;
        String[] projection = { MediaStore.Files.FileColumns.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if(cursor == null){
            path = uri.getPath();
        }
        else{
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }
        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==doc_selected){
            Uri uri = data.getData();
            docpathselected = getPath(uri);
            String []mimeType = getContentResolver().getType(uri).split("/");
            String docname[]=docpathselected.split("/")  ;
            int i=docname.length;
            txtinf.setText(docname[i-1]);
           try {   get_attachment_name("."+mimeType[1]);
                    copyFile(uri);
                    new UploadFileAsync().execute(public_ver.picfilepath + "/munipic/"+ uploadfilename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
    private void get_attachment_name(String extension){
       if (replyid.length()>0) {uploadfilename=replyid+"_img"+extension;}else{uploadfilename=infoid+"_img"+extension;}
    }
    private void copyFile(Uri pathFrom) throws IOException {

        Uri pathTo = Uri.fromFile(new File(public_ver.picfilepath  + "/munipic/"+uploadfilename));
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
        Reducesize(new File(public_ver.picfilepath  + "/munipic/"+uploadfilename))  ;
    }
    private void  Reducesize(File file){
        try {
            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image
            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();
            // The new size we want to scale to
            final int REQUIRED_SIZE=75;
            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();
            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);
        } catch (Exception e) {

        }
    }
    private AlertDialog showYNbox(){
        String Y,N;
        if(public_ver.FEN==0){toasmsg="Want to publicly post the message?";Y="YES";N="NO";}else{toasmsg="सार्वजनिक रूपमा सन्देश पोस्ट गर्न चाहनुहुन्छ?";Y="हो";N="होइन";}

        AlertDialog QuitYNbox = new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(toasmsg)
                .setPositiveButton(Y, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int Sb) {
                        if(replytext.length()>0) {
                            Send_info();
                            if(public_ver.FEN==0){toasmsg="Message Posted.";}else{toasmsg="सन्देश पोस्ट गरियो।";}
                            Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(getIntent());
                        }else{
                            if(public_ver.FEN==0){toasmsg="Enter Information to be published.";}else{toasmsg="प्रकाशित गर्न जानकारी प्रविष्ट गर्नुहोस्।";}
                            Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                            editinfo.setText("");
                        }

                    }})
                .setNegativeButton(N, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int Sb) {
                        if(public_ver.FEN==0){toasmsg="Process canceled.";}else{toasmsg="प्रक्रिया रद्द गरियो।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                    }})
                .create();
        return QuitYNbox;
    }
    private AlertDialog showimageuploadYN(){
        String Y,N;
        if(public_ver.FEN==0){toasmsg="Want to upload image with the message?";Y="YES";N="NO";}else{toasmsg="सन्देशको साथ फोटो अपलोड गर्न चाहनुहुन्छ?";Y="हो";N="होइन";}

        AlertDialog QuitYNbox = new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(toasmsg)
                .setPositiveButton(Y, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int Sb) {
                        if(public_ver.FEN==0){toasmsg="Choose a image";}else{toasmsg="एउटा छवि छान्नुहोस्।";}
                        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                        chooseFile.setType("image/*");
                        chooseFile = Intent.createChooser(chooseFile,toasmsg );
                        startActivityForResult(chooseFile, 1);
                    }})
                .setNegativeButton(N, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int Sb) {
                        public_ver.gpic="-";
                        publicegriv_reply_withorwithoutimage();
                    }})
                .create();
        return QuitYNbox;
    }
    private void publicegriv_reply_withorwithoutimage(){
        if (infosender.equals("Public") ) {
            Send_griv();editinfo.setText("");
        }else{// reply by ward or Municipal
                Send_griv_reply(grivreplyreceiver);

        }
    }
    public void loadspinner_usertype(){

        if( public_ver.usertype==1){
            if(public_ver.FEN==0) { arraySpinner = new String[] {"Municipality", "Ward"};}else{ arraySpinner = new String[] {"नगरपालिका", "वार्ड"};}
        }else{
            if(public_ver.FEN==0) { arraySpinner = new String[] {"Individual","Public"};}else{ arraySpinner = new String[] {"व्यक्तिगत","सार्वजनिक" };}
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spreceiver.setAdapter(adapter);
    }

    private void save_info(){
        dboper.SAVE_GRIV(infoid,grivtext,infosender,inforeceiver);
        if ( public_ver.usertype==1){ load_sent_griv(public_ver.loginmail);}else{load_sent_griv("");}
    }
    private void save_reply(){
        dboper.reply_griv(infoid,replytext,replyid);
        if ( public_ver.usertype==1){ load_sent_griv(public_ver.loginmail);}else{load_sent_griv("");}
    }
    private void save_grivinfo(){
        dboper.SAVE_INFO(replyid,replytext,infosender,"Public");
    }

    private void Send_griv(){

        String urlSuffix = "savegriv2.php?infoid=" + infoid+"&infotext="+grivtext+"&infosender="+infosender+"&inforeceiver="+inforeceiver+"&gpic="+public_ver.gpic;
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
                        if(public_ver.FEN==0){toasmsg="Grievance published successfully.";}else{toasmsg="गुनासो सफलतापूर्वक प्रकाशित भयो।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                   }
                    if (s.equals("Info update")){ save_info();
                        if(public_ver.FEN==0){toasmsg="Grievance updated successfully.";}else{toasmsg="पगुनासो सफलतापूर्वक अपडेट गरियो।";}
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

    private void Send_info(){

        String urlSuffix = "saveinfo.php?infoid=" + replyid+"&infotext="+replytext+"&infosender="+infosender+"&inforeceiver=Public";
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
                    if (s.equals("Info created")){ save_grivinfo();
                        if(public_ver.FEN==0){toasmsg="Info published successfully.";}else{toasmsg="जानकारी सफलतापूर्वक प्रकाशित भयो।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                        editinfo.setText("");
                   }
                    if (s.equals("Info update")){ save_grivinfo();
                        if(public_ver.FEN==0){toasmsg="Info updated successfully.";}else{toasmsg="जानकारी सफलतापूर्वक अपडेट गरियो।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                        editinfo.setText("");
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
    private void Send_griv_reply(int replypub){

        String urlSuffix = "replygriv2.php?infoid=" + infoid+"&replytext="+replytext+"&replyid="+replyid+"&rpic="+public_ver.gpic;
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
                    if(public_ver.FEN==0){toasmsg="Reply not save.";}else{toasmsg="जवाफ सुरक्षित गरिएको छैन।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                }else{
                  save_reply();
                    if(public_ver.FEN==0){toasmsg="Grievance reply posted successfully.";}else{toasmsg="गुनासो जवाफ सफलतापूर्वक पोस्ट गरियो।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                    if (replypub==1){
                    AlertDialog YNbox = showYNbox();
                    YNbox.show();}else{  editinfo.setText("");}
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
    public void load_sent_griv(String msgfilter){
        final ArrayList<custgrivpicview> arrayList = new ArrayList<custgrivpicview>();
        dboper.get_grv_send_arraylist(msgfilter,arrayList);
        custgrivadpterview customadaptor = new custgrivadpterview(this, arrayList);
        lstgrivence.setAdapter(customadaptor);
    }
    private AlertDialog Delete_msgYN(int position){
        String Y,N;
        if(public_ver.FEN==0){toasmsg="Want to Delete message?";Y="YES";N="NO";}else{toasmsg="सन्देश मेटाउन चाहनुहुन्छ?";Y="हो";N="होइन";}

        AlertDialog QuitYNbox = new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(toasmsg)
                .setPositiveButton(Y, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int Sb) {
                        dboper.Delete_griv(position);
                        if ( public_ver.usertype==1){ load_sent_griv(public_ver.loginmail);}else{load_sent_griv("");}
                    }})
                .setNegativeButton(N, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int Sb) {
                        if(public_ver.FEN==0){toasmsg="Delete canceled.";}else{toasmsg="मेटाउन रद्द गरियो।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                    }})
                .create();
        return QuitYNbox;
    }
    private class UploadFileAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String sourceFileUri=params[0];
            int serverResponseCode = 0;
            BufferedReader bufferReader=null;
            String result="fail";
            try {

                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;
                File sourceFile = new File(sourceFileUri);

                if (sourceFile.isFile()) {

                    try {
                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(
                                sourceFile);
                        URL url = new URL("https://vista2074.com/muni/uploaddocs.php?");

                        // Open a HTTP connection to the URL
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE",
                                "multipart/form-data");
                        conn.setRequestProperty("Content-Type",
                                "multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("bill", sourceFileUri);
                        dos = new DataOutputStream(conn.getOutputStream());
                        dos.writeBytes(twoHyphens + boundary + lineEnd);

                        dos.writeBytes("Content-Disposition: form-data; name=\"bill\";filename=\""
                                + uploadfilename + "\"" + lineEnd);
                        dos.writeBytes(lineEnd);
                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];
                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {

                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math
                                    .min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0,
                                    bufferSize);

                        }

                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens
                                + lineEnd);

                        // Responses from the server (code and message)
                        serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn
                                .getResponseMessage();

                        if (serverResponseCode == 200) {
                            // successfully uploaded
                            bufferReader=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            result=bufferReader.readLine();

                        }else{

                        }
                        fileInputStream.close();
                        dos.flush();
                        dos.close();

                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
                } // End else block
            } catch (Exception ex) {
                // ex.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("success")){
                public_ver.gpic=uploadfilename;
                publicegriv_reply_withorwithoutimage();
            }
            if(result.equals("fail")){
                if(public_ver.FEN==0){toasmsg="Picture upload Failed.";}else{toasmsg="तस्वीर अपलोड गर्न सकिएन।";}
                Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                public_ver.gpic="-";
                publicegriv_reply_withorwithoutimage();
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
