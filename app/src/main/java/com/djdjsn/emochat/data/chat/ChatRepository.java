package com.djdjsn.emochat.data.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.djdjsn.emochat.utils.ListUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class ChatRepository {

    private final CollectionReference chatCollection;

    @Inject
    public ChatRepository(FirebaseFirestore firestore) {
        chatCollection = firestore.collection("chats");
    }

    public void getChatBetween(String uid, String other,
                            OnSuccessListener<Chat> onSuccessListener,
                            OnFailureListener onFailureListener) {

        chatCollection.get()
                .addOnSuccessListener(snapshots -> {
                    if (snapshots == null) {
                        onFailureListener.onFailure(new Exception("snapshots is null"));
                        return;
                    }
                    List<Chat> chats = snapshots.toObjects(Chat.class);
                    for (Chat chat : chats) {
                        if (chat.getId().contains(uid) && chat.getId().contains(other)) {
                            onSuccessListener.onSuccess(chat);
                            return;
                        }
                    }
                    onSuccessListener.onSuccess(null);
                })
                .addOnFailureListener(onFailureListener);
    }

    public void addChat(Chat chat, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {

        chatCollection.document(chat.getId())
                .set(chat)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public LiveData<Chat> getChat(String chatId) {

        MutableLiveData<Chat> data = new MutableLiveData<>();
        if (chatId == null) {
            data.setValue(null);
            return data;
        }

        chatCollection.document(chatId).addSnapshotListener((value, error) -> {
            if (error != null) {
                error.printStackTrace();
                data.setValue(null);
                return;
            }
            if (value == null) {
                new Exception("value is null").printStackTrace();
                data.setValue(null);
                return;
            }
            data.setValue(value.toObject(Chat.class));
        });

        return data;
    }

    public LiveData<List<Chat>> getChats(List<String> chatIds) {

        MutableLiveData<List<Chat>> data = new MutableLiveData<>();

        chatCollection.addSnapshotListener((value, error) -> {
            if (error != null) {
                error.printStackTrace();
            }
            if (error != null || value == null) {
                data.setValue(null);
                return;
            }
            List<Chat> chats = new ArrayList<>();
            for (DocumentSnapshot snapshot : value) {
                Chat chat = snapshot.toObject(Chat.class);
                if (chat != null) {
                    if (chatIds != null && !chatIds.contains(chat.getId())) {
                        continue;
                    }
                    chats.add(chat);
                }
            }
            data.setValue(chats);
        });

        return data;
    }

    public LiveData<List<Chat>> getChatsOf(String uid) {

        MutableLiveData<List<Chat>> data = new MutableLiveData<>();
        if (uid == null) {
            data.setValue(null);
            return data;
        }

        chatCollection.addSnapshotListener((value, error) -> {
            if (error != null) {
                error.printStackTrace();
                data.setValue(null);
                return;
            }
            if (value == null) {
                data.setValue(null);
                return;
            }
            List<Chat> chats = ListUtils.filter(value.toObjects(Chat.class), chat -> chat.hasUser(uid));
            data.setValue(chats);
        });

        return data;
    }

}




















