package com.example.embedded;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class WaitForTheFirstUnlockService extends IntentService {


    private static final String TAG = "WaitService";

    public WaitForTheFirstUnlockService() {
        super("WaitForTheFirstUnlock");
        System.out.println("WaitForTheFirstUnlock");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent: ");
        if (intent != null) {
//            final String action = intent.getAction();
//            if (ACTION_READY_REMIND_SERVICE.equals(action)) {//
                Log.i(TAG, "equals");
                handleActionReadyRemind();
            for (int i=0;i<10000;i++){
                Log.i(TAG, i+"");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            }
        }
    }



    private void handleActionReadyRemind() {//启动broadcastReceiver
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        ScreenUnlockReceiver receiver=new ScreenUnlockReceiver();
        getApplicationContext().registerReceiver(receiver,intentFilter);
    }

}
