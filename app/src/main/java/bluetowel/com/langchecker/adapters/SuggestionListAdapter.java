package bluetowel.com.langchecker.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import bluetowel.com.langchecker.R;

/**
 * Created by Pawan on 5/25/2017.
 */

public class SuggestionListAdapter extends BaseAdapter {

    ArrayList<String> replacements;
    String before, after;
    Context context;
    LayoutInflater inflater = null;

    class ViewHolder {
        TextView textView;
    }

    public SuggestionListAdapter(Context context, ArrayList<String> replacements, String before, String after) {
        this.context = context;
        this.replacements = replacements;
        this.before = before;
        this.after = after;
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
//        if(view==null)
//        {
        //TODO create view
//        }else
        ViewHolder viewHolder = new ViewHolder();
        View rowView = inflater.inflate(R.layout.suggestion_row, null);
        viewHolder.textView = (TextView) rowView.findViewById(R.id.sr_tv);

        String correction = replacements.get(i);
        String myString = before + " " + correction + after;
        SpannableString spannableString = new SpannableString(myString);
        spannableString.setSpan(new ForegroundColorSpan(Color.GREEN), before.length() + 1, before.length() + 1 + correction.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.textView.setText(spannableString);
        return rowView;
    }
}
