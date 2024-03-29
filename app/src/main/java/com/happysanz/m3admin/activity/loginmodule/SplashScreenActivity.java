package com.happysanz.m3admin.activity.loginmodule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.activity.MainActivity;
import com.happysanz.m3admin.activity.piamodule.PiaDashboard;
import com.happysanz.m3admin.activity.tnsrlmmodule.PiaActivity;
import com.happysanz.m3admin.activity.tnsrlmmodule.TnsrlmDashboard;
import com.happysanz.m3admin.utils.AppValidator;
import com.happysanz.m3admin.utils.PreferenceStorage;

public class SplashScreenActivity extends Activity {

    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (PreferenceStorage.getUserName(getApplicationContext()) != null && AppValidator.checkNullString(PreferenceStorage.getUserName(getApplicationContext()))) {
                    if (PreferenceStorage.getUserType(getApplicationContext()).equalsIgnoreCase("1")) {
                        Intent i = new Intent(SplashScreenActivity.this, TnsrlmDashboard.class);
                        startActivity(i);
                        finish();
                    } else if (PreferenceStorage.getUserType(getApplicationContext()).equalsIgnoreCase("3")) {
                        Intent i = new Intent(SplashScreenActivity.this, PiaDashboard.class);
                        startActivity(i);
                        finish();
                    }

                } else {
                    Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SplashScreenActivity.this, new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            String newToken = instanceIdResult.getToken();
                            Log.e("newToken", newToken);
                            PreferenceStorage.saveGCM(getApplicationContext(), newToken);
                        }
                    });
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);

    }
}

