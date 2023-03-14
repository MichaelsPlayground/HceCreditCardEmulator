package de.androidcrypto.hcecreditcardemulator;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import de.androidcrypto.hcecreditcardemulator.common.logger.Log;
import de.androidcrypto.hcecreditcardemulator.common.logger.LogFragment;
import de.androidcrypto.hcecreditcardemulator.common.logger.LogNode;
import de.androidcrypto.hcecreditcardemulator.common.logger.LogWrapper;
import de.androidcrypto.hcecreditcardemulator.common.logger.MessageOnlyLogFilter;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    TextView tvLog;

    // Whether the Log Fragment is currently shown
    private boolean mLogShown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);

        tvLog = findViewById(R.id.tvLog);

        initializeLogging();

        Log.e(TAG, getTimestampMillis() + " error");
        Log.d(TAG, getTimestampMillis() + " no error");
    }

    /** Create a chain of targets that will receive log data */
    //@Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.

        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());
        //msgFilter.setNext((LogNode) tvLog);

        Log.i(TAG, "Ready");

        // change textSize in LogFragment line 84
        // mLogView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
    }

    private static String getTimestampMillis() {
        // O = SDK 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return ZonedDateTime
                    .now(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("uuuu.MM.dd HH:mm:ss.SSS"));
        } else {
            return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS").format(new Date());
        }
    }


}