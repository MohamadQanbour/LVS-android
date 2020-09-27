package com.littlevillageschool.lvs.Services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.onesignal.OSNotificationReceivedResult;
import com.littlevillageschool.lvs.R;

/**
 * Created by Raafat Alhoumaidy on 10/05/2016.
 */

public class NotificationExtenderService extends com.onesignal.NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(final OSNotificationReceivedResult notification) {


        final Bitmap largIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        OverrideSettings overrideSettings = new OverrideSettings();

        overrideSettings.extender = new NotificationCompat.Extender() {
            @Override
            public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
                // Sets the background notification color to Green on Android 5.0+ devices.
                return builder.setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setLargeIcon(largIcon);
            }
        };

        displayNotification(overrideSettings);

        return true;
    }
}
