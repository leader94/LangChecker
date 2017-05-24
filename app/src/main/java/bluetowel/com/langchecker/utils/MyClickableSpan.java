package bluetowel.com.langchecker.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bluetowel.com.langchecker.R;
import bluetowel.com.langchecker.services.ClipBoardWatcherService;

/**
 * Created by Pawan on 5/24/2017.
 */

public class MyClickableSpan extends ClickableSpan {
    //    private int offset=-1;
//    private int length=-1;
//
    JSONObject jsonObject;

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(Color.RED);
    }

    public MyClickableSpan(JSONObject jsonObject) {

        this.jsonObject = jsonObject;
//        this.offset=offset;
//        this.length =length;


    }

    @Override
    public void onClick(final View widget) {
//        mListener.onTagClicked(mText);
        //TODO call popup class with offset and length values
//        Log.d("TAG", "on click called "+offset+ "  " +length);
//        ClipBoardWatcherService.openSuggestions(offset,length);
        openSuggestions(jsonObject);
    }

    protected void openSuggestions(JSONObject jsonObject) {

        /*LayoutInflater layoutInflater = (LayoutInflater)ClipBoardWatcherService.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myView = layoutInflater.inflate(R.layout.popup_suggestion, null);
        PopupWindow popupWindow = new PopupWindow(myView,WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,true);
*/

        Dialog dialog = new Dialog(ClipBoardWatcherService.context);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_suggestion);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        DisplayMetrics displayMetrics = ClipBoardWatcherService.context.getResources().getDisplayMetrics();
        lp.width = (int) (displayMetrics.widthPixels * .9);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);

        TextView messagetv = (TextView) dialog.findViewById(R.id.ps_tv_message);
        TextView incorrectPhrasetv = (TextView) dialog.findViewById(R.id.ps_tv_incorrect_phrase);
        ListView listView = (ListView) dialog.findViewById(R.id.ps_lv_sugg);


        try {
            String message = jsonObject.optString("message", "Failed");
            messagetv.setText(message);

            JSONObject contextJSON = jsonObject.optJSONObject("context");
            String contextString = contextJSON.optString("text");
            int contextOffset = contextJSON.optInt("offset");
            int contextLength = contextJSON.optInt("length");


            String myString= "..."+contextString.substring(contextOffset,contextOffset+contextLength)+"...";
            final SpannableString spannableString = new SpannableString(myString);
            spannableString.setSpan(new ForegroundColorSpan(Color.RED), 3,3+contextLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            incorrectPhrasetv.setText(spannableString);


            //add array to arraylist
            ArrayList<String> replacements = new ArrayList<>();

            JSONArray repJSONArray = jsonObject.optJSONArray("replacements");

            for (int i = 0; i < repJSONArray.length(); i++) {
                JSONObject rep = (JSONObject) repJSONArray.opt(i);
                if (rep != null) {
                    String value = rep.optString("value");
                    if (value != null) {
                        replacements.add(value);
                    }
                }
            }


//            android.widget.ListAdapter listAdapter = new ListAdapter(ClipBoardWatcherService.context, android.R.layout.simple_list_item_1, replacements);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ClipBoardWatcherService.context,
                    R.layout.suggestion_row, replacements);



            dialog.show();
            listView.setAdapter(adapter);adapter.notifyDataSetChanged();
        } catch (Exception e) {

            e.printStackTrace();
        }


    }
}

class ListAdapter extends ArrayAdapter<String> {

    public ListAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }


    public ListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);

    }
}