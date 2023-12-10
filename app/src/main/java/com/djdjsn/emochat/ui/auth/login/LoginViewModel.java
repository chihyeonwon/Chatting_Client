package com.djdjsn.emochat.ui.auth.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.djdjsn.emochat.data.PreferencesData;
import com.djdjsn.emochat.data.user.User;
import com.djdjsn.emochat.data.user.UserRepository;
import com.djdjsn.emochat.utils.AuthUtils;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginViewModel extends ViewModel {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private String id = "";                         // 입력된 유저 아이디 값
    private String password = "";                       // 입력된 비밀번호 값

    private final LiveData<String> currentUid;          // 현재 로그인 된 계정의 uid

    private User userData;

    private final FirebaseAuth firebaseAuth;
    private final PreferencesData preferencesData;
    private final UserRepository userRepository;


    @Inject
    public LoginViewModel(FirebaseAuth firebaseAuth, PreferencesData preferencesData, UserRepository userRepository) {
        this.firebaseAuth = firebaseAuth;
        this.preferencesData = preferencesData;
        this.userRepository = userRepository;

        currentUid = preferencesData.getCurrentUidLive();
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<String> getCurrentUid() {
        return currentUid;
    }


    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        String uid = firebaseAuth.getUid();
        if (uid != null) {
            if (userData != null && userData.getUid().equals(uid)) {
                userRepository.addUser(userData,
                        unused -> preferencesData.setCurrentUid(uid),
                        e -> {
                            e.printStackTrace();
                            event.setValue(new Event.ShowGeneralMessage("회원정보 생성에 실패했습니다"));
                        });
            } else {
                preferencesData.setCurrentUid(uid);
            }
        }
    }

    public void onIdChanged(String value) {
        // 아이디 입력값이 변경되었을 때
        id = value.trim();
    }

    public void onPasswordChanged(String value) {
        // 비밀번호 입력값이 변경되었을 때
        password = value;
    }

    public void onLoginClicked() {

        // 로그인을 요청했을 때 : 로그인 시도하기

        if (id.length() < 4) {
            // 에러 : 짧은 아이디
            event.setValue(new Event.ShowGeneralMessage("아이디를 4글자 이상 입력해주세요"));
            return;
        }
        if (password.length() < 6) {
            // 에러 : 짧은 패스워드
            event.setValue(new Event.ShowGeneralMessage("비밀번호를 6글자 이상 입력해주세요"));
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(AuthUtils.emailize(id), password)
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    event.setValue(new Event.ShowGeneralMessage("회원정보를 확인해주세요"));
                });
    }

    public void onRegisterClicked() {
        event.setValue(new Event.NavigateToRegisterScreen());
    }

    public void onRegisterResult(String id, String password, User userData) {

        event.setValue(new Event.ShowGeneralMessage("회원가입이 완료되었습니다"));

        this.userData = userData;

        firebaseAuth.signInWithEmailAndPassword(AuthUtils.emailize(id), password)
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    event.setValue(new Event.ShowGeneralMessage("회원정보를 확인해주세요"));
                });
    }


    public static class Event {

        public static class ShowGeneralMessage extends Event {
            public final String message;

            public ShowGeneralMessage(String message) {
                this.message = message;
            }
        }

        public static class NavigateToRegisterScreen extends Event {
        }
    }

}