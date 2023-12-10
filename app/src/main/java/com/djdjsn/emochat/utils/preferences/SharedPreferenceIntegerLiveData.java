package com.djdjsn.emochat.utils.preferences;

import android.content.SharedPreferences;

public class SharedPreferenceIntegerLiveData extends SharedPreferenceLiveData<Integer> {
    // Shared Preferences 에 저장된 정수형 값과 연동할 수 있는 LiveData 클래스
    
    public SharedPreferenceIntegerLiveData(SharedPreferences preferences, String key, Integer defValue) {
        super(preferences, key, defValue);
    }

    @Override
    Integer getValueFromPreferences(String key, Integer defValue) {
        return sharedPrefs.getInt(key, defValue);
    }
}