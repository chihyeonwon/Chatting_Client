package com.djdjsn.emochat.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class StorageUtils {

    public static final String URL_FORMAT = "https://firebasestorage.googleapis.com/v0/b/emochat-925c0.appspot.com/o/{FOLDER_NAME}%2F{FILE_NAME}?alt=media";
    public static final String ARG_FOLDER_NAME = "{FOLDER_NAME}";
    public static final String ARG_FILE_NAME = "{FILE_NAME}";
    public static final int MEGABYTES = 1048576;


    public static String getImageUrl(String folderName, String fileName) {
        return URL_FORMAT.replace(ARG_FOLDER_NAME, folderName).replace(ARG_FILE_NAME, fileName);
    }

    public static void uploadImage(StorageReference folder, String fileName, Bitmap image,
                                   OnSuccessListener<Void> onSuccessListener,
                                   OnFailureListener onFailureListener) {

        // 이미지를 스토리지(=이미지 폴더) 에 업로드한다
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        folder.child(fileName)
                .putBytes(data)
                .addOnSuccessListener(runnable -> onSuccessListener.onSuccess(null))
                .addOnFailureListener(onFailureListener);
    }

    public static void downloadImage(StorageReference folder, String imageName,
                                     OnSuccessListener<Bitmap> onSuccessListener,
                                     OnFailureListener onFailureListener) {

        //  이미지를 스토리지에서 불러온다
        folder.child(imageName + ".jpg")
                .getBytes(10 * MEGABYTES)
                .addOnSuccessListener(bytes -> {
                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (image != null) {
                        onSuccessListener.onSuccess(image);
                    } else {
                        onFailureListener.onFailure(new Exception("Bitmap is null."));
                    }
                })
                .addOnFailureListener(onFailureListener);
    }

}
