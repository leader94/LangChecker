package bluetowel.com.langchecker;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

/**
 * Created by Pawan on 5/23/2017.
 */

public class PopupMainActivity extends Activity {

    ImageButton close_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_ACTION_BAR);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
//                WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        // Params for the window.
        // You can easily set the alpha and the dim behind the window from here
//        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.alpha = 1.0f;    // lower than one makes it more transparent
//        params.dimAmount = 0f;  // set it higher if you want to dim behind the window
//        getWindow().setAttributes(params);

        // Gets the display size so that you can set the window to a percent of that
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;


        // You could also easily used an integer value from the shared preferences to set the percent
        if (height > width) {
//            getWindow().setLayout((int) (width * .9), (int) (height * .7));
            getWindow().setLayout((int) (width * .9), WindowManager.LayoutParams.WRAP_CONTENT);
        } else {
            getWindow().setLayout((int) (width * .7), (int) (height * .8));
        }

        setContentView(R.layout.popup_main);

        close_btn = (ImageButton) findViewById(R.id.pm_ib_close);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
