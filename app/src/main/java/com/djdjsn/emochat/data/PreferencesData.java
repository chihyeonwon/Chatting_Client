package com.djdjsn.emochat.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.lifecycle.LiveData;

import com.djdjsn.emochat.utils.preferences.SharedPreferenceStringLiveData;

import javax.inject.Inject;

public class PreferencesData {
    // 프로그래머가 Shared Preferences (프레퍼런스) 에 정보를 입력하고 조회할 수 있는 클래스

    public static final String KEY_CURRENT_UID = "current_uid";
    public static final String KEY_IS_IN_CHAT = "is_in_chat";

    private final SharedPreferences preferences;        // Shared Preferences 객체
    private final LiveData<String> currentUid;           // 현재 로그인 된 계정의 아이디(uid) 값


    public PreferencesData(Application application) {

        preferences = PreferenceManager.getDefaultSharedPreferences(application);
        
        // Shared Preferences 의 값을 currentUid 에 연동한다
        currentUid = new SharedPreferenceStringLiveData(preferences, KEY_CURRENT_UID, null);
    }

    public LiveData<String> getCurrentUidLive() {
        // 현재 로그인 된 계정의 아이디 값을 불러온다
        return currentUid;
    }

    public String getCurrentUid() {
        // 현재 로그인 된 계정의 아이디 값을 불러온다
        return preferences.getString(KEY_CURRENT_UID, null);
    }

    public void setCurrentUid(String uid) {
        // 현재 로그인 된 계정의 아이디 값을 설정한다
        preferences.edit().putString(KEY_CURRENT_UID, uid).apply();
    }


    public boolean isInChat() {
        return preferences.getBoolean(KEY_IS_IN_CHAT, false);
    }

    public void setIsInChat(boolean value) {
        preferences.edit().putBoolean(KEY_IS_IN_CHAT, value).apply();
    }


}





