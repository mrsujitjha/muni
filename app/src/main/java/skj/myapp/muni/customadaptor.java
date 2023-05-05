package skj.myapp.muni;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static skj.myapp.muni.Myapp.getContext;

public class customadaptor extends ArrayAdapter<infocustomview> {
    public customadaptor(@NonNull Context context, ArrayList<infocustomview> arrayList) {
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentItemView = convertView;
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.custommsgview, parent, false);
        }

        infocustomview currentNumberPosition = getItem(position);
        assert currentNumberPosition != null;
       ImageView publishimage = currentItemView.findViewById(R.id.imageView);
        publishimage.setImageResource(currentNumberPosition.getImginfoid());
        TextView textView1 = currentItemView.findViewById(R.id.textView1);
        textView1.setText(currentNumberPosition.getHeadingtext());
        TextView textView2 = currentItemView.findViewById(R.id.textView2);
        textView2.setText(currentNumberPosition.getBodytext());


        // then return the recyclable view
        return currentItemView;
    }
}

