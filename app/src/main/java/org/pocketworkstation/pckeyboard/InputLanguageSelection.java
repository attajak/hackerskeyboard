package org.pocketworkstation.pckeyboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class InputLanguageSelection extends BaseSettingsActivity {

    // Languages for which auto-caps should be disabled
    public static final Set<String> NOCAPS_LANGUAGES = new HashSet<>();
    static {
        NOCAPS_LANGUAGES.add("ar");
        NOCAPS_LANGUAGES.add("iw");
        NOCAPS_LANGUAGES.add("th");
    }

    // Languages which should not use dead key logic.
    public static final Set<String> NODEADKEY_LANGUAGES = new HashSet<>();
    static {
        NODEADKEY_LANGUAGES.add("ar");
        NODEADKEY_LANGUAGES.add("iw");
        NODEADKEY_LANGUAGES.add("th");
    }

    // Languages which should not auto-add space after completions
    public static final Set<String> NOAUTOSPACE_LANGUAGES = new HashSet<>();
    static {
        NOAUTOSPACE_LANGUAGES.add("th");
    }

    @Override
    protected PreferenceFragmentCompat createSettingsFragment() {
        return new InputLanguageSelectionFragment();
    }

    public static class InputLanguageSelectionFragment extends BaseSettingsFragment {

        private static final String TAG = "PCKeyboardILS";
        private ArrayList<Loc> mAvailableLanguages = new ArrayList<>();
        private static final String[] BLACKLIST_LANGUAGES = {"ko", "ja", "zh"};

        private static final String[] KBD_LOCALIZATIONS = {
            "ar", "bg", "bg_ST", "ca", "cs", "cs_QY", "da", "de", "de_NE",
            "el", "en", "en_CX", "en_DV", "en_GB", "es", "es_LA", "es_US",
            "fa", "fi", "fr", "fr_CA", "he", "hr", "hu", "hu_QY", "hy", "in",
            "it", "iw", "ja", "ka", "ko", "lo", "lt", "lv", "nb", "nl", "pl",
            "pt", "pt_PT", "rm", "ro", "ru", "ru_PH", "si", "sk", "sk_QY", "sl",
            "sr", "sv", "ta", "th", "tl", "tr", "uk", "vi", "zh_CN", "zh_TW"
        };

        private static final String[] KBD_5_ROW = {
            "ar", "bg", "bg_ST", "cs", "cs_QY", "da", "de", "de_NE", "el",
            "en", "en_CX", "en_DV", "es", "es_LA", "fa", "fi", "fr",
            "fr_CA", "he", "hr", "hu", "hu_QY", "hy", "it", "iw", "lo", "lt",
            "nb", "pt_PT", "ro", "ru", "ru_PH", "si", "sk", "sk_QY", "sl",
            "sr", "sv", "ta", "th", "tr", "uk"
        };

        private static final String[] KBD_4_ROW = {
            "ar", "bg", "bg_ST", "cs", "cs_QY", "da", "de", "de_NE", "el",
            "en", "en_CX", "en_DV", "es", "es_LA", "es_US", "fa", "fr", "fr_CA",
            "he", "hr", "hu", "hu_QY", "iw", "nb", "ru", "ru_PH", "sk", "sk_QY",
            "sl", "sr", "sv", "tr", "uk"
        };

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferenceScreen(getPreferenceManager().createPreferenceScreen(requireContext()));
            
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
            String selectedLanguagePref = sp.getString(LatinIME.PREF_SELECTED_LANGUAGES, "");
            String[] languageList = selectedLanguagePref.split(",");

            mAvailableLanguages = getUniqueLocales();

            Set<String> availableLanguages = new HashSet<>();
            for (Loc loc : mAvailableLanguages) {
                availableLanguages.add(get5Code(loc.locale));
            }
            Set<String> languageSelections = new HashSet<>();
            for (String spec : languageList) {
                if (availableLanguages.contains(spec)) {
                    languageSelections.add(spec);
                } else if (spec.length() > 2) {
                    String lang = spec.substring(0, 2);
                    if (availableLanguages.contains(lang)) languageSelections.add(lang);
                }
            }

            PreferenceCategory parent = new PreferenceCategory(requireContext());
            parent.setTitle(R.string.language_selection_title);
            getPreferenceScreen().addPreference(parent);

            for (Loc loc : mAvailableLanguages) {
                CheckBoxPreference pref = new CheckBoxPreference(requireContext());
                pref.setKey(get5Code(loc.locale));
                pref.setTitle(loc.label + " [" + loc.locale.toString() + "]");
                pref.setChecked(languageSelections.contains(get5Code(loc.locale)));

                String fivecode = get5Code(loc.locale);
                String language = loc.locale.getLanguage();
                boolean has4Row = arrayContains(KBD_4_ROW, fivecode) || arrayContains(KBD_4_ROW, language);
                boolean has5Row = arrayContains(KBD_5_ROW, fivecode) || arrayContains(KBD_5_ROW, language);
                
                List<String> summaries = new ArrayList<>();
                if (has5Row) summaries.add("5-row");
                if (has4Row) summaries.add("4-row");
                
                if (!summaries.isEmpty()) {
                    pref.setSummary(TextUtils.join(", ", summaries));
                }
                parent.addPreference(pref);
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            String checkedLanguages = "";
            for (Loc loc : mAvailableLanguages) {
                CheckBoxPreference pref = findPreference(get5Code(loc.locale));
                if (pref != null && pref.isChecked()) {
                    checkedLanguages += get5Code(loc.locale) + ",";
                }
            }
            if (checkedLanguages.length() < 1) checkedLanguages = null;
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
            sp.edit().putString(LatinIME.PREF_SELECTED_LANGUAGES, checkedLanguages).apply();
        }

        private String get5Code(Locale locale) {
            String country = locale.getCountry();
            return locale.getLanguage() + (TextUtils.isEmpty(country) ? "" : "_" + country);
        }

        private boolean arrayContains(String[] array, String value) {
            for (String s : array) {
                if (s.equalsIgnoreCase(value)) return true;
            }
            return false;
        }

        private ArrayList<Loc> getUniqueLocales() {
            Set<String> localeSet = new HashSet<>();
            Set<String> langSet = new HashSet<>();
            for (String kl : KBD_LOCALIZATIONS) {
                if (kl.length() == 2 && langSet.contains(kl)) continue;
                if (kl.length() == 6) kl = kl.substring(0, 2) + "_" + kl.substring(4, 6);
                localeSet.add(kl);
            }
            
            ArrayList<Loc> uniqueLocales = new ArrayList<>();
            for (String s : localeSet) {
                int len = s.length();
                if (len == 2 || len == 5 || len == 6) {
                    String language = s.substring(0, 2);
                    Locale l = (len == 5) ? new Locale(language, s.substring(3, 5)) : 
                               (len == 6) ? new Locale(language, s.substring(4, 6)) : new Locale(language);
                    if (arrayContains(BLACKLIST_LANGUAGES, language)) continue;
                    uniqueLocales.add(new Loc(LanguageSwitcher.toTitleCase(l.getDisplayName(l)), l));
                }
            }
            return uniqueLocales;
        }
    }

    private static class Loc {
        String label;
        Locale locale;
        public Loc(String label, Locale locale) {
            this.label = label;
            this.locale = locale;
        }
    }
}
