package bluetowel.com.langchecker.services;

import android.app.Activity;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
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

    public static Context context;
    Handler handler;
    WindowManager windowManager;
    public String text;

    EditText editText;
    ImageButton close_btn;
    ClipboardManager clipboard;
    View myView;
    private ClipboardManager.OnPrimaryClipChangedListener listener = new ClipboardManager.OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
//            Intent window = new Intent(getBaseContext(), PopupMainActivity.class);
//            window.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(window);

            doSomeActivity();
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


    public ClipBoardWatcherService(){
//        context=getApplicationContext();
    }

    public   Context returnContext(){
        return  getBaseContext();
    }

    @Override
    public void onCreate() {
        handler = new Handler();
        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).addPrimaryClipChangedListener(listener);
    }


    private void doSomeActivity() {
        context=getApplicationContext();
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        myView = layoutInflater.inflate(R.layout.popup_main, null);

        DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        WindowManager.LayoutParams p = new WindowManager.LayoutParams(
                (int)(displayMetrics.widthPixels*.9),
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        // p.flags = p.flags & ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        p.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        p.x = 0;
        p.y = 50;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(myView, p);

        close_btn = (ImageButton) myView.findViewById(R.id.pm_ib_close);


        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(myView);

            }
        });


        checkForErrors();
    }

    private void checkForErrors() {

        clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        text = clipboard.getText().toString();
        editText = (EditText) myView.findViewById(R.id.pm_et_textbox);
//        editText.setText(text);

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


                        for(int i =0;i<jsonArray.length();i++){
                            JSONObject match = (JSONObject) jsonArray.get(i);

                            int start=match.optInt("offset");
                            int length  =match.optInt("length");
                            spannableString.setSpan(new MyClickableSpan(match), start, start+length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                editText.setText(spannableString);
                                editText.setMovementMethod(LinkMovementMethod.getInstance());
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    // DONT DO ANYTHING
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Connection to Server Failed",Toast.LENGTH_SHORT).show();
                            close_btn.callOnClick();
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


    public static void openSuggestions(int offset,int length){
//        LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
//        View myView = layoutInflater.inflate(R.layout.popup_main, null);
//        PopupWindow popupWindow = new PopupWindow(myView,-2,-2,true);

    }
}


