package bluetowel.com.langchecker.services;

//TODO implement back press listner

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
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
import bluetowel.com.langchecker.utils.Utilities;

/**
 * Created by Pawan on 5/14/2017.
 */

public class ClipBoardWatcherService extends Service {
    private static String TAG = "myTag";

    private boolean isVisible = false;
    public static Context context;
    private Handler handler;
    private WindowManager windowManager;
    public String text;
    private TextView copyAndExitBtn;
    public static EditText editText;
    public static ImageButton closeBtn, refreshBtn;
    private ClipboardManager clipboard;
    private View myView;
    private WindowManager.LayoutParams popupLayoutParams;
    private ClipboardManager.OnPrimaryClipChangedListener listener = new ClipboardManager.OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
//            Intent window = new Intent(getBaseContext(), PopupMainActivity.class);
//            window.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(window);

            if (!isVisible) {
                isVisible = true;
                doSomeActivity();
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }


    public ClipBoardWatcherService() {
//        context=getApplicationContext();
    }

//    public Context setContext() {
//        return getBaseContext();
//    }

    @Override
    public void onCreate() {
        handler = new Handler();
        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).addPrimaryClipChangedListener(listener);
    }


    private void doSomeActivity() {
        context = getApplicationContext();
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        myView = layoutInflater.inflate(R.layout.popup_main, null);
        DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();
//        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
//        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        popupLayoutParams = new WindowManager.LayoutParams(
                (int) (displayMetrics.widthPixels * .9),
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,     // WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        // p.flags = p.flags & ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        popupLayoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        popupLayoutParams.x = 0;
        popupLayoutParams.y = 50;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);


        doSetup();

        clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        text = clipboard.getText().toString();
        //TODO change this to avoid crash
        checkForErrors(text);
    }

    private void doSetup() {

        closeBtn = (ImageButton) myView.findViewById(R.id.pm_ib_close);
        refreshBtn = (ImageButton) myView.findViewById(R.id.pm_ib_refresh);
        editText = (EditText) myView.findViewById(R.id.pm_et_textbox);
        copyAndExitBtn = (TextView) myView.findViewById(R.id.pm_tv_copy_and_exit);

//        editText.clearFocus();
//        editText.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (myView != null && myView.isShown()) {
                        windowManager.removeView(myView);
                        isVisible = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForErrors(editText.getText().toString());
            }
        });

        copyAndExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("GramWise Corrected Text", editText.getText().toString());
                clipboard.setPrimaryClip(clip);
                closeBtn.callOnClick();
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
                        int totalErrors = jsonArray.length();


                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject match = (JSONObject) jsonArray.get(i);

                            int start = match.optInt("offset");
                            int length = match.optInt("length");

                            int count=0;
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


                            if(count==0){
                                spannableString.setSpan(new MyClickableSpan(match,count), start, start + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }else {
                            spannableString.setSpan(new MyClickableSpan(match,count), start, start + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                editText.setText(spannableString);
                                editText.setMovementMethod(LinkMovementMethod.getInstance());
                                if (myView.getWindowToken() == null) {
                                    windowManager.addView(myView, popupLayoutParams);
                                }


                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    // DONT DO ANYTHING
                    //TODO remove show of popup
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isVisible=false;
                            Toast.makeText(getApplicationContext(), "GramWise: Connection to Server Failed", Toast.LENGTH_SHORT).show();
//                            closeBtn.callOnClick();
                        }
                    });

                }

            }
        };

        networkUtils.POSTcall(text, null, callback);
//        String response=networkUtils.POSTcall(text,null,callback);
/*
        if(response!=null){

            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject language = jsonObject.optJSONObject("language");
                JSONArray jsonArray = jsonObject.optJSONArray("matches");

                int count= jsonArray.length();

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }*/
    }


}


