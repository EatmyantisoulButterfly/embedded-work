package com.example.embedded.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.embedded.R;

public class UserUtils {
    public static void saveUserNameAndUserId(Context context,String userName, String userId){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(context.getString(R.string.user_name_key),userName)
                .putString(context.getString(R.string.prompt_userId),userId)
                .apply();
    }
    public static String getUserName(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(context.getString(R.string.user_name_key),"");
    }
    public static String getUserId(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(context.getString(R.string.prompt_userId),"");
    }

    public static void setUserStatus(Context context,boolean status){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(context.getString(R.string.user_status),status)
                .apply();
    }
    public static boolean getUserStatus(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.user_status),false);
    }
}
