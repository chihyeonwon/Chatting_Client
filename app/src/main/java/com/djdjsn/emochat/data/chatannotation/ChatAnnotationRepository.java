package com.djdjsn.emochat.data.chatannotation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ChatAnnotationRepository {

    private final CollectionReference annotationCollection;

    @Inject
    public ChatAnnotationRepository(FirebaseFirestore firestore) {

        annotationCollection = firestore.collection("chat_annotations");
    }

    public void addAnnotation(ChatAnnotation chatAnnotation,
                              OnSuccessListener<Void> onSuccessListener,
                              OnFailureListener onFailureListener) {

        annotationCollection.document(chatAnnotation.getChatId())
                .set(chatAnnotation)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public LiveData<ChatAnnotation> getAnnotation(String chatId) {

        MutableLiveData<ChatAnnotation> data = new MutableLiveData<>();
        if (chatId == null) {
            data.setValue(null);
            return data;
        }

        annotationCollection.document(chatId)
                .addSnapshotListener((values, error) -> {
                    if (error != null) {
                        data.setValue(null);
                        return;
                    }
                    if (values == null) {
                        data.setValue(null);
                        return;
                    }
                    data.setValue(values.toObject(ChatAnnotation.class));
                });

        return data;
    }

    public LiveData<List<ChatAnnotation>> getAnnotationList(List<String> chatIds) {

        MutableLiveData<List<ChatAnnotation>> data = new MutableLiveData<>();

        annotationCollection.addSnapshotListener((value, error) -> {
            if (error != null) {
                error.printStackTrace();
            }
            if (error != null || value == null) {
                data.setValue(null);
                return;
            }
            List<ChatAnnotation> annotations = new ArrayList<>();
            for (DocumentSnapshot snapshot : value) {
                ChatAnnotation annotation = snapshot.toObject(ChatAnnotation.class);
                if (annotation != null) {
                    if (chatIds != null && !chatIds.contains(annotation.getChatId())) {
                        continue;
                    }
                    annotations.add(annotation);
                }
            }
            data.setValue(annotations);
        });

        return data;
    }
}










