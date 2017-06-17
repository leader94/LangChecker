package bluetowel.com.langchecker.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import bluetowel.com.langchecker.MainActivity;
import bluetowel.com.langchecker.R;
import bluetowel.com.langchecker.utils.UniversalVariables;

public class SettingsFragment extends Fragment {


    private ToggleButton popupToggle;
    private Spinner langSpinner;
    private EditText etServer, etPort;
    private Button bSave;


    public SettingsFragment() {
        // Required empty public constructor

//        SharedPreferences.Editor editor = settings.edit();
//        editor.putBoolean("silentMode", mSilentMode);
//
//        // Commit the edits!
//        editor.commit();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

// TODO: 6/17/2017 language set

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        popupToggle = (ToggleButton) view.findViewById(R.id.fs_tb_popup);
        langSpinner = (Spinner) view.findViewById(R.id.fs_s_lang);
        etServer = (EditText) view.findViewById(R.id.fs_et_server);
        etPort = (EditText) view.findViewById(R.id.fs_et_port);
        bSave = (Button) view.findViewById(R.id.fs_b_save);

        SharedPreferences settings = MainActivity.context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
        String server = settings.getString(UniversalVariables.server, UniversalVariables.notSet);
        String port = settings.getString(UniversalVariables.portNumber, UniversalVariables.notSet);
        if (!server.equalsIgnoreCase(UniversalVariables.notSet)) {
            etServer.setText(server);
        }

        if (!port.equalsIgnoreCase(UniversalVariables.notSet)) {
            etPort.setText(server);
        }

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        return view;
    }

    private void saveData() {
        SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(UniversalVariables.showPopup, popupToggle.isChecked());
        if (etServer != null) {
            String serverAdd = etServer.getText().toString();
            serverAdd = serverAdd.trim();
            if (serverAdd.isEmpty()) {
                editor.putString(UniversalVariables.server, UniversalVariables.notSet);
            } else {
                editor.putString(UniversalVariables.server, serverAdd);
            }
        }

        if (etPort != null) {
            String port = etPort.getText().toString();
            port = port.trim();
            if (port.isEmpty()) {
                editor.putString(UniversalVariables.portNumber, UniversalVariables.notSet);
            } else {
                editor.putString(UniversalVariables.portNumber, port);
            }
        }

        editor.commit();

        // FIXME: 6/17/2017 remove this color change
        bSave.setBackgroundResource(R.drawable.circle_color_secondary);

    }


}
