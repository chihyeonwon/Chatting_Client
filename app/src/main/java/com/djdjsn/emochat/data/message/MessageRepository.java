package com.djdjsn.emochat.data.message;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.djdjsn.emochat.data.chatannotation.ChatAnnotation;
import com.djdjsn.emochat.data.chatannotation.ChatAnnotationRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MessageRepository {

    private final CollectionReference messageCollection;
    private final ChatAnnotationRepository chatAnnotationRepository;

    @Inject
    public MessageRepository(FirebaseFirestore firestore, ChatAnnotationRepository chatAnnotationRepository) {
        messageCollection = firestore.collection("messages");
        this.chatAnnotationRepository = chatAnnotationRepository;
    }

    public void addMessage(Message message,
                           OnSuccessListener<Void> onSuccessListener,
                           OnFailureListener onFailureListener) {

        messageCollection.document(message.getId())
                .set(message)
                .addOnSuccessListener(unused -> {
                    ChatAnnotation preview = new ChatAnnotation(
                            message.getChatId(), message.getUid(),
                            message.getContent(), message.getCreated());
                    chatAnnotationRepository.addAnnotation(preview, onSuccessListener, e -> {
                        e.printStackTrace();
                        onSuccessListener.onSuccess(null);
                    });
                })
                .addOnFailureListener(onFailureListener);
    }

    public void updateMessagesAsRead(String chatId, String senderUid) {

        messageCollection.whereEqualTo("chatId", chatId)
                .whereEqualTo("uid", senderUid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        messageCollection.document(snapshot.getId()).update("read", true);
                    }
                });
    }

    public LiveData<List<Message>> getMessages(List<String> messageIds) {

        MutableLiveData<List<Message>> data = new MutableLiveData<>();

        messageCollection.addSnapshotListener((value, error) -> {
            if (error != null) {
                error.printStackTrace();
            }
            if (error != null || value == null) {
                data.setValue(null);
                return;
            }
            List<Message> messages = new ArrayList<>();
            for (DocumentSnapshot snapshot : value) {
                Message message = snapshot.toObject(Message.class);
                if (message != null) {
                    if (messageIds != null && !messageIds.contains(message.getId())) {
                        continue;
                    }
                    messages.add(message);
                }
            }
            data.setValue(messages);
        });

        return data;
    }

    public LiveData<List<Message>> getMessagesIn(String chatId) {

        MutableLiveData<List<Message>> data = new MutableLiveData<>();
        if (chatId == null) {
            data.setValue(null);
            return data;
        }

        messageCollection
                .whereEqualTo("chatId", chatId)
                .orderBy("created", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                        data.setValue(null);
                        return;
                    }
                    if (value == null) {
                        data.setValue(null);
                        return;
                    }
                    data.setValue(value.toObjects(Message.class));
                });

        return data;
    }

}

















