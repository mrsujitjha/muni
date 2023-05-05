package skj.myapp.muni;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class custgrivadpterview extends ArrayAdapter<custgrivpicview> {
String downloadfilename="";
    // invoke the suitable constructor of the ArrayAdapter class
    public custgrivadpterview(@NonNull Context context, ArrayList<custgrivpicview> arrayList) {

        // pass the context and arrayList for the super
        // constructor of the ArrayAdapter class
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.custgrivwithpic, parent, false);
        }


        custgrivpicview currentNumberPosition = getItem(position);
        assert currentNumberPosition != null;
        ImageView publishimage = currentItemView.findViewById(R.id.imageView);
        ImageView replyimage = currentItemView.findViewById(R.id.imageView2);

        ImageView postedimage = currentItemView.findViewById(R.id.imageViewpic);
        ImageView repliedimage = currentItemView.findViewById(R.id.imageViewrpic);

        publishimage.setImageResource(currentNumberPosition.getImginfoid());
        replyimage.setImageResource(currentNumberPosition.getImgreplyid());

        load_pic(postedimage,currentNumberPosition.getimgattachid());
        load_pic(  repliedimage,currentNumberPosition.getimgattachrid());


        TextView textView1 = currentItemView.findViewById(R.id.textView1);
        textView1.setText(currentNumberPosition.getHeadingtext());
        TextView textView2 = currentItemView.findViewById(R.id.textView2);
        textView2.setText(currentNumberPosition.getBodytext());
        TextView textView3 = currentItemView.findViewById(R.id.textView3);
        textView3.setText(currentNumberPosition.getReplyHeadingtext());
        TextView textView4 = currentItemView.findViewById(R.id.textView4);
        textView4.setText(currentNumberPosition.getReplyBodytext());

        return currentItemView;
    }

    private void load_pic(ImageView iphoto,String imageFileName){
        if (imageFileName.length()>3) {
            File photoFile = new File(public_ver.picfilepath + "/munipic/" + imageFileName);
            Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            if (myBitmap == null) {
               // iphoto.setImageResource(R.drawable.camerasync);
                downloadfilename=imageFileName;
                DownloadsImage ur=new DownloadsImage();
                ur.execute("https://vista2074.com/muni/docs/"+imageFileName);
            } else {
                iphoto.setImageBitmap(myBitmap);
            }
        }else{iphoto.setImageBitmap(null);}
    }
    private class DownloadsImage extends AsyncTask<String, Void,Void> {
        @Override
        protected Void doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL(strings[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap bm = null;
            try {
                bm =    BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            File imageFile = new File(public_ver.picfilepath  + "/munipic/"+downloadfilename);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(imageFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try{
                bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
                out.flush();
                out.close();
            } catch(Exception e) {
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
}
