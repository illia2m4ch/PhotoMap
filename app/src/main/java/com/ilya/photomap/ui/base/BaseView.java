package com.ilya.photomap.ui.base;

import android.view.View;

public interface BaseView {

    void setUIState(UIState state);
    void registerUIState(UIState state, View view);

}
