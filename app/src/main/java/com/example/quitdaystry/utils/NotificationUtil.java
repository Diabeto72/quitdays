package com.example.quitdaystry.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.quitdaystry.R;

/** Static helpers for creating and posting notifications. */
public class NotificationUtil {

    private static final String CHANNEL_ID = "daily_reminder";
    private static final int REMINDER_NOTIFICATION_ID = 1001;

    private NotificationUtil() {}

    /** Creates the daily reminder notification channel (required on API 26+). */
    public static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "תזכורת יומית",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("תזכורת יומית לסמן את יום הגמילה");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    /**
     * Posts the daily reminder notification.
     *
     * @param context       application context
     * @param habitsPending text describing pending habits
     */
    public static void showReminder(Context context, String habitsPending) {
        if (!hasPostNotificationsPermission(context)) return;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("QuitDays")
                .setContentText(habitsPending)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat.from(context).notify(REMINDER_NOTIFICATION_ID, builder.build());
    }

    /**
     * Returns true if POST_NOTIFICATIONS permission is granted.
     * Below API 33 this always returns true (permission not required).
     */
    public static boolean hasPostNotificationsPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
}
