package com.yeniuygulama;

import android.support.v4.app.NotificationCompat;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;

public class NotificationExtender extends NotificationExtenderService {
   @Override
   protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
      OverrideSettings overrideSettings = new OverrideSettings();
      overrideSettings.extender = new NotificationCompat.Extender() {
         @Override
         public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
            return builder.setColor(getResources().getColor(R.color.notificationbackgroundcolor));
         }
      };
      OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
      return true;
   }
}