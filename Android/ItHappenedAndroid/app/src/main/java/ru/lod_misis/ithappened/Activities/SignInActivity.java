package ru.lod_misis.ithappened.Activities;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.SignInButton;
import com.nvanbenschoten.motion.ParallaxImageView;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import ru.lod_misis.ithappened.Domain.Tracking;
import ru.lod_misis.ithappened.Infrastructure.ITrackingRepository;
import ru.lod_misis.ithappened.Models.RegistrationResponse;
import ru.lod_misis.ithappened.Models.SynchronizationRequest;
import ru.lod_misis.ithappened.R;
import ru.lod_misis.ithappened.Retrofit.ItHappenedApplication;
import ru.lod_misis.ithappened.StaticInMemoryRepository;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SignInActivity extends Activity {

    private final static String G_PLUS_SCOPE =
            "oauth2:https://www.googleapis.com/auth/plus.me";
    private final static String USERINFO_SCOPE =
            "https://www.googleapis.com/auth/userinfo.profile";
    private final static String EMAIL_SCOPE =
            "https://www.googleapis.com/auth/userinfo.email";
    private final static String SCOPES = G_PLUS_SCOPE + " " + USERINFO_SCOPE + " " + EMAIL_SCOPE;
    private static final String TAG = "ASYNCREG";

    ParallaxImageView mainBackground;
    SignInButton signIn;
    TextView mainTitle;
    ProgressBar mainPB;

    CardView offlineWork;

    Subscription regSub;
    Subscription syncSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "Запущено!");

        setContentView(R.layout.activity_registration);
        mainPB = (ProgressBar) findViewById(R.id.mainProgressBar);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MAIN_KEYS", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("UserId", "");
        boolean flag = sharedPreferences.getBoolean("LOGOUT", false);
        if(id == "") {
            findControlsById();
            mainBackground.registerSensorManager();
            AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
            animation.setDuration(3000);
            animation.setFillAfter(true);
            mainTitle.setAnimation(animation);

            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                            false, null, null, null, null);
                    startActivityForResult(intent, 228);
                }
            });

            offlineWork = (CardView) findViewById(R.id.offline);

            offlineWork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MAIN_KEYS", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("UserId", "Offline");
                    editor.putString("Nick", "Offline");
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), UserActionsActivity.class);
                    startActivity(intent);
                }
            });
        }else{
            Intent intent = new Intent(getApplicationContext(), UserActionsActivity.class);
            startActivity(intent);
        }
    }

    private void findControlsById(){

        signIn = (SignInButton) findViewById(R.id.signin);
        mainBackground = (ParallaxImageView) findViewById(R.id.mainBackground);
        mainTitle = (TextView) findViewById(R.id.mainTitle);

    }

    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data){


        if (requestCode == 228 && resultCode == RESULT_OK) {
            showLoading();
            final String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

            AsyncTask<Void, Void, String> getToken = new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {

                    String idToken = "";

                    try {
                        idToken = GoogleAuthUtil.getToken(SignInActivity.this, accountName,
                                SCOPES);
                        return idToken;

                    } catch (UserRecoverableAuthException userAuthEx) {
                        startActivityForResult(userAuthEx.getIntent(), 228);
                    } catch (IOException ioEx) {
                        Log.e(TAG, "IOException"+ioEx);
                        this.cancel(true);
                    } catch (GoogleAuthException fatalAuthEx) {
                        this.cancel(true);
                        //hideLoading();
                        Log.e(TAG, "Fatal Authorization Exception" + fatalAuthEx.getLocalizedMessage());
                    }
                    return idToken;
                }

                @Override
                protected void onCancelled() {
                    Toast.makeText(getApplicationContext(), "Разорвано подключение!", Toast.LENGTH_SHORT).show();
                    hideLoading();
                }

                @Override
                protected void onPostExecute(String idToken) {
                    reg(idToken);
                }
            };

            getToken.execute(null, null, null);
        }
    }

    private void reg(String idToken){

        Log.e(TAG, "Токен получен");

       regSub = ItHappenedApplication.getApi().SignUp(idToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RegistrationResponse>() {
                    @Override
                    public void call(RegistrationResponse registrationResponse) {
                        SharedPreferences sharedPreferences = getSharedPreferences("MAIN_KEYS", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("UserId", registrationResponse.getUserId());
                        editor.putString("Nick", registrationResponse.getUserNickname());
                        editor.putString("Url", registrationResponse.getPicUrl());
                        editor.putLong("NickDate", registrationResponse.getNicknameDateOfChange().getTime());
                        editor.commit();

                        SynchronizationRequest synchronizationRequest = new SynchronizationRequest(registrationResponse.getUserNickname(),
                                new Date(sharedPreferences.getLong("NickDate", 0)),
                                new StaticInMemoryRepository(getApplicationContext(), sharedPreferences.getString("UserId", "")).getInstance().GetTrackingCollection());

                       syncSub = ItHappenedApplication.
                                getApi().SynchronizeData(registrationResponse.getUserId(), synchronizationRequest)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<SynchronizationRequest>() {
                                    @Override
                                    public void call(SynchronizationRequest sync) {
                                        saveDataToDb(sync.getTrackingCollection());

                                        SharedPreferences sharedPreferences = getSharedPreferences("MAIN_KEYS", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putLong("NickDate", sync.getNicknameDateOfChange().getTime());
                                        editor.commit();

                                        Toast.makeText(getApplicationContext(), "Синхронизировано", Toast.LENGTH_SHORT).show();
                                        hideLoading();
                                        Intent intent = new Intent(getApplicationContext(), UserActionsActivity.class);
                                        startActivity(intent);
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        hideLoading();
                                        Log.e("RxSync", ""+throwable);
                                        Toast.makeText(getApplicationContext(), "Синхронизация не прошла!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        hideLoading();
                        Log.e("Reg", ""+throwable);
                        Toast.makeText(getApplicationContext(), "Разорвано подключение!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void saveDataToDb(List<Tracking> trackings){
        SharedPreferences sharedPreferences = getSharedPreferences("MAIN_KEYS", MODE_PRIVATE);
        ITrackingRepository trackingRepository = new StaticInMemoryRepository(getApplicationContext(), sharedPreferences.getString("UserId", "")).getInstance();
        trackingRepository.SaveTrackingCollection(trackings);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(regSub!=null)
            regSub.unsubscribe();
        if(syncSub!=null)
            syncSub.unsubscribe();
    }

    private  void showLoading(){
        mainBackground.setVisibility(View.INVISIBLE);
        mainPB.setVisibility(View.VISIBLE);
        signIn.setVisibility(View.INVISIBLE);
        mainTitle.setVisibility(View.INVISIBLE);
        offlineWork.setVisibility(View.INVISIBLE);
    }

    private void hideLoading(){
        mainBackground.setVisibility(View.VISIBLE);
        mainPB.setVisibility(View.INVISIBLE);
        signIn.setVisibility(View.VISIBLE);
        mainTitle.setVisibility(View.VISIBLE);
        offlineWork.setVisibility(View.VISIBLE);
    }

}