package com.djdjsn.emochat.ui.main.chatlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.djdjsn.emochat.data.PreferencesData;
import com.djdjsn.emochat.data.chat.Chat;
import com.djdjsn.emochat.data.chat.ChatPreview;
import com.djdjsn.emochat.data.chat.ChatRepository;
import com.djdjsn.emochat.data.chatannotation.ChatAnnotation;
import com.djdjsn.emochat.data.chatannotation.ChatAnnotationRepository;
import com.djdjsn.emochat.data.user.User;
import com.djdjsn.emochat.data.user.UserRepository;
import com.djdjsn.emochat.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChatListViewModel extends ViewModel {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final LiveData<List<ChatPreview>> chatPreviews;


    @Inject
    public ChatListViewModel(PreferencesData preferencesData,
                             ChatRepository chatRepository,
                             ChatAnnotationRepository chatAnnotationRepository,
                             UserRepository userRepository) {

        String uid = preferencesData.getCurrentUid();

        LiveData<List<Chat>> chats = chatRepository.getChatsOf(uid);

        LiveData<List<ChatAnnotation>> chatAnnotations = Transformations.switchMap(chats, _chats ->
                chatAnnotationRepository.getAnnotationList(ListUtils.map(_chats, Chat::getId)));

        LiveData<Map<String, User>> recentUserMap = Transformations.switchMap(chatAnnotations, _chatAnnotations ->
                userRepository.getUserMap(ListUtils.map(_chatAnnotations, ChatAnnotation::getRecentUserUid)));

        chatPreviews = Transformations.switchMap(chatAnnotations, _chatAnnotations ->
                Transformations.map(recentUserMap, _recentUserMap -> {
                    List<ChatPreview> _chatPreviews = new ArrayList<>();
                    for (int i = 0; i < _chatAnnotations.size(); i++) {
                        ChatAnnotation chatAnnotation = _chatAnnotations.get(i);
                        User recentUser = _recentUserMap.get(chatAnnotation.getRecentUserUid());
                        if (recentUser != null) {
                            ChatPreview chatPreview = new ChatPreview(
                                    chatAnnotation.getChatId(),
                                    recentUser,
                                    chatAnnotation.getRecentMessageContent(),
                                    chatAnnotation.getRecentMessageMillis());
                            _chatPreviews.add(chatPreview);
                        }
                    }
                    return _chatPreviews;
                }));
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }


    public LiveData<List<ChatPreview>> getChatPreviews() {
        return chatPreviews;
    }


    public void onChatClick(ChatPreview chatPreview) {
        event.setValue(new Event.NavigateToChatScreen(chatPreview.getChatId()));
    }


    public static class Event {

        public static class NavigateToChatScreen extends Event {
            public final String chatId;

            public NavigateToChatScreen(String chatId) {
                this.chatId = chatId;
            }
        }
    }

}







