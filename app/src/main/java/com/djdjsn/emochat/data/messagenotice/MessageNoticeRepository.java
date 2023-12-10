package com.djdjsn.emochat.data.messagenotice;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import javax.inject.Inject;

public class MessageNoticeRepository {

    private final CollectionReference messageNoticeCollection;

    @Inject
    public MessageNoticeRepository(FirebaseFirestore firestore) {
        messageNoticeCollection = firestore.collection("message_notices");
    }

    public void addNotice(MessageNotice messageNotice) {
        messageNoticeCollection.document(messageNotice.getMessageId()).set(messageNotice);
    }

    public void getRecentNoticeOf(String uid,
                                  OnSuccessListener<MessageNotice> onSuccessListener,
                                  OnFailureListener onFailureListener) {

        messageNoticeCollection.whereEqualTo("receiverUid", uid)
                .orderBy("created", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                        MessageNotice messageNotice = snapshot.toObject(MessageNotice.class);
                        if (messageNotice != null) {
                            onSuccessListener.onSuccess(messageNotice);
                        } else {
                            onFailureListener.onFailure(new Exception("cannot deserialize a message notice"));
                        }
                    } else {
                        onSuccessListener.onSuccess(null);
                    }
                })
                .addOnFailureListener(onFailureListener);
    }

    public void deleteNoticesOf(String uid) {

        messageNoticeCollection.whereEqualTo("receiverUid", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        messageNoticeCollection.document(snapshot.getId()).delete();
                    }
                });
    }

}
