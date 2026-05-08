package org.pocketworkstation.pckeyboard;

import android.app.backup.BackupManager;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import android.text.AutoText;
import android.text.InputType;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

public class LatinIMESettings extends BaseSettingsActivity {

    @Override
    protected PreferenceFragmentCompat createSettingsFragment() {
        return new LatinIMESettingsFragment();
    }

    public static class LatinIMESettingsFragment extends BaseSettingsFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        public static final String PREF_SETTINGS_KEY = "settings_key";
        private static final String QUICK_FIXES_KEY = "quick_fixes";
        private static final String PREDICTION_SETTINGS_KEY = "prediction_settings";
        private static final String VOICE_SETTINGS_KEY = "voice_mode";
        static final String INPUT_CONNECTION_INFO = "input_connection_info";

        private static final String TAG = "LatinIMESettings";

        private CheckBoxPreference mQuickFixes;
        private ListPreference mVoicePreference;
        private ListPreference mSettingsKeyPreference;
        private ListPreference mKeyboardModePortraitPreference;
        private ListPreference mKeyboardModeLandscapePreference;
        private Preference mInputConnectionInfo;
        private Preference mLabelVersion;

        private boolean mVoiceOn;
        private String mVoiceModeOff;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.prefs);
            
            mQuickFixes = findPreference(QUICK_FIXES_KEY);
            mVoicePreference = findPreference(VOICE_SETTINGS_KEY);
            mSettingsKeyPreference = findPreference(PREF_SETTINGS_KEY);
            mInputConnectionInfo = findPreference(INPUT_CONNECTION_INFO);
            mLabelVersion = findPreference("label_version");

            mKeyboardModePortraitPreference = findPreference("pref_keyboard_mode_portrait");
            mKeyboardModeLandscapePreference = findPreference("pref_keyboard_mode_landscape");

            SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
            prefs.registerOnSharedPreferenceChangeListener(this);

            mVoiceModeOff = getString(R.string.voice_mode_off);
            mVoiceOn = !(prefs.getString(VOICE_SETTINGS_KEY, mVoiceModeOff).equals(mVoiceModeOff));
        }

        @Override
        public void onResume() {
            super.onResume();
            // Note: AutoText.getSize is deprecated and might not work as expected on modern Android
            // but we keep the logic for now.
            
            Log.i(TAG, "compactModeEnabled=" + LatinIME.sKeyboardSettings.compactModeEnabled);
            if (!LatinIME.sKeyboardSettings.compactModeEnabled) {
                if (mKeyboardModePortraitPreference != null) {
                    CharSequence[] oldEntries = mKeyboardModePortraitPreference.getEntries();
                    CharSequence[] oldValues = mKeyboardModePortraitPreference.getEntryValues();

                    if (oldEntries != null && oldEntries.length > 2) {
                        CharSequence[] newEntries = new CharSequence[]{oldEntries[0], oldEntries[2]};
                        CharSequence[] newValues = new CharSequence[]{oldValues[0], oldValues[2]};
                        mKeyboardModePortraitPreference.setEntries(newEntries);
                        mKeyboardModePortraitPreference.setEntryValues(newValues);
                    }
                }
                if (mKeyboardModeLandscapePreference != null) {
                    CharSequence[] oldEntries = mKeyboardModeLandscapePreference.getEntries();
                    CharSequence[] oldValues = mKeyboardModeLandscapePreference.getEntryValues();
                    if (oldEntries != null && oldEntries.length > 2) {
                        CharSequence[] newEntries = new CharSequence[]{oldEntries[0], oldEntries[2]};
                        CharSequence[] newValues = new CharSequence[]{oldValues[0], oldValues[2]};
                        mKeyboardModeLandscapePreference.setEntries(newEntries);
                        mKeyboardModeLandscapePreference.setEntryValues(newValues);
                    }
                }
            }

            updateSummaries();

            String version = "";
            try {
                PackageInfo info = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), PackageManager.GET_SIGNATURES);
                version = info.versionName;
                boolean isOfficial = false;
                if (info.signatures != null) {
                    for (Signature sig : info.signatures) {
                        byte[] b = sig.toByteArray();
                        int out = 0;
                        for (int i = 0; i < b.length; ++i) {
                            int pos = i % 4;
                            out ^= b[i] << (pos * 4);
                        }
                        if (out == -466825) {
                            isOfficial = true;
                        }
                    }
                }
                version += isOfficial ? " official" : " custom";
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Could not find version info.");
            }

            if (mLabelVersion != null) {
                mLabelVersion.setSummary(version);
            }
        }

        @Override
        public void onDestroy() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onDestroy();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            new BackupManager(requireContext()).dataChanged();
            // If turning on voice input, show dialog (logic simplified, original showVoiceConfirmation skipped for now)
            mVoiceOn = !(prefs.getString(VOICE_SETTINGS_KEY, mVoiceModeOff).equals(mVoiceModeOff));
            updateSummaries();
        }

        private void updateSummaries() {
            if (isAdded()) {
                Resources res = getResources();
                if (mSettingsKeyPreference != null) {
                    int index = mSettingsKeyPreference.findIndexOfValue(mSettingsKeyPreference.getValue());
                    if (index >= 0) {
                        mSettingsKeyPreference.setSummary(res.getStringArray(R.array.settings_key_modes)[index]);
                    }
                }

                if (mInputConnectionInfo != null) {
                    mInputConnectionInfo.setSummary(String.format("%s type=%s",
                            LatinIME.sKeyboardSettings.editorPackageName,
                            inputTypeDesc(LatinIME.sKeyboardSettings.editorInputType)
                    ));
                }
                
                if (mVoicePreference != null) {
                    int index = mVoicePreference.findIndexOfValue(mVoicePreference.getValue());
                    if (index >= 0) {
                        mVoicePreference.setSummary(res.getStringArray(R.array.voice_input_modes_summary)[index]);
                    }
                }
            }
        }

        static Map<Integer, String> INPUT_CLASSES = new HashMap<>();
        static Map<Integer, String> DATETIME_VARIATIONS = new HashMap<>();
        static Map<Integer, String> TEXT_VARIATIONS = new HashMap<>();
        static Map<Integer, String> NUMBER_VARIATIONS = new HashMap<>();

        static {
            INPUT_CLASSES.put(0x00000004, "DATETIME");
            INPUT_CLASSES.put(0x00000002, "NUMBER");
            INPUT_CLASSES.put(0x00000003, "PHONE");
            INPUT_CLASSES.put(0x00000001, "TEXT");
            INPUT_CLASSES.put(0x00000000, "NULL");

            DATETIME_VARIATIONS.put(0x00000010, "DATE");
            DATETIME_VARIATIONS.put(0x00000020, "TIME");

            NUMBER_VARIATIONS.put(0x00000010, "PASSWORD");

            TEXT_VARIATIONS.put(0x00000020, "EMAIL_ADDRESS");
            TEXT_VARIATIONS.put(0x00000030, "EMAIL_SUBJECT");
            TEXT_VARIATIONS.put(0x000000b0, "FILTER");
            TEXT_VARIATIONS.put(0x00000050, "LONG_MESSAGE");
            TEXT_VARIATIONS.put(0x00000080, "PASSWORD");
            TEXT_VARIATIONS.put(0x00000060, "PERSON_NAME");
            TEXT_VARIATIONS.put(0x000000c0, "PHONETIC");
            TEXT_VARIATIONS.put(0x00000070, "POSTAL_ADDRESS");
            TEXT_VARIATIONS.put(0x00000040, "SHORT_MESSAGE");
            TEXT_VARIATIONS.put(0x00000010, "URI");
            TEXT_VARIATIONS.put(0x00000090, "VISIBLE_PASSWORD");
            TEXT_VARIATIONS.put(0x000000a0, "WEB_EDIT_TEXT");
            TEXT_VARIATIONS.put(0x000000d0, "WEB_EMAIL_ADDRESS");
            TEXT_VARIATIONS.put(0x000000e0, "WEB_PASSWORD");
        }

        private static void addBit(StringBuilder buf, int bit, String str) {
            if (bit != 0) {
                buf.append("|");
                buf.append(str);
            }
        }

        private static String inputTypeDesc(int type) {
            int cls = type & 0x0000000f; // MASK_CLASS
            int flags = type & 0x00fff000; // MASK_FLAGS
            int var = type & 0x00000ff0; // MASK_VARIATION

            StringBuilder out = new StringBuilder();
            String clsName = INPUT_CLASSES.get(cls);
            out.append(clsName != null ? clsName : "?");

            if (cls == InputType.TYPE_CLASS_TEXT) {
                String varName = TEXT_VARIATIONS.get(var);
                if (varName != null) {
                    out.append(".");
                    out.append(varName);
                }
                addBit(out, flags & 0x00010000, "AUTO_COMPLETE");
                addBit(out, flags & 0x00008000, "AUTO_CORRECT");
                addBit(out, flags & 0x00001000, "CAP_CHARACTERS");
                addBit(out, flags & 0x00004000, "CAP_SENTENCES");
                addBit(out, flags & 0x00002000, "CAP_WORDS");
                addBit(out, flags & 0x00040000, "IME_MULTI_LINE");
                addBit(out, flags & 0x00020000, "MULTI_LINE");
                addBit(out, flags & 0x00080000, "NO_SUGGESTIONS");
            } else if (cls == InputType.TYPE_CLASS_NUMBER) {
                String varName = NUMBER_VARIATIONS.get(var);
                if (varName != null) {
                    out.append(".");
                    out.append(varName);
                }
                addBit(out, flags & 0x00002000, "DECIMAL");
                addBit(out, flags & 0x00001000, "SIGNED");
            } else if (cls == InputType.TYPE_CLASS_DATETIME) {
                String varName = DATETIME_VARIATIONS.get(var);
                if (varName != null) {
                    out.append(".");
                    out.append(varName);
                }
            }
            return out.toString();
        }
    }
}
