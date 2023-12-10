package com.djdjsn.emochat.ui.main.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.djdjsn.emochat.data.PreferencesData;
import com.djdjsn.emochat.data.chat.Chat;
import com.djdjsn.emochat.data.chat.ChatRepository;
import com.djdjsn.emochat.data.userrelation.UserRelation;
import com.djdjsn.emochat.data.userrelation.UserRelationsRepository;
import com.djdjsn.emochat.data.user.User;
import com.djdjsn.emochat.data.user.UserRepository;
import com.djdjsn.emochat.utils.ListUtils;
import com.djdjsn.emochat.utils.res.StringRes;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final String uid;

    private final LiveData<User> currentUser;

    private final LiveData<List<User>> friends;


    private final UserRelationsRepository userRelationsRepository;
    private final ChatRepository chatRepository;


    @Inject
    public HomeViewModel(PreferencesData preferencesData,
                         UserRepository userRepository,
                         UserRelationsRepository userRelationsRepository,
                         ChatRepository chatRepository) {

        this.userRelationsRepository = userRelationsRepository;
        this.chatRepository = chatRepository;

        uid = preferencesData.getCurrentUid();

        currentUser = userRepository.getUser(uid);

        LiveData<List<UserRelation>> relations = userRelationsRepository.getRelations(uid);
        LiveData<Map<String, User>> friendMap = Transformations.switchMap(relations, _relations -> {
           if (_relations != null) {
               List<String> others = ListUtils.map(_relations, UserRelation::getOther);
               return userRepository.getUserMap(others);
           }
           return new MutableLiveData<>(null);
        });
        friends = Transformations.map(friendMap, map -> {
            return map != null ? new ArrayList<>(map.values()) : null;
        });
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<List<User>> getFriends() {
        return friends;
    }


    public void onBackClick() {
        event.setValue(new Event.ConfirmSignOut());
    }

    public void onSignOutConfirmed() {
        FirebaseAuth.getInstance().signOut();
    }

    public void onSearchClick() {
        event.setValue(new Event.NavigateToSearchScreen());
    }

    public void onSearchResult(User newUser) {
        event.setValue(new Event.ShowGeneralMessage(StringRes.newFriendAdded(newUser.getNickname())));
    }
    
    public void onChatClick(User other) {
        chatRepository.getChatBetween(uid, other.getUid(),
                chat -> {
                    if (chat != null) {
                        event.setValue(new Event.NavigateToChatRoom(chat));
                    } else {
                        Chat newChat = new Chat(uid, other.getUid());
                        chatRepository.addChat(newChat,
                                unused -> event.setValue(new Event.NavigateToChatRoom(newChat)),
                                e -> {
                                    e.printStackTrace();
                                    event.setValue(new Event.ShowGeneralMessage("채팅방을 만들지 못했습니다"));
                                });
                    }
                },
                e -> {
                    e.printStackTrace();
                    event.setValue(new Event.ShowGeneralMessage("채팅방에 입장하지 못했습니다"));
                });
    }
    
    public void onDeleteFriendClick(User user) {
        event.setValue(new Event.ConfirmDeleteFriend(user));
    }

    public void onDeleteFriendConfirmed(User user) {
        userRelationsRepository.deleteRelation(uid, user.getUid(),
                unused -> event.setValue(new Event.ShowGeneralMessage(StringRes.friendDeleted(user.getNickname()))),
                e -> {
                    e.printStackTrace();
                    event.setValue(new Event.ShowGeneralMessage("친구 삭제에 실패했습니다"));
                });
    }


    public static class Event {
        public static class ShowGeneralMessage extends Event {
            public final String message;
            public ShowGeneralMessage(String message) {
                this.message = message;
            }
        }
        public static class NavigateToSearchScreen extends Event {
        }

        public static class ConfirmSignOut extends Event {
        }

        public static class ConfirmDeleteFriend extends Event {
            public final User user;
            public ConfirmDeleteFriend(User user) {
                this.user = user;
            }
        }

        public static class NavigateToChatRoom extends Event {
            public final Chat chat;
            public NavigateToChatRoom(Chat chat) {
                this.chat = chat;
            }
        }
    }

}






