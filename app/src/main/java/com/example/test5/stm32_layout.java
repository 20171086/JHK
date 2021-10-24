package com.example.test5;

import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ViewAnimator;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.test5.bluetoothchat;
import com.example.test5.common.activities.SampleActivityBase;
import com.example.test5.common.logger.Log;
import com.example.test5.common.logger.LogFragment;
import com.example.test5.common.logger.LogWrapper;
import com.example.test5.common.logger.MessageOnlyLogFilter;

public class stm32_layout extends AppCompatActivity {

    public static final String TAG = "stm32_layout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.stm32_layout);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            bluetoothchat fragment = new bluetoothchat();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
    }
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        /*LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);*/
        //msgFilter.setNext(logFragment.getLogView());

        Log.i(TAG, "Ready");
    }
}
