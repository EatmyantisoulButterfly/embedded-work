package com.example.embedded.MyComponent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.DialogPreference;

import com.example.embedded.R;

public class TimePickerPreference extends DialogPreference {
    private String mText;
    @Nullable
    private OnBindTimePickerListener mOnBindTimePickerListener;

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setSummaryProvider(SimpleSummaryProvider.getInstance());
        setDialogLayoutResource(R.layout.time_picker_dialog);
    }

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @SuppressLint("RestrictedApi")
    public TimePickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context, R.attr.dialogPreferenceStyle,
                android.R.attr.dialogPreferenceStyle));
    }

    public TimePickerPreference(Context context) {
        this(context, null);
    }

    public String getText() {
        return mText;
    }

    @Override
    public boolean shouldDisableDependents() {
        return TextUtils.isEmpty(mText) || super.shouldDisableDependents();
    }
    public void setText(String text) {
        final boolean wasBlocking = shouldDisableDependents();

        mText = text;

        persistString(text);

        final boolean isBlocking = shouldDisableDependents();
        if (isBlocking != wasBlocking) {
            notifyDependencyChange(isBlocking);
        }

        notifyChanged();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        setText(getPersistedString((String) defaultValue));
    }

    public void setOnBindTimePickerListener(@Nullable OnBindTimePickerListener onBindTimePickerListener) {
        mOnBindTimePickerListener = onBindTimePickerListener;
    }


    @Nullable
    OnBindTimePickerListener getOnBindTimePickerListener() {
        return mOnBindTimePickerListener;
    }

    public interface OnBindTimePickerListener {
        void onBindTimePicker(@NonNull TimePicker timePicker);
    }

    public static final class SimpleSummaryProvider implements SummaryProvider<TimePickerPreference> {

        private static SimpleSummaryProvider sSimpleSummaryProvider;

        private SimpleSummaryProvider() {}

        public static SimpleSummaryProvider getInstance() {
            if (sSimpleSummaryProvider == null) {
                sSimpleSummaryProvider = new SimpleSummaryProvider();
            }
            return sSimpleSummaryProvider;
        }

        @SuppressLint("PrivateResource")
        @Override
        public CharSequence provideSummary(TimePickerPreference preference) {
            if (TextUtils.isEmpty(preference.getText())) {
                return (preference.getContext().getString(R.string.not_set));
            } else {
                return preference.getText();
            }
        }
    }

}
