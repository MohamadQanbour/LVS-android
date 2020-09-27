package com.littlevillageschool.lvs.Activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.littlevillageschool.lvs.LvsApplication;
import com.littlevillageschool.lvs.Model.Parent;
import com.littlevillageschool.lvs.Model.User;
import com.littlevillageschool.lvs.R;

import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private int REQUEST_INTERNT = 1;
    private LinearLayout parentLayout;
    private ImageView loginIconImgV;
    private TextInputLayout userIdTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private Button loginBtn;
    private DotProgressBar dotProgressBar;
    private EditText userIdEdtTxt;
    private EditText passwordEdtTxt;

    private boolean backPressed = false;
    private boolean error = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        parentLayout = (LinearLayout) findViewById(R.id.loginParentRLayout);
        loginIconImgV = (ImageView) findViewById(R.id.loginLogoImgV);
        userIdTextInputLayout = (TextInputLayout) findViewById(R.id.userIdTextInput);
        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.passwordTextInput);
        userIdEdtTxt = (EditText) findViewById(R.id.userIdEdtTxt);
        passwordEdtTxt = (EditText) findViewById(R.id.passwordEdtTxt);
        dotProgressBar = (DotProgressBar) findViewById(R.id.dot_progress_bar);
        loginBtn = (Button) findViewById(R.id.loginBtn);


        if (LvsApplication.currUser != null)
            LvsApplication.currUser.setCallBack(new User.MyCallBack() {
                @Override
                public void onSucc(User.UserService service) {
                    switch (service) {
                        case LOGIN:

                            break;
                        case PROFILE:
                            LvsApplication.currUser.callService(User.UserService.REGISTER_DEVICE);
                            break;
                        case GET_ADDRESS_BOOK:

                            break;

                        case STUDENT_OF_FAMILY:
                            if (((Parent) LvsApplication.currUser).getStudentList().size() > 0)
                                LvsApplication.selectedChild = ((Parent) LvsApplication.currUser).getStudentList().get(0);
                            break;
                    }
                }

                @Override
                public void onFail(User.UserService service, String errorMsg) {

                    switch (service) {
                        case LOGIN:
                            dotProgressBar.setVisibility(View.GONE);
                            loginBtn.setVisibility(View.VISIBLE);
                            userIdTextInputLayout.setVisibility(View.VISIBLE);
                            passwordTextInputLayout.setVisibility(View.VISIBLE);
                            break;

                        default:
                            error = true;
                            if (!LvsApplication.isNetworkAvailable()) {
                                final Snackbar snackbar =
                                        Snackbar.make(parentLayout, R.string.no_internet_error, Snackbar.LENGTH_INDEFINITE);

                                snackbar.show();
                                break;

                            }
                            Snackbar.make(parentLayout, errorMsg, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(R.string.retry, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            LvsApplication.currUser.callService(User.UserService.PROFILE);
                                            LvsApplication.currUser.callService(User.UserService.GET_ADDRESS_BOOK);
                                            if (LvsApplication.currUser.getType() == User.UserType.PARENT) {
                                                LvsApplication.currUser.callService(User.UserService.STUDENT_OF_FAMILY);
                                            } else {
                                                LvsApplication.currUser.callService(User.UserService.ABSENTS);
                                                LvsApplication.currUser.callService(User.UserService.MATERIALS);
                                                LvsApplication.currUser.callService(User.UserService.SCHADUAL);
                                            }
                                        }
                                    }).show();

                            break;
                    }

                }
            });

        assert loginBtn != null;
        loginBtn.setOnClickListener(this);


        final Animation loginIconAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.login_icon_animation);
        final Animation loginTxtAnim1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.login_textinput_animation);
        final Animation loginTxtAnim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.login_textinput_animation);
        final Animation loginTxtAnim3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.login_textinput_animation);

        loginTxtAnim2.setStartOffset(500);
        loginTxtAnim3.setStartOffset(1000);
        loginIconAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (LvsApplication.ACCESS_TOKEN != null) {
                    if (error)
                        return;

                    Intent toMainActivity;
                    toMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(toMainActivity);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    LoginActivity.this.finish();

                } else {
                    userIdTextInputLayout.setVisibility(View.VISIBLE);
                    passwordTextInputLayout.setVisibility(View.VISIBLE);
                    loginBtn.setVisibility(View.VISIBLE);
                    userIdTextInputLayout.setAnimation(loginTxtAnim1);
                    passwordTextInputLayout.setAnimation(loginTxtAnim2);
                    loginBtn.setAnimation(loginTxtAnim3);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        loginIconImgV.startAnimation(loginIconAnim);

//        loginIconImgV.animate()
//                .scaleXBy(2.0f).scaleY(2.0f).setDuration(1000)
//                .scaleYBy(.5f).scaleYBy(.5f).setStartDelay(1000).setDuration(1300);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        Intent getEmailIntent = new Intent(LvsApplication.APP_CTX, GetEmailsReciever.class);
//
//        PendingIntent pendingIntent=
//                PendingIntent.getBroadcast(LvsApplication.APP_CTX,14,getEmailIntent,PendingIntent.FLAG_CANCEL_CURRENT);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeZone(TimeZone.getDefault());
//
//        LvsApplication.alarmManager.setRepeating(
//                AlarmManager.RTC_WAKEUP,
//                calendar.getTimeInMillis()+4000,
//                LvsApplication.INTERVAL_3_MINUETS,
//                pendingIntent);
    }

    private boolean mayRequestInternet() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if (checkSelfPermission(INTERNET) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED)
            return true;

        if (shouldShowRequestPermissionRationale(INTERNET))
            Snackbar.make(parentLayout, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{INTERNET, ACCESS_NETWORK_STATE}, REQUEST_INTERNT);
                        }
                    });
        else
            requestPermissions(new String[]{INTERNET, ACCESS_NETWORK_STATE}, REQUEST_INTERNT);

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_INTERNT) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                logIn();

            }
        }
    }

    private boolean validateLogin() {
        if (TextUtils.isEmpty(userIdEdtTxt.getText())) {
            userIdEdtTxt.setError(getString(R.string.empty_user_error));
            userIdEdtTxt.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(passwordEdtTxt.getText())) {
            passwordEdtTxt.setError(getString(R.string.empty_password_error));
            userIdEdtTxt.requestFocus();

            return false;
        }

        return true;
    }


    private void logIn() {

        if (!mayRequestInternet())
            return;

        if (!validateLogin())
            return;

        if (!LvsApplication.isNetworkAvailable()) {
            final Snackbar snackbar =
                    Snackbar.make(parentLayout, R.string.no_internet_error, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.retry, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    logIn();
                                }
                            });

            snackbar.show();
            return;

        }

        final Animation loginBtnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.login_btn_animation);
        Animation loginBtnAnim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.login_btn_animation);
        Animation loginBtnAnim3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.login_btn_animation);

        final Animation loginBtnRevAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.login_btn_reverse_animation);

        loginBtnAnim3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dotProgressBar.setVisibility(View.VISIBLE);

                loginBtn.setVisibility(View.INVISIBLE);
                userIdTextInputLayout.setVisibility(View.INVISIBLE);
                passwordTextInputLayout.setVisibility(View.INVISIBLE);

                loginBtn.clearAnimation();
                userIdTextInputLayout.clearAnimation();
                passwordTextInputLayout.clearAnimation();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }
        });

        loginBtnAnim2.setStartOffset(200);
        loginBtnAnim3.setStartOffset(400);

        loginBtn.clearAnimation();
        userIdTextInputLayout.clearAnimation();
        passwordTextInputLayout.clearAnimation();

        loginBtn.setAnimation(loginBtnAnim);
        userIdTextInputLayout.setAnimation(loginBtnAnim3);
        passwordTextInputLayout.setAnimation(loginBtnAnim2);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {

                LvsApplication.currUser = new User();
                LvsApplication.currUser.setUserName(userIdEdtTxt.getText().toString());
                LvsApplication.currUser.setPassWord(passwordEdtTxt.getText().toString());

                LvsApplication.currUser.setCallBack(new User.MyCallBack() {
                    @Override
                    public void onSucc(User.UserService service) {
                        switch (service) {
                            case LOGIN:
                                LvsApplication.currUser.callService(User.UserService.PROFILE);
                                LvsApplication.currUser.callService(User.UserService.GET_ADDRESS_BOOK);
                                if (LvsApplication.currUser.getType() == User.UserType.PARENT)
                                    LvsApplication.currUser.callService(User.UserService.STUDENT_OF_FAMILY);
                                else
                                    LvsApplication.currUser.callService(User.UserService.SCHADUAL);


                                LvsApplication.sharedPreferences
                                        .edit()
                                        .putString(User.ACCESS_TOKEN_KEY, LvsApplication.currUser.getAccessToken())
                                        .putInt(User.TYPE_KEY, LvsApplication.currUser.getType().ordinal())
                                        .putString(User.PASSWORD_KEY, LvsApplication.currUser.getPassWord().replace(LvsApplication.PASSWORD_SECURITY, ""))
                                        .apply();

                                break;
                            case PROFILE:
                                LvsApplication.currUser.callService(User.UserService.REGISTER_DEVICE);
                                Intent toMainActivity;
                                toMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(toMainActivity);
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                LoginActivity.this.finish();

                                break;
                            case GET_ADDRESS_BOOK:

                                break;

                            case STUDENT_OF_FAMILY:
                                if(((Parent) LvsApplication.currUser).getStudentList().size()>0)
                                LvsApplication.selectedChild = ((Parent) LvsApplication.currUser).getStudentList().get(0);
                                break;
                        }
                    }

                    @Override
                    public void onFail(User.UserService service, String errorMsg) {
                        dotProgressBar.setVisibility(View.INVISIBLE);
                        loginBtn.setVisibility(View.VISIBLE);
                        userIdTextInputLayout.setVisibility(View.VISIBLE);
                        passwordTextInputLayout.setVisibility(View.VISIBLE);

                        Snackbar.make(parentLayout, errorMsg, Snackbar.LENGTH_LONG).show();

                    }
                });

                LvsApplication.currUser.callService(User.UserService.LOGIN);

            }
        }, 500);


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.loginBtn:
                logIn();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressed)
            super.onBackPressed();
        else {
            Toast.makeText(this, R.string.exit_msg, Toast.LENGTH_SHORT).show();
            backPressed = true;
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    backPressed = false;
                }
            }, 3000);
        }
    }
}
