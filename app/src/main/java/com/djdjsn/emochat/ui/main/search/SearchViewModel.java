package com.djdjsn.emochat.ui.main.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.djdjsn.emochat.data.PreferencesData;
import com.djdjsn.emochat.data.userrelation.UserRelationsRepository;
import com.djdjsn.emochat.data.user.User;
import com.djdjsn.emochat.data.user.UserRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SearchViewModel extends ViewModel {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final String uid;
    private final MutableLiveData<String> query = new MutableLiveData<>("");
    private final LiveData<List<User>> usersQueried;

    private final UserRelationsRepository userRelationsRepository;


    @Inject
    public SearchViewModel(PreferencesData preferencesData,
                           UserRelationsRepository userRelationsRepository,
                           UserRepository userRepository) {

        this.userRelationsRepository = userRelationsRepository;

        uid = preferencesData.getCurrentUid();

        LiveData<List<User>> queried = Transformations.switchMap(query, userRepository::getUserByPhone);
        usersQueried = Transformations.map(queried, q -> {
            if (q != null) {
                q.removeIf(user -> user.getUid().equals(uid));
                return q;
            }
            return null;
        });
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<List<User>> getUsersQueried() {
        return usersQueried;
    }


    public void onQueryChanged(String value) {
        query.setValue(value.trim());
        event.setValue(new Event.ShowLoadingUI());
    }

    public void onUserClicked(User user) {
        event.setValue(new Event.ShowLoadingUI());
        userRelationsRepository.hasRelationBetween(uid, user.getUid(),
                hasRelation -> {
                    if (!hasRelation) {
                        userRelationsRepository.addRelation(uid, user.getUid(),
                                unused -> event.setValue(new Event.NavigateBackWithResult(user)),
                                e -> {
                                    e.printStackTrace();
                                    event.setValue(new Event.ShowGeneralMessageAndHideLoadingUI("친구 추가에 실패했습니다"));
                                });
                    } else {
                        event.setValue(new Event.ShowGeneralMessageAndHideLoadingUI("이미 친구인 회원입니다"));
                    }
                },
                e -> {
                    e.printStackTrace();
                    event.setValue(new Event.ShowGeneralMessageAndHideLoadingUI("친구 추가에 실패했습니다"));
                });
    }


    public static class Event {
        public static class ShowLoadingUI extends Event {
        }

        public static class NavigateBackWithResult extends Event {
            User user;

            public NavigateBackWithResult(User user) {
                this.user = user;
            }
        }

        public static class ShowGeneralMessage extends Event {
            public final String message;

            public ShowGeneralMessage(String message) {
                this.message = message;
            }
        }

        public static class ShowGeneralMessageAndHideLoadingUI extends Event {
            public final String message;

            public ShowGeneralMessageAndHideLoadingUI(String message) {
                this.message = message;
            }
        }
    }

}






