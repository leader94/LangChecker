package bluetowel.com.langchecker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import bluetowel.com.langchecker.R;

/**
 * Created by Pawan on 6/12/2017.
 */

public class DrawerItemCustomAdapter extends BaseAdapter {

    private Context context;
            int [] icon;
            String[] text;
    LayoutInflater inflater = null;

    private class ViewHolder{
        ImageView imageView;
        TextView textView;

    }

    public DrawerItemCustomAdapter(Context context,int [] icon, String[] text) {
        this.icon=new int[icon.length];
        this.text=new String[text.length];
        this.icon = icon;
        this.text = text;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return text.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        ViewHolder viewHolder = new ViewHolder();
        View rowView = inflater.inflate(R.layout.drawer_list_item, null);
        viewHolder.imageView= (ImageView) rowView.findViewById(R.id.dli_iv_icon);
        viewHolder.textView= (TextView) rowView.findViewById(R.id.dli_tv_title);
        viewHolder.imageView.setImageResource(icon[i]);
        viewHolder.textView.setText(text[i]);

        return rowView;
    }
}
