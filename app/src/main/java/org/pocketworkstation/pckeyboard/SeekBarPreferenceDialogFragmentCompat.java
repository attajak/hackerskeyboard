package org.pocketworkstation.pckeyboard;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.preference.PreferenceDialogFragmentCompat;

public class SeekBarPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    private SeekBar mSeek;
    private TextView mMinText;
    private TextView mMaxText;
    private TextView mValText;

    public static SeekBarPreferenceDialogFragmentCompat newInstance(String key) {
        final SeekBarPreferenceDialogFragmentCompat fragment = new SeekBarPreferenceDialogFragmentCompat();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        SeekBarPreference preference = (SeekBarPreference) getPreference();

        mSeek = view.findViewById(R.id.seekBarPref);
        mMinText = view.findViewById(R.id.seekMin);
        mMaxText = view.findViewById(R.id.seekMax);
        mValText = view.findViewById(R.id.seekVal);

        mValText.setText(preference.formatFloatDisplay(preference.mVal));
        mMinText.setText(preference.formatFloatDisplay(preference.mMin));
        mMaxText.setText(preference.formatFloatDisplay(preference.mMax));
        mSeek.setProgress(preference.getProgressVal());

        mSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {}
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float newVal = preference.percentToSteppedVal(progress, preference.mMin, preference.mMax, preference.mStep, preference.mLogScale);
                    if (newVal != preference.mVal) {
                        preference.onChange(newVal);
                    }
                    preference.setVal(newVal);
                    mSeek.setProgress(preference.getProgressVal());
                }
                mValText.setText(preference.formatFloatDisplay(preference.mVal));
            }
        });
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        ((SeekBarPreference) getPreference()).onDialogClosed(positiveResult);
    }
}
