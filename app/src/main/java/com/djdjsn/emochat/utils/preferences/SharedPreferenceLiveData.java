package com.djdjsn.emochat.utils.preferences;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;

abstract class SharedPreferenceLiveData<T> extends LiveData<T> {
    // Shared Preferences 에 저장된 값과 연동할 수 있는 LiveData 클래스

    protected final SharedPreferences sharedPrefs;
    protected final String key;
    protected final T defValue;

    public SharedPreferenceLiveData(SharedPreferences preferences, String key, T defValue) {
        this.sharedPrefs = preferences;
        this.key = key;
        this.defValue = defValue;
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (SharedPreferenceLiveData.this.key.equals(key)) {
                setValue(getValueFromPreferences(key, defValue));
            }
        }
    };

    abstract T getValueFromPreferences(String key, T defValue);

    @Override
    protected void onActive() {
        super.onActive();
        setValue(getValueFromPreferences(key, defValue));
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    protected void onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        super.onInactive();
    }

}
