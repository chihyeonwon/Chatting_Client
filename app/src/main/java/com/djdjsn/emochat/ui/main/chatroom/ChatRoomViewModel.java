package com.djdjsn.emochat.ui.main.chatroom;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.djdjsn.emochat.data.PreferencesData;
import com.djdjsn.emochat.data.chat.Chat;
import com.djdjsn.emochat.data.chat.ChatRepository;
import com.djdjsn.emochat.data.emoji.Emoji;
import com.djdjsn.emochat.data.emoji.EmojiRepository;
import com.djdjsn.emochat.data.message.Message;
import com.djdjsn.emochat.data.message.MessageRepository;
import com.djdjsn.emochat.data.messagenotice.MessageNotice;
import com.djdjsn.emochat.data.messagenotice.MessageNoticeRepository;
import com.djdjsn.emochat.data.user.User;
import com.djdjsn.emochat.data.user.UserRepository;
import com.djdjsn.emochat.utils.StorageUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChatRoomViewModel extends ViewModel {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private String currentMessage = "";
    private final MutableLiveData<Emoji> selectedEmoji = new MutableLiveData<>();

    private final String chatId;
    private final String currentUid;
    private final LiveData<Chat> chat;
    private final LiveData<List<Message>> messages;

    private final LiveData<User> currentUser;
    private final LiveData<User> counterpartUser;

    private final LiveData<List<Emoji>> emojis;

    private final MessageRepository messageRepository;
    private final MessageNoticeRepository messageNoticeRepository;
    private final PreferencesData preferencesData;


    @Inject
    public ChatRoomViewModel(SavedStateHandle savedStateHandle,
                             PreferencesData preferencesData,
                             ChatRepository chatRepository,
                             MessageRepository messageRepository,
                             MessageNoticeRepository messageNoticeRepository,
                             UserRepository userRepository,
                             EmojiRepository emojiRepository) {

        this.messageRepository = messageRepository;
        this.messageNoticeRepository = messageNoticeRepository;
        this.preferencesData = preferencesData;

        chatId = savedStateHandle.get("chat_id");
        currentUid = preferencesData.getCurrentUid();

        chat = chatRepository.getChat(chatId);
        messages = messageRepository.getMessagesIn(chatId);
        currentUser = userRepository.getUser(currentUid);
        counterpartUser = Transformations.switchMap(chat, chat -> {
            if (chat != null) {
                return userRepository.getUser(currentUid.equals(chat.getUid1()) ? chat.getUid2() : chat.getUid1());
            }
            return new MutableLiveData<>(null);
        });

        emojis = emojiRepository.getEmojis();
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }


    public String getCurrentUid() {
        return currentUid;
    }

    public LiveData<List<Message>> getMessages() {
        return messages;
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<User> getCounterpartUser() {
        return counterpartUser;
    }

    public LiveData<List<Emoji>> getEmojis() {
        return emojis;
    }

    public LiveData<Emoji> getSelectedEmoji() {
        return selectedEmoji;
    }


    public void onCurrentMessageChanged(String value) {
        currentMessage = value.trim();
    }


    public void onResumeChat() {
        preferencesData.setIsInChat(true);
    }

    public void onPauseChat() {
        preferencesData.setIsInChat(false);
    }

    public void onTick() {
        updateMessagesAsRead();
        //deleteMessageNotices();
    }

    private void updateMessagesAsRead() {
        // 상대의 메세지를 읽음 처리한다
        User counterpart = counterpartUser.getValue();
        if (counterpart != null) {
            messageRepository.updateMessagesAsRead(chatId, counterpart.getUid());
        }
    }

    private void deleteMessageNotices() {
        messageNoticeRepository.deleteNoticesOf(currentUid);
    }

    public void onSubmitClick() {

        if (currentMessage.isEmpty() && selectedEmoji.getValue() == null) {
            return;
        }

        User counterpart = counterpartUser.getValue();
        if (counterpart == null) {
            return;
        }

        String emojiUrl = null;
        if (selectedEmoji.getValue() != null) {
            String emojiFileName = selectedEmoji.getValue().getFileName();
            emojiUrl = StorageUtils.getImageUrl("emojis", emojiFileName);
        }

        Message message = new Message(chatId, currentUid, emojiUrl, currentMessage);
        messageRepository.addMessage(message,
                unused -> {
                    event.setValue(new Event.ClearMessageInput());
                    selectedEmoji.setValue(null);
                    messageNoticeRepository.addNotice(
                            new MessageNotice(message.getId(), chatId, currentUid, counterpart.getUid()));
                },
                e -> {
                    e.printStackTrace();
                    event.setValue(new Event.ShowGeneralMessage("메세지를 보내지 못했습니다"));
                });
    }

    public void onEmojiClick(Emoji emoji) {
        Emoji emojiValue = selectedEmoji.getValue();

        if (emojiValue != null && emojiValue.getFileName().equals(emoji.getFileName())) {
            selectedEmoji.setValue(null);
        } else {
            selectedEmoji.setValue(emoji);
        }
    }

    public static class Event {
        public static class ShowGeneralMessage extends Event {
            public final String message;
            public ShowGeneralMessage(String message) {
                this.message = message;
            }
        }

        public static class ClearMessageInput extends Event {
        }
    }

}









