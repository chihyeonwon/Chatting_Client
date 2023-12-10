package com.djdjsn.emochat.data.userrelation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import javax.inject.Inject;

public class UserRelationsRepository {

    private final CollectionReference relationCollection;

    @Inject
    public UserRelationsRepository(FirebaseFirestore firestore) {

        relationCollection = firestore.collection("user_relations");
    }

    public void hasRelationBetween(String uid, String other,
                            OnSuccessListener<Boolean> onSuccessListener,
                            OnFailureListener onFailureListener) {

        relationCollection.document(String.format("%s-%s", uid, other)).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot == null) {
                        onSuccessListener.onSuccess(false);
                        return;
                    }
                    UserRelation userRelation = snapshot.toObject(UserRelation.class);
                    onSuccessListener.onSuccess(userRelation != null);
                })
                .addOnFailureListener(onFailureListener);
    }

    public void addRelation(String uid, String other,
                            OnSuccessListener<Void> onSuccessListener,
                            OnFailureListener onFailureListener) {

        relationCollection.document(String.format("%s-%s", uid, other))
                .set(new UserRelation(uid, other))
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void deleteRelation(String uid, String other,
                               OnSuccessListener<Void> onSuccessListener,
                               OnFailureListener onFailureListener) {

        relationCollection.document(String.format("%s-%s", uid, other))
                .delete()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public LiveData<List<UserRelation>> getRelations(String uid) {

        MutableLiveData<List<UserRelation>> data = new MutableLiveData<>();
        if (uid == null) {
            data.setValue(null);
            return data;
        }

        relationCollection
                .whereEqualTo("uid", uid)
                .addSnapshotListener((values, error) -> {
                    if (error != null) {
                        data.setValue(null);
                        return;
                    }
                    if (values == null) {
                        data.setValue(null);
                        return;
                    }
                    data.setValue(values.toObjects(UserRelation.class));
                });

        return data;
    }

}









