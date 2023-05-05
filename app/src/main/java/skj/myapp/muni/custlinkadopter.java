package skj.myapp.muni;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class custlinkadopter extends BaseAdapter {
    private Context mContext;
    private final String[] web;
    private final String[] Imageid;

    public custlinkadopter(Context c,String[] web,String[] Imageid ) {
        mContext = c;
        this.Imageid = Imageid;
        this.web = web;
    }

    public custlinkadopter(String[] web, String[] imageid) {
        this.web = web;
        Imageid = imageid;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return web.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.listtextview, null);
            TextView textView = (TextView) grid.findViewById(R.id.textView1);
            ImageView imageView = (ImageView)grid.findViewById(R.id.imageView);
            textView.setText(web[position]);
            textView.setTextSize(public_ver.webfontsize);
            imageView.setImageResource(Integer.parseInt(Imageid[position]));
        } else {
            grid = (View) convertView;
        }

        return grid;
    }
}