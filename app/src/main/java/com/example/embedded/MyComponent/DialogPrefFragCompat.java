package com.example.embedded.MyComponent;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.preference.PreferenceDialogFragmentCompat;

import com.example.embedded.R;

public class DialogPrefFragCompat extends PreferenceDialogFragmentCompat {
    private TimePicker mTimePicker;
    private CharSequence mText;

    public static DialogPrefFragCompat newInstance(String key) {
        final DialogPrefFragCompat fragment = new DialogPrefFragCompat();
        final Bundle bundle = new Bundle(1);
        bundle.putString(ARG_KEY, key);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mText = getTimePickerPreference().getText();
            if(TextUtils.isEmpty(mText))
                mText=getContext().getText(R.string.default_remind_time);
        }
    }

    private TimePickerPreference getTimePickerPreference() {
        return (TimePickerPreference) getPreference();
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mTimePicker = view.findViewById(R.id.time_picker);

        if (mTimePicker == null) {
            throw new IllegalStateException("Dialog view must contain an EditText with id" +
                    " @android:id/edit");
        }
        String[] hourAndMinute= mText.toString().split(":");
        int hour = Integer.parseInt(hourAndMinute[0]);
        int minute = Integer.parseInt(hourAndMinute[1]);
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minute);
        if (getTimePickerPreference().getOnBindTimePickerListener() != null) {
            getTimePickerPreference().getOnBindTimePickerListener().onBindTimePicker(mTimePicker);
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int hour = mTimePicker.getCurrentHour();
            int minute = mTimePicker.getCurrentMinute();
            String value = String.format("%02d:%02d",hour,minute);
            Toast.makeText(getContext(), value, Toast.LENGTH_SHORT).show();
            final TimePickerPreference preference = getTimePickerPreference();
            if (preference.callChangeListener(value)) {
                preference.setText(value);
            }
        }
    }

}
