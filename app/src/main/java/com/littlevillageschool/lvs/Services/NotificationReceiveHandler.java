package com.littlevillageschool.lvs.Services;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by Raafat Alhoumaidy on 10/05/2016.
 */

public class NotificationReceiveHandler implements OneSignal.NotificationReceivedHandler {
    @Override
    public void notificationReceived(OSNotification notification) {
        JSONObject data = notification.payload.additionalData;

    }
}
