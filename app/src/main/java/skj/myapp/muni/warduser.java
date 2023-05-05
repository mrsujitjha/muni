package skj.myapp.muni;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;

import static skj.myapp.muni.MainActivity.create_folder_in_app_package_media_dir_new2;

public class warduser extends AppCompatActivity {
    private ImageButton btninfo,btngrievance;
    private String arrayuser[] = {"ALL","Public", "Ward", "Municipality", "Admin"};
    private ImageView iphoto;
    private File photoFile;
    private String imageFileName;
    private String toasmsg;
    String msyncdate="";

    private static dboperation dboper=null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.warddash);
        String wno= public_ver.muid+"";
        btninfo= (ImageButton) findViewById(R.id.imageButton7);
        btngrievance= (ImageButton) findViewById(R.id.imageButton8);
        iphoto=(ImageView)  findViewById(R.id.imageView3);

        dboper = new dboperation();
        if(public_ver.FEN==0) {
            this.setTitle("WARD DASHBOARD : Ward-"+wno.substring(3,5));
        }else{
            this.setTitle("वडा ड्यासबोर्ड : वडा न.-"+public_ver.wnonep[ Integer.parseInt( wno.substring(3,5))]);
        }

        load_profile_pic();

        btninfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pinfo=new Intent(getApplicationContext(),publishinfo.class);
               startActivity(pinfo);
           }
        });
        btngrievance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent grievance=new Intent(getApplicationContext(),grievances.class);
                startActivity(grievance);
            }
        });
    }
    private File getPhotoFileUri(String fileName) {
        String npath="";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            npath = create_folder_in_app_package_media_dir_new2(this);
        }else {
            npath =Environment.getExternalStorageDirectory().toString();
        }

        File file = new File(npath + "/munipic/"+ fileName);
        return file;
    }
    private void load_profile_pic() {
        imageFileName = public_ver.loginmail.replace("@gmail.com", "_gm") + "_myphoto.jpg";
        photoFile = getPhotoFileUri(imageFileName);
        Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        if (myBitmap == null) {
            iphoto.setImageResource(R.drawable.clickme);
        } else {
            iphoto.setImageBitmap(myBitmap);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wardmun, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.search:
                Intent register=new Intent(getApplicationContext(),register.class);
                startActivity(register);
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