package bluetowel.com.langchecker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import bluetowel.com.langchecker.services.ClipBoardWatcherService;
import bluetowel.com.langchecker.services.PopupService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, ClipBoardWatcherService.class));


    }
}
