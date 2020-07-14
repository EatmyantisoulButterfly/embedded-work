package com.example.embedded;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.example.embedded.utilities.AlarmUtils;
import com.example.embedded.utilities.NotificationUtils;

public class ScreenUnlockReceiver extends BroadcastReceiver {

    private static final String TAG = "Receiver";
    public static final String ACTION_FIRST_UNLOCK_REMIND_REGISTER = "com.example.embedded.action.ready_remind_service";
    static ScreenUnlockReceiver receiver = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(ACTION_FIRST_UNLOCK_REMIND_REGISTER)) {
            KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (mKeyguardManager != null)
                if (!mKeyguardManager.inKeyguardRestrictedInputMode()) {
                    NotificationUtils.remindSubmit(context);
                } else {
                    registerRemindReceiver(context);
                    Log.i(TAG, "register");
                }

        } else if (action != null && action.equals(Intent.ACTION_USER_PRESENT)) {
            NotificationUtils.remindSubmit(context);
            unregisterRemindReceiver(context);
            Log.i(TAG, "unregister");
        } else {
            Log.i(TAG, "no equals");

        }
        AlarmUtils.setAlarm(context);
    }

    private void unregisterRemindReceiver(Context context) {
        if (receiver != null)
            context.getApplicationContext().unregisterReceiver(receiver);
    }

    private void registerRemindReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        receiver = new ScreenUnlockReceiver();
        context.getApplicationContext().registerReceiver(receiver, intentFilter);
    }

}
