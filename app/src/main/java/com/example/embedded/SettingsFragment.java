package com.example.embedded;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.embedded.MyComponent.DialogPrefFragCompat;
import com.example.embedded.MyComponent.TimePickerPreference;
import com.example.embedded.utilities.AlarmUtils;
import com.example.embedded.utilities.UserUtils;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        Preference logOutBtn=findPreference(getString(R.string.log_out_key));
        logOutBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UserUtils.saveUserNameAndUserId(getContext(),"","");
                getActivity().finish();
                return true;
            }
        });
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof TimePickerPreference) {
            DialogFragment dialogFragment = DialogPrefFragCompat.newInstance(preference.getKey());
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getParentFragmentManager(), null);//getFragmentManager()
        } else super.onDisplayPreferenceDialog(preference);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.need_remind_key)))
            AlarmUtils.setAlarm(getContext());
    }

}
