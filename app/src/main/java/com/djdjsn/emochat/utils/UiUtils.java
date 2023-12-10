package com.djdjsn.emochat.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.function.Consumer;

public class UiUtils {

    public static void setOnTextChangeListener(EditText editText, Consumer<String> listener) {
        // 텍스트뷰에 텍스트 변경 리스너를 지정한다
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listener.accept(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

}
