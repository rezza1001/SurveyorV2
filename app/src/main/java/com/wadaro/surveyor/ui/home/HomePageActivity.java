package com.wadaro.surveyor.ui.home;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wadaro.surveyor.R;
import com.wadaro.surveyor.base.MyActivity;
import com.wadaro.surveyor.module.FileProcessing;
import com.wadaro.surveyor.ui.absent.AbsentActivity;
import com.wadaro.surveyor.ui.assignment.AssignmentFrg;
import com.wadaro.surveyor.ui.credit.CreditActivity;
import com.wadaro.surveyor.ui.datasurvey.DataSurveyFragment;
import com.wadaro.surveyor.ui.product.ProductActivity;
import com.wadaro.surveyor.ui.profile.ProfileFrg;
import com.wadaro.surveyor.ui.rekap.RecapFragment;

public class HomePageActivity extends MyActivity {

    private FrameLayout frame_body_00;
    private BottomNavigationView navigationView;

    @Override
    protected int setLayout() {
        return R.layout.ui_home_activity_main;
    }

    @Override
    protected void initLayout() {
        frame_body_00   = findViewById(R.id.frame_body_00);
        navigationView  = findViewById(R.id.bottom_nav_bar);
        navigationView.inflateMenu(R.menu.menu_demobooker);
        setCheckedBottomMenu(R.id.bottom_home);
        selectedMenu(0);
    }

    void setCheckedBottomMenu(int rIdmenu){
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(rIdmenu);
        if(menuItem !=null) menuItem.setChecked(true);

    }

    @Override
    protected void initData() {
        FileProcessing.createFolder(mActivity,"Wadaro");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void initListener() {
        navigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.bottom_home:
                    selectedMenu(0);
                    return true;
                case R.id.bottom_booking:
                    selectedMenu(1);
                    return true;
                case R.id.bottom_data:
                    selectedMenu(2);
                    return true;
                case R.id.bottom_penugasan:
                    selectedMenu(3);
                    return true;
                case R.id.bottom_profile:
                    selectedMenu(4);
                    return true;
            }
            return false;
        });
    }

    private void selectedMenu(int menu){
        Fragment fragment ;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (menu){
            default:
                fragment = HomeFrg.newInstance();
                fragmentTransaction.replace(frame_body_00.getId(), fragment,"main");
                fragmentTransaction.detach(fragment);
                fragmentTransaction.attach(fragment);
                fragmentTransaction.commit();
                break;
            case 1:
                fragment = AssignmentFrg.newInstance();
                fragmentTransaction.replace(frame_body_00.getId(), fragment,"main");
                fragmentTransaction.detach(fragment);
                fragmentTransaction.attach(fragment);
                fragmentTransaction.commit();
                break;
            case 2:
                fragment = DataSurveyFragment.newInstance();
                fragmentTransaction.replace(frame_body_00.getId(), fragment,"main");
                fragmentTransaction.detach(fragment);
                fragmentTransaction.attach(fragment);
                fragmentTransaction.commit();
                break;
            case 3:
                fragment = RecapFragment.newInstance();
                fragmentTransaction.replace(frame_body_00.getId(), fragment,"main");
                fragmentTransaction.detach(fragment);
                fragmentTransaction.attach(fragment);
                fragmentTransaction.commit();
                break;
            case 4:
                fragment = ProfileFrg.newInstance();
                fragmentTransaction.replace(frame_body_00.getId(), fragment,"main");
                fragmentTransaction.detach(fragment);
                fragmentTransaction.attach(fragment);
                fragmentTransaction.commit();
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("OPEN_ASSIGNMENT");
        intentFilter.addAction("OPEN_DATASURVEY");
        intentFilter.addAction("OPEN_PROFILE");
        intentFilter.addAction("OPEN_RECAP");
        intentFilter.addAction("OPEN_CREDIT");
        intentFilter.addAction("OPEN_ABSENT");
        intentFilter.addAction("OPEN_PRODUCT");
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case "OPEN_ASSIGNMENT":
                    setCheckedBottomMenu(R.id.bottom_booking);
                    selectedMenu(1);
                    break;
                case "OPEN_DATASURVEY":
                    setCheckedBottomMenu(R.id.bottom_data);
                    selectedMenu(2);
                    break;
                case "OPEN_RECAP":
                    setCheckedBottomMenu(R.id.bottom_data);
                    selectedMenu(3);
                    break;
                case "OPEN_PROFILE":
                    setCheckedBottomMenu(R.id.bottom_profile);
                    selectedMenu(4);
                    break;
                case "OPEN_CREDIT":
                    startActivity(new Intent(mActivity, CreditActivity.class));
                    break;
                case "OPEN_ABSENT":
                    startActivity(new Intent(mActivity, AbsentActivity.class));
                    break;
                case "OPEN_PRODUCT":
                    startActivity(new Intent(mActivity, ProductActivity.class));
                    break;
            }
        }
    };
}
