package com.example.ithappenedandroid.Activities;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ithappenedandroid.Application.TrackingService;
import com.example.ithappenedandroid.Domain.Tracking;
import com.example.ithappenedandroid.Fragments.EventsFragment;
import com.example.ithappenedandroid.Fragments.ProfileSettingsFragment;
import com.example.ithappenedandroid.Fragments.StatisticsFragment;
import com.example.ithappenedandroid.Fragments.TrackingsFragment;
import com.example.ithappenedandroid.Infrastructure.ITrackingRepository;
import com.example.ithappenedandroid.R;
import com.example.ithappenedandroid.Retrofit.ItHappenedApplication;
import com.example.ithappenedandroid.StaticInMemoryRepository;

import java.util.List;
import java.util.UUID;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class UserActionsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView userNick;
    TrackingsFragment trackFrg;
    FragmentTransaction fTrans;
    FrameLayout layoutFrg;

    ProfileSettingsFragment profileStgsFrg;

    ProgressBar syncPB;

    Subscription mainSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#a9a9a9"));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        syncPB = (ProgressBar) findViewById(R.id.syncPB);
        layoutFrg = (FrameLayout) findViewById(R.id.trackingsFrg);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        setTitle("Мои отслеживания");
            trackFrg = new TrackingsFragment();
            fTrans = getFragmentManager().beginTransaction();
            fTrans.replace(R.id.trackingsFrg, trackFrg);
            fTrans.commit();
        }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            setTitle("Мои отслеживания");
            trackFrg = new TrackingsFragment();

            fTrans = getFragmentManager().beginTransaction();
            fTrans.replace(R.id.trackingsFrg, trackFrg);
            fTrans.commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tracking_drawer, menu);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MAIN_KEYS", Context.MODE_PRIVATE);
        userNick = (TextView) findViewById(R.id.userNickname);
        userNick.setText(sharedPreferences.getString("Nick",""));
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.my_events){
            trackFrg = new TrackingsFragment();

            fTrans = getFragmentManager().beginTransaction();
            fTrans.replace(R.id.trackingsFrg, trackFrg);
            fTrans.commit();

            setTitle("Мои отслеживания");
        }

        if (id == R.id.events_history) {
            setTitle("История событий");
            EventsFragment eventsFrg = new EventsFragment();

            fTrans = getFragmentManager().beginTransaction();
            fTrans.replace(R.id.trackingsFrg, eventsFrg);
            fTrans.commit();
        }

        if(id == R.id.statistics){
            setTitle("Статистика");
            StatisticsFragment statFrg = new StatisticsFragment();

            fTrans = getFragmentManager().beginTransaction();
            fTrans.replace(R.id.trackingsFrg, statFrg);
            fTrans.commit();
        }

        if(id == R.id.synchronisation){
            showLoading();
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MAIN_KEYS", Context.MODE_PRIVATE);
           mainSync = ItHappenedApplication.
                    getApi().
                    SynchronizeData(sharedPreferences.getString("UserId", ""),
                            new StaticInMemoryRepository(getApplicationContext()).getInstance().
                            GetTrackingCollection())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<Tracking>>() {
                @Override
                public void call(List<Tracking> trackings) {
                    saveDataToDb(trackings);
                    finish();
                    startActivity(getIntent());
                    Toast.makeText(getApplicationContext(), "Синхронизировано", Toast.LENGTH_SHORT).show();
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Log.e("RxSync", ""+throwable);
                    hideLoading();
                    Toast.makeText(getApplicationContext(), "Траблы", Toast.LENGTH_SHORT).show();
                }
            });
        }
           if(id == R.id.proile_settings){
               profileStgsFrg = new ProfileSettingsFragment();
               fTrans = getFragmentManager().beginTransaction();
               fTrans.replace(R.id.trackingsFrg, profileStgsFrg);
               fTrans.commit();
           }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void okClicked(UUID trackingId) {

        TrackingService trackingService = new TrackingService("", new StaticInMemoryRepository(getApplicationContext()).getInstance());
        trackingService.RemoveTracking(trackingId);
        Toast.makeText(this, "Отслеживание удалено", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, UserActionsActivity.class);
        startActivity(intent);

    }

    public void cancelClicked() {
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void saveDataToDb(List<Tracking> trackings){
        ITrackingRepository trackingRepository = new StaticInMemoryRepository(getApplicationContext()).getInstance();
        trackingRepository.SaveTrackingCollection(trackings);
    }

    private void showLoading(){
        layoutFrg.setVisibility(View.INVISIBLE);
        syncPB.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        layoutFrg.setVisibility(View.VISIBLE);
        syncPB.setVisibility(View.INVISIBLE);
    }

    public void logout(){
        SharedPreferences sharedPreferences = getSharedPreferences("MAIN_KEYS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    public void cancelLogout(){}

}
