package com.djdjsn.emochat.utils.preferences;

import android.content.SharedPreferences;

public class SharedPreferencesBooleanLiveData extends SharedPreferenceLiveData<Boolean> {
    // Shared Preferences 에 저장된 논리형 값과 연동할 수 있는 LiveData 클래스
    
    public SharedPreferencesBooleanLiveData(SharedPreferences preferences, String key, Boolean defValue) {
        super(preferences, key, defValue);
    }

    @Override
    Boolean getValueFromPreferences(String key, Boolean defValue) {
        return sharedPrefs.getBoolean(key, defValue);
    }
}
