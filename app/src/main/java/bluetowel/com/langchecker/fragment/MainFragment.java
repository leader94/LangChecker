package bluetowel.com.langchecker.fragment;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bluetowel.com.langchecker.R;
import bluetowel.com.langchecker.network.networkUtils;
import bluetowel.com.langchecker.utils.BasicCallback;
import bluetowel.com.langchecker.utils.MyClickableSpan;
import bluetowel.com.langchecker.utils.UniversalVariables;
import bluetowel.com.langchecker.utils.Utilities;

public class MainFragment extends Fragment {

    public String text;
    public static EditText editText;
    private TextView tvCount, tvCopyAndExit;
    public static TextView tvCause;
    public static ImageButton refresh;
    public static GridView gvSuggestions;
    private Handler handler;

    private ClipboardManager clipboard;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        editText = (EditText) view.findViewById(R.id.am_et);
        tvCause = (TextView) view.findViewById(R.id.am_tv_cause);
        tvCount = (TextView) view.findViewById(R.id.am_tv_count);
        tvCopyAndExit = (TextView) view.findViewById(R.id.am_tv_copy_and_exit);
        refresh = (ImageButton) view.findViewById(R.id.am_ib_refresh);
        gvSuggestions = (GridView) view.findViewById(R.id.am_gv_suggestions);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        doSetup();

        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();

        if (networkUtils.checkNetworkConnectivity()) {
            if ((clip != null) && (clip.getDescription() != null && clip.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) || clip.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML))) {
                text = clipboard.getText().toString();
                checkForErrors(text);
            } else {
                checkForErrors("Paste your own text here and click the 'Refresh' button. Click the colored phrases for details on potential errors. or use this text too see an few of of the problems that LanguageTool can detecd. What do you thinks of grammar checkers? Please not that they are not perfect. It's 5 P.M. in the afternoon. LanguageTool 3.6 was released on Tuesday, 28 December 2016.");
            }
        } else editText.setHint(UniversalVariables.noNetworkMsg);
    }

    private void doSetup() {


        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
        });


        tvCopyAndExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(UniversalVariables.AppName, editText.getText().toString());
                clipboard.setPrimaryClip(clip);
                getActivity().finish();
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
                                tvCount.setVisibility(View.VISIBLE);
                                tvCount.setText(String.valueOf(totalErrors));
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (code == Utilities.CallbackResultCode.FAIL) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editText.setHint(UniversalVariables.noNetworkMsg);
                        }
                    });
                }


            }
        };

        text = text.trim();
        if (!text.isEmpty()) {
            tvCount.setVisibility(View.INVISIBLE);
            networkUtils.POSTcall(text, null, callback);
        }

    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_open_keyboard) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            gvSuggestions.setVisibility(View.INVISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }
}
