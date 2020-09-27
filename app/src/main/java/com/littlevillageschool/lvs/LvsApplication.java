package com.littlevillageschool.lvs;

import android.app.AlarmManager;
import android.app.Application;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.onesignal.OneSignal;
import com.littlevillageschool.lvs.Model.Messaging;
import com.littlevillageschool.lvs.Model.Misc;
import com.littlevillageschool.lvs.Model.Parent;
import com.littlevillageschool.lvs.Model.Student;
import com.littlevillageschool.lvs.Model.User;
import com.littlevillageschool.lvs.Services.ExampleNotificationOpenedHandler;
import com.littlevillageschool.lvs.Services.NotificationReceiveHandler;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Alalaa Center on 18/07/2016.
 */
public class LvsApplication extends Application {
    public static final String DOWNLOAD_PATH = "/storage/emulated/0/LVS/";
    public static final String DOWNLOAD_FOLDER = "LVS/";

    public static final String SERVICE_URL = "https://app.littlevillageschool.com/ajax/";
    public static final String SECURITY_KEY = "lvssecuritykey";
    public static final String QUERY = "membership";
    public static String ACCESS_TOKEN = null;
    public static User.UserType TYPE;
    public static String PASSWORD = null;

    public static final String PASSWORD_SECURITY = "LVS2016";

    public static final String USER_PREF_KEY = "USER_KEY";

    public static User currUser;
    public static Student selectedChild;
    public static Parent parUser;
    public static Messaging messaging;
    public static Misc misc;

    public static Context APP_CTX;
    public static SharedPreferences sharedPreferences;
    public static RequestQueue volleyRequestQ;

    public static DownloadManager downloadManager;
    public static AlarmManager alarmManager;
    public static NotificationManager notificationManager;
    public static long INTERVAL_3_MINUETS = 3 * 1000;

    @Override
    public void onCreate() {
        super.onCreate();

        APP_CTX = getApplicationContext();
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .setNotificationReceivedHandler(new NotificationReceiveHandler())
                .autoPromptLocation(true)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .disableGmsMissingPrompt(true)
                .init();

        volleyRequestQ = Volley.newRequestQueue(APP_CTX);

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(APP_CTX);

        ACCESS_TOKEN = sharedPreferences.getString(User.ACCESS_TOKEN_KEY, null);
        TYPE = User.UserType.values()[sharedPreferences.getInt(User.TYPE_KEY, 0)];
        PASSWORD = sharedPreferences.getString(User.PASSWORD_KEY, null);
        messaging = new Messaging(APP_CTX);
        misc = new Misc();
        selectedChild = new Student();

        if (ACCESS_TOKEN != null) {

            if (TYPE == User.UserType.STUDENT)
                currUser = new Student();
            else
                currUser = new Parent();

            currUser.setAccessToken(ACCESS_TOKEN);
            currUser.setType(TYPE);
            currUser.setPassWord(PASSWORD);

            currUser.setCallBack(new User.MyCallBack() {
                @Override
                public void onSucc(User.UserService service) {

                }

                @Override
                public void onFail(User.UserService service, String errorMsg) {

                }
            });

            currUser.callService(User.UserService.PROFILE);
            currUser.callService(User.UserService.GET_ADDRESS_BOOK);
            if (currUser.getType() == User.UserType.PARENT)
                currUser.callService(User.UserService.STUDENT_OF_FAMILY);
            else {
                currUser.callService(User.UserService.SCHADUAL);
            }


        }


    }


    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) APP_CTX.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static String hashString(String str) {

        String md5 = "MD5";

        try {
            MessageDigest digest = MessageDigest.getInstance(md5);
            digest.update(str.getBytes());
            byte[] messageDigest = digest.digest();

            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : messageDigest) {
                String h = Integer.toHexString(0xFF & b);
                while (h.length() < 2)
                    h = "0" + h;
                stringBuilder.append(h);
            }

            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }
}


