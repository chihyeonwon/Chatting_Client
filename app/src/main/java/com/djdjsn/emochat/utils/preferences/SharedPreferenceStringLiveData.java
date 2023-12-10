package com.djdjsn.emochat.utils.preferences;

import android.content.SharedPreferences;

public class SharedPreferenceStringLiveData extends SharedPreferenceLiveData<String> {
    // Shared Preferences 에 문자열 값과 연동할 수 있는 LiveData 클래스
    
    public SharedPreferenceStringLiveData(SharedPreferences preferences, String key, String defValue) {
        super(preferences, key, defValue);
    }

    @Override
    String getValueFromPreferences(String key, String defValue) {
        return sharedPrefs.getString(key, defValue);
    }
}

