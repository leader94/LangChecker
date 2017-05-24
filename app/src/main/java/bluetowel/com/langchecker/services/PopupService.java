package bluetowel.com.langchecker.services;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;

import bluetowel.com.langchecker.R;

public class PopupService extends Service {

    private WindowManager windowManager;
    private ImageView chatHead;

    boolean mHasDoubleClicked = false;
    long lastPressTime;

    private ClipboardManager.OnPrimaryClipChangedListener listener = new ClipboardManager.OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
//            Intent window = new Intent(getBaseContext(), PopupMainActivity.class);
//            window.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(window);

            doSomeActivity();
        }
    };


    public PopupService() {
    }



    void doSomeActivity(){

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        chatHead = new ImageView(this);

        chatHead.setImageResource(R.drawable.chat_head);



        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        windowManager.addView(chatHead, params);

        try {
            chatHead.setOnTouchListener(new View.OnTouchListener() {
                private WindowManager.LayoutParams paramsF = params;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            // Get current time in nano seconds.
                            long pressTime = System.currentTimeMillis();


                            // If double click...
//                            if (pressTime - lastPressTime <= 300) {
//                                createNotification();
//                                ServiceFloating.this.stopSelf();
//                                mHasDoubleClicked = true;
//                            }
//                            else {     // If not double click....
//                                mHasDoubleClicked = false;
//                            }
                            lastPressTime = pressTime;
                            initialX = paramsF.x;
                            initialY = paramsF.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                            paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(chatHead, paramsF);
                            break;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
        }


        chatHead.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                initiatePopupWindow(chatHead);
//                _enable = false;
                //				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                //				getApplicationContext().startActivity(intent);
            }
        });

    }

    private void initiatePopupWindow(View anchor) {
try
{

    Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    ListPopupWindow popup = new ListPopupWindow(this);
    popup.setAnchorView(anchor);
    popup.setWidth((int) (display.getWidth()/(1.5)));
    popup.show();

    LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
    View myView = layoutInflater.inflate(R.layout.popup_main, null);
    PopupWindow popupWindow = new PopupWindow(myView,-2,-2,true);
//    popupWindow.setContentView(myView);
    popupWindow.setFocusable(true);
//    popupWindow.setWindowLayoutMode(
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT);
//    popupWindow.setHeight(1);
//    popupWindow.setWidth(1);
//    Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//
//    Display display1 = getWindowManager().getDefaultDisplay();
//    WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//    Display display = window.getDefaultDisplay();
//    Point size = new Point();
//    display.getSize(size);
    DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
    int width = metrics.widthPixels;
    int height = metrics.heightPixels;
//    int width = size.x;
//    int height = size.y;


    // You could also easily used an integer value from the shared preferences to set the percent
//    if (height > width) {
////            getWindow().setLayout((int) (width * .9), (int) (height * .7));
//
//        popupWindow.setWidth((int) (width * 1.9));
//        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
////        getWindow().setLayout((int) (width * .9),WindowManager.LayoutParams.WRAP_CONTENT );
//    } else {
//        popupWindow.setWidth((int) (width * 1.7));
//        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
////        getWindow().setLayout((int) (width * .7), (int) (height * .8));
//    }
//    popupWindow.setWidth(200);
//    popupWindow.setHeight(300);





//    popupWindow.setContentView(myView);
//    popupWindow.setBackgroundDrawable(new ColorDrawable());
//    popupWindow.showAsDropDown(anchor);

    int location[] = new int[2];

    // Get the View's(the one that was clicked in the Fragment) location
    anchor.getLocationOnScreen(location);

//    popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY,
//            location[0], location[1] + anchor.getHeight());
popupWindow.showAtLocation(anchor,Gravity.CENTER,0,0);

}
catch (Exception e){
    e.printStackTrace();
    //TODO
}


    }

    @Override
    public void onCreate() {
        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).addPrimaryClipChangedListener(listener);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
