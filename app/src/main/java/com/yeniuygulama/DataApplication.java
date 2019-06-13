package com.yeniuygulama;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class DataApplication extends Application {

   @Override
   public void onCreate() {
      super.onCreate();
      OneSignal.startInit(this)
              .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
              .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
              .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
              .unsubscribeWhenNotificationsAreDisabled(true)
              .init();
   }

   private class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
      @Override
      public void notificationReceived(OSNotification notification) {
         JSONObject data = notification.payload.additionalData;
         String notificationID = notification.payload.notificationID;
         String title = notification.payload.title;
         String body = notification.payload.body;
         String smallIcon = notification.payload.smallIcon;
         String largeIcon = notification.payload.largeIcon;
         String bigPicture = notification.payload.bigPicture;
         String smallIconAccentColor = notification.payload.smallIconAccentColor;
         String sound = notification.payload.sound;
         String ledColor = notification.payload.ledColor;
         int lockScreenVisibility = notification.payload.lockScreenVisibility;
         String groupKey = notification.payload.groupKey;
         String groupMessage = notification.payload.groupMessage;
         String fromProjectNumber = notification.payload.fromProjectNumber;
         String rawPayload = notification.payload.rawPayload;
         String customKey;
         if (data != null) {
            customKey = data.optString("customkey", null);
            if (customKey != null)
               Log.i("OneSignalExample", "customkey set with value: " + customKey);
         }
      }
   }

   private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
      @Override
      public void notificationOpened(OSNotificationOpenResult result) {
         OSNotificationAction.ActionType actionType = result.action.type;
         JSONObject data = result.notification.payload.additionalData;
         String launchUrl = result.notification.payload.launchURL; // update docs launchUrl

         String customKey;
         String openURL = null;
         Object activityToLaunch = MainActivity.class;

         if (data != null) {
            customKey = data.optString("customkey", null);
            openURL = data.optString("openURL", null);

            if (customKey != null)
               Log.i("OneSignalExample", "customkey set with value: " + customKey);

            if (openURL != null)
               Log.i("OneSignalExample", "openURL to webview with URL value: " + openURL);
         }

         if (actionType == OSNotificationAction.ActionType.ActionTaken) {
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

            if (result.action.actionID.equals("id1")) {
               Log.i("OneSignalExample", "button id called: " + result.action.actionID);
               activityToLaunch = MainActivity.class;
            } else
               Log.i("OneSignalExample", "button id called: " + result.action.actionID);
         }

         Intent intent = new Intent(getApplicationContext(), (Class<?>) activityToLaunch);
         intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
         intent.putExtra("openURL", launchUrl);
         Log.i("OneSignalExample", "openURL = " + openURL);
         startActivity(intent);
      }
   }
}