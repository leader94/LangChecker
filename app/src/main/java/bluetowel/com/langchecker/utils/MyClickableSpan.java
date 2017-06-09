package bluetowel.com.langchecker.utils;

import android.app.Dialog;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import bluetowel.com.langchecker.MainActivity;
import bluetowel.com.langchecker.R;
import bluetowel.com.langchecker.adapters.SuggestionListAdapter;
import bluetowel.com.langchecker.adapters.SuggestionListAdapterBasic;
import bluetowel.com.langchecker.services.ClipBoardWatcherService;

/**
 * Created by Pawan on 5/24/2017.
 */

public class MyClickableSpan extends ClickableSpan {
    //    private int offset=-1;
//    private int length=-1;
//
    private JSONObject jsonObject;
    private int type;
private UniversalVariables.caller caller;

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        if (type == 0) {
            ds.setColor(Color.CYAN);

        } else {
            ds.setColor(Color.RED);
        }


        ds.setFakeBoldText(true);
        ds.setUnderlineText(false);
    }

    /*
    *
    * param jsonObeject :
    *  @param type : number of replacemnts available
    *  type= 0 : color blue, eg date suggestion
    *  type>0 : color red
    */
    public MyClickableSpan(JSONObject jsonObject, int type,UniversalVariables.caller caller) {

        this.jsonObject = jsonObject;
        this.type = type;
        this.caller = caller;
//        this.offset=offset;
//        this.length =length;


    }

    @Override
    public void onClick(final View widget) {
//        mListener.onTagClicked(mText);
        //TODO call popup class with offset and length values
//        Log.d("TAG", "on click called "+offset+ "  " +length);
//        ClipBoardWatcherService.openSuggestions(offset,length);
        if(caller==UniversalVariables.caller.POPUP){
        openSuggestions(jsonObject);
        }else if(caller==UniversalVariables.caller.ACTIVITY){
            suggestionsMainActivity(jsonObject);
        }
    }



    private  void suggestionsMainActivity(JSONObject jsonObject) {


        try {
            String message = jsonObject.optString("message", "failed");
            if (!message.equalsIgnoreCase("failed")) {
                MainActivity.tvCause.setText(message);
            }

            final int offset = jsonObject.optInt("offset");
            final int length = jsonObject.optInt("length");

            JSONObject contextJSON = jsonObject.optJSONObject("context");
            String contextString = contextJSON.optString("text");
            int contextOffset = contextJSON.optInt("offset");
            int contextLength = contextJSON.optInt("length");

            String before = Utilities.getStringBefore(contextString.substring(0, contextOffset));
            String after = Utilities.getStringAfter(contextString.substring(contextOffset + contextLength, contextString.length()));
            String myString = before + contextString.substring(contextOffset, contextOffset + contextLength) + after;
            SpannableString spannableString = new SpannableString(myString);
            spannableString.setSpan(new ForegroundColorSpan(Color.RED), before.length() , before.length()  + contextLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);



            if (type == 0) {
                MainActivity.gvSuggestions.setVisibility(View.INVISIBLE);
            } else {
                //add suggestions to arraylist
                if(MainActivity.gvSuggestions.getVisibility()==View.INVISIBLE ||MainActivity.gvSuggestions.getVisibility()==View.GONE){
                    MainActivity.gvSuggestions.setVisibility(View.VISIBLE);
                }
                final ArrayList<String> replacements = new ArrayList<>();
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

                MainActivity.gvSuggestions.setAdapter(new SuggestionListAdapterBasic(MainActivity.context,replacements));

                MainActivity.gvSuggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String change = replacements.get(i);
                        Editable editable = MainActivity.editText.getText();
                        StyleSpanRemover spanRemover = new StyleSpanRemover();
                        spanRemover.RemoveAll(editable, offset, offset + length);
                        editable.replace(offset, offset + length, change);
                        MainActivity.editText.setText(editable);
                        MainActivity.refresh.callOnClick();

                        MainActivity.gvSuggestions.setVisibility(View.INVISIBLE);
                    }
                });
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    protected void openSuggestions(JSONObject jsonObject) {

        /*LayoutInflater layoutInflater = (LayoutInflater)ClipBoardWatcherService.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myView = layoutInflater.inflate(R.layout.popup_suggestion, null);
        PopupWindow popupWindow = new PopupWindow(myView,WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,true);
*/

        final Dialog dialog = new Dialog(ClipBoardWatcherService.context);
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
            String message = jsonObject.optString("message", "failed");
            if (!message.equalsIgnoreCase("failed")) {
                messagetv.setText(message);
            }

            final int offset = jsonObject.optInt("offset");
            final int length = jsonObject.optInt("length");

            JSONObject contextJSON = jsonObject.optJSONObject("context");
            String contextString = contextJSON.optString("text");
            int contextOffset = contextJSON.optInt("offset");
            int contextLength = contextJSON.optInt("length");

            String before = Utilities.getStringBefore(contextString.substring(0, contextOffset));
            String after = Utilities.getStringAfter(contextString.substring(contextOffset + contextLength, contextString.length()));
            String myString = before + contextString.substring(contextOffset, contextOffset + contextLength) + after;
            SpannableString spannableString = new SpannableString(myString);
            spannableString.setSpan(new ForegroundColorSpan(Color.RED), before.length() , before.length()  + contextLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            incorrectPhrasetv.setText(spannableString);

            if (type == 0) {
                listView.setVisibility(View.GONE);
                incorrectPhrasetv.setVisibility(View.GONE);
            } else {
                //add suggestions to arraylist
                final ArrayList<String> replacements = new ArrayList<>();
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

                listView.setAdapter(new SuggestionListAdapter(ClipBoardWatcherService.context, replacements, before, after));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String change = replacements.get(i);
                        updateEditText(change, offset, length);
                        dialog.dismiss();
                    }
                });

            }

            dialog.show();

        } catch (Exception e) {

            e.printStackTrace();
        }


    }


    void updateEditText(String change, int offset, int length) {
//        final Editable editable= ClipBoardWatcherService.editText.getText();
//        editable.replace(offset,offset+length,change);
//        SpannableString spannableString = new SpannableString(editable);
//        spannableString.setSpan(ClickableSpan.class, offset,offset+length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), offset,offset+length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////        spannableString.setSpan(new ClickableSpan() {
////            @Override
////            public void onClick(View view) {
////
////            }
////        }, offset, offset + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//      //  editable.removeSpan(spannableString);
//
////        Spannable  spannable= editable;
////        editable.removeSpan(ClickableSpan.class);
//        spannableString.removeSpan(ClickableSpan.class);
////        spannable.removeSpan(spannableString);
//        ClipBoardWatcherService.editText.setText(spannableString);
//
        Editable editable = ClipBoardWatcherService.editText.getText();
        StyleSpanRemover spanRemover = new StyleSpanRemover();
        spanRemover.RemoveAll(editable, offset, offset + length);
        editable.replace(offset, offset + length, change);
//        Spannable spannable = editable;

        ClipBoardWatcherService.editText.setText(editable);
        // TODO change this hack
        ClipBoardWatcherService.refreshBtn.callOnClick();
    }
}

