package com.example.embedded.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.example.embedded.R;
import com.example.embedded.ScreenUnlockReceiver;

import java.util.Calendar;


public class AlarmUtils {
    private static final int REQUEST_CODE = 1;
    private static final String TAG = "AlarmUtils";

    public static void setAlarm(Context context) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = getPendingIntent(context);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (sp.getBoolean(context.getString(R.string.need_remind_key), false)) {
            String timeStr = sp.getString(context.getString(R.string.remind_time_key),
                    "");
            if (!timeStr.isEmpty()) {
                String[] hourAndMinute = timeStr.split(":");
                long readyTime = getReadyTime(Integer.parseInt(hourAndMinute[0]),
                        Integer.parseInt(hourAndMinute[1]),
                        0).getTimeInMillis();
                multiVersionSetAlarm(readyTime, alarmManager, pendingIntent);
            }
        } else{
            alarmManager.cancel(pendingIntent);
        }
    }

    private static void multiVersionSetAlarm(long readyTime, AlarmManager alarmManager, PendingIntent pendingIntent) {
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC,
                        readyTime, pendingIntent);
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                        readyTime, pendingIntent);
            else alarmManager.set(AlarmManager.RTC_WAKEUP,
                        readyTime, pendingIntent);
        }
    }

    private static PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, ScreenUnlockReceiver.class);
        intent.setAction(ScreenUnlockReceiver.ACTION_FIRST_UNLOCK_REMIND_REGISTER);
        return PendingIntent.getBroadcast(context.getApplicationContext(),
                REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static Calendar getReadyTime(int hour, int minute, int second) {
        Calendar scheduledTime = Calendar.getInstance();
        Log.i(TAG, "getReadyTime: ");
        scheduledTime.set(Calendar.HOUR_OF_DAY, hour);
        scheduledTime.set(Calendar.MINUTE, minute);
        scheduledTime.set(Calendar.SECOND, second);
        if (Calendar.getInstance().after(scheduledTime))
            scheduledTime.add(Calendar.DATE, 1);//今天的预定时间已过则设置为下一天的
        Log.i(TAG, scheduledTime.toString());
        return scheduledTime;
    }
}
