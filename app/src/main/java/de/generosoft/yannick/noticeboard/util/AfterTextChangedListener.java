package de.generosoft.yannick.noticeboard.util;

import android.text.Editable;
import android.text.TextWatcher;

public interface AfterTextChangedListener extends TextWatcher {

    @Override
    default void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    default void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    void afterTextChanged(Editable editable);
}
