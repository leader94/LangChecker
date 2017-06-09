package bluetowel.com.langchecker.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import bluetowel.com.langchecker.MainActivity;
import bluetowel.com.langchecker.R;

/**
 * Created by Pawan on 6/9/2017.
 */

    public class SuggestionListAdapterBasic extends BaseAdapter {

    ArrayList<String> replacements;
    LayoutInflater inflater = null;




    public SuggestionListAdapterBasic(Context context,ArrayList<String> replacements) {
        this.replacements = replacements;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return replacements.size();
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
        View rowView = inflater.inflate(R.layout.suggestion_row, null);
        viewHolder.textView = (TextView) rowView.findViewById(R.id.sr_tv);
        viewHolder.textView.setBackground(ContextCompat.getDrawable(MainActivity.context,R.drawable.rectangle_curved_secondary_light));
        viewHolder.textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        String correction = replacements.get(i);
        viewHolder.textView.setText(correction);
        return rowView;
    }

    class ViewHolder {
        TextView textView;
    }
}
