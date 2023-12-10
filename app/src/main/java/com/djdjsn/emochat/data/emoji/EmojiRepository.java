package com.djdjsn.emochat.data.emoji;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class EmojiRepository {

    private final CollectionReference emojiCollection;

    @Inject
    public EmojiRepository(FirebaseFirestore firestore) {
        emojiCollection = firestore.collection("emojis");
    }

    public LiveData<List<Emoji>> getEmojis() {

        MutableLiveData<List<Emoji>> data = new MutableLiveData<>();

        emojiCollection.addSnapshotListener((value, error) -> {
            if (error != null) {
                error.printStackTrace();
                data.setValue(null);
                return;
            }
            List<Emoji> emojis = new ArrayList<>();
            if (value == null) {
                data.setValue(emojis);
                return;
            }
            data.setValue(value.toObjects(Emoji.class));
        });

        return data;
    }



}











