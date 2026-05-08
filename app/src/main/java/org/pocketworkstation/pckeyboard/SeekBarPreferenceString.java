package org.pocketworkstation.pckeyboard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

/**
 * Variant of SeekBarPreference that stores values as string preferences.
 * 
 * This is for compatibility with existing preferences, switching types
 * leads to runtime errors when upgrading or downgrading.
 */
public class SeekBarPreferenceString extends SeekBarPreference {

    private static Pattern FLOAT_RE = Pattern.compile("(\\d+\\.?\\d*).*");

    public SeekBarPreferenceString(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekBarPreferenceString(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // Some saved preferences from old versions have " ms" or "%" suffix, remove that.
    private float floatFromString(String pref) {
        if (pref == null) return 0.0f;
        Matcher num = FLOAT_RE.matcher(pref);
        if (!num.matches()) return 0.0f;
        return Float.valueOf(num.group(1));
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return floatFromString(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        if (defaultValue == null) {
            setVal(floatFromString(getPersistedString("0.0")));
        } else {
            setVal((Float) defaultValue);
        }
        savePrevVal();
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (!positiveResult) {
            restoreVal();
            return;
        }
        if (shouldPersist()) {
            savePrevVal();
            persistString(getValString());
        }
        notifyChanged();
    }
}