package bluetowel.com.langchecker.utils;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import bluetowel.com.langchecker.MainActivity;
import bluetowel.com.langchecker.R;
import bluetowel.com.langchecker.fragment.MainFragment;
import bluetowel.com.langchecker.fragment.SettingsFragment;

/**
 * Created by Pawan on 6/9/2017.
 */

public class UniversalVariables {
    public static String AppName = "GramWise";
    public static boolean debug = true;
    public static String noNetworkMsg="No network connectivity";


    public static String showPopup = "showPopup";
    public static String server= "server";
    public static String portNumber = "port";
    public static String notSet= "notSet";
    public static enum caller {
        POPUP, ACTIVITY
    }


    private static Context context;
    public static android.support.v4.app.FragmentManager fragmentManager;
    public static void setContext(final Context context1) {
        if (context1 == null) return;
        context = context1;
        if (context1 instanceof Activity) {
            fragmentManager = ((FragmentActivity) context1).getSupportFragmentManager();
        }
    }



    public static void OpenMainFragment(){
        MainFragment fragment = new MainFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment_frame, fragment)

.commitAllowingStateLoss();

    }

    public static void OpenSettingsFragment(){
        SettingsFragment fragment1 = new SettingsFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment_frame, fragment1)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }
}
