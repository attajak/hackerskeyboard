package org.pocketworkstation.pckeyboard;

import java.util.Locale;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * SeekBarPreference provides a dialog for editing float-valued preferences with a slider.
 */
public class SeekBarPreference extends DialogPreference {

    protected float mMin;
    protected float mMax;
    protected float mVal;
    protected float mPrevVal;
    protected float mStep;
    protected boolean mAsPercent;
    protected boolean mLogScale;
    protected String mDisplayFormat;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    
    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        setDialogLayoutResource(R.layout.seek_bar_dialog);
        
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference);
        mMin = a.getFloat(R.styleable.SeekBarPreference_minValue, 0.0f);
        mMax = a.getFloat(R.styleable.SeekBarPreference_maxValue, 100.0f);
        mStep = a.getFloat(R.styleable.SeekBarPreference_step, 0.0f);
        mAsPercent = a.getBoolean(R.styleable.SeekBarPreference_asPercent, false);
        mLogScale = a.getBoolean(R.styleable.SeekBarPreference_logScale, false);
        mDisplayFormat = a.getString(R.styleable.SeekBarPreference_displayFormat);
        a.recycle();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getFloat(index, 0.0f);
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        if (defaultValue == null) {
            setVal(getPersistedFloat(mVal));
        } else {
            setVal((Float) defaultValue);
        }
        savePrevVal();
    }

    public String formatFloatDisplay(Float val) {
        // Use current locale for format, this is for display only.
        if (mAsPercent) {
            return String.format("%d%%", (int) (val * 100));
        }
        
        if (mDisplayFormat != null) {
            return String.format(mDisplayFormat, val);
        } else {
            return Float.toString(val);
        }
    }
    
    protected void setVal(Float val) {
        mVal = val;
    }
    
    protected void savePrevVal() {
        mPrevVal = mVal;
    }

    protected void restoreVal() {
        mVal = mPrevVal;
    }

    protected String getValString() {
        return Float.toString(mVal);
    }
    
    public float percentToSteppedVal(int percent, float min, float max, float step, boolean logScale) {
        float val;
        if (logScale) {
            val = (float) Math.exp(percentToSteppedVal(percent, (float) Math.log(min), (float) Math.log(max), step, false));
        } else {
            float delta = percent * (max - min) / 100.0f;
            if (step != 0.0f) {
                delta = Math.round(delta / step) * step;
            }
            val = min + delta;
        }
        // Hack: Round number to 2 significant digits so that it looks nicer.
        val = Float.valueOf(String.format(Locale.US, "%.2g", val));
        return val;
    }

    public int getPercent(float val, float min, float max) {
        return (int) (100.0f * (val - min) / (max - min));
    }
    
    public int getProgressVal() {
        if (mLogScale) {
            return getPercent((float) Math.log(mVal), (float) Math.log(mMin), (float) Math.log(mMax));
        } else {
            return getPercent(mVal, mMin, mMax);
        }
    }

    public void onChange(float val) {
        // override in subclasses
    }

    @Override
    public CharSequence getSummary() {
        return formatFloatDisplay(mVal);
    }
    
    public void onDialogClosed(boolean positiveResult) {
        if (!positiveResult) {
            restoreVal();
            return;
        }
        if (shouldPersist()) {
            persistFloat(mVal);
            savePrevVal();
        }
        notifyChanged();
    }
}
