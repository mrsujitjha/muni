package skj.myapp.muni;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;

public class changepass  extends AppCompatActivity {
  private TextView email;
  private EditText newpass,renewpass;
  private Button btnchpass;
  private String toasmsg;
  private static final String AddPlace_URL="https://vista2074.com/muni/";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepass);

        email=(TextView)  findViewById(R.id.textView5);
        newpass=(EditText)  findViewById(R.id.editTextTextPassword4);
        renewpass=(EditText)  findViewById(R.id.editTextTextPassword5);
        btnchpass=(Button)  findViewById(R.id.button11);
        email.setText(public_ver.loginmail);
        if(public_ver.FEN==0) {
            this.setTitle("Password Change");
            newpass.setHint("Enter New Password");
            renewpass.setHint("Re-Enter New Password");
            btnchpass.setText("Change Password");
            email.setHint("Registered Email");
        }else{
            this.setTitle("पासवर्ड परिवर्तन");
            newpass.setHint("नयाँ पासवर्ड प्रविष्ट गर्नुहोस्");
            renewpass.setHint("नयाँ पासवर्ड पुन: प्रविष्ट गर्नुहोस्");
            btnchpass.setText("पासवर्ड परिवर्तन गर्नुहोस्");
            email.setHint("दर्ता गरिएको इमेल");
        }


        btnchpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             if(newpass.getText().toString().equals(renewpass.getText().toString())){
             Changepassword();}else{
                 if(public_ver.FEN==0){toasmsg="Both entered Password are not same.";}else{toasmsg="प्रविष्ट गरिएका दुवै पासवर्ड समान छैनन्।";}
                 Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
             }
            }
        });
    }
    private void Changepassword(){
        String mmail=email.getText().toString();
        String mpassword=newpass.getText().toString();
        String urlSuffix = "changepassword.php?umail=" + mmail+"&password="+mpassword;
        class RegisterUser extends AsyncTask<String, Void, String> {
            // ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }
            @Override
            protected void onPostExecute(String s) {
                if (s.equals("Unable to Connect")){
                    Toast.makeText(getApplicationContext(), "Please check internet server not connected to verify User.", Toast.LENGTH_SHORT).show();
                }else {
                    if (s.equals("success")) {
                        Toast.makeText(getApplicationContext(), "Password change successfully.", Toast.LENGTH_SHORT).show();
                        if(public_ver.FEN==0){toasmsg="Password changed successfully.";}else{toasmsg="पासवर्ड सफलतापूर्वक परिवर्तन भयो।";}
                        Toast.makeText(getApplicationContext(),toasmsg,Toast.LENGTH_SHORT).show();
                    } else {
                        if(public_ver.FEN==0){toasmsg="Update Fail.Password must be different than old one";}else{toasmsg="अपडेट असफल। पासवर्ड पुरानो भन्दा फरक हुनुपर्छ";}
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


}
