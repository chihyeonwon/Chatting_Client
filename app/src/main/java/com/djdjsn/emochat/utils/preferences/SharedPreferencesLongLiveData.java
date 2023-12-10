package com.djdjsn.emochat.utils.preferences;

import android.content.SharedPreferences;

public class SharedPreferencesLongLiveData extends SharedPreferenceLiveData<Long> {
    // Shared Preferences 에 저장된 Long 형 값과 연동할 수 있는 LiveData 클래스

    public SharedPreferencesLongLiveData(SharedPreferences preferences, String key, Long defValue) {
        super(preferences, key, defValue);
    }

    @Override
    Long getValueFromPreferences(String key, Long defValue) {
        return sharedPrefs.getLong(key, defValue);
    }
}