package bluetowel.com.langchecker.services;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bluetowel.com.langchecker.R;
import bluetowel.com.langchecker.network.networkUtils;
import bluetowel.com.langchecker.utils.BasicCallback;
import bluetowel.com.langchecker.utils.Utilities;

/**
 * Created by Pawan on 5/14/2017.
 */

public class ClipBoardWatcherService extends Service {
    private static String TAG = "myTag";

    WindowManager windowManager;
    String text;


    EditText editText;
    Button close_btn;
    ClipboardManager clipboard;
    View myView;
    private ClipboardManager.OnPrimaryClipChangedListener listener = new ClipboardManager.OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            doSomeActivity();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).addPrimaryClipChangedListener(listener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void doSomeActivity() {
        clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        text = clipboard.getText().toString();
//                clipboard.get
//        Toast.makeText(getBaseContext(),"Copy:\n"+a,Toast.LENGTH_LONG).show();

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        myView = layoutInflater.inflate(R.layout.main_overlay, null);

        editText = (EditText) myView.findViewById(R.id.mo_edit_text);
        close_btn = (Button) myView.findViewById(R.id.mo_close_btn);


        editText.setText(text);

        WindowManager.LayoutParams p = new WindowManager.LayoutParams(
                // Shrink the window to wrap the content rather than filling the screen
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                // Display it on top of other application windows, but only for the current user
                WindowManager.LayoutParams.TYPE_TOAST,
                // Don't let it grab the input focus
                0,
                // Make the underlying application window visible through any transparent parts
                PixelFormat.TRANSLUCENT);
        // p.flags = p.flags & ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

// Define the position of the window within the screen
        p.gravity = Gravity.TOP;
        p.x = 0;
        p.y = 25;


        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(myView, p);


        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(myView);
            }
        });


//        myView.setOnTouchListener(new OnSwipeTouchListener(){
//
//            public boolean onSwipeRight() {
//                windowManager.removeView(myView);
//                return true;
//            }
//            public boolean onSwipeLeft() {
//                windowManager.removeView(myView);
//                return true;
//            }
//
//
//        });

        checkForErrors();
    }

    private void checkForErrors() {

        BasicCallback callback = new BasicCallback() {
            @Override
            public void callBack(Utilities.CallbackResultCode code, Object data) {
        if(code== Utilities.CallbackResultCode.SUCCESS){

            String jsonData= String.valueOf(data);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(jsonData);
                JSONObject language = jsonObject.optJSONObject("language");
                JSONArray jsonArray = jsonObject.optJSONArray("matches");

                // fetch length offset etc from mathces 
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }else {
            
            // // TODO: 5/14/2017  toast failed  

        }

            }
        };

        networkUtils.POSTcall(text,null,callback);
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
