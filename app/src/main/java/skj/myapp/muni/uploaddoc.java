package skj.myapp.muni;
import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileChannel;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

public class uploaddoc extends AppCompatActivity {
    private Spinner spdoctype;
    private ImageButton imdel,imsearch,imsync;
    private EditText editinfo;
    private TextView txtview;
    private ListView lstinfo;
    private String docpathselected;
    private static final int doc_selected = 1;
    private static dboperation dboper=null;
    private String[] arraySpinner;
    public static ArrayAdapter<CharSequence> adaptersentmsg ;
    private int docid;
    private String uploadfilename;
    private static final String AddPlace_URL="https://vista2074.com/muni/";
    private String toasmsg;
    private long pressedTime;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dboper = new dboperation();

        setContentView(R.layout.uploaddoc);
        spdoctype= (Spinner) findViewById(R.id.spinner5);
        imdel=(ImageButton)  findViewById(R.id.imageButton19);
        imsearch=(ImageButton)  findViewById(R.id.imageButton18);
        imsync=(ImageButton)  findViewById(R.id.imageButton20);

        editinfo =(EditText)  findViewById(R.id.editTextTextMultiLine2);
        txtview =(TextView)  findViewById(R.id.textView2);
        lstinfo =(ListView)  findViewById(R.id.listview);

        if(public_ver.FEN==0) {
            this.setTitle("Upload User Documents");
            txtview.setText("Documents Description");

        }else{
            this.setTitle("प्रयोगकर्ता कागजातहरू अपलोड गर्नुहोस्");
            txtview.setText("कागजात विवरण");

        }
        imdel.setVisibility(View.GONE);
        Load_doctype();
        load_attachments();

