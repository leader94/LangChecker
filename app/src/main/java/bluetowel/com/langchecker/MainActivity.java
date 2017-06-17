package bluetowel.com.langchecker;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bluetowel.com.langchecker.adapters.DrawerItemCustomAdapter;
import bluetowel.com.langchecker.fragment.MainFragment;
import bluetowel.com.langchecker.fragment.SettingsFragment;
import bluetowel.com.langchecker.network.networkUtils;
import bluetowel.com.langchecker.services.ClipBoardWatcherService;
import bluetowel.com.langchecker.utils.BasicCallback;
import bluetowel.com.langchecker.utils.MyClickableSpan;
import bluetowel.com.langchecker.utils.UniversalVariables;
import bluetowel.com.langchecker.utils.Utilities;

public class MainActivity extends AppCompatActivity {


    public static final String PREFS_NAME = "MyPrefsFile";
    private String[] drawerTitles = {"Home", "Settings", "Rate Us", "About Us", "Donate",};
    private int[] icons = {R.drawable.ic_home_white_48dp, R.drawable.ic_settings_white_48dp
            , R.drawable.ic_favorite_border_white_48dp, R.drawable.ic_supervisor_account_white_48dp,
            R.drawable.ic_payment_white_48dp};

    private String[] mDrawerTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    public String text;


    public static Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, ClipBoardWatcherService.class));
        context = getBaseContext();


        UniversalVariables.setContext(this);

        doSetup();


        UniversalVariables.OpenMainFragment();

    }

    private void doSetup() {




        mTitle = mDrawerTitle = getTitle();

        mDrawerTitles = getResources().getStringArray(R.array.drawerTitles);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.am_drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.am_left_drawer);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);


        // Set the adapter for the list view
        mDrawerList.setAdapter(new DrawerItemCustomAdapter(this, icons, drawerTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
// Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Exception here
        }

        getSupportActionBar().setHomeButtonEnabled(true);


    }





    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }


    private void selectItem(int position) {

        switch (position){
            case 0:
                UniversalVariables.OpenMainFragment();
                break;
            case 1:
                UniversalVariables.OpenSettingsFragment();
                break;
            case 2:break;
            case 3:break;
        }

            // Highlight the selected item, update the title, and close the drawer
            mDrawerList.setItemChecked(position, true);
            setTitle(mDrawerTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
