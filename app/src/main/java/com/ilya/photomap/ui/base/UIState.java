package com.ilya.photomap.ui.base;

public enum UIState {
    EMPTY(0),
    LOADING(1),
    CONTENT(2),
    ERROR(4);

    private int value;

    UIState(int value) {
        this.value = value;
    }

    int getValue() {
        return value;
    }
}