        spdoctype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                docid=pos;
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        lstinfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                dboper.get_selected_doc(position);
                if (pressedTime + 2000 > System.currentTimeMillis()) {
                    File openFile = new File(public_ver.picfilepath  + "/munipic/"+public_ver.gpic);
                    openFile(openFile);
                    imdel.setVisibility(View.GONE);
                } else {
                    if(public_ver.FEN==0){toasmsg="To open documents tap again";}else{toasmsg="कागजातहरू खोल्न फेरि ट्याप गर्नुहोस्";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                    imdel.setVisibility(View.VISIBLE);
                }
                pressedTime = System.currentTimeMillis();
            }});
        imsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(public_ver.FEN==0){toasmsg="Choose a file";}else{toasmsg="एउटा फाइल छान्नुहोस्।";}
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                chooseFile = Intent.createChooser(chooseFile,toasmsg );
               startActivityForResult(chooseFile, doc_selected);

            }
        });
        imdel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog YNbox = DelYNbox();
                YNbox.show();
        }
        });
        imsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog YNbox = syncYNbox();
                YNbox.show();
            }
        });


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
            txtview.setText(docname[i-1]);
            if(mimeType[0].equals("image")){
                try {
                    get_attachment_name("."+mimeType[1]);
                    copyFile(uri);
                    Reducesize(new File(public_ver.picfilepath  + "/munipic/"+uploadfilename));
                    preparetouploaddocuments();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
            int MaxKb = 1024;
            Cursor returnCursor =getContentResolver().query(uri, null, null, null, null);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            int file_size = (int) returnCursor.getLong(sizeIndex);
             file_size=file_size/1024;
            if(file_size > MaxKb) {
                if(public_ver.FEN==0){toasmsg="Upload document size limit is 1mb only.";}else{toasmsg="अपलोड कागजात आकार सीमा 1mb मात्र हो।";}
                Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
            }else{
                try {
                    get_attachment_name("."+mimeType[1]);
                    copyFile(uri);
                    preparetouploaddocuments();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        }
    }
    private void preparetouploaddocuments(){
        if( isNetworkConnected()) {
        AlertDialog YNbox = showYNbox();
        YNbox.show();
        }else{
        if(public_ver.FEN==0){toasmsg="Internet required.";}else{toasmsg="इन्टरनेट आवश्यक छ।";}
        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();}
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
    public void Load_doctype(){

        if(public_ver.FEN==0) {
            arraySpinner = new String[]{"Citizenship", "Father Citizenship", "Mother Citizenship", "ID Card", "Others"};
        }else{
            arraySpinner = new String[]{"नागरिकता", "बुबाको नागरिकता", "आमाको नागरिकता", "पहिचान पत्र", "अन्य"};
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spdoctype.setAdapter(adapter);
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
                       // dos.writeBytes("Content-Disposition: form-data; name=\"bill\";filename=\""+ sourceFileUri + "\"" + lineEnd);

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

                        // send multipart form data necesssary after file
                        // data...
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
                            //Public_veriable.uploadst = "Error Uploading photo!Try with Good net !! " + i;
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
                if(public_ver.FEN==0){toasmsg="Documents upload done.";}else{toasmsg="कागजातहरू अपलोड गरियो।";}
                Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                add_attchment_details(uploadfilename,editinfo.getText().toString());
            }
            if(result.equals("fail")){
                if(public_ver.FEN==0){toasmsg="Documents upload Failed.";}else{toasmsg="कागजातहरू अपलोड गर्न सकिएन।";}
                Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
    private AlertDialog showYNbox(){
        String Y,N;
        if(public_ver.FEN==0){toasmsg="Want to upload selected document?";Y="YES";N="NO";}else{toasmsg="चयन गरिएको कागजात अपलोड गर्न चाहनुहुन्छ?";Y="हो";N="होइन";}
        AlertDialog QuitYNbox = new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(toasmsg)
                .setPositiveButton(Y, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int Sb) {
                    new UploadFileAsync().execute(public_ver.picfilepath + "/munipic/"+ uploadfilename);
                    }})
                .setNegativeButton(N, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int Sb) {
                        if(public_ver.FEN==0){toasmsg="Upload Canceled.";}else{toasmsg="अपलोड रद्द गरियो।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                    }})
                .create();
        return QuitYNbox;
    }
    private AlertDialog DelYNbox(){
        String Y,N;
        if(public_ver.FEN==0){toasmsg="Want to Delete selected document?";Y="YES";N="NO";}else{toasmsg="चयन गरिएको कागजात मेटाउन चाहनुहुन्छ?";Y="हो";N="होइन";}

        AlertDialog QuitYNbox = new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(toasmsg)
                .setPositiveButton(Y, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int Sb) {
                        deletedocuments(public_ver.gpic);
                    }})
                .setNegativeButton(N, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int Sb) {
                        if(public_ver.FEN==0){toasmsg="Delete Canceled.";}else{toasmsg="मेटाउन रद्द गरियो।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                    }})
                .create();
        return QuitYNbox;
    }
    private AlertDialog syncYNbox(){
        String Y,N;
        if(public_ver.FEN==0){toasmsg="Want to Sync uploaded document?";Y="YES";N="NO";}else{toasmsg="अपलोड गरिएको कागजात सिंक गर्न चाहनुहुन्छ?";Y="हो";N="होइन";}

        AlertDialog QuitYNbox = new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(toasmsg)
                .setPositiveButton(Y, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int Sb) {
                        Syncuploadeddoc(public_ver.loginmail.replace("@gmail.com",""));
                    }})
                .setNegativeButton(N, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int Sb) {
                        if(public_ver.FEN==0){toasmsg="Sync Canceled.";}else{toasmsg="सिंक रद्द गरियो।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                    }})
                .create();
        return QuitYNbox;
    }


    private void load_attachments(){
        public_ver.weblink="";
        public_ver.wimageid="";
        public_ver.webfontsize=14;
        dboper.get_all_attachments_list();
        String[] web =public_ver.weblink.split(",");
        String[] imageId =public_ver.wimageid.split(",");
        if( public_ver.weblink.equals("") ){
            if(public_ver.FEN==0){toasmsg="Add Your Picture First.";}else{toasmsg="पहिला आफ्नो फोटो संलग्न गर्नुहोस।";}
            Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();imsearch.setVisibility(View.GONE);
       }else{
            custlinkadopter adaptersentmsg = new custlinkadopter(getApplicationContext(), web, imageId);
            lstinfo.setAdapter(adaptersentmsg);

        }
    }
   private void get_attachment_name(String extension){
      String [] dtype = new String[]{"Citizenship", "Father Citizenship", "Mother Citizenship", "ID Card", "Others"};
        uploadfilename=public_ver.loginmail.replace("@gmail.com","_gm")+"_"+dtype[docid];
       int attcount=dboper.getattacmentcount(uploadfilename);
       uploadfilename=uploadfilename+"_"+attcount+extension;
   }
    private void add_attchment_details(String attid,String attdescr){

        String urlSuffix = "addattchment.php?attid=" + attid+"&attdes="+attdescr;
        class addattchdetail extends AsyncTask<String, Void, String> {
            // ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getApplicationContext(), "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                if (s.equals("Exist")) {
                    if(public_ver.FEN==0){toasmsg="Attachment Exist Description Update.";}else{toasmsg="संलग्न विवरण अद्यावधिक अवस्थित छ।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                    dboper.Savepicinforecord(uploadfilename,editinfo.getText().toString());
                    load_attachments();
                }
                if (s.equals("Added")) {
                    if(public_ver.FEN==0){toasmsg="Attachment Detail Added.";}else{toasmsg="संलग्न विवरण थपियो।";}
                    Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                    dboper.Savepicinforecord(uploadfilename,editinfo.getText().toString());
                    load_attachments();
                }
                if (s.equals("Error")) {
                    if(public_ver.FEN==0){toasmsg="Attachment Detail Inserting failed.";}else{toasmsg="संलग्न विवरण सम्मिलित गर्न असफल भयो।";}
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
        addattchdetail ur=new addattchdetail();
        ur.execute(urlSuffix);
    }
    private void deletedocuments(String fname){
        String urlSuffix = "deletedoc.php?file_name=" + fname;
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
                    if (s.equals("deleted")) {
                        if(public_ver.FEN==0){toasmsg="Documents deleted.";}else{toasmsg="कागजातहरू मेटाइयो।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                        dboper.deletedoc(fname);
                        load_attachments();
                        File delfile= new File(public_ver.picfilepath  + "/munipic/"+fname);
                        delfile.delete();
                    } else {
                        Toast.makeText(getApplicationContext(), "Documents not deleted.", Toast.LENGTH_SHORT).show();
                        if(public_ver.FEN==0){toasmsg="Documents not deleted.";}else{toasmsg="कागजातहरू मेटाउन सकिएन।";}
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
    private void Syncuploadeddoc(String mtype){
        public_ver.newgrivmessage="";
        String urlSuffix = "syncuploaddoc.php?utype=" + mtype;
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
                    dboper.delete_uploaddoc_info();
                    load_attachments();
                }else{
                    dboper.sync_uploaddoc(s);
                    load_attachments();
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
    }
    private void openFile(File url) {

        try {
            Uri uri = FileProvider.getUriForFile(this, "skj.myapp.muni.fileprovider", url);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (url.toString().contains(".zip")) {
                // ZIP file
                intent.setDataAndType(uri, "application/zip");
            } else if (url.toString().contains(".rar")){
                // RAR file
                intent.setDataAndType(uri, "application/x-rar-compressed");
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                // JPG file
               intent.setDataAndType(uri, "image/jpeg");
            } else if (url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") ||
                    url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                intent.setDataAndType(uri, "*/*");
            }
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);

           this.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
