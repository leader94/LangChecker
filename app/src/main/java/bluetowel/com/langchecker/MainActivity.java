package bluetowel.com.langchecker;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bluetowel.com.langchecker.network.networkUtils;
import bluetowel.com.langchecker.services.ClipBoardWatcherService;
import bluetowel.com.langchecker.utils.BasicCallback;
import bluetowel.com.langchecker.utils.MyClickableSpan;
import bluetowel.com.langchecker.utils.UniversalVariables;
import bluetowel.com.langchecker.utils.Utilities;
import me.grantland.widget.AutofitHelper;

public class MainActivity extends AppCompatActivity {

    public String text;
    private ClipboardManager clipboard;
    public static EditText editText;
    private TextView  tvCount, tvCopyAndExit;
    public static TextView tvCause;
    public static ImageButton refresh;
    public static GridView gvSuggestions;

    public static Context context;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_keyboard:
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);
                        break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, ClipBoardWatcherService.class));
        context=getApplicationContext();

        doSetup();


        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();

        if(clip==null || clip.getDescription()==null)
            return;
        if (clip.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

            text = clipboard.getText().toString();
            checkForErrors(text);
        }
    }

    private void doSetup() {
        editText = (EditText) findViewById(R.id.am_et);
        tvCause = (TextView) findViewById(R.id.am_tv_cause);
        tvCount = (TextView) findViewById(R.id.am_tv_count);
        tvCopyAndExit = (TextView) findViewById(R.id.am_tv_copy_and_exit);
        refresh = (ImageButton) findViewById(R.id.am_ib_refresh);
        gvSuggestions = (GridView) findViewById(R.id.am_gv_suggestions);

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
        });



        tvCopyAndExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(UniversalVariables.AppName, editText.getText().toString());
                clipboard.setPrimaryClip(clip);
                finish();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForErrors(editText.getText().toString());
            }
        });
    }


    private void checkForErrors(String text) {

        final SpannableString spannableString = new SpannableString(text);

        BasicCallback callback = new BasicCallback() {
            @Override
            public void callBack(Utilities.CallbackResultCode code, Object data) {
                if (code == Utilities.CallbackResultCode.SUCCESS) {

                    String jsonData = String.valueOf(data);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(jsonData);
                        JSONObject language = jsonObject.optJSONObject("language");
                        JSONArray jsonArray = jsonObject.optJSONArray("matches");


                        // total errors
                        final int totalErrors = jsonArray.length();


                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject match = (JSONObject) jsonArray.get(i);

                            int start = match.optInt("offset");
                            int length = match.optInt("length");

                            int count = 0;
                            JSONArray repJSONArray = match.optJSONArray("replacements");

                            for (int j = 0; j < repJSONArray.length(); j++) {
                                JSONObject rep = (JSONObject) repJSONArray.opt(j);
                                if (rep != null) {
                                    String value = rep.optString("value");
                                    if (value != null) {
                                        count++;
                                        break;
                                    }
                                }
                            }

                            spannableString.setSpan(new MyClickableSpan(match, count, UniversalVariables.caller.ACTIVITY), start, start + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                editText.setText(spannableString);
                                editText.setMovementMethod(LinkMovementMethod.getInstance());
                                tvCount.setText(String.valueOf(totalErrors));
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            }
        };

        text = text.trim();
        if (!text.isEmpty()) {
            networkUtils.POSTcall(text, null, callback);
        }

    }

}
