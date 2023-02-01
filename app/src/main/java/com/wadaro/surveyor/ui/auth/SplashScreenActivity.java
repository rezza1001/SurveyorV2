package com.wadaro.surveyor.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.UpdateAppsActivity;
import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.Global;
import com.wadaro.surveyor.database.MyPreference;
import com.wadaro.surveyor.database.table.UserDB;
import com.wadaro.surveyor.module.MyDevice;
import com.wadaro.surveyor.ui.home.HomePageActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1000;
    MyDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        device = new MyDevice(this);
        setContentView(R.layout.activity_splash_screen);
        String synchDate = MyPreference.getString(this,Global.PREF_SYNCH_DATE);
        if (!synchDate.isEmpty()){
            DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", new Locale("id"));
            String today = format2.format(new Date());
            if (!today.equals(synchDate)){
                MyPreference.delete(this,Global.PREF_SYNCH_DATE);
                MyPreference.delete(this, Global.PREF_DATA_ASSIGNMENT);
                MyPreference.delete(this, Global.PREF_DATA_RECAP);
                MyPreference.delete(this, Global.PREF_REPORT_SURVEY);
            }
        }
        /*
         * Showing splash screen with a timer. This will be useful when you
         * want to show case your app logo / company
         */
        new Handler().postDelayed(() -> {

            // init sharedPreference
            Global.startAppIniData(SplashScreenActivity.this);

            if (MyPreference.getInt(SplashScreenActivity.this, Global.PREF_OFFLINE_MODE) == 1){
                redirect();
            }
            else {
                checkApps();
            }
        }, SPLASH_TIME_OUT);

    }

    private void redirect(){

        UserDB userDB = new UserDB();
        userDB.getData(this);
        if (userDB.user_id.isEmpty()){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        else {
            startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
        }
        finish();
    }

    private void checkApps(){
        PostManager post = new PostManager(this,"https://erp-api.wadaro.id/wadaro-erp/android/api/v1/","check-version-app/surveyor");
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONObject data = obj.getJSONObject("data");
                    int version = Integer.parseInt(data.getString("version"));
                    Log.d("SplashScreenActivity","version DB "+ version+" : "+device.getVersionCode());
                    if (device.getVersionCode() < version){
                        startActivity(new Intent(SplashScreenActivity.this, UpdateAppsActivity.class));
                        SplashScreenActivity.this.finish();
                    }
                    else {
                        redirect();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                redirect();
            }
        });
    }

}
